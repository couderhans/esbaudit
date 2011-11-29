package org.fusesource.esbaudit.web

import javax.ws.rs.{PathParam, GET, Path}
import com.sun.jersey.api.view.ImplicitProduces
import org.fusesource.esbaudit.backend.model.Flow
import org.fusesource.esbaudit.backend.{Backend, MongoDB}
import java.util.Calendar

@ImplicitProduces(Array("text/html;qs=5"))
@Path("/flows/bydate/{date}")
class FlowsByDate(@PathParam("date") val date: String) {

  lazy val backend : Backend = MongoDB()


  def flows : Iterator[Flow] = {
    println("Getting all flow objects for query: %s".format(date));
    val selected = Calendar.getInstance()
    selected.set(Calendar.YEAR, date.split("-").slice(1,2).asInstanceOf[Int])
    selected.set(Calendar.MONTH, date.split("-").slice(2,3).asInstanceOf[Int])
    selected.set(Calendar.DAY_OF_MONTH, date.split("-").slice(3,4).asInstanceOf[Int])
    backend.flowsByDate(selected);
  }

  @GET
  def index() = "Hello World"
}