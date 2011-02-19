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
package org.fusesource.esbaudit.backend

import com.mongodb.Mongo
import com.mongodb.casbah.Imports._
import model.{Flow,Message, Active}

/**
 * MongoDB based implementation for {@link Backend}
 */
class MongoDB(val collection: String) extends Backend with Adapter {

  val connection = MongoConnection()
  val database = connection("test")

  def all = for (val found <- database(collection).find;
                 val exchange_id = found.getAs[String]("exchange_id"))
        yield Flow(exchange_id match {
          case Some(value) => value
          case None => "**unknown**"
        }, Message("//TODO: fix this"), Active())

  def store(flow: Flow) = {
    val record = MongoDBObject.newBuilder
    record += "exchange_id" -> flow.id
    record += "status" -> flow.status.toString


    val in = MongoDBObject.newBuilder
    in += "body" -> flow.in.body
    record += "in" -> in.result.asDBObject

    database(collection) += record.result.asDBObject
  }

  def update(flow: Flow) = println("Not updating %s - not implemented yet".format(flow))

}

object MongoDB {

  def apply() = new MongoDB("servicemix")
  def apply(collection: String) = new MongoDB(collection)

}