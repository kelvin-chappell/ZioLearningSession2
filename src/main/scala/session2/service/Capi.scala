package session2.service

import com.gu.contentapi.client.{ContentApiClient, GuardianContentClient}
import com.gu.contentapi.client.model.v1.Content
import com.gu.contentapi.client.model.v1.Tag
import session2.Failure
import zio._

// service definition
trait Capi {
  def searchForContent(query: String): ZIO[Any, Failure, List[Content]]
  def searchForTags(query: String): ZIO[Any, Failure, List[Tag]]
}

object Capi {
  // Capability accessors
  def searchForContent(query: String): ZIO[Capi, Failure, List[Content]] =
    ZIO.serviceWithZIO(_.searchForContent(query))

  def searchForTags(query: String): ZIO[Capi, Failure, List[Tag]] =
    ZIO.serviceWithZIO(_.searchForTags(query))
}

// Implementation
object CapiLive {
  val layer: ZLayer[Config, Failure, Capi] =
    ZLayer.fromZIO(for {
      config <- ZIO.service[Config]
      client = new GuardianContentClient(config.apiKey)
    } yield new Capi {

      override def searchForContent(query: String): ZIO[Any, Failure, List[Content]] =
        ZIO
          .fromFuture { implicit ec =>
            val search = ContentApiClient.search.q(query)
            client.getResponse(search).map(_.results.toList)
          }
          .mapError(Failure.fromThrowable)

      override def searchForTags(query: String): ZIO[Any, Failure, List[Tag]] =
        ZIO
          .fromFuture { implicit ec =>
            val search = ContentApiClient.tags.q(query)
            client.getResponse(search).map(_.results.toList)
          }
          .mapError(Failure.fromThrowable)
    })
}
