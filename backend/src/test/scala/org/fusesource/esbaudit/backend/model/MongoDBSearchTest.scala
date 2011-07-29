/**
 * Copyright (C) 2009-2010 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.esbaudit.backend.model

import org.junit.Test
import org.junit.Assert._

import Flow._


class MongoDBSearchTest extends MongoAware {

  @Test
  def testPlainTextSearch = ifMongoAvailable { mongo =>
    mongo.store(Flow("record-01",
                IN_MESSAGE -> Message(
                  "COOKIES IN A JAR"
                ),
                STATUS -> Active(),
                TIMESTAMP -> Timestamp("yyyy-DD-dd","HH:mm")))
    mongo.store(Flow("record-02",
                IN_MESSAGE -> Message(
                  "BISCUITS IN A JAR"
                ),
                STATUS -> Active(),
                TIMESTAMP -> Timestamp("yyyy-DD-dd","HH:mm")))
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
                TAGS -> Seq("cookie"),
                TIMESTAMP -> Timestamp("yyyy-DD-dd","HH:mm")))
    mongo.store(Flow("record-02",
                IN_MESSAGE -> Message(
                  "BISCUITS IN A JAR"
                ),
                STATUS -> Active(),
                TAGS -> Seq("biscuit"),
                TIMESTAMP -> Timestamp("yyyy-DD-dd","HH:mm")))
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
                TAGS -> Seq("cookie"),
                TIMESTAMP -> Timestamp("yyyy-DD-dd","HH:mm")))
    mongo.store(Flow("record-02",
                IN_MESSAGE -> Message(
                  "BISCUITS IN A JAR"
                ),
                STATUS -> Active(),
                TAGS -> Seq("biscuit"),
                TIMESTAMP -> Timestamp("yyyy-DD-dd","HH:mm")))
    val flows = mongo.search("COOKIES label:cookie").toBuffer
    assertEquals("We should have found 1 matching flow", 1, flows.size)
    assertEquals("We wanted cookies and not biscuits", flows.head.in.body, "COOKIES IN A JAR")

    val noflows = mongo.search("COOKIES label:biscuit").toBuffer
    assertEquals("We should have found no matching flows", 0, noflows.size)
  }


}