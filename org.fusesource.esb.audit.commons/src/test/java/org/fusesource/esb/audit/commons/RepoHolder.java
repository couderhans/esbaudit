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

package org.fusesource.esb.audit.commons;

import java.io.File;
import java.io.IOException;

import javax.jcr.Repository;

import org.apache.jackrabbit.core.TransientRepository;
import org.apache.servicemix.util.FileUtil;

/**
 * 
 * Help class for holding JCR repository
 * 
 * @author krejcirik
 * 
 */
public class RepoHolder {

    private static RepoHolder instance;
    private Repository repository;

    protected RepoHolder() {
        // singleton
        this.destroyRepository();

        try {
            repository = new TransientRepository("target/repository.xml", "target/repository");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RepoHolder getInstance() {
        
        if (instance == null) {
            instance = new RepoHolder();    
        }
        
        return instance;
    }

    public Repository getRepository() {
        return this.repository;
    }

    private void destroyRepository() {

        if (repository != null) {
            ((TransientRepository) repository).shutdown();
        }

        FileUtil.deleteFile(new File("target/repository"));
    }

}
