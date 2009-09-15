package org.fusesource.esb.audit.camel;

import static org.fusesource.esb.audit.commons.RepositoryUtils.getOrCreate;

import java.util.GregorianCalendar;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.processor.DelegateProcessor;
import org.apache.jackrabbit.value.DateValue;

public class AuditInterceptor extends DelegateProcessor {

    private final AuditInterceptStrategy strategy;
    private Session session;

    public AuditInterceptor(AuditInterceptStrategy strategy, Processor target) {
        super(target);
        this.strategy = strategy;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Node root = getSession().getRootNode();
        //Node exchanges = root.addNode("exchanges");
        System.out.println(exchange.getExchangeId());
        Node node = getOrCreate(root, "exchanges/" + exchange.getExchangeId());
        node.setProperty("created", new DateValue(new GregorianCalendar()));
        node.setProperty("endpointId", exchange.getFromEndpoint().toString());
        System.out.println(exchange.getFromEndpoint());
        System.out.println(exchange.getIn());
        System.out.println(exchange.getOut());
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
