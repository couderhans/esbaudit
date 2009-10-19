package org.fusesource.esb.audit.camel;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.jackrabbit.core.TransientRepository;
import org.fusesource.esb.audit.testsupport.NodeAssertions;


public class AuditInterceptorMultiStepsTest extends AbstractAuditTestSupport {

     private TransientRepository repository;

    private static final String MESSAGE = "<just><a>test</a></just>";

    public void testRoute() throws Exception {

        final MockEndpoint step1 = getMockEndpoint("mock:step1");
        step1.expectedMessageCount(1);
        template.sendBody("direct:a", MESSAGE);
        step1.assertIsSatisfied();

        final Exchange exchange = step1.getExchanges().get(0);

        assertNode("content/audit/exchanges/" + exchange.getExchangeId(), new NodeAssertions() {

            public void check(Node node) throws Exception {
            	System.out.println("step next");
            	NodeIterator iter = node.getNodes();
            	assertNotNull(iter);
                assertTrue(node.hasNode("step1"));
                assertTrue(node.hasNode("step2"));
                assertFalse(node.hasNode("step3"));
           }
        });


    }

	 @Override
	    protected RouteBuilder createRouteBuilder() throws Exception {
	        // TODO Auto-generated method stub
	        return new RouteBuilder() {
	            @Override
	            public void configure() throws Exception {
	                getContext().addInterceptStrategy(new AuditInterceptStrategy(getRepository()));
	                from("direct:a")
	                .setBody(constant("Step1")).setBody(constant("Step2")).to("mock:step1");
	            }
	        };
	    }

	 private void assertSteps(Node node) {



	 }

}
