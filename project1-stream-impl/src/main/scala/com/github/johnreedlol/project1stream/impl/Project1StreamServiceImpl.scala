package com.github.johnreedlol.project1stream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.github.johnreedlol.project1stream.api.Project1StreamService
import com.github.johnreedlol.project1.api.Project1Service

import scala.concurrent.Future

/**
  * Implementation of the Project1StreamService.
  */
class Project1StreamServiceImpl(project1Service: Project1Service) extends Project1StreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(project1Service.hello(_: String).invoke()))
  }
}
