package com.github.johnreedlol.project1stream.api

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

/**
  * The Project1 stream interface.
  *
  * This describes everything that Lagom needs to know about how to serve and
  * consume the Project1Stream service.
  */
trait Project1StreamService extends Service {

  /**
    * A streamed message is a message of type Source. Source is an Akka streams API
    * that allows asynchronous streaming and handling of messages.
    * This uses WebSockets under the hood.
    */
  def stream: ServiceCall[Source[String, NotUsed], Source[String, NotUsed]]

  override final def descriptor: Descriptor = {
    import Service._

    named("project1-stream")
      .withCalls(
        namedCall("stream", stream)
      ).withAutoAcl(true)
  }
}

