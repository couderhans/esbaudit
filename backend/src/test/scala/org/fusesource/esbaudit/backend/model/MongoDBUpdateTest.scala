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
        ),
        STATUS -> Active(),
        TIMESTAMP -> Timestamp("yyyy-DD-dd", "HH:mm")))

      val flows = mongo.flow("record-01")
      assertEquals("We should have found 1 matching flow", 1, flows.size)
      assertEquals("CHeck if the records was updated",
        flows.head.in.body, "RECORD-01 after update")
  }

}