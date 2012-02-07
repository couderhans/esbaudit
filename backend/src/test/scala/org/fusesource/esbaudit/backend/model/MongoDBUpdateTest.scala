package org.fusesource.esbaudit.backend.model

import org.junit.Test
import org.junit.Assert._

import Flow._


class MongoDBUpdateTest extends MongoAware {

  @Test
  def testUpdateRecord = ifMongoAvailable {
    mongo =>
      mongo.store(Flow("record-01",
        IN_MESSAGE -> Message(
          "RECORD-01 before update"
        ),
        STATUS -> Active(),
        TIMESTAMP -> Timestamp("yyyy-DD-dd", "HH:mm")))

      mongo.update(Flow("record-01",
        IN_MESSAGE -> Message(
          "RECORD-01 after update"
        )))

      val flows = mongo.flow("record-01")
      assertEquals("We should have found 1 matching flow", 1, flows.size)
      assertEquals("Check if the records was updated", flows.head.in.body, "RECORD-01 after update")
      assertEquals("Check if other values have been kept in the record", Active(), flows.head.status)
  }

}