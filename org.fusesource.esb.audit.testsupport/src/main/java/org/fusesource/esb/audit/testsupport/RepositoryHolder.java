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
 * 
 * Help class for holding JCR repository
 * 
 * @author krejcirik
 * 
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
