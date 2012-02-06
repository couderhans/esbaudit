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
import model._
import scala.collection.JavaConversions._
import model.Flow._
import java.util.Date
import java.text.SimpleDateFormat


/**
 * MongoDB based implementation for {@link Backend}
 */
class MongoDB(val collection: String) extends Backend with Adapter with Log {

  val connection = MongoConnection("127.0.0.1")
  val database = connection("test")

  def all = for (val found <- database(collection).find) yield toFlow(found)

  def flowsByDate(date: Date) = {
    val query = MongoDBObject("timestamp.date" -> date)
    for (val found <- database(collection).find(query)) yield toFlow(found)
  }

  def flowsByDate(timestamp: Timestamp) = {
    val query = MongoDBObject("timestamp.date" -> timestamp.date, "timestamp.time" -> timestamp.time)
    for (val found <- database(collection).find(query)) yield toFlow(found)
  }

  def flowsByTags(tags: Seq[String]) = {
    val query = "tags" $all tags
    for (val found <- database(collection).find(query)) yield toFlow(found)

  }

  def flowsByStatus(status: String) = {
    val query = MongoDBObject("status" -> status)
    for (val found <- database(collection).find(query)) yield toFlow(found)
  }

  def search(queryString: String) = {
    val reqs = for (part <- queryString.split("\\s")) yield {
      if (part.startsWith("label:")) {
        val tags = part.split(":").slice(1, 2)
        "tags" $all tags
      } else if (part.startsWith("on:")) {
        val date = part.split(":").slice(1, 2)
        "timestamp.date" $all date
      } else if (part.startsWith("status:")) {
        val status = part.split(":").slice(1, 2)
        "status" $all status
      } else if (part.startsWith("property:")) {
        val property = part.split(":").slice(1, 2)
        "properties" $all property
      } else if (part.startsWith("header:")) {
        val header = part.split(":").slice(1, 2)
        "headers" $all header
      } else {
        val builder = MongoDBObject.newBuilder
        builder += "in.body" -> part.r
        builder.result.asDBObject
      }
    }
    val query = reqs.foldLeft(MongoDBObject.empty)(_ ++ _)
    debug("Querying MongoDB: %s", query)
    for (val found <- database(collection).find(query)) yield toFlow(found)
  }

  def toFlow(record: DBObject) = {
    record.getAs[String]("exchange_id") match {
      case Some(id) => Flow(id,
        IN_MESSAGE -> toMessage(record.getAs[DBObject]("in")),
        OUT_MESSAGE -> toMessage(record.getAs[DBObject]("out")),
        STATUS -> toStatus(record.getAs[String]("status")),
        TAGS -> toSeq(record.getAs[BasicDBList]("tags")),
        PROPERTIES -> toMap(record.getAs[DBObject]("properties")),
        EXCEPTION -> record.getAs[DBObject]("exception").toString,
        TIMESTAMP -> toTimestamp(record.getAs[DBObject]("timestamp")))
      case None => warn("Unable to find exchange id in %s", record); null;
    }
  }

  def toStatus(option: Option[String]): Status = {
    option match {
      case Some("done") => Done()
      case Some("error") => Error()
      case Some("active") => Active()
      case _ => null
    }
  }

  def toTimestamp(option: Option[DBObject]): Timestamp = {
    option match {
      case Some(record) => {
        Timestamp(record.get("date").toString, record.get("time").toString)
      }
      case _ => null
    }
  }

  def toMessage(option: Option[DBObject]): Message = {
    option match {
      case Some(record) => {
        Message(record.get("body"), toMap(record.getAs[DBObject]("headers")))
      }
      case None => null
    }
  }

  def toMap(option: Option[DBObject]): Map[String, AnyRef] = {
    val result = scala.collection.mutable.Map[String, AnyRef]()
    option match {
      case Some(record) => {
        for (val row <- record) result += row._1 -> row._2
        result.toMap
      }
      case None => null
    }
  }

  def toSeq(option: Option[BasicDBList]): Seq[String] = {
    option match {
      case Some(list) => for (item <- list.toSeq) yield item.toString
      case None => Seq()
    }
  }


  def store(flow: Flow) = {

    val record = MongoDBObject.newBuilder
    record += "exchange_id" -> flow.id
    record += "status" -> flow.status.toString

    if (flow.data.contains(PROPERTIES)) {
      val properties = MongoDBObject.newBuilder
      for (property <- flow.properties) properties += property._1.replaceAll("\\.", "_") -> property._2

      record += "properties" -> properties.result.asDBObject
    }

    val in = MongoDBObject.newBuilder
    in += "body" -> flow.in.body

    if (flow.data.contains(TAGS)) {
      record += "tags" -> flow.tags
    }

    val headers = MongoDBObject.newBuilder
    for (header <- flow.in.headers) headers += header._1.replaceAll("\\.", "_") -> header._2

    in += "headers" -> headers.result.asDBObject

    record += "in" -> in.result.asDBObject

    val timestamp = MongoDBObject.newBuilder
    timestamp += "date" -> flow.timestamp.date
    timestamp += "time" -> flow.timestamp.time

    record += "timestamp" -> timestamp.result.asDBObject


    database(collection) += record.result.asDBObject
  }


  def flow(id: String): Option[Flow] = {
    val query = MongoDBObject("exchange_id" -> id)
    database(collection).findOne(query) match {
      case Some(record) => Some(toFlow(record))
      case None => None
    }
  }


  def update(flow: Flow) = {

    val query = MongoDBObject("exchange_id" -> flow.id)

    val update = MongoDBObject.newBuilder
    update += "exchange_id" -> flow.id
    update += "status" -> flow.status.toString

    if (flow.data.contains(PROPERTIES)) {
      val properties = MongoDBObject.newBuilder
      for (property <- flow.properties) properties += property._1.replaceAll("\\.", "_") -> property._2
      update += "properties" -> properties.result.asDBObject
    }

    val in = MongoDBObject.newBuilder
    in += "body" -> flow.in.body

    if (flow.data.contains(TAGS)) {
      update += "tags" -> flow.tags
    }

    val headers = MongoDBObject.newBuilder
    for (header <- flow.in.headers) headers += header._1.replaceAll("\\.", "_") -> header._2

    in += "headers" -> headers.result.asDBObject

    update += "in" -> in.result.asDBObject

    val timestamp = MongoDBObject.newBuilder
    timestamp += "date" -> flow.timestamp.date
    timestamp += "time" -> flow.timestamp.time

    update += "timestamp" -> timestamp.result.asDBObject

    database(collection).update(query, update.result.asDBObject, true, false)
  }

}

object MongoDB {

  def apply() = new MongoDB("servicemix")

  def apply(collection: String) = new MongoDB(collection)

}