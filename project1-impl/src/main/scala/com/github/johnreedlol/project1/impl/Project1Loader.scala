package com.github.johnreedlol.project1.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.github.johnreedlol.project1.api.Project1Service
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.softwaremill.macwire._

class Project1Loader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new Project1Application(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new Project1Application(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[Project1Service])
}

abstract class Project1Application(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[Project1Service](wire[Project1ServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry: Project1SerializerRegistry.type = Project1SerializerRegistry

  // Register the Project1 persistent entity
  persistentEntityRegistry.register(wire[Project1Entity])
}
