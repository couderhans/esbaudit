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
package org.fusesource.esbaudit.backend

import java.util.Date
import model.{Timestamp, Flow}

/**
 * Interface to represent a storage backend
 */
trait Backend {

  def all: Iterator[Flow]

  def flow(id: String): Option[Flow]

  def flowsByTags(tags: Seq[String]) : Iterator[Flow]

  def flowsByStatus(status: String) : Iterator[Flow]

  def flowsByDate(date: Date) : Iterator[Flow]

  def flowsByDate(date: Timestamp) : Iterator[Flow]

  def search(query: String) : Iterator[Flow]

}
