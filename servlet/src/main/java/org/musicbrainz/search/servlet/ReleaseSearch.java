package org.musicbrainz.search.servlet;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.SearcherManager;
import org.musicbrainz.search.index.DatabaseIndex;
import org.musicbrainz.search.index.ReleaseIndexField;
import org.musicbrainz.search.servlet.mmd1.ReleaseMmd1XmlWriter;
import org.musicbrainz.search.servlet.mmd2.ReleaseWriter;

public class ReleaseSearch extends AbstractSearchServer {

  protected void setupDefaultFields() {
    defaultFields = new ArrayList<String>();
    defaultFields.add(ReleaseIndexField.RELEASE.getName());
  }

  public ReleaseSearch() throws Exception {

    resultsWriter = new ReleaseWriter();
    mmd1Writer = new ReleaseMmd1XmlWriter();
    setupDefaultFields();
    analyzer = DatabaseIndex.getAnalyzer(ReleaseIndexField.class);
  }

  public ReleaseSearch(SearcherManager searcherManager) throws Exception {
    this();
    this.searcherManager = searcherManager;
    setLastServerUpdatedDate();
    resultsWriter.setLastServerUpdatedDate(this.getServerLastUpdatedDate());
  }

  @Override
  public QueryParser getParser() {
    return new ReleaseQueryParser(defaultFields.toArray(new String[0]), analyzer);
  }

  @Override
  protected String printExplainHeader(Document doc) throws IOException, ParseException {
    return doc.get(ReleaseIndexField.RELEASE_ID.getName()) + ':' + doc.get(ReleaseIndexField.RELEASE.getName()) + '\n';
  }

}