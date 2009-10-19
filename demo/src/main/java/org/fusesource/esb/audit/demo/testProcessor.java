package org.fusesource.esb.audit.demo;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class testProcessor implements Processor {

    public void process(Exchange exchange) throws Exception {
           String payload = exchange.getIn().getBody(String.class);
           exchange.getIn().setBody("Changed Hello World message into this text");
    }

}
