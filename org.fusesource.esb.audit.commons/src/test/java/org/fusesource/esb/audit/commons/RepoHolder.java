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
