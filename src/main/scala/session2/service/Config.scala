package session2.service

import session2.Failure
import zio._

case class Config(apiKey: String)

object ConfigLive {

  private val load =
    (for {
      optApiKey <- zio.System.env("API_KEY")
      apiKey    <- ZIO.fromOption(optApiKey).orElseFail(new IllegalArgumentException("No API key in environment"))
    } yield Config(apiKey))
      .mapError(Failure.fromThrowable)

  val layer: ZLayer[Any, Failure, Config] = ZLayer.fromZIO(load)
}
