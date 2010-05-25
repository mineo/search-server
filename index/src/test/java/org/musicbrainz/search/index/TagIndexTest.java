package org.musicbrainz.search.index;

import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.RAMDirectory;
import org.musicbrainz.search.analysis.PerFieldEntityAnalyzer;

import java.sql.Connection;
import java.sql.Statement;


public class TagIndexTest extends AbstractIndexTest {

    public void setUp() throws Exception {
        super.setup();
    }

    private void createIndex(RAMDirectory ramDir) throws Exception {
        PerFieldAnalyzerWrapper analyzer = new PerFieldEntityAnalyzer(TagIndexField.class);
        IndexWriter writer = new IndexWriter(ramDir, analyzer, true, IndexWriter.MaxFieldLength.LIMITED);
        TagIndex ci = new TagIndex(createConnection());
        ci.init(writer);
        ci.indexData(writer, 0, Integer.MAX_VALUE);
        ci.destroy();
        writer.close();

    }

    private void addTagOne() throws Exception {
        Connection conn = createConnection();
        conn.setAutoCommit(true);


        Statement stmt = conn.createStatement();
        stmt.addBatch("INSERT INTO tag(id, name, refcount) VALUES (1, 'rock', 1);");
        stmt.executeBatch();
        stmt.close();
        conn.close();
    }

    public void testIndexTag() throws Exception {

        addTagOne();

        RAMDirectory ramDir = new RAMDirectory();
        createIndex(ramDir);
        IndexReader ir = IndexReader.open(ramDir, true);
        assertEquals(1, ir.numDocs());
        {
            Document doc = ir.document(0);
            assertEquals(1, doc.getFields(TagIndexField.TAG.getName()).length);
            assertEquals("rock", doc.getField(TagIndexField.TAG.getName()).stringValue());
            ir.close();
        }
    }


}