package org.fusesource.esb.audit.camel;

import javax.jcr.ItemExistsException;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.processor.DelegateProcessor;

import static org.fusesource.esb.audit.commons.RepositoryUtils.*;

public class AuditInterceptor extends DelegateProcessor {

	private final AuditInterceptStrategy strategy;
	private Session session;

	public AuditInterceptor(AuditInterceptStrategy strategy, Processor target) {
		super(target);
		this.strategy = strategy;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		System.out.print("test");
        Node root = getSession().getRootNode();
        //Node exchanges = root.addNode("exchanges");
        System.out.println(exchange.getExchangeId());
        Node node = getOrCreate(root, "exchanges/" + exchange.getExchangeId());
        getSession().save();
		super.proceed(exchange);
	}
	

	protected Session getSession() throws LoginException, RepositoryException {
		if (session == null) {
			session = strategy.getRepository().login(new SimpleCredentials("user", "pass".toCharArray()));			
		}

		return session;
	}
}
