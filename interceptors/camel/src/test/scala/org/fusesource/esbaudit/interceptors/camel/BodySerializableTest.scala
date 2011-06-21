package org.fusesource.esbaudit.interceptors.camel

import org.apache.camel.{Exchange, Processor}
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.camel.scala.dsl.builder.{RouteBuilder, RouteBuilderSupport}
import org.fusesource.esbaudit.backend.model.Done
import org.junit.Assert._
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: joris
 * Date: 20/06/11
 * Time: 15:56
 * To change this template use File | Settings | File Templates.
 */

class BodySerializableTest extends CamelTestSupport with RouteBuilderSupport {

  val BODY = "The message's body text"
  val PROPERTY_KEY = "property.key"
  val PROPERTY_VALUE = "property.value"
  val HEADER_KEY = "header.key"
  val HEADER_VALUE = "header.value"

  val adapter = new MockAdapter

  @Test
  def serializableTest = {
    getMockEndpoint("mock:simple").expectedMessageCount(1)

    val exchange = template.asyncSend("direct:simple", new Processor() {
      def process(exchange: Exchange) = {
        exchange.getIn.setBody(new NonSerializableObject(BODY))
        exchange.getIn.setHeader(HEADER_KEY, new NonSerializableObject(HEADER_VALUE))
        exchange.setProperty(PROPERTY_KEY, new NonSerializableObject(PROPERTY_VALUE))
      }
    })

    assertMockEndpointsSatisfied

    Thread.sleep(100)

    assertEquals("Should have audited one flow", 1, adapter.flows.size)
    val flow = adapter.flows.head
    assertEquals("Flow should match the exchange id", exchange.get.getExchangeId, flow.id)
    assertEquals("Original body was not serializable, so should have been converted to String",
                 BODY, flow.in.body)
    assertEquals("Original header was not serializable, so should have been converted to String",
                 HEADER_VALUE, flow.in.headers(HEADER_KEY))
    assertEquals("Original property was not serializable, so should have been converted to String",
                 PROPERTY_VALUE, flow.properties(PROPERTY_KEY))

    assertEquals("Flow ended succesfully", Done(), flow.status)
  }

  override def createCamelContext = {
    val context = super.createCamelContext
    context.addInterceptStrategy(AuditorStrategy(adapter).addTag("po"))
    context
  }

  override def createRouteBuilder = new RouteBuilder {
    "direct:simple" to "mock:simple"
  }

  class NonSerializableObject(val value: String) extends Object {

    //this thing is not serializable

    override def toString = value

  }

}