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
import scala.collection.JavaConversions._
import model.{Flow,Message, Active, Status, Done, Error}

/**
 * MongoDB based implementation for {@link Backend}
 */
class MongoDB(val collection: String) extends Backend with Adapter {

  val connection = MongoConnection()
  val database = connection("test")

  def all = for (val found <- database(collection).find) yield toFlow(found)

  def toFlow(record: DBObject) = {
    println(record.get("in"))
    record.getAs[String]("exchange_id") match {
      case Some(id) => Flow(id, toMessage(record.getAs[DBObject]("in")), toStatus(record.getAs[String]("status")), toMap(record.getAs[DBObject]("properties")))
      case None => null  //TODO: Log a WARNING instead
    }
  }

  def toStatus(option: Option[String]) : Status = {
    option match {
      case Some("done") => Done()
      case Some("error") => Error()
      case Some("active") => Active()
      case _ => null
    }
  }

  def toMessage(option: Option[DBObject]) : Message = {
    option match {
      case Some(record) => {
        Message(record.get("body"), toMap(record.getAs[DBObject]("headers")))
      }
      case None => null
    }
  }

  def toMap(option: Option[DBObject]) : Map[String, AnyRef] = {
    val result = scala.collection.mutable.Map[String, AnyRef]()
    option match {
      case Some(record) => {
        for( val row <- record.keySet) println(row)
        for(val row <- record) result += row._1 -> row._2
        result.toMap
      }
      case None => null
    }
  }

  def oldAll = for (val found <- database(collection).find;
                 val exchange_id = found.getAs[String]("exchange_id"))
        yield Flow(exchange_id match {
          case Some(value) => value
          case None => "**unknown**"
        }, Message("//TODO: fix this", Map("fix" -> "this")), Active(), null)

  def store(flow: Flow) = {
    val record = MongoDBObject.newBuilder
    record += "exchange_id" -> flow.id
    record += "status" -> flow.status.toString

    val properties = MongoDBObject.newBuilder
    for (property <- flow.properties) properties += property._1 -> property._2

    record += "properties" -> properties.result.asDBObject

    val in = MongoDBObject.newBuilder
    in += "body" -> flow.in.body

    val headers = MongoDBObject.newBuilder
    for (header <- flow.in.headers) headers += header._1 -> header._2

    in += "headers" -> headers.result.asDBObject

    record += "in" -> in.result.asDBObject

    database(collection) += record.result.asDBObject
  }

  def update(flow: Flow) = println("Not updating %s - not implemented yet".format(flow))

}

object MongoDB {

  def apply() = new MongoDB("servicemix")
  def apply(collection: String) = new MongoDB(collection)

}