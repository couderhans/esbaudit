package org.fusesource.esbaudit.backend

import org.slf4j.LoggerFactory

/**
 * Yet another logging trait
 */
trait Log {

  private val log = LoggerFactory.getLogger(this.getClass.getName)

  def debug(message: String, parameters: AnyRef*) = log.debug(message.format(parameters: _*))
  def info(message: String, parameters: AnyRef*) = log.info(message.format(parameters: _*))
  def warn(message: String, parameters: AnyRef*) = log.warn (message.format(parameters : _*))
  def warn(message: String, exception: Throwable, parameters: AnyRef*) = log.warn(message.format(parameters: _*), exception)

}