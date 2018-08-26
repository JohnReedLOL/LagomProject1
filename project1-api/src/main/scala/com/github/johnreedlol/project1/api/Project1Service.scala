package com.github.johnreedlol.project1.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}

object Project1Service  {
  val TOPIC_NAME = "greetings"
}

/**
  * The Project1 service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and consume the Project1Service.
  * Note that ServiceCalls here must be implemented by Project1ServiceImpl.
  */
trait Project1Service extends Service {

  /**
    * Example: curl http://localhost:9000/api/hello/Alice
    * Note that the ServiceCall's Request type is NotUsed and Response type is String.
    */
  def hello(id: String): ServiceCall[NotUsed, String]

  /**
    * Example: curl -H "Content-Type: application/json" -X POST -d '{"message":"Hi"}' http://localhost:9000/api/hello/Alice
    * Note that the ServiceCall's Request type is GreetingMessage and Response type is Done.
    */
  def useGreeting(id: String): ServiceCall[GreetingMessage, Done]


  /**
    * This gets published to Kafka.
    */
  def greetingsTopic(): Topic[GreetingMessageChanged]

  /**
    * The Project1Service descriptor defines the service name and the REST endpoints it offers.
    */
  override final def descriptor: Descriptor = {
    import Service._
    // @formatter:off
    named("project1")
      .withCalls(
        // For each of these endpoints, declare an abstract method in the service interface as illustrated in the HelloService.hello method.
        pathCall("/api/hello/:id", hello _),
        // pathCall takes two implicit parameters of type MessageSerializer which are used to serialize the Request and the Response.
        // Serialization is a functionality which cuts across data types, so the type class pattern is used. Type class pattern: https://blog.scalac.io/2017/04/19/typeclasses-in-scala.html
        // The implicit for the Serialization of case class GreetingMessage is provided by "implicit val format: Format[GreetingMessage]".
        pathCall("/api/hello/:id", useGreeting _)
      )
      .withTopics(
        topic(Project1Service.TOPIC_NAME, greetingsTopic())
          // Kafka partitions messages, messages within the same partition will
          // be delivered in order, to ensure that all messages for the same user
          // go to the same partition (and hence are delivered in order with respect
          // to that user), we configure a partition key strategy that extracts the
          // name as the partition key.
          .addProperty(
            KafkaProperties.partitionKeyStrategy,
            PartitionKeyStrategy[GreetingMessageChanged]((_: GreetingMessageChanged).name)
          )
      )
      .withAutoAcl(true)
    // @formatter:on
  }
}

/**
  * The greeting message class.
  */
case class GreetingMessage(message: String)

object GreetingMessage {
  /**
    * Format for converting greeting messages to and from JSON.
    *
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    * The output will look like:
    * {
    *   "message":"id"
    * }
    */
  implicit val format: Format[GreetingMessage] = Json.format[GreetingMessage]
  // Note that if your case class references another, non primitive type, such as another case class, you’ll need to also define a format for that other case class.
}



/**
  * The greeting message class used by the topic stream.
  * Different than [[GreetingMessage]], this message includes the name (id).
  */
case class GreetingMessageChanged(name: String, message: String)

object GreetingMessageChanged {
  /**
    * Format for converting greeting messages to and from JSON.
    *
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    */
  implicit val format: Format[GreetingMessageChanged] = Json.format[GreetingMessageChanged]
}
