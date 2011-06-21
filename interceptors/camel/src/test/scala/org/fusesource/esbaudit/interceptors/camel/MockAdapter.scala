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
package org.fusesource.esbaudit.interceptors.camel

import org.fusesource.esbaudit.backend.Adapter
import org.fusesource.esbaudit.backend.model.Flow
import collection.mutable.ListBuffer

/**
 * Mock {@link Adapter} implementation used for testing
 */
class MockAdapter extends Adapter {

  val flows = new ListBuffer[Flow]

  def store(flow: Flow) = flows += validate(flow)

  def update(flow: Flow) = {
    flows.find(_.id == flow.id) match {
      case Some(found) => {
        //TODO: make this less naive
        println(found)
        println(flow)
        flows -= found
        flows += Flow(found.id, (found.data ++ flow.data).toSeq: _*)
        println(flows)
      }
      case None => throw new IllegalArgumentException("Unable to update %s - flow does not exist in store".format(flow.id))
    }
  }

  def validate(flow: Flow) : Flow = {
    if (!flow.in.body.isInstanceOf[java.io.Serializable]) throw new IllegalStateException("Payload should have been serializable")
    flow
  }

  def reset = {
    println("Resetting...")
    flows.clear
  }
}