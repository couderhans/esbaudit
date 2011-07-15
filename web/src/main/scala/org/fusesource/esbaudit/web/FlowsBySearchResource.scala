package org.fusesource.esbaudit.web

import com.sun.jersey.api.view.ImplicitProduces
import org.fusesource.esbaudit.backend.model.Flow
import org.fusesource.esbaudit.backend.{Backend, MongoDB}
import javax.ws.rs._

@ImplicitProduces(Array("text/html;qs=5"))
@Path("/flows/searched/{query}")
class FlowsBySearchResource(@FormParam("query") val query: String) {

  lazy val backend : Backend = MongoDB()
  //lazy val selected = query.split("\\+").toSeq
  lazy val selected = query

  def flows() : Iterator[Flow] = {
    println("Getting all flow objects for query: %s".format(query));
    backend.search(query);
  }

  @POST
  def index() = "Hello World"

}