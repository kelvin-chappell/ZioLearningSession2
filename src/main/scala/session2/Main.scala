package session2

import session2.service.{Capi, CapiLive, ConfigLive}
import zio._

object Main extends ZIOAppDefault {

  private val program: ZIO[Capi, Failure, Unit] =
    for {
      _       <- Console.print("Query: ").mapError(Failure.fromThrowable)
      query   <- Console.readLine.mapError(Failure.fromThrowable)
      results <- Capi.searchForContent(query)
      _ <- ZIO
        .foreachDiscard(results)(result =>
          Console.printLine(
            s"\n${result.webPublicationDate.map(_.iso8601).getOrElse("Undated")}\n${result.webTitle}\n${result.webUrl}\n",
          ),
        )
        .mapError(Failure.fromThrowable)
    } yield ()

  override def run = program.forever.provide(ConfigLive.layer, CapiLive.layer)
}
