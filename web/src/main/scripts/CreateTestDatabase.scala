/**
 * Copyright (C) 2009-2010 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.{Calendar, Date}
import org.fusesource.esbaudit.backend.MongoDB
import org.fusesource.esbaudit.backend.model._
import org.fusesource.esbaudit.backend.model.Flow._

//log.info("Inserting 50 flows")

val BODY = "<?xml version='1.0' encoding='UTF-8'?>\r\n<po xmlns='http://www.mycompany.com/schemata/PurchaseOrder/v1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' created='2008-06-04'\r\n    id='510429'>\r\n    <customer externalid='CUST07030'/>\r\n    <supplier id='1234'>\r\n        <name>NAME SUPPLIER</name>\r\n        <address>\r\n            <street>SUPPLIERSTREET 292</street>\r\n            <city>BRUSSELS</city>\r\n            <postalCode>1190</postalCode>\r\n            <country>B</country>\r\n        </address>\r\n    </supplier>\r\n    <delivery>\r\n        <date>2008-06-06</date>\r\n    </delivery>\r\n    <lines>\r\n        <line id='001'>\r\n            <article externalid='54312ABCDEF' id='05.91.6030'>\r\n                <description i18n='en'>CHEESE</description>\r\n                <description i18n='fr'>FROMAGE</description>\r\n            </article>\r\n            <quantity uom='KARTON'>10,00</quantity>\r\n        </line>\r\n    </lines>\r\n</po>"
val mongo = MongoDB()

val timestamp = new Timestamp("","")

for (i <- 1 to 1000) {

  mongo.store(Flow("flow-testing-%05d".format(i),
                       IN_MESSAGE -> Message(BODY.toString(), Map("my-header-%05d".format(i) -> "my-value-%05d".format(i))),
                       STATUS -> Active(),
                       PROPERTIES -> Map("my-property-%05d".format(i) -> "my-value-%05d".format(i),
                         "date" -> new Date() ),
                       TAGS -> Seq("Invoice", "Test"),
                       TIMESTAMP -> timestamp))

}

for (i <- 1100 to 2000) {

  mongo.store(Flow("flow-testing-%05d".format(i),
                       IN_MESSAGE -> Message(BODY.toString(), Map("my-header-%05d".format(i) -> "my-value-%05d".format(i))),
                       STATUS -> Done(),
                       PROPERTIES -> Map("my-property-%05d".format(i) -> "my-value-%05d".format(i),
                          "date" -> new Date() ),
                       TAGS -> Seq("po", "inbound"),
                       TIMESTAMP -> timestamp))

}

for (i <- 2100 to 3000) {

  mongo.store(Flow("flow-testing-%05d".format(i),
                       IN_MESSAGE -> Message(BODY.toString(), Map("my-header-%05d".format(i) -> "my-value-%05d".format(i))),
                       STATUS -> Error(),
                       PROPERTIES -> Map("my-property-%05d".format(i) -> "my-value-%05d".format(i),
                          "date" -> new Date() ),
                       TAGS -> Seq("da", "outbound"),
                       TIMESTAMP -> timestamp))
}

for (i <- 3100 to 4000) {

  mongo.store(Flow("flow-testing-%05d".format(i),
                       IN_MESSAGE -> Message(BODY, Map("my-header-%05d".format(i) -> "my-value-%05d".format(i))),
                       STATUS -> Done(),
                       PROPERTIES -> Map("my-property-%05d".format(i) -> "my-value-%05d".format(i),
                          "date" -> new Date() ),
                       TAGS -> Seq("po", "outbound"),
                       TIMESTAMP -> timestamp))
}

for (i <- 4100 to 5000) {

  mongo.store(Flow("flow-testing-%05d".format(i),
                       IN_MESSAGE -> Message(BODY, Map("my-header-%05d".format(i) -> "my-value-%05d".format(i))),
                       STATUS -> Done(),
                       PROPERTIES -> Map("my-property-%05d".format(i) -> "my-value-%05d".format(i),
                          "date" -> new Date() ),
                       TAGS -> Seq("da", "inbound"),
                       TIMESTAMP -> timestamp))
}
