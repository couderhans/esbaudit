package org.fusesource.esb.audit.camel;

import javax.jcr.Repository;

import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.spi.InterceptStrategy;

public class AuditInterceptStrategy implements InterceptStrategy {

	private Repository repository;

	public AuditInterceptStrategy(Repository repository) throws Exception {
		this.repository = repository;
	}

	public Processor wrapProcessorInInterceptors(CamelContext context,
			ProcessorDefinition definition, Processor target, Processor nexttarget)
			throws Exception {
        System.out.println("wrapping " + target);
		return new AuditInterceptor(this, target);
	}
	
	public Repository getRepository() {
		return repository;
	}

}
