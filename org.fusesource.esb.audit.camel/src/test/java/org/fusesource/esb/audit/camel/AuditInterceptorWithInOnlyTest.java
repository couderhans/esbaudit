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

import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Headers;
import org.apache.camel.OutHeaders;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.direct.DirectEndpoint;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.language.ExpressionDefinition;
import org.fusesource.esb.audit.commons.RepositoryUtils;
import org.fusesource.esb.audit.testsupport.MockOrderService;
import org.fusesource.esb.audit.testsupport.NodeAssertions;
import org.fusesource.esb.audit.testsupport.MockOrderService.OrderFailedException;


public class AuditInterceptorWithInOnlyTest extends AbstractAuditTestSupport {
	
	private static String PAYLOAD = "Just a simple payload";
	
    public void testInOnlyWithBody() throws Exception {
        MockEndpoint inOnly = getMockEndpoint("mock:in-only");
        inOnly.expectedMessageCount(1);
        template.sendBody("direct:in-only", PAYLOAD);
        inOnly.assertIsSatisfied();
        
        Exchange exchange = inOnly.getExchanges().get(0);
        
        assertNode("exchanges/" + exchange.getExchangeId() + "/in", new NodeAssertions() {
			
			public void check(Node node) throws Exception {
				assertNotNull(node.getProperty("content"));
				assertEquals(PAYLOAD, node.getProperty("content").getString());
				assertEquals("isTransacted",node.getProperty("status").getString());
			}
		});
    }
    
    public void testInOnlyWithErrorHandler() throws Exception {
        
    	MockEndpoint error = getMockEndpoint("mock:error");
        error.expectedMessageCount(1);
        MockEndpoint result = getMockEndpoint("mock:result");
        result.expectedMessageCount(0);
        template.sendBodyAndHeader("direct:start", "Order: fail", "customerid", "555");
        
        error.assertIsSatisfied();
        result.assertIsSatisfied();
       
        Exchange exchange = error.getExchanges().get(0);

        System.out.println("PROPERTIES ERROR: " + exchange.getProperties().toString());
        
        assertNode("exchanges/" + exchange.getExchangeId() + "/in", new NodeAssertions() {
			
			public void check(Node node) throws Exception {
				assertNotNull(node.getProperty("status"));
				assertEquals("isTransacted",node.getProperty("status").getString());
			}
		});
   	
    }
    
    public void testInOnlyWithError() throws Exception {
    	
    	MockEndpoint file = getMockEndpoint("mock:file");
    	file.expectedMessageCount(1);
    	template.sendBody("direct:file", PAYLOAD);
        file.assertIsNotSatisfied();
        
        Exchange exchange = file.getExchanges().get(0);
        
        assertNode("exchanges/" + exchange.getExchangeId() + "/in", new NodeAssertions() {
			
			public void check(Node node) throws Exception {
				assertNotNull(node.getProperty("status"));
				System.out.println("STATUS: " + node.getProperty("status").getString());
				assertEquals("isFailed",node.getProperty("status").getString());
			}
		});
  	
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                
            	getContext().addInterceptStrategy(new AuditInterceptStrategy(getRepository()));
                
                onException(OrderFailedException.class)
                   .handled(true)
                   .bean(MockOrderService.class, "orderFailed")
                   .to("mock:error");

            	from("direct:in-only").to("mock:in-only");
            	              
                errorHandler(deadLetterChannel("mock:error").maximumRedeliveries(1));

                from("direct:start")
                    .bean(MockOrderService.class, "handleOrder")
                    .to("mock:result");
                
                from("direct:file")
                    .to("mock:file")
                    .to("file:/opt/etc/src/something/somethingfaulty");
            }
        };
    }

}
