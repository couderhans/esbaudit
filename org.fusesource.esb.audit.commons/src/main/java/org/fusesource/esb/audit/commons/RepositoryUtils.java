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

import java.util.Iterator;
import java.util.LinkedList;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

public class RepositoryUtils {

    public static Node get(Node parent, String name) throws RepositoryException {
        try {
            return parent.getNode(name);
        } catch (PathNotFoundException e) {
            return null;
        }
    }

    public static Node getOrCreate(Node parent, String name) throws RepositoryException {
        try {
            return parent.getNode(name);
        } catch (PathNotFoundException e) {
            if (name.contains("/")) {
                String head = name.substring(0, name.indexOf("/"));
                String remainder = name.substring(name.indexOf("/") + 1);
                return getOrCreate(getOrCreate(parent, head), remainder);
            }
            return parent.addNode(name);
        }
    }
   

    public static void clear(Node parent) throws RepositoryException {
        for (Node node : iterable(parent)) {
            // delete everything exception system nodes
            if (!node.getName().startsWith("jcr:")) {
                node.remove();                
            }
        }
    }

    private static Iterable<Node> iterable(final Node parent) throws RepositoryException { 
        return new Iterable<Node>() {
            @SuppressWarnings("unchecked")
            public Iterator<Node> iterator() {
                try {
                    return parent.getNodes();
                } catch (RepositoryException e) {
                    return new LinkedList<Node>().iterator();
                }
            }
        };
    }
}
