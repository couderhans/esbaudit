package org.fusesource.esbaudit.web

import javax.ws.rs.{PathParam, GET, Path}
import com.sun.jersey.api.view.ImplicitProduces
import org.fusesource.esbaudit.backend.model.Flow
import org.fusesource.esbaudit.backend.{Backend, MongoDB}


@ImplicitProduces(Array("text/html;qs=5"))
@Path("/flows/active")
class FlowsActive {

  lazy val backend : Backend = MongoDB()

  def flows : Iterator[Flow] = {
    println("Getting all active flow objects");
    backend.flowsByStatus("active");
  }

}
