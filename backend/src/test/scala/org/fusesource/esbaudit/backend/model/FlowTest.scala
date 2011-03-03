package org.fusesource.esbaudit.backend.model

import org.junit.Test
import org.junit.Assert._

class FlowTest {

  @Test
  def testDocumentType = {
    val doctype = Flow(null, null, null, Map(Flow.DOCUMENT_TYPE -> "Purchase Order"), null)
    assertEquals("Purchase Order", doctype.documentType)

    val nodoctype = Flow(null, null, null, Map(), null)
    assertEquals("Unknown", nodoctype.documentType)
  }

  @Test
  def testTags = {
    val tag = Flow(null, null, null, null, Seq("Invoice"))
    //assertEquals(true, tag.tags.contains("Invoice"))
    for(val tag <- tag.tags) {
      println(tag)
      assertEquals("Invoice", tag)
    }
  }



}