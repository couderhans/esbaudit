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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.processor.DelegateProcessor;
import org.apache.camel.spi.Synchronization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fusesource.esb.audit.commons.RepositoryUtils;

public class AuditInterceptor extends DelegateProcessor {

    private static final Log LOG = LogFactory.getLog(AuditInterceptor.class);
    private final AuditInterceptStrategy strategy;
    private Session session;

    public AuditInterceptor(AuditInterceptStrategy strategy, Processor target) {
    	super(target);
    	this.strategy = strategy;
    }


    @Override
    public void process(Exchange exchange) throws Exception {
    	LOG.info("Processing Exchange");

        createActive(exchange);

        exchange.addOnCompletion(new Synchronization() {

            public void onFailure(Exchange exchange) {
                try {
                    failure(exchange);
                } catch (RepositoryException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onComplete(Exchange exchange) {
                try {
                    complete(exchange);
                } catch (RepositoryException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        super.proceed(exchange);
    }

    protected void createActive(Exchange exchange) throws Exception, RepositoryException {
        LOG.info("Node path - Active Exchange: " + exchange.getExchangeId().toString());
        exchange.setProperty("status", ExchangeStatus.Active.toString());
    	audit(exchange);
    }

    protected void failure(Exchange exchange) throws Exception, RepositoryException {
        LOG.info("Node path - FAILURE: " + exchange.getExchangeId().toString());
        exchange.setProperty("status", ExchangeStatus.Error.toString());
        audit(exchange);
    }

    protected void complete(Exchange exchange) throws Exception, RepositoryException {
        LOG.info("Node path - COMPLETE: " + exchange.getExchangeId().toString());
        exchange.setProperty("status", ExchangeStatus.Done.toString());
        audit(exchange);
    }

    protected Session getSession() throws LoginException, RepositoryException {
        if (session == null) {
            session = strategy.getRepository().login(strategy.getCredentials());
        }
        return session;
    }

    private void audit(Exchange exchange) throws Exception {
    	
    	Node content = RepositoryUtils.getOrCreate(getSession().getRootNode(), "content");

    	Node audit = RepositoryUtils.getOrCreate(content, "audit");
    	audit.setProperty("sling:resourceType", "audit");
    	
    	Node exchanges = RepositoryUtils.getOrCreate(audit, "exchanges");
    	exchanges.setProperty("sling:resourceType", "audit/exchanges");
    	
    	Node camel = RepositoryUtils.getOrCreate(exchanges,"camel");
    	camel.setProperty("sling:resourceType", "audit/exchanges/camel");
    	
    	String name = (exchange.hasOut() ? "out/" : "") + exchange.getExchangeId().toString();
    	
        Node node = RepositoryUtils.get(camel, name);
        if (node == null) {
        	node = RepositoryUtils.getOrCreate(camel, name);
        	node.addMixin("mix:versionable");
        	node.setProperty("sling:resourceType", "audit/exchanges/camel/exchange");
        	node.setProperty("exchangeId", exchange.getExchangeId().toString());
        	node.setProperty("created", new SimpleDateFormat("ddMMyyyy").format(new Date()).toString());
   	    } else {
   	    	node.checkout();
   	    }
        node.setProperty("endpointId", exchange.getFromEndpoint().getEndpointUri().toString());
        node.setProperty("status", exchange.getProperty("status").toString());
        node.setProperty("content", exchange.getIn().getBody(String.class));
        LOG.info("ENDPOINT: " + exchange.getFromEndpoint().getEndpointUri().toString().split(":")[0]);

        getSession().save();
        node.checkin();
    }


    @Override
    protected void doStop() throws Exception {
        super.doStop();

        LOG.debug("Closing JCR Session for " + this);
        getSession().logout();
    }
}
