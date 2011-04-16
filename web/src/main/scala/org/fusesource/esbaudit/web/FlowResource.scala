package org.fusesource.esbaudit.web

import javax.ws.rs.{PathParam, Path, GET}
import org.fusesource.esbaudit.backend.MongoDB
import com.sun.jersey.api.view.ImplicitProduces

/**
 * Resource class for /flows/flow URIs
 */
@Path("/flows/flow")
class FlowResource {

  val backend = MongoDB()

  @ImplicitProduces(Array("text/html;qs=5"))
  @GET
  @Path("/{id}")
  def showFlow(@PathParam("id") id: String) = {
    println("finding by id " + id)
    val found = backend.flow(id)
    println("found flow " + found)
    found match {
      case Some(flow) => flow
      case None => None
    }

  }

}