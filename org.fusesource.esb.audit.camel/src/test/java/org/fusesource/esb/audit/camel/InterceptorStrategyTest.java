package org.fusesource.esb.audit.camel;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.jackrabbit.core.NodeIdIterator;
import org.apache.jackrabbit.core.TransientRepository;

public class InterceptorStrategyTest extends ContextTestSupport {
	
	private TransientRepository repository; 
	
	public void testRoute() throws Exception {
		
		MockEndpoint a = getMockEndpoint("mock:a");
		a.expectedMessageCount(1);
		template.sendBody("direct:a", "mytest");
		
		a.assertIsSatisfied();
		String id = a.getExchanges().get(0).getExchangeId();
		
		Session session = repository.login();
		Node exchanges = session.getRootNode().getNode("exchanges");
		NodeIterator it =  exchanges.getNodes();
		while(it.hasNext()) {
			System.out.println(it.next());
		}
		assertNotNull(exchanges.getNode(id));
	}
	
	@Override
	public void setUp() throws Exception {
		try {
			repository = new TransientRepository("target/repository.xml", "target/repository");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block.
			e.printStackTrace();
		}
		
		super.setUp();
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		// TODO Auto-generated method stub
		return new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				getContext().addInterceptStrategy(new AuditInterceptStrategy(repository)); 
				from("direct:a").to("mock:a");
				
				
			}
		};
	}
	

}
