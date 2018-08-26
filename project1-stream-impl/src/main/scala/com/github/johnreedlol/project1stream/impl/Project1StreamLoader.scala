package com.github.johnreedlol.project1stream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.github.johnreedlol.project1stream.api.Project1StreamService
import com.github.johnreedlol.project1.api.Project1Service
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.softwaremill.macwire._

class Project1StreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new Project1StreamApplication(context) {
      override def serviceLocator: ServiceLocator.NoServiceLocator.type = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new Project1StreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[Project1StreamService])
}

abstract class Project1StreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[Project1StreamService](wire[Project1StreamServiceImpl])

  // Bind the Project1Service client
  lazy val project1Service: Project1Service = serviceClient.implement[Project1Service]
}
