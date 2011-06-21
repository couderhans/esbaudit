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

import java.util.Date
import org.fusesource.esbaudit.backend.MongoDB
import org.fusesource.esbaudit.backend.model._
import org.fusesource.esbaudit.backend.model.Flow._

//log.info("Inserting 50 flows")

val BODY = "<?xml version='1.0' encoding='UTF-8'?>\\n<po xmlns='http://www.mycompany.com/schemata/PurchaseOrder/v1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' created='2008-06-04'\\n    id='510429'>\\n    <customer externalid='CUST07030'/>\\n    <supplier id='1234'>\\n        <name>NAME SUPPLIER</name>\\n        <address>\\n            <street>SUPPLIERSTREET 292</street>\\n            <city>BRUSSELS</city>\\n            <postalCode>1190</postalCode>\\n            <country>B</country>\\n        </address>\\n    </supplier>\\n    <delivery>\\n        <date>2008-06-06</date>\\n    </delivery>\\n    <lines>\\n        <line id='001'>\\n            <article externalid='54312ABCDEF' id='05.91.6030'>\\n                <description i18n='en'>CHEESE</description>\\n                <description i18n='fr'>FROMAGE</description>\\n            </article>\\n            <quantity uom='KARTON'>10,00</quantity>\\n        </line>\\n    </lines>\\n</po>"
val mongo = MongoDB()

for (i <- 1 to 1000) {

  mongo.store(Flow("flow-testing-%05d".format(i),
                       IN_MESSAGE -> Message(BODY.toString(), Map("my-header-%05d".format(i) -> "my-value-%05d".format(i))),
                       STATUS -> Active(),
                       PROPERTIES -> Map("my-property-%05d".format(i) -> "my-value-%05d".format(i),
                         "date" -> new Date().toString() ),
                       TAGS -> Seq("Invoice", "Test")))

}

for (i <- 1100 to 2000) {

  mongo.store(Flow("flow-testing-%05d".format(i),
                       IN_MESSAGE -> Message(BODY.toString(), Map("my-header-%05d".format(i) -> "my-value-%05d".format(i))),
                       STATUS -> Done(),
                       PROPERTIES -> Map("my-property-%05d".format(i) -> "my-value-%05d".format(i),
                          "date" -> new Date().toString() ),
                       TAGS -> Seq("po", "inbound")))

}

for (i <- 2100 to 3000) {

  mongo.store(Flow("flow-testing-%05d".format(i),
                       IN_MESSAGE -> Message(BODY.toString(), Map("my-header-%05d".format(i) -> "my-value-%05d".format(i))),
                       STATUS -> Error(),
                       PROPERTIES -> Map("my-property-%05d".format(i) -> "my-value-%05d".format(i),
                          "date" -> new Date().toString() ),
                       TAGS -> Seq("da", "outbound")))

}

for (i <- 3100 to 4000) {

  mongo.store(Flow("flow-testing-%05d".format(i),
                       IN_MESSAGE -> Message(BODY, Map("my-header-%05d".format(i) -> "my-value-%05d".format(i))),
                       STATUS -> Done(),
                       PROPERTIES -> Map("my-property-%05d".format(i) -> "my-value-%05d".format(i),
                          "date" -> new Date().toString() ),
                       TAGS -> Seq("po", "outbound")))

}

for (i <- 4100 to 5000) {

  mongo.store(Flow("flow-testing-%05d".format(i),
                       IN_MESSAGE -> Message(BODY, Map("my-header-%05d".format(i) -> "my-value-%05d".format(i))),
                       STATUS -> Done(),
                       PROPERTIES -> Map("my-property-%05d".format(i) -> "my-value-%05d".format(i),
                          "date" -> new Date().toString() ),
                       TAGS -> Seq("da", "inbound")))

}
