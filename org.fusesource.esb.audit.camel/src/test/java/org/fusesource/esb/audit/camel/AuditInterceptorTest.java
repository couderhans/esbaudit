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

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.jackrabbit.core.TransientRepository;

public class AuditInterceptorTest extends AbstractAuditTestSupport {
	
    private TransientRepository repository;

    private static final String MESSAGE = "<just><a>test</a></just>";

    public void testRoute() throws Exception {
        MockEndpoint a = getMockEndpoint("mock:a");
        a.expectedMessageCount(1);
        template.sendBody("direct:a", MESSAGE);
        a.assertIsSatisfied();
        assertRepoNotNull(a.getExchanges().get(0).getExchangeId());
    }

    public void testInOnly() throws Exception {
        MockEndpoint inOnly = getMockEndpoint("mock:in-only");
        inOnly.expectedMessageCount(1);
        template.sendBody("direct:in-only", MESSAGE);
        inOnly.assertIsSatisfied();
        assertRepoNotNull(inOnly.getExchanges().get(0).getExchangeId());
    }
	
    public void testInOut() throws Exception {
        MockEndpoint inOut = getMockEndpoint("mock:in-out");
        inOut.expectedMessageCount(1);
        template.sendBody("direct:in-out", MESSAGE);
        inOut.assertIsSatisfied();
        assertRepoNotNull(inOut.getExchanges().get(0).getExchangeId());
    }
	

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        // TODO Auto-generated method stub
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                getContext().addInterceptStrategy(new AuditInterceptStrategy(getRepository()));
                from("direct:a").to("mock:a");
                from("direct:in-only").to("mock:in-only");
                from("direct:in-out").to("mock:in-out");
            }
        };
    }
	
    private void assertRepoNotNull(String exchangeId) throws Exception {
        Session session = getRepository().login();
        Node exchanges = session.getRootNode().getNode("exchanges");
        NodeIterator it =  exchanges.getNodes();
        while(it.hasNext()) {
            System.out.println(it.next());
        }
        assertNotNull(exchanges.getNode(exchangeId));
    }

}
