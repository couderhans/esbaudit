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
import org.apache.camel.spi.InterceptStrategy
import org.apache.camel.model.ProcessorDefinition
import org.apache.camel.processor.{DelegateAsyncProcessor, DelegateProcessor}
import org.apache.camel._

/**
 * Auditor strategy to send flows/exchanges/... to a storage adapter
 */
case class AuditorStrategy(val adapter: Adapter) extends InterceptStrategy {

  def wrapProcessorInInterceptors(context: CamelContext, definition: ProcessorDefinition[_],
                                  from: Processor, to: Processor) = Auditor(to)


  case class Auditor(val delegate: Processor) extends DelegateAsyncProcessor(delegate) {
    override def process(exchange: Exchange, callback: AsyncCallback) = {
      adapter.store(Flow(exchange.getExchangeId))
      super.process(exchange, callback)
    }
  }
}