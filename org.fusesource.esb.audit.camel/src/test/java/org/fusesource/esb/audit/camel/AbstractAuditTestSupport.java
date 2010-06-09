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

import org.apache.camel.ContextTestSupport;
import org.fusesource.esb.audit.testsupport.Assertions;
import org.fusesource.esb.audit.testsupport.NodeAssertions;
import org.fusesource.esb.audit.testsupport.RepositoryHolder;

import javax.jcr.NamespaceException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Repository;
import javax.jcr.Session;

public abstract class AbstractAuditTestSupport extends ContextTestSupport {

    protected Session session;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        session = getRepository().login();
        NamespaceRegistry registry = session.getWorkspace().getNamespaceRegistry();
        try {
            registry.getURI("sling");
        } catch (NamespaceException e) {
            registry.registerNamespace("sling", "http://jackrabbit.apache.org/sling/1.0");
        }
    }

    public static Repository getRepository() throws Exception {
        return RepositoryHolder.getRepository();
    }

    public void assertNodeExists(String path) throws Exception {
        Assertions.assertNodeExists(session, path);
    }

    public void assertNode(String path, NodeAssertions assertions) throws Exception {
        Assertions.assertNode(session, path, assertions);
    }

    @Override
    protected void tearDown() throws Exception {
        session.logout();
        super.tearDown();
    }

}