package api

import java.util.UUID

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.AuctionsApi.{GetInput, PostInput}
import entities.{AuctionData, AuctionId}
import mappings.JsonMappings

import scala.util.Try

object AuctionsApi {
  case class GetInput(id: AuctionId) {
    require(Try(UUID.fromString(id).toString).isSuccess, "Invalid auction Id. Auction Id must have a UUID format.")
  }
  case class PostInput(data: AuctionData)
}

trait AuctionsApi extends JsonMappings with ServiceHolder with BaseApi with InputValidator {

  val auctionsApi: Route = pathPrefix("auctions") {
    pathEnd {
      post {
        entity(as[PostInput]) { (input: PostInput) =>
            complete(service.createAuction(input.data))
        }
      }
    } ~
      path(Segment).as(GetInput) { input =>
        findAuctionOrNotFound(input.id) { auction =>
          complete(auction)
        }
      }
  }
}
