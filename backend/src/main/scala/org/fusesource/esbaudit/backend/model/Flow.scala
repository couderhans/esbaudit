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
package org.fusesource.esbaudit.backend.model

/**
 * Models a Flow
 * - corresponds to a Camel Exchange
 * - corresponds to a set of correlated NMR Exchange
 *
 *
 */
//TODO Do we need this one?
case class Flow(val id: String, val in: Message, val status: Status, val properties: Map[String, AnyRef], val tags: Seq[String] = Seq()) {

  def documentType = properties.getOrElse(Flow.DOCUMENT_TYPE, "Unknown")

}

abstract case class Status(val value: String) {
  override def toString = value
}
case class Done extends Status("done")
case class Error extends Status("error")
case class Active extends Status("active")

object Flow  {

  val DOCUMENT_TYPE = "org_fusesource_esbaudit_backend_model_Flow_DOCUMENT_TYPE"

}
