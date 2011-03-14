package org.fusesource.esbaudit.backend.model

import org.junit.Test
import org.junit.Assert._

import Flow._

class FlowTest {

  @Test
  def testTags = {
    val tag = Flow("some-bogus-id", TAGS -> Seq("Invoice"))
    assertEquals(true, tag.tags.contains("Invoice"))
    for(val tag <- tag.tags) {
      println(tag)
      assertEquals("Invoice", tag)
    }
  }



}