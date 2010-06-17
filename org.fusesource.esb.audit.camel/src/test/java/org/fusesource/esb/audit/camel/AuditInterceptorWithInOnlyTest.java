/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fusesource.esb.audit.camel;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.fusesource.esb.audit.testsupport.MockOrderService;
import org.fusesource.esb.audit.testsupport.MockOrderService.OrderFailedException;

public class AuditInterceptorWithInOnlyTest extends AbstractAuditTestSupport {

	private static String PAYLOAD = "Just a simple payload";

	public void testInOnlyWithBody() throws Exception {

		MockEndpoint inOnly = getMockEndpoint("mock:in-only");

		inOnly.expectedMessageCount(1);
		template.sendBody("direct:in-only", PAYLOAD);
		inOnly.assertIsSatisfied();

		Exchange exchange = inOnly.getExchanges().get(0);
        assertStatus(exchange, ExchangeStatus.Done.toString());
        assertPayload(exchange);
	}

	public void testInOnlyWithErrorHandler() throws Exception {

		MockEndpoint error = getMockEndpoint("mock:error");
		error.expectedMessageCount(1);
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(0);
		template.sendBodyAndHeader("direct:start", "Order: fail", "customerid",	"555");

		error.assertIsSatisfied();
		result.assertIsSatisfied();

		Exchange exchange = error.getExchanges().get(0);
		System.out.println("PROPERTIES ERROR: " + exchange.getProperties().toString());
        assertStatus(exchange, ExchangeStatus.Done.toString());

	}

	public void testInOnlyWithError() throws Exception {

		MockEndpoint file = getMockEndpoint("mock:file");
		file.expectedMessageCount(1);
		template.send("direct:file", new Processor() {
			public void process(Exchange exchange) throws Exception {
				exchange.getIn().setBody(PAYLOAD);
			}
		});
		file.assertIsSatisfied();

		Exchange exchange = file.getExchanges().get(0);
        assertStatus(exchange, ExchangeStatus.Error.toString());

	}

	private void assertStatus(Exchange exchange, String status) throws Exception {
		Node content = session.getRootNode().getNode("content");
		Node audit = content.getNode("audit");
		Node exchanges = audit.getNode("exchanges");
		Node camel = exchanges.getNode("camel");
		NodeIterator it = camel.getNodes();

		while (it.hasNext()) {
			System.out.println("NODE:" + it.next());
		}
		assertEquals(camel.getNode(exchange.getExchangeId().toString())
				.getProperty("status").getValue().getString(), status);
	}
	
	private void assertPayload(Exchange exchange) throws Exception {
		Node content = session.getRootNode().getNode("content");
		Node audit = content.getNode("audit");
		Node exchanges = audit.getNode("exchanges");
		Node camel = exchanges.getNode("camel");
		NodeIterator it = camel.getNodes();

		while (it.hasNext()) {
			System.out.println("NODE:" + it.next());
		}
		assertEquals(camel.getNode(exchange.getExchangeId().toString())
				.getProperty("content").getValue().getString(), PAYLOAD);
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {

				getContext().addInterceptStrategy(
						new AuditInterceptStrategy(getRepository()));
				onException(OrderFailedException.class).handled(true).bean(
						MockOrderService.class, "orderFailed").to("mock:error");
				// let's not handle any runtime exceptions
				onException(RuntimeCamelException.class).handled(false);

				from("direct:in-only").to("mock:in-only");

				errorHandler(deadLetterChannel("mock:error")
						.maximumRedeliveries(1));
				from("direct:start")
						.bean(MockOrderService.class, "handleOrder").to(
								"mock:result");
				from("direct:file").to("mock:file").throwException(
						new RuntimeCamelException(
								"Something is completely going wrong here!"));
			}
		};
	}

}
