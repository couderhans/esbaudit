package org.fusesource.esb.audit.testsupport;

import java.io.File;
import java.io.IOException;

import javax.jcr.Repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.core.TransientRepository;
import org.apache.servicemix.util.FileUtil;

import sun.util.LocaleServiceProviderPool.LocalizedObjectGetter;
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

public class RepositoryHolder {

    private static TransientRepository repository;
    private static Log LOG = LogFactory.getLog(RepositoryHolder.class);

    private RepositoryHolder() {
    	// make constructor private to avoid creating an instance of RepositoryHolder
    }
    
    public static Repository getRepository() throws IOException {
    	if (repository == null) {
    		LOG.info("Creating repository");
			repository = new TransientRepository("target/repository.xml", "target/repository");
			Runtime.getRuntime().addShutdownHook(new Thread("RepositoryHolder shutdown hook") {
				@Override
				public void run() {
					LOG.info("Closing the JCR repository on JVM shutdown");
					closeRepository();
				}
			});
		}

		return repository;
    }

    public static void closeRepository() {
        LOG.info("Closing repository");
        if (repository != null) {
            ((TransientRepository) repository).shutdown();
        }

        FileUtil.deleteFile(new File("target/repository"));
    }

}
