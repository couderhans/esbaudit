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


import org.junit.Assert._
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.camel.scala.dsl.builder.{RouteBuilder, RouteBuilderSupport}
import org.junit.{Before, Test}
import org.fusesource.esbaudit.backend.model.Done
import java.util.concurrent.TimeUnit
import org.fusesource.esbaudit.backend.model.Error
import org.apache.camel.{ExchangePattern, Exchange, Processor}
import org.fusesource.esbaudit.backend.MongoDB

/**
 * Test to ensure basic business level auditing happens correctly
 */
class BasicBusinessAuditTest extends CamelTestSupport with RouteBuilderSupport {

  val MESSAGE = "Stand aside! Important message comin' through!"
  val REPLY_MESSAGE = "Is this t' answer you were expectin'?"

  val PROPERTY_NAME = "some.property.name"
  val PROPERTY_VALUE = "some.property.value"

  val HEADER_NAME = "X-Test"
  val HEADER_VALUE = "Header value"

  val REPLY_HEADER_NAME = "X-Test-Reply"
  val REPLY_HEADER_VALUE = "Reply value"

  val adapter = new MockAdapter

  @Before
  def reset = adapter.reset

  @Test
  def ourFirstExchangeTest = {
    getMockEndpoint("mock:simple").expectedMessageCount(1)

    val exchange = template.asyncSend("direct:simple", new Processor() {
      def process(exchange: Exchange) = {
        exchange.setProperty(PROPERTY_NAME, PROPERTY_VALUE)
        exchange.getIn.setBody(MESSAGE)
        exchange.getIn.setHeader(HEADER_NAME, HEADER_VALUE)
      }
    })

    assertMockEndpointsSatisfied

    Thread.sleep(100)

    assertEquals("Should have audited one flow", 1, adapter.flows.size)
    val flow = adapter.flows.head
    assertEquals("Flow should match the exchange id", exchange.get.getExchangeId, flow.id)
    assertEquals("Original body should have been audited",
                 MESSAGE, flow.in.body)
    assertEquals("Flow should contain property key and value", PROPERTY_VALUE, flow.properties(PROPERTY_NAME))
    assertEquals("Flow should contain message headers also", HEADER_VALUE, flow.in.headers(HEADER_NAME))
    assertTrue("Flow should have been tagged with 'po'", flow.tags.contains("po"))
    assertEquals("Flow ended succesfully", Done(), flow.status)
  }

  @Test
  def multipleHopsTest = {
    getMockEndpoint("mock:hops").expectedMessageCount(1)

    val exchange = template.asyncSend("direct:hops", new Processor() {
      def process(exchange: Exchange) = exchange.getIn.setBody("Stand aside! Important message comin' through!")
    })

    assertMockEndpointsSatisfied

    Thread.sleep(100)

    assertEquals("Should have audited one flow", 1, adapter.flows.size)
    val flow = adapter.flows.head
    assertEquals("Flow should match the exchange id", exchange.get.getExchangeId, flow.id)
    assertEquals("Original body should have been audited",
                 MESSAGE, flow.in.body)
    assertEquals("Flow ended succesfully", Done(), flow.status)
  }

 @Test
  def inOutTest = {
    getMockEndpoint("mock:inout").expectedMessageCount(1)

    val exchange = template.asyncSend("direct:inout", new Processor() {
      def process(exchange: Exchange) = {
        exchange.setPattern(ExchangePattern.InOut)
        exchange.setProperty(PROPERTY_NAME, PROPERTY_VALUE)
        exchange.getIn.setBody(MESSAGE)
        exchange.getIn.setHeader(HEADER_NAME, HEADER_VALUE)
      }
    })

    assertMockEndpointsSatisfied

    Thread.sleep(500)

    assertEquals("Should have audited one flow", 1, adapter.flows.size)
    val flow = adapter.flows.head

    assertEquals("Flow should match the exchange id", exchange.get.getExchangeId, flow.id)
    assertEquals("Original body should have been audited", MESSAGE, flow.in.body)
    assertEquals("Reply message body should have been audited", REPLY_MESSAGE, flow.out.body)

    assertEquals("Flow should contain property key and value", PROPERTY_VALUE, flow.properties(PROPERTY_NAME))

    assertEquals("Flow should contain in message headers", HEADER_VALUE, flow.in.headers(HEADER_NAME))
    assertEquals("Flow should contain out message headers", REPLY_HEADER_VALUE, flow.out.headers(REPLY_HEADER_NAME))

    assertTrue("Flow should have been tagged with 'po'", flow.tags.contains("po"))
    assertEquals("Flow ended succesfully", Done(), flow.status)
  }

  @Test
  def errorExchangeTest = {
    getMockEndpoint("mock:error").expectedMessageCount(0)

    val exchange = template.asyncSend("direct:error", new Processor() {
      def process(exchange: Exchange) = {
        exchange.getIn.setBody("I dare you to throw an exception on me!")
      }
    })

    assertMockEndpointsSatisfied(100, TimeUnit.MICROSECONDS)

    Thread.sleep(100)

    val flow = adapter.flows.head

    assertEquals("Flow status was Error() as expected", Error(), flow.status)

  }

  override def createCamelContext = {
    val context = super.createCamelContext
    context.addInterceptStrategy(AuditorStrategy(adapter).addTag("po"))
    context
  }

  override def createRouteBuilder = new RouteBuilder {
    "direct:simple" to "mock:simple"
    "direct:hops" to "log:hops" to "mock:hops"

    "direct:inout" process { exchange : Exchange =>
      val out = exchange.getOut
      out.setBody(REPLY_MESSAGE)
      out.setHeader(REPLY_HEADER_NAME, REPLY_HEADER_VALUE)
    } to("mock:inout")

    "direct:error" throwException(new RuntimeException("Oh oh - this is going wrong!!"))
  }
}