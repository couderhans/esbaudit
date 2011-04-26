package org.fusesource.esbaudit.backend.model

import org.junit.Test
import org.junit.Assert._

import Flow._

/**
 * Created by IntelliJ IDEA.
 * User: hans
 * Date: 15/04/11
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */

class MongoDBSearchTest extends MongoAware {

  @Test
  def testPlainTextSearch = ifMongoAvailable { mongo =>
    mongo.store(Flow("record-01",
                IN_MESSAGE -> Message(
                  "COOKIES IN A JAR"
                ),
                STATUS -> Active()))
    mongo.store(Flow("record-02",
                IN_MESSAGE -> Message(
                  "BISCUITS IN A JAR"
                ),
                STATUS -> Active()))

    val flows = mongo.search("COOKIES").toBuffer
    assertEquals("We should have found 1 matching flow", 1, flows.size)
    assertEquals("We wanted cookies and not biscuits", flows.head.in.body, "COOKIES IN A JAR")
  }

  @Test
  def testSearchByTag = ifMongoAvailable { mongo =>
    mongo.store(Flow("record-01",
                IN_MESSAGE -> Message(
                  "COOKIES IN A JAR"
                ),
                STATUS -> Active(),
                TAGS -> Seq("cookie")))
    mongo.store(Flow("record-02",
                IN_MESSAGE -> Message(
                  "BISCUITS IN A JAR"
                ),
                STATUS -> Active(),
                TAGS -> Seq("biscuit")))

    val flows = mongo.search("label:cookie").toBuffer
    assertEquals("We should have found 1 matching flow", 1, flows.size)
    assertEquals("We wanted cookies and not biscuits", flows.head.in.body, "COOKIES IN A JAR")
  }

  @Test
  def testSearchByTagAndByPlainText = ifMongoAvailable { mongo =>
    mongo.store(Flow("record-01",
                IN_MESSAGE -> Message(
                  "COOKIES IN A JAR"
                ),
                STATUS -> Active(),
                TAGS -> Seq("cookie")))
    mongo.store(Flow("record-02",
                IN_MESSAGE -> Message(
                  "BISCUITS IN A JAR"
                ),
                STATUS -> Active(),
                TAGS -> Seq("biscuit")))

    val flows = mongo.search("COOKIES label:cookie").toBuffer
    assertEquals("We should have found 1 matching flow", 1, flows.size)
    assertEquals("We wanted cookies and not biscuits", flows.head.in.body, "COOKIES IN A JAR")

    val noflows = mongo.search("COOKIES label:biscuit").toBuffer
    assertEquals("We should have found no matching flows", 0, noflows.size)
  }


}