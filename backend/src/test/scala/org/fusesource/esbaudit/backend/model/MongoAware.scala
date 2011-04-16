package org.fusesource.esbaudit.backend.model

import java.io.IOException
import org.fusesource.esbaudit.backend.{Log, MongoDB}
import org.junit.{After, AfterClass}

/**
 * Created by IntelliJ IDEA.
 * User: hans
 * Date: 15/04/11
 * Time: 14:51
 * To change this template use File | Settings | File Templates.
 */

trait MongoAware extends Log {

  def ifMongoAvailable(testcase: MongoDB => Unit) = {
    val collection = "servicemix_%s".format(System.currentTimeMillis)

    val option = try {
      Some(MongoDB(collection))
    } catch {
      case e: Exception => None
    }

    option match {
        case Some(mongo) =>
          try {
            info("Created collection %s".format(collection))
            testcase(mongo)
          } finally {
            mongo.database.getCollection(collection).drop
            info("Dropped collection %s".format(collection))
          }
        case None =>
          warn("No MongoDB instance running on local machine -- skipping unit tests that require MongoDB server")
    }
  }

}