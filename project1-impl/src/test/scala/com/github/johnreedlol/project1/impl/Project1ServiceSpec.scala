package com.github.johnreedlol.project1.impl

import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import com.github.johnreedlol.project1.api._

class Project1ServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  private val server: ServiceTest.TestServer[Project1Application with LocalServiceLocator] = ServiceTest.startServer(
    ServiceTest.defaultSetup
      .withCassandra()
  ) { ctx =>
    new Project1Application(ctx) with LocalServiceLocator
  }

  val client: Project1Service = server.serviceClient.implement[Project1Service]

  override protected def afterAll(): Unit = server.stop()

  "Project1 service" should {

    "say hello" in {
      client.hello("Alice").invoke().map { answer =>
        answer should ===("Hello, Alice!")
      }
    }

    "allow responding with a custom message" in {
      for {
        _ <- client.useGreeting("Bob").invoke(GreetingMessage("Hi"))
        answer <- client.hello("Bob").invoke()
      } yield {
        answer should ===("Hi, Bob!")
      }
    }
  }
}
