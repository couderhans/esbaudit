package org.fusesource.esb.audit.testsupport;

import java.util.Map;

import org.apache.camel.Body;
import org.apache.camel.Headers;

public class MockOrderService {

    public Object handleOrder(@Headers Map in, @Body String payload) throws OrderFailedException {
        if ("Order: fail".equals(payload)) {
            throw new OrderFailedException("Cannot order: fail");
        } else {
            return "Order OK";
        }
    }

    public Object orderFailed(@Headers Map in, @Body String payload) {
        System.out.println("Order ERROR");
        return "Order ERROR";
    }

    public static class OrderFailedException extends Exception {
        public OrderFailedException(String message) {
            super(message);
            System.out.println("OrderFailedExeption: " + message);
        }
    }

}
