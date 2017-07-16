package org.musicbrainz.search.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;
import org.musicbrainz.search.LuceneVersion;
import org.musicbrainz.search.MbDocument;
import org.musicbrainz.search.index.AnnotationIndexField;
import org.musicbrainz.search.type.AnnotationType;
import org.musicbrainz.search.index.DatabaseIndex;
import org.musicbrainz.search.index.MetaIndexField;

/**
 * Test retrieving Annotations entries from index and Outputting as Html
 */
public class FindAnnotationTest {

  private AbstractSearchServer ss;
  private AbstractDismaxSearchServer sd;


  @Before
  public void setUp() throws Exception {
    RAMDirectory ramDir = new RAMDirectory();
    Analyzer analyzer = DatabaseIndex.getAnalyzer(AnnotationIndexField.class);
    IndexWriterConfig writerConfig = new IndexWriterConfig(LuceneVersion.LUCENE_VERSION,analyzer);
    IndexWriter writer = new IndexWriter(ramDir, writerConfig);

    //A complete Release Annotation entry
    {
      MbDocument doc = new MbDocument();
      doc.addField(AnnotationIndexField.TYPE, AnnotationType.RELEASE.getName());
      doc.addField(AnnotationIndexField.NAME, "Pieds nus sur la braise");
      doc.addField(AnnotationIndexField.ENTITY, "bdb24cb5-404b-4f60-bba4-7b730325ae47");
      doc.addField(AnnotationIndexField.TEXT, "EAN: 0828768226629 - DiscID: TWj6cLku360MfFYAq_MEaT_stgc-");
      writer.addDocument(doc.getLuceneDocument());
    }

    //A complete Artist Annotation entry
    {
      MbDocument doc = new MbDocument();
      doc.addField(AnnotationIndexField.TYPE, AnnotationType.ARTIST.getName());
      doc.addField(AnnotationIndexField.NAME, "Bjork");
      doc.addField(AnnotationIndexField.ENTITY, "123456qa-404b-4f60-bba4-7b730325ae47");
      doc.addField(AnnotationIndexField.TEXT, "Icelandic");
      writer.addDocument(doc.getLuceneDocument());
    }

    //A complete Recording Annotation entry
    {
      MbDocument doc = new MbDocument();
      doc.addField(AnnotationIndexField.TYPE, AnnotationType.RECORDING.getName());
      doc.addField(AnnotationIndexField.NAME, "Give my love to Kevin");
      doc.addField(AnnotationIndexField.ENTITY, "bdb24cdd-404b-4f60-bba4-7b730325ae47");
      doc.addField(AnnotationIndexField.TEXT, "Single");
      writer.addDocument(doc.getLuceneDocument());
    }

    //A complete Label Annotation entry
    {
      MbDocument doc = new MbDocument();
      doc.addField(AnnotationIndexField.TYPE, AnnotationType.LABEL.getName());
      doc.addField(AnnotationIndexField.NAME, "Reception Records");
      doc.addField(AnnotationIndexField.ENTITY, "bdb32cb5-404b-4f60-bba4-7b730325ae47");
      doc.addField(AnnotationIndexField.TEXT, "Setup by Wedding Present, released a few records by others");
      writer.addDocument(doc.getLuceneDocument());
    }

    //A complete Release Group Annotation entry
    {
      MbDocument doc = new MbDocument();
      doc.addField(AnnotationIndexField.TYPE, AnnotationType.RELEASE_GROUP.getName());
      doc.addField(AnnotationIndexField.NAME, "3 Songs");
      doc.addField(AnnotationIndexField.ENTITY, "aaa24cb5-404b-4f60-bba4-7b730325ae47");
      doc.addField(AnnotationIndexField.TEXT, "Also known as Corduroy");
      writer.addDocument(doc.getLuceneDocument());
    }

    //A complete Work Annotation entry
    {
      MbDocument doc = new MbDocument();
      doc.addField(AnnotationIndexField.TYPE, AnnotationType.WORK.getName());
      doc.addField(AnnotationIndexField.NAME, "Song 2");
      doc.addField(AnnotationIndexField.ENTITY, "DDDD24cb5-404b-4f60-bba4-7b730325ae47");
      doc.addField(AnnotationIndexField.TEXT, "There was no Song 1");
      writer.addDocument(doc.getLuceneDocument());
    }

      //A complete Area Annotation entry
      {
          MbDocument doc = new MbDocument();
          doc.addField(AnnotationIndexField.TYPE, AnnotationType.AREA.getName());
          doc.addField(AnnotationIndexField.NAME, "Song 2");
          doc.addField(AnnotationIndexField.ENTITY, "DDDD24cb5-404b-4f60-bba4-7b730325ae47");
          doc.addField(AnnotationIndexField.TEXT, "There was no Song 1");
          writer.addDocument(doc.getLuceneDocument());
      }

      //A complete Series Annotation entry
      {
          MbDocument doc = new MbDocument();
          doc.addField(AnnotationIndexField.TYPE, AnnotationType.SERIES.getName());
          doc.addField(AnnotationIndexField.NAME, "Song 2");
          doc.addField(AnnotationIndexField.ENTITY, "DDDD24cb5-404b-4f60-bba4-7b730325ae47");
          doc.addField(AnnotationIndexField.TEXT, "There was no Song 1");
          writer.addDocument(doc.getLuceneDocument());
      }

      //A complete Instrument Annotation entry
      {
          MbDocument doc = new MbDocument();
          doc.addField(AnnotationIndexField.TYPE, AnnotationType.INSTRUMENT.getName());
          doc.addField(AnnotationIndexField.NAME, "Song 2");
          doc.addField(AnnotationIndexField.ENTITY, "DDDD24cb5-404b-4f60-bba4-7b730325ae47");
          doc.addField(AnnotationIndexField.TEXT, "There was no Song 1");
          writer.addDocument(doc.getLuceneDocument());
      }

      //A complete Place Annotation entry
      {
          MbDocument doc = new MbDocument();
          doc.addField(AnnotationIndexField.TYPE, AnnotationType.PLACE.getName());
          doc.addField(AnnotationIndexField.NAME, "Song 2");
          doc.addField(AnnotationIndexField.ENTITY, "DDDD24cb5-404b-4f60-bba4-7b730325ae47");
          doc.addField(AnnotationIndexField.TEXT, "There was no Song 1");
          writer.addDocument(doc.getLuceneDocument());
      }


      //A complete Event Annotation entry
      {
          MbDocument doc = new MbDocument();
          doc.addField(AnnotationIndexField.TYPE, AnnotationType.EVENT.getName());
          doc.addField(AnnotationIndexField.NAME, "Song 2");
          doc.addField(AnnotationIndexField.ENTITY, "DDDD24cb5-404b-4f60-bba4-7b730325ae47");
          doc.addField(AnnotationIndexField.TEXT, "There was no Song 1");
          writer.addDocument(doc.getLuceneDocument());
      }

      {
      MbDocument doc = new MbDocument();
      doc.addField(MetaIndexField.META, MetaIndexField.META_VALUE);
      doc.addNumericField(MetaIndexField.LAST_UPDATED, new Date().getTime());
      writer.addDocument(doc.getLuceneDocument());
    }

    writer.close();
    SearcherManager searcherManager = new SearcherManager(ramDir, new MusicBrainzSearcherFactory(
        ResourceType.ANNOTATION));
    ss = new AnnotationSearch(searcherManager);
    sd = new AnnotationDismaxSearch(ss);

  }

  @Test
  public void testSearchByTypeRelease() throws Exception {
    Results res = ss.search("type:release", 0, 10);
    assertEquals(1, res.getTotalHits());
  }

  @Test
  public void testSearchByTypeArtist() throws Exception {
    Results res = ss.search("type:artist", 0, 10);
    assertEquals(1, res.getTotalHits());
  }

  @Test
  public void testSearchByDismax1() throws Exception {
    Results res = sd.search("Pieds nus", 0, 10);
    assertEquals(1, res.getTotalHits());
  }

  @Test
  public void testSearchByDismax2() throws Exception {
    Results res = sd.search("0828768226629", 0, 10);
    assertEquals(1, res.getTotalHits());
  }

  @Test
  public void testSearchByTypeReleaseGroup() throws Exception {
    Results res = ss.search("type:release-group", 0, 10);
    assertEquals("3 Songs", res.results.get(0).getDoc().get(AnnotationIndexField.NAME));
    assertEquals(1, res.getTotalHits());
  }

  @Test
  public void testSearchByTypeRecording() throws Exception {
    Results res = ss.search("type:recording", 0, 10);
    assertEquals(1, res.getTotalHits());
  }

  @Test
  public void testSearchByTypeWork() throws Exception {
    Results res = ss.search("type:work", 0, 10);
    assertEquals(1, res.getTotalHits());
  }

    @Test
    public void testSearchByTypeArea() throws Exception {
        Results res = ss.search("type:area", 0, 10);
        assertEquals(1, res.getTotalHits());
    }

    @Test
    public void testSearchByTypeSeries() throws Exception {
        Results res = ss.search("type:series", 0, 10);
        assertEquals(1, res.getTotalHits());
    }

    @Test
    public void testSearchByTypeInstrument() throws Exception {
        Results res = ss.search("type:instrument", 0, 10);
        assertEquals(1, res.getTotalHits());
    }

    @Test
    public void testSearchByTypePlace() throws Exception {
        Results res = ss.search("type:place", 0, 10);
        assertEquals(1, res.getTotalHits());
    }

    @Test
    public void testSearchByTypeEvent() throws Exception {
        Results res = ss.search("type:event", 0, 10);
        assertEquals(1, res.getTotalHits());
    }

    @Test
  public void testSearchByName() throws Exception {
    Results res = ss.search("name:Pieds nus sur la braise", 0, 10);
    assertEquals(1, res.getTotalHits());
  }

  @Test
  public void testSearchByNameNoMatch() throws Exception {
    Results res = ss.search("name:fred", 0, 10);
    assertEquals(0, res.getTotalHits());
  }

  @Test
  public void testSearchByEntity() throws Exception {
    Results res = ss.search("entity:bdb24cb5-404b-4f60-bba4-7b730325ae47", 0, 10);
    assertEquals(1, res.getTotalHits());
  }

  @Test
  public void testSearchByEntitydNoMatch() throws Exception {
    Results res = ss.search("entity:bdb24cb5-404b-4f60-bba4-000000000000", 0, 10);
    assertEquals(0, res.getTotalHits());
  }

  @Test
  public void testSearchByText() throws Exception {
    Results res = ss.search("text:DiscID", 0, 10);
    assertEquals(1, res.getTotalHits());
  }

  @Test
  public void testSearchByTextNoMatch() throws Exception {
    Results res = ss.search("text:fred", 0, 10);
    assertEquals(0, res.getTotalHits());
  }

  @Test
  public void testSearchByDefaultField() throws Exception {

    Results res = ss.search("DiscID", 0, 10);
    assertEquals(1, res.getTotalHits());
  }

  /**
   * @throws Exception
   */
  @Test
  public void testOutputXml() throws Exception {

    Results res = ss.search("entity:bdb24cb5-404b-4f60-bba4-7b730325ae47", 0, 1);
    org.musicbrainz.search.servlet.mmd2.ResultsWriter writer = ss.getMmd2Writer();
    StringWriter sw = new StringWriter();
    PrintWriter pr = new PrintWriter(sw);
    writer.write(pr, res, SearchServerServlet.RESPONSE_XML, true);
    pr.close();

    String output = sw.toString();
    System.out.println("Xml is" + output);
    assertTrue(output.contains("xmlns:ext=\"http://musicbrainz.org/ns/ext#-2.0\""));
    assertTrue(output.contains("count=\"1\""));
    assertTrue(output.contains("offset=\"0\""));
    assertTrue(output.contains("score=\"100\""));
    assertTrue(output.contains("<name>Pieds nus sur la braise</name>"));
    assertTrue(output.contains("type=\"release\""));
    assertTrue(output.contains("<entity>bdb24cb5-404b-4f60-bba4-7b730325ae47</entity>"));
    assertTrue (output.contains("<text>EAN: 0828768226629 - DiscID: TWj6cLku360MfFYAq_MEaT_stgc-</text>"));

  }

  @Test
  public void testOutputJson() throws Exception {

    Results res = ss.search("entity:bdb24cb5-404b-4f60-bba4-7b730325ae47", 0, 1);
    org.musicbrainz.search.servlet.mmd2.ResultsWriter writer = ss.getMmd2Writer();
    StringWriter sw = new StringWriter();
    PrintWriter pr = new PrintWriter(sw);
    writer.write(pr, res, SearchServerServlet.RESPONSE_JSON);
    pr.close();

    String output = sw.toString();
    System.out.println("Json is" + output);

    assertTrue(output.contains("\"count\":1"));
    assertTrue(output.contains("\"offset\":0,"));
    assertTrue(output.contains("\"score\":\"100\","));
    assertTrue(output.contains("\"name\":\"Pieds nus sur la braise\""));
    assertTrue(output.contains("\"type\":\"release\""));
    assertTrue(output.contains("\"entity\":\"bdb24cb5-404b-4f60-bba4-7b730325ae47\""));
    assertTrue(output.contains("\"text\":\"EAN: 0828768226629 - DiscID: TWj6cLku360MfFYAq_MEaT_stgc-\""));

  }

  @Test
  public void testOutputJsonNew() throws Exception {

    Results res = ss.search("entity:bdb24cb5-404b-4f60-bba4-7b730325ae47", 0, 1);
    org.musicbrainz.search.servlet.mmd2.ResultsWriter writer = ss.getMmd2Writer();
    StringWriter sw = new StringWriter();
    PrintWriter pr = new PrintWriter(sw);
    writer.write(pr, res, SearchServerServlet.RESPONSE_JSON_NEW);
    pr.close();

    String output = sw.toString();
    System.out.println("Json New is" + output);

    assertTrue(output.contains("annotations"));
    assertTrue(output.contains("\"score\":100.0"));
    assertTrue(output.contains("\"name\":\"Pieds nus sur la braise\""));
    assertTrue(output.contains("\"type\":\"release\""));
    assertTrue(output.contains("\"entity\":\"bdb24cb5-404b-4f60-bba4-7b730325ae47\""));
    assertTrue(output.contains("\"text\":\"EAN: 0828768226629 - DiscID: TWj6cLku360MfFYAq_MEaT_stgc-\""));
    assertTrue(output.contains("\"count\":1"));
    assertTrue(output.contains("\"offset\":0,"));

  }

  @Test
  public void testOutputJsonNewPretty() throws Exception {

    Results res = ss.search("entity:bdb24cb5-404b-4f60-bba4-7b730325ae47", 0, 1);
    org.musicbrainz.search.servlet.mmd2.ResultsWriter writer = ss.getMmd2Writer();
    StringWriter sw = new StringWriter();
    PrintWriter pr = new PrintWriter(sw);
    writer.write(pr, res, SearchServerServlet.RESPONSE_JSON_NEW, true);
    pr.close();

    String output = sw.toString();
    System.out.println("Json New Pretty is" + output);
    assertTrue(output.contains("\"offset\" : 0,"));

  }
}
