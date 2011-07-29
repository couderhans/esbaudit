Introduction
============

The esbaudit project aims to provide a web-based tool for auditing exchanges inside ServiceMix.
It leverages nosql data storage and uses Scalate for building the web ui.


Building
========
You can build esbaudit locally using Maven 3.0.x:
   
    mvn clean install
    
    
Testing
=======

After building the project locally, you can test it by installing the features in ServiceMix, 
adding the interceptor to your Camel routes and then running the web console itself.

Prerequisites
-------------

* You should have MongoDB running locally.

Install the esbaudit feature
----------------------------

In the ServiceMix console, run these two command

    features:addurl mvn:org.fusesource.esbaudit/features/1.0-SNAPSHOT/xml/features
    features:install esbaudit
    
Add the interceptors to your Camel route
----------------------------------------

In the Blueprint XML file defining your CamelContext, add these elements:

    //TODO: @Joris, can you copy-paste this from your example
    
Run the web console
-------------------

In the `web` module, execute `mvn jetty:run` to start the web console.  You can access the
web console on http://localhost:8080