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

import org.fusesource.esbaudit.backend.Log

/**
 * Models a Flow
 * - corresponds to a Camel Exchange
 * - corresponds to a set of correlated NMR Exchange
 *
 *
 */
//TODO Do we need this one?
/* class Flow(val id: String, val in: Message, val status: Status, val properties: Map[String, AnyRef], val tags: Seq[String] = Seq(), val exception: Exception = null, val out: Message = null) { */
class Flow(val id: String, val data: Map[String, AnyRef]) {

  import Flow._

  def extract[T](key: String) = data(key).asInstanceOf[T]

  lazy val status = extract[Status](STATUS)

  lazy val in = extract[Message](IN_MESSAGE)
  lazy val out = extract[Message](OUT_MESSAGE)

  lazy val properties : Map[String, AnyRef] = extract[Map[String, AnyRef]](PROPERTIES)

  lazy val tags = extract[Seq[String]](TAGS)

  lazy val exception = extract[Exception](EXCEPTION)

  override def toString = "Flow[%s](%s)".format(id, data)
}

abstract case class Status(val value: String) {
  override def toString = value
}
case class Done extends Status("done")
case class Error extends Status("error")
case class Active extends Status("active")

object Flow extends Log {

  val IN_MESSAGE = "in"
  val OUT_MESSAGE = "out"
  val STATUS = "status"
  val TAGS = "tags"
  val PROPERTIES = "properties"
  val EXCEPTION = "exception"

  def apply(id: String, data: (String, AnyRef)*) = {
    new Flow(id, validate(data))
  }

  def validate(input: Seq[(String, AnyRef)]) = {
    val result = new scala.collection.mutable.HashMap[String, AnyRef]
    for (tuple <- input) {
      tuple._1 match {
        case IN_MESSAGE if !tuple._2.isInstanceOf[Message] => warn("Invalid type for %s - skipping", tuple)
        case OUT_MESSAGE if !tuple._2.isInstanceOf[Message] => warn("Invalid type for %s - skipping", tuple)
        case STATUS if !tuple._2.isInstanceOf[Status] => warn("Invalid type for %s - skipping", tuple)
        case TAGS if !tuple._2.isInstanceOf[Iterable[String]] => warn("Invalid type for %s - skipping", tuple)
        case EXCEPTION if !tuple._2.isInstanceOf[Exception] => warn("Invalid type for %s - skipping", tuple)
        case PROPERTIES if !tuple._2.isInstanceOf[Map[String, AnyRef]] => warn("Invalid type for %s - skipping", tuple)
        case _ => result += tuple
      }
    }

    result.toMap
  }

}
