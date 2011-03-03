package org.fusesource.esbaudit.web

import javax.ws.rs.{PathParam, GET, Path}
import com.sun.jersey.api.view.ImplicitProduces
import org.fusesource.esbaudit.backend.model.Flow
import org.fusesource.esbaudit.backend.{Backend, MongoDB}

/**
 * Created by IntelliJ IDEA.
 * User: system
 * Date: 3/2/11
 * Time: 3:49 PM
 * To change this template use File | Settings | File Templates.
 */

@ImplicitProduces(Array("text/html;qs=5"))
@Path("/flows/tag/{id}")
class FlowsByTagResource(@PathParam("id") val tags: String) {

  val backend : Backend = MongoDB()

  lazy val selected = tags.split("\\+").toSeq
  lazy val remaining = List("inbound", "outbound", "po", "da")--selected.toList

  def flows : Iterator[Flow] = {
    println("Getting all flow objects for %s".format(tags));
    backend.flowsByTags(selected);
  }

  def urlAdd(tag: String): String = {
    val result = selected.toList ++ Seq(tag)
    "/flows/tag/%s".format(result.mkString("+"))
  }

  def urlRemove(tag: String): String = {
    val result = selected.toList - tag
    result.size match {
      case 0 => "/"
      case _ => "/flows/tag/%s".format(result.mkString("+"))
    }
  }

  @GET
  def index() = "Hello World"


}