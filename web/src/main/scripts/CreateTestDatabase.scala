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
import org.fusesource.esbaudit.backend.MongoDB
import org.fusesource.esbaudit.backend.model._

log.info("Inserting 50 flows")

for (i <- 1 to 10) {

  MongoDB().store(Flow("flow-testing-%05d".format(i),
                       Message("some body", Map("my-header-%05d".format(i) -> "my-value-%05d".format(i))),
                       Active(),
                       Map("my-property-%05d".format(i) -> "my-value-%05d".format(i),
                           Flow.DOCUMENT_TYPE -> "Invoice"),
                       Seq("Invoice", "Test")))

}

for (i <- 11 to 20) {

  MongoDB().store(Flow("flow-testing-%05d".format(i),
                       Message("some body", Map("my-header-%05d".format(i) -> "my-value-%05d".format(i))),
                       Active(),
                       Map("my-property-%05d".format(i) -> "my-value-%05d".format(i),
                           Flow.DOCUMENT_TYPE -> "Invoice"),
                       Seq("po", "inbound")))

}

for (i <- 21 to 30) {

  MongoDB().store(Flow("flow-testing-%05d".format(i),
                       Message("some body", Map("my-header-%05d".format(i) -> "my-value-%05d".format(i))),
                       Active(),
                       Map("my-property-%05d".format(i) -> "my-value-%05d".format(i),
                           Flow.DOCUMENT_TYPE -> "Invoice"),
                       Seq("da", "outbound")))

}

for (i <- 31 to 40) {

  MongoDB().store(Flow("flow-testing-%05d".format(i),
                       Message("some body", Map("my-header-%05d".format(i) -> "my-value-%05d".format(i))),
                       Active(),
                       Map("my-property-%05d".format(i) -> "my-value-%05d".format(i),
                           Flow.DOCUMENT_TYPE -> "Invoice"),
                       Seq("po", "outbound")))

}

for (i <- 41 to 50) {

  MongoDB().store(Flow("flow-testing-%05d".format(i),
                       Message("some body", Map("my-header-%05d".format(i) -> "my-value-%05d".format(i))),
                       Active(),
                       Map("my-property-%05d".format(i) -> "my-value-%05d".format(i),
                           Flow.DOCUMENT_TYPE -> "Invoice"),
                       Seq("da", "inbound")))

}