package org.fusesource.esbaudit.web

import org.junit.Test
import org.junit.Assert._


class FlowByTagsResourceTest {

  @Test
  def testAddTag {
    val resource = new FlowsByTagResource("inbound")
    assertEquals("/flows/tag/inbound+po", resource.urlAdd("po"))
  }

  @Test
  def testRemoveTag {
    val resource = new FlowsByTagResource("inbound+po")
    assertEquals("/flows/tag/inbound", resource.urlRemove("po"))
  }

    @Test
  def testRemoveLastTag {
    val resource = new FlowsByTagResource("inbound")
    assertEquals("/", resource.urlRemove("inbound"))
  }

}