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
package org.fusesource.esbaudit.interceptors.camel

import org.fusesource.esbaudit.backend.Adapter
import org.apache.camel.model.ProcessorDefinition
import org.apache.camel.processor.{DelegateAsyncProcessor, DelegateProcessor}
import org.apache.camel.{Exchange, Message, Processor, AsyncCallback, CamelContext}
import collection.mutable.ListBuffer
import org.apache.camel.spi.{Synchronization, InterceptStrategy}
import org.fusesource.esbaudit.backend.model._
import scala.collection.JavaConversions.asScalaMap
import scala.collection.mutable.Map
import org.fusesource.esbaudit.backend.model.Flow._
import java.io.Serializable


/**
 * Auditor strategy to send flows/exchanges/... to a storage adapter
 */
case class AuditorStrategy(val adapter: Adapter) extends InterceptStrategy with Synchronization {

  val pending = new ListBuffer[String]

  var tags = Set[String]()

  def wrapProcessorInInterceptors(context: CamelContext, definition: ProcessorDefinition[_],
                                  from: Processor, to: Processor) = Auditor(to)

  def addTag(tag: String) : AuditorStrategy = {
    tags += tag
    this;
  }

  def onComplete(exchange: Exchange) = {
    println("Done %s".format(exchange))

    if (exchange.hasOut) {
      adapter.update(
        Flow(exchange.getExchangeId,
             OUT_MESSAGE -> toModel(exchange.getOut)))
    }

    adapter.update(
      Flow(exchange.getExchangeId,
           STATUS -> Done()))
  }


  def onFailure(exchange: Exchange) = {
    println("Error %s".format(exchange))
    adapter.update(
      Flow(exchange.getExchangeId,
            STATUS -> Error(),
            EXCEPTION -> exchange.getException))
     }

  case class Auditor(val delegate: Processor) extends DelegateAsyncProcessor(delegate) {

    override def process(exchange: Exchange, callback: AsyncCallback) = {

      if (!pending.contains(exchange.getExchangeId)) {
        exchange.addOnCompletion(AuditorStrategy.this)
        pending += exchange.getExchangeId

        adapter.store(toModel(exchange))
    }

      super.process(exchange, callback)
    }
  }

  def toModel(exchange: Exchange) : Flow = {
    Flow(exchange.getExchangeId,
         IN_MESSAGE -> toModel(exchange.getIn()),
         STATUS -> Active(),
         PROPERTIES -> extractProperties(exchange), //asScalaMap(exchange.getProperties).toMap,
         TAGS -> tags.toSeq,
         EXCEPTION -> exchange.getException)
  }

  def toModel(camel: Message) : org.fusesource.esbaudit.backend.model.Message = {
    val body = camel.getBody(classOf[java.io.Serializable])
    org.fusesource.esbaudit.backend.model.Message(if (body == null) camel.getBody(classOf[String]) else body,
                                                  extractHeaders(camel))
  }

  def extractHeaders(message: Message) = {
    val result = Map[String, AnyRef]()
    for ( (key,value) <- asScalaMap(message.getHeaders).toMap) {
      val serializable = message.getHeader(key, classOf[Serializable])
      result(key) = if (serializable == null) message.getHeader(key, classOf[String]) else serializable
    }
    result.toMap

  }


  def extractProperties(exchange: Exchange) = {
    val result = Map[String, AnyRef]()
    for ( (key,value) <- asScalaMap(exchange.getProperties).toMap) {
      val serializable = exchange.getProperty(key, classOf[Serializable])
      result(key) = if (serializable == null) exchange.getProperty(key, classOf[String]) else serializable
    }
    result.toMap

  }

}