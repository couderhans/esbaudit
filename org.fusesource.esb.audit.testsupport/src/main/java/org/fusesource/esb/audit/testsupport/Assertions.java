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
package org.fusesource.esb.audit.testsupport;

import static org.junit.Assert.assertNotNull;

import javax.jcr.Node;
import javax.jcr.Session;

import org.fusesource.esb.audit.commons.RepositoryUtils;

public class Assertions {

    private Assertions() {
        // hide the constructor -- only static methods
    }

    public static final void assertNodeExists(Session session, String path) throws Exception {
        assertNotNull("Node " + path + " does not exist in the JCR repository", RepositoryUtils.get(session.getRootNode(), path));
    }

    public static final void assertNode(Session session, String path, NodeAssertions assertions) throws Exception {
        assertNodeExists(session, path);
        Node node = RepositoryUtils.get(session.getRootNode(), path);
        assertions.check(node);
    }

}
