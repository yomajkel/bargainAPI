package api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import entities.{AuctionData}
import mappings.JsonMappings
import persistence.MemStorage

trait AuctionsApi extends JsonMappings {

  val persistence = MemStorage.shared

  val auctionsApi: Route = pathPrefix("auctions") {
    pathEnd {
      post {
        entity(as[AuctionData]) { (auctionData: AuctionData) =>
          val auctionId = persistence.createAuction(auctionData)
          complete(auctionId)
        }
      }
    } ~
      path(Segment) { id =>
        get {
          persistence.getAuction(id) match {
            case Some(auction) => complete(auction)
            case _ => complete(StatusCodes.NotFound)
          }
        }
      }
  }
}