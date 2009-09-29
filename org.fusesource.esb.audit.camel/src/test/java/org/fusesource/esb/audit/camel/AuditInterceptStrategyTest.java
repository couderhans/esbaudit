package org.fusesource.esb.audit.camel;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;

import junit.framework.TestCase;

public class AuditInterceptStrategyTest extends TestCase {

    private static final String PASSWORD = "test";
    private static final String USERNAME = "test";

    public void testInjectCredentials() throws Exception {

        AuditInterceptStrategy strategy = new AuditInterceptStrategy(null, USERNAME, PASSWORD);
        Credentials credentials = strategy.getCredentials();
        assertNotNull("We should have Credentials", credentials);
        assertTrue("Just SimpleCredentials should do fine", credentials instanceof SimpleCredentials);

        SimpleCredentials simple = (SimpleCredentials) credentials;
        assertEquals(USERNAME, simple.getUserID());
        assertEquals(PASSWORD, String.copyValueOf(simple.getPassword()));

        assertSame("Should not create a new instance every time again", credentials, strategy.getCredentials());

    }

    public void testInjectCredentialsDefaults() throws Exception {

        AuditInterceptStrategy strategy = new AuditInterceptStrategy(null);
        Credentials credentials = strategy.getCredentials();
        assertNotNull("We should have Credentials", credentials);
        assertTrue("Just SimpleCredentials should do fine", credentials instanceof SimpleCredentials);

        SimpleCredentials simple = (SimpleCredentials) credentials;
        assertEquals("user", simple.getUserID());
        assertEquals("pass", String.copyValueOf(simple.getPassword()));

    }

}
