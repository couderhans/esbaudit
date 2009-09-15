package org.fusesource.esb.audit.camel;

import java.io.IOException;

import javax.jcr.Repository;

import org.apache.camel.ContextTestSupport;
import org.fusesource.esb.audit.testsupport.RepositoryHolder;

public abstract class AbstractAuditTestSupport extends ContextTestSupport {


	public static Repository getRepository() throws Exception {
		return RepositoryHolder.getRepository();
	}
	
	
	

}
