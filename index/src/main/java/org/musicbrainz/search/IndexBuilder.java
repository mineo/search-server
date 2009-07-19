/* Copyright (c) 2009 Lukas Lalinsky
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the MusicBrainz project nor the names of the
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.musicbrainz.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.sql.*;
import java.io.*;
import org.apache.lucene.analysis.Analyzer;
import org.musicbrainz.search.analysis.StandardUnaccentAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.apache.commons.lang.time.StopWatch;

public class IndexBuilder
{
	// Number of rows to process for each database 'chunk'
	private static final int IDS_PER_CHUNK = 10000;
	
	// Number of rows to process in 'test' mode
	private static final int MAX_TEST_ID = 50000;

	// Lucene parameters
	private static final int MAX_BUFFERED_DOCS = 10000;
	private static final int MERGE_FACTOR = 3000;
	
	
    public static void main(String[] args) throws SQLException, IOException
    {

    	IndexBuilderOptions options = new IndexBuilderOptions();
        CmdLineParser parser = new CmdLineParser(options);
        
    	try {
	        parser.parseArgument(args);
        } catch (CmdLineException e) {
        	System.err.println("Couldn't parse command line parameters");
            parser.printUsage(System.out);
            System.exit(1);
        }
    	
        // If requested, print command line usage
        if (options.isHelp()) {
            parser.printUsage(System.out);
            System.exit(1);
        }
        if (options.isTest()) { System.out.println("Running in test mode."); }
        
        // Try loading PostgreSql driver
		try {
			Class.forName("org.postgresql.Driver");
		}
		catch (ClassNotFoundException e) {
			System.err.println("Couldn't load org.postgresql.Driver");
			System.exit(1);
		}

		// Connect to main database
		String url = "jdbc:postgresql://" + options.getMainDatabaseHost() + "/" + options.getMainDatabaseName();
		Properties props = new Properties();
		props.setProperty("user", options.getMainDatabaseUser());
		props.setProperty("password", options.getMainDatabasePassword());
		Connection mainDbConn = DriverManager.getConnection(url, props);
		
		// Connect to raw database
		url = "jdbc:postgresql://" + options.getRawDatabaseHost() + "/" + options.getRawDatabaseName();
		props = new Properties();
		props.setProperty("user", options.getRawDatabaseUser());
		props.setProperty("password", options.getRawDatabasePassword());
		Connection rawDbConn = DriverManager.getConnection(url, props);
		
		StopWatch clock = new StopWatch();
		
		// MusicBrainz data indexing
		Index[] indexes = {
			new ArtistIndex(mainDbConn),
			new ReleaseIndex(mainDbConn),
			new ReleaseGroupIndex(mainDbConn),
			new TrackIndex(mainDbConn),
			new LabelIndex(mainDbConn),
			new AnnotationIndex(mainDbConn),
			new CDStubIndex(rawDbConn),
		};

		Analyzer analyzer = new StandardUnaccentAnalyzer();

		for (Index index : indexes) {
			
			// Check if this index should be built
			if (!options.buildIndex(index.getName())) {
				System.out.println("Skipping index: " + index.getName());
				continue;
			}
			
			clock.start();
			
			IndexWriter indexWriter;
			String path = options.getIndexesDir() + index.getName() + "_index";
			System.out.println("Building index: " + path);
			indexWriter = new IndexWriter(FSDirectory.getDirectory(path), analyzer, true, IndexWriter.MaxFieldLength.LIMITED);
			indexWriter.setMaxBufferedDocs(MAX_BUFFERED_DOCS);
			indexWriter.setMergeFactor(MERGE_FACTOR);

			int maxId = index.getMaxId();
			if (options.isTest() && MAX_TEST_ID < maxId)
				maxId = MAX_TEST_ID;
			int j = 0;
			while (j < maxId) {
				System.out.print("  Indexing " + j + "..." + (j + IDS_PER_CHUNK) + " / " + maxId + " (" + (100*j/maxId) + "%)\r");
				index.indexData(indexWriter, j, j + IDS_PER_CHUNK);
				j += IDS_PER_CHUNK;
			}
			System.out.println("\n  Optimizing");
			indexWriter.optimize();
			indexWriter.close();
			
			clock.stop();
			System.out.println("  Finished in " + Float.toString(clock.getTime()/1000) + " seconds");
			clock.reset();
		}

		// FreeDB data indexing
		FreeDBIndex index = new FreeDBIndex();
		if (options.buildIndex(index.getName())) {
			File dumpFile = new File(options.getFreeDBDump());
			index.setDumpFile(dumpFile);
			
			IndexWriter indexWriter;
			String path = options.getIndexesDir() + index.getName() + "_index";
			System.out.println("Building index: " + path);
			indexWriter = new IndexWriter(FSDirectory.getDirectory(path), analyzer, true, IndexWriter.MaxFieldLength.LIMITED);
			indexWriter.setMaxBufferedDocs(MAX_BUFFERED_DOCS);
			indexWriter.setMergeFactor(MERGE_FACTOR);
			
			clock.start();
			
			index.indexData(indexWriter);
			
			System.out.println("  Optimizing");
			indexWriter.optimize();
			indexWriter.close();
			
			clock.stop();
			System.out.println("  Finished in " + Float.toString(clock.getTime()/1000) + " seconds");
		}
    }
    
}

class IndexBuilderOptions {
	
	// Main database connection parameters
	
    @Option(name="--db-host", aliases = { "-h" }, usage="The database server to connect to. (default: localhost)")
    private String mainDatabaseHost = "localhost";
    public String getMainDatabaseHost() { return mainDatabaseHost; }
    
	@Option(name="--db-name", aliases = { "-d" }, usage="The name of the database server to connect to. (default: musicbrainz_db)")
    private String mainDatabaseName = "musicbrainz_db";        
	public String getMainDatabaseName() { return mainDatabaseName; }
	
    @Option(name="--db-user", aliases = { "-u" }, usage="The username to connect with. (default: musicbrainz_user)")
    private String mainDatabaseUser = "musicbrainz_user";
	public String getMainDatabaseUser() { return mainDatabaseUser; }

    @Option(name="--db-password", aliases = { "-p" }, usage="The password for the db user. (default: -blank-)")
    private String mainDatabasePassword = "";
	public String getMainDatabasePassword() { return mainDatabasePassword; }

    // Raw database connection parameters
    
    @Option(name="--raw-db-host", aliases = { "-o" }, usage="The raw database server to connect to. (default: localhost)")
    private String rawDatabaseHost = "localhost";
    public String getRawDatabaseHost() { return rawDatabaseHost; }
    
	@Option(name="--raw-db-name", aliases = { "-a" }, usage="The name of the raw database server to connect to. (default: musicbrainz_db_raw)")
    private String rawDatabaseName = "musicbrainz_db_raw";     
	public String getRawDatabaseName() { return rawDatabaseName; }

    @Option(name="--raw-db-user", aliases = { "-s" }, usage="The username for the raw database to connect with. (default: musicbrainz_user)")
    private String rawDatabaseUser = "musicbrainz_user";
	public String getRawDatabaseUser() { return rawDatabaseUser; }

    @Option(name="--raw-db-password", aliases = { "-w" }, usage="The password of the db user of the raw database. (default: -blank-)")
    private String rawDatabasePassword = "";
	public String getRawDatabasePassword() { return rawDatabasePassword; }

    // Indexes directory
    @Option(name="--indexes-dir", usage="The directory . (default: ./data/)")
    private String indexesDir = "." + System.getProperty("file.separator") + "data" + System.getProperty("file.separator");
	public String getIndexesDir() {
		if (!indexesDir.endsWith(System.getProperty("file.separator")))
			return indexesDir + System.getProperty("file.separator"); 
		else
			return indexesDir;
	}

	// FreeDB dump file
	@Option(name="--freedb-dump", usage="The FreeDB dump file to index.")
	private String freeDBDump = "";
	public String getFreeDBDump() { return freeDBDump; }
	
    // Selection of indexes to build
    @Option(name="--indexes", usage="A comma-separated list of indexes to build (artist,releasegroup,release,track,label,annotation,cdstub)")
    private String indexes = "artist,label,release,track,releasegroup,annotation,cdstub";
    public boolean buildIndex(String indexName) { return new ArrayList<String>(Arrays.asList(indexes.split(","))).contains(indexName); }
    
    // Test mode
    @Option(name="--test", aliases = { "-t" }, usage="Test the index builder by creating small text indexes.")
    private boolean test = false;
	public boolean isTest() { return test; }

    @Option(name="--help", usage="Print this usage information.")
    private boolean help = false;
	public boolean isHelp() { return help; }
	
}
