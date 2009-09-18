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

import static org.fusesource.esb.audit.commons.RepositoryUtils.getOrCreate;

import java.util.GregorianCalendar;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.processor.DelegateProcessor;
import org.apache.camel.spi.Synchronization;
import org.apache.jackrabbit.value.DateValue;
import org.fusesource.esb.audit.commons.RepositoryUtils;

public class AuditInterceptor extends DelegateProcessor {

    private final AuditInterceptStrategy strategy;
    private Session session;
    
    

    public AuditInterceptor(AuditInterceptStrategy strategy, Processor target) {
        super(target);
        this.strategy = strategy;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
       System.out.println("Processing Exchange");
        exchange.addOnCompletion(new Synchronization() {
			
			public void onFailure(Exchange exchange) {
				System.out.println("onFAILURE");
				Node node;
				try {
					node = getOrCreate(getSession().getRootNode(), "content/exchanges/" + exchange.getExchangeId() + "/in");
					node.setProperty("created", new DateValue(new GregorianCalendar()));
					node.setProperty("endpointId", exchange.getFromEndpoint().toString());
					node.setProperty("status", ExchangeStatus.Error.toString());
			        getSession().save();
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
            }
			
			public void onComplete(Exchange exchange) {
				Node node;
				try {
					node = getOrCreate(getSession().getRootNode(), "content/exchanges/" + exchange.getExchangeId() + "/in");
					node.setProperty("created", new DateValue(new GregorianCalendar()));
					node.setProperty("endpointId", exchange.getFromEndpoint().toString());
				    node.setProperty("content", exchange.getIn().getBody().toString());
					node.setProperty("status", ExchangeStatus.Done.toString());
			        getSession().save();
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
			}
		});
        super.proceed(exchange);
    }

    /* TODO
    protected void failure(Exchange exchange) {
		// TODO Save status, endpoint, ...
    	Node node = RepositoryUtils.getOrCreate("content/exchanges/", exchange.getExchangeId());
    	
        audit(exchange, node);
        audit(exchange.getIn(), RepositoryUtils.getOrCreate(node, "in"));
    	if (exchange.hasOut()) {
    		audit(exchange.getOut(), RepositoryUtils.getOrCreate(node, "out"));
    	}
		
	}

	private void audit(Exchange exchange, Node node) {
		// TODO Exchange properties + status opslaan
		
	}

	private void audit(Message in, Node node) {
		// TODO Message properties + content opslaan
	}
*/
	protected Session getSession() throws LoginException, RepositoryException {
        if (session == null) {
            session = strategy.getRepository().login(strategy.getCredentials());
        }
        return session;
    }
}
