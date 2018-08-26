package com.github.johnreedlol.project1.impl

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class Project1EntitySpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private val system = ActorSystem("Project1EntitySpec",
    JsonSerializerRegistry.actorSystemSetupFor(Project1SerializerRegistry))

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  private def withTestDriver(block: PersistentEntityTestDriver[Project1Command[_], Project1Event, Project1State] => Unit): Unit = {
    val driver = new PersistentEntityTestDriver(system, new Project1Entity, "project1-1")
    block(driver)
    driver.getAllIssues should have size 0
  }

  "Project1 entity" should {

    "say hello by default" in withTestDriver { driver =>
      val outcome: PersistentEntityTestDriver.Outcome[Project1Event, Project1State] = driver.run(Hello("Alice"))
      outcome.replies should contain only "Hello, Alice!"
    }

    "allow updating the greeting message" in withTestDriver { driver =>
      val outcome1: PersistentEntityTestDriver.Outcome[Project1Event, Project1State] = driver.run(UseGreetingMessage("Hi"))
      outcome1.events should contain only GreetingMessageChanged("Hi")
      val outcome2: PersistentEntityTestDriver.Outcome[Project1Event, Project1State] = driver.run(Hello("Alice"))
      outcome2.replies should contain only "Hi, Alice!"
    }

  }
}
