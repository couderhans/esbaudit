package org.fusesource.esbaudit.web

import javax.ws.rs.{PathParam, GET, Path}
import com.sun.jersey.api.view.ImplicitProduces
import org.fusesource.esbaudit.backend.model.Flow
import org.fusesource.esbaudit.backend.{Backend, MongoDB}


@ImplicitProduces(Array("text/html;qs=5"))
@Path("/flows/bystatus/{status}")
class FlowsByStatus(@PathParam("status") val status: String) {

  lazy val backend : Backend = MongoDB()
  //lazy val selected = query.split("\\+").toSeq
  lazy val selected = status

  def flows : Iterator[Flow] = {
    println("Getting all flow objects for query: %s".format(status));
    backend.flowsByStatus(selected);
  }

  @GET
  def index() = "Hello World"
}