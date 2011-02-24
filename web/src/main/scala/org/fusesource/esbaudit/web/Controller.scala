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
package org.fusesource.esbaudit.web

import org.fusesource.esbaudit.backend.MongoDB
import org.fusesource.esbaudit.backend.model._

/**
 * Web application controller
 */
class Controller {

  val backend = MongoDB()

  def all = backend.all


  //def map = backend.all map { Flow => (Flow.id, Flow.status) }
  def page(end: Int) = all.slice(0, end)
  //def page: Iterable[Flow] = all
  //def pages =  all.sliding(0, 25) map { Flow => (Flow.id, Flow.status) }
  //def page(end: Int) = pages.slice(0, 10)

}
