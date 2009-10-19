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

import java.util.Date;
import java.util.GregorianCalendar;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.processor.DelegateProcessor;
import org.apache.camel.spi.Synchronization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.value.DateValue;
import org.fusesource.esb.audit.commons.RepositoryUtils;

public class AuditInterceptor extends DelegateProcessor {

    private static final Log LOG = LogFactory.getLog(AuditInterceptor.class);

    private final AuditInterceptStrategy strategy;
    private Session session;
    private long steps;

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
        Node node = RepositoryUtils.getOrCreate(getSession().getRootNode(), "content/audit/exchanges/"
                + exchange.getExchangeId().toString());
        LOG.info("Node path - Active Exchange: " + node.getPath().toString());
    	audit(exchange, node, ExchangeStatus.Active.toString());
        Node step = RepositoryUtils.getOrCreate(getSession().getRootNode(), "content/audit/exchanges/"
                + exchange.getExchangeId().toString() + "/step" + steps);

        audit(step, ExchangeStatus.Active.toString());
    }

    protected void failure(Exchange exchange) throws Exception, RepositoryException {
        Node node = RepositoryUtils.getOrCreate(getSession().getRootNode(), "content/audit/exchanges/"
                + exchange.getExchangeId().toString());

        LOG.info("Node path - FAILURE: " + node.getPath().toString());
        audit(exchange, node, ExchangeStatus.Error.toString());
        audit(exchange.getIn(), RepositoryUtils.getOrCreate(node, "in"));
        if (exchange.hasOut()) {
            audit(exchange.getOut(), RepositoryUtils.getOrCreate(node, "out"));
        }
    }

    protected void complete(Exchange exchange) throws Exception, RepositoryException {
        Node node = RepositoryUtils.getOrCreate(getSession().getRootNode(), "content/audit/exchanges/"
                + exchange.getExchangeId().toString());
        LOG.info("Node path - COMPLETE: " + node.getPath().toString());
        audit(exchange, node, ExchangeStatus.Done.toString());
        audit(exchange.getIn(), RepositoryUtils.getOrCreate(node, "in"));
        if (exchange.hasOut()) {
            audit(exchange.getOut(), RepositoryUtils.getOrCreate(node, "out"));
        }
    }

    private void audit(Exchange exchange, Node node, String status) throws Exception, RepositoryException {
        node.setProperty("sling:resourceType", "audit/camel/exchange");
        node.setProperty("exchangeId", exchange.getExchangeId().toString());
        node.setProperty("created", new Date().toString());
        node.setProperty("endpointId", exchange.getFromEndpoint().getEndpointUri().toString());
        node.setProperty("status", status);
        if (node.hasProperty("steps")) {
            steps = node.getProperty("steps").getValue().getLong();
            node.setProperty("steps",steps++);
        } else {
            steps = 1;
            node.setProperty("steps", steps);
        }
        getSession().save();
    }

    private void audit(Node step, String status) throws RepositoryException {
        step.setProperty("sling:resourceType", "audit/camel/exchange/step");
        step.setProperty("created", new Date().toString());
        LOG.info("Creation of new step node");
        getSession().save();
    }

    private void audit(Message message, Node node) throws Exception, RepositoryException {
        node.setProperty("content", message.getBody(String.class));
        getSession().save();
    }

    protected Session getSession() throws LoginException, RepositoryException {
        if (session == null) {
            session = strategy.getRepository().login(strategy.getCredentials());
        }
        return session;
    }
}