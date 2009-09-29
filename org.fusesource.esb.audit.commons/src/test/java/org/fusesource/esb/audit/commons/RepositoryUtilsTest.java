/*
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

package org.fusesource.esb.audit.commons;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RepositoryUtilsTest {

    private RepoHolder repoHolder;
    private Session session;

    @Test
    public synchronized void getReturnsNullOrNode() throws Exception {
        Node root = session.getRootNode();
        assertNull(RepositoryUtils.get(root, "my-node"));
        root.addNode("my-node", "nt:folder");
        session.save();
        assertNotNull(RepositoryUtils.get(root, "my-node"));
    }

    @Test
    public void getOrCreateAlwaysReturnsNode() throws Exception {
        Node root = session.getRootNode();
        assertNull(RepositoryUtils.get(root, "my-node"));
        assertNotNull(RepositoryUtils.getOrCreate(root, "my-node"));
        session.save();
        assertNotNull(RepositoryUtils.get(root, "my-node"));
    }

    @Test
    public void getOrCreateAddsParentNodes() throws Exception {
        Node root = session.getRootNode();
        assertNull(RepositoryUtils.get(root, "parent"));
        assertNotNull(RepositoryUtils.getOrCreate(root, "parent/my-node"));
        session.save();
        assertNotNull(RepositoryUtils.get(root, "parent/my-node"));
    }

    @Test
    public void clearRemovesAllChildren() throws Exception {
        Node root = session.getRootNode();
        RepositoryUtils.getOrCreate(root, "parent/my-node");
        RepositoryUtils.getOrCreate(root, "my-node");
        session.save();
        RepositoryUtils.clear(root);
        session.save();
        assertNull(RepositoryUtils.get(root, "parent/my-node"));
        assertNull(RepositoryUtils.get(root, "my-node"));
    }

    // initialization and shutdown
    @BeforeClass
    public static void initRepository() throws IOException {

    }

    @Before
    public void init() throws Exception {

        repoHolder = RepoHolder.getInstance();
        session = repoHolder.getRepository().login(new SimpleCredentials("admin", "admin".toCharArray()));
        // let's clear the entire repo
        RepositoryUtils.clear(session.getRootNode());
        session.save();
    }

    @After
    public void destroy() {
        if (session != null) {
            session.logout();
            System.out.println("@@@@@@@@@@@@@@@@@@RepositoryUtilsTest: logout");
        }
    }
}