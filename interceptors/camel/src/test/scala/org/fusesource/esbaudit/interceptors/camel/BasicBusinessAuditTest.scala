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

import org.junit.Test;
import org.junit.Assert._
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.camel.scala.dsl.builder.{RouteBuilder, RouteBuilderSupport}
import org.fusesource.esbaudit.backend.MongoDB
import org.apache.camel.{Exchange, Processor}

/**
 * Test to ensure basic business level auditing happens correctly
 */
class BasicBusinessAuditTest extends CamelTestSupport with RouteBuilderSupport {

  val adapter = new MockAdapter

  @Test
  def ourFirstExchangeTest = {
    getMockEndpoint("mock:test").expectedMessageCount(1)

    val exchange = template.asyncSend("direct:test", new Processor() {
      def process(exchange: Exchange) = exchange.getIn.setBody("Stand aside! Important message coming through!")
    })

    assertMockEndpointsSatisfied

    assertEquals("Should have audited one flow", 1, adapter.flows.size)
    val flow = adapter.flows.head
    assertEquals("Flow should match the exchange id", exchange.get.getExchangeId, flow.id)
  }


  override def createCamelContext = {
    val context = super.createCamelContext
    context.addInterceptStrategy(AuditorStrategy(adapter))
    context
  }

  override def createRouteBuilder = new RouteBuilder {
    "direct:test" to "mock:test"
  }
}