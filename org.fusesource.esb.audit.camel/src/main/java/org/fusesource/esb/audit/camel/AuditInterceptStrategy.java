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

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;

import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.spi.InterceptStrategy;

public class AuditInterceptStrategy implements InterceptStrategy {

	
    private Repository repository;
	private String username;
	private String password;
	private Credentials credentials;

    public AuditInterceptStrategy(Repository repository) throws Exception {
        this(repository, "user", "pass");
    }
    
    public AuditInterceptStrategy(Repository repository, String username, String password) {
    	super();
    	this.repository = repository;
    	this.username = username;
    	this.password = password;
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

	public Credentials getCredentials() {
        if (credentials == null) {
	        credentials = new SimpleCredentials(this.username, this.password.toCharArray());
	    }
        return credentials;
	}

}
