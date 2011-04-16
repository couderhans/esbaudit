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
import org.fusesource.esbaudit.backend.MongoDB
import org.fusesource.esbaudit.backend.model._

val collection = "servicemix_%s".format(System.currentTimeMillis)
println("- Creating collection %s".format(collection))
val mongo = MongoDB(collection)

println
println("- Creating an active flow")
// create an new Active Flow
mongo.store(Flow("flow-testing-00001",
                 Message("some body", Map("some" -> "header")),
                 Active(), Map("property" -> "value"), Seq("invoice")))

println
println("- Creating another active flow...")
mongo.store(Flow("flow-testing-00002",
                 Message("some body", Map("some" -> "header")),
                 Active(), Map("property" -> "value"), Seq("po", "inbound")))

println("  ... and marking it done")
mongo.update(Flow("flow-testing-00002",
                     null,
                     Done(), Map("property" -> "value")))

println
println("- Display all flow objects in the database")
for (flow <- mongo.all) {
  println("  %s".format(flow))
}

println
println("- Display raw record information in the database".format(collection))
for (record <- mongo.database(mongo.collection).find()) {
  println("  %s".format(record))
}

println
println("- Display all flows tagged as invoice in the database")
for (flow <- mongo.flowsByTags(Seq("po","invoice"))) {
  println("  %s".format(flow))
}

println
println("- Display all flows found by the searcg as in the database")
for (flow <- mongo.search("KARTON"){
  println("  %s".format(flow))
}

println
println("- Dropping collection %s".format(collection))
mongo.database.getCollection(collection).drop

