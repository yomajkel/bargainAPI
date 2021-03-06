package persistence

import entities._
import java.util.UUID

import scalaz._
import Scalaz._
import api.InputValidator
import api.InputValidator.{VNel, _}

class MemStorage extends AuctionService with InputValidator {
  private var auctions = Seq(
    Auction("4ac772c5-bc52-4d3c-ba9e-4010f511e175", "First"),
    Auction("0ae024c0-0ddb-4e59-9e2c-721a27c386f6", "Second"),
    Auction("b9549a8e-be47-4b8b-a38c-226c2647073d", "Third")
  )

  private var lots = Seq(
    Lot("6b2e9336-7911-4381-a0e7-382a4e510291", "4ac772c5-bc52-4d3c-ba9e-4010f511e175", "Lot 1 for auction 1"),
    Lot("826a8ebb-8c9c-45b7-b08e-c784c442f55b", "4ac772c5-bc52-4d3c-ba9e-4010f511e175", "Lot 2 for auction 1"),
    Lot("2e5faabf-47eb-40c1-a961-b1ca7e928b49", "4ac772c5-bc52-4d3c-ba9e-4010f511e175", "Lot 3 for auction 1")
  )

  override def createAuction(data: AuctionData): VNel[AuctionId] = {
    def _createAuction(data: String): AuctionId = {
      val newId = newUUIDString
      val auction = Auction(newId, data)
      auctions :+= auction
      newId
    }

    Apply[VNel].apply(
      validData(data, auctionDataErrorMsg)
    ) {
      _ => _createAuction(data)
    }
  }

  override def getAuction(id: AuctionId): VNel[Auction] = auctions.find( _.id == id).toSuccessNel(auctionNotFoundErrorMsg)

  override def addLot(auctionId: AuctionId, data: LotData): VNel[LotId] = {
    newLot(auctionId, data) map { lot =>
      lots :+= lot
      lot.id
    }
  }

  override def getLots(auctionId: AuctionId, maybeLimit: Option[Int], maybeOffset: Option[Int]): LimitedResult[Lot] = {
    val limit = maybeLimit.getOrElse(10)
    val offset = maybeOffset.getOrElse(0)

    val auctionLots = lots.filter( _.auctionId == auctionId)
    val auctionLotsSlice = auctionLots.slice(offset, offset + limit)
    LimitedResult(auctionLotsSlice, limit, offset, auctionLots.length)
  }

  private def newUUIDString: String = UUID.randomUUID.toString

  private def newLot(auctionId: AuctionId, data: LotData): VNel[Lot] =
    ((uuid(auctionId, auctionIdErrorMsg) andThen findAuction) |@| validData(data, lotDataErrorMsg)) { (_, _) =>
      Lot(newUUIDString, auctionId, data)
    }

  private def findAuction(uuid: UUID): VNel[Auction] = {
    val stringUUID = uuid.toString
    auctions.find( _.id == stringUUID).toSuccessNel(auctionNotFoundErrorMsg)
  }

}

object MemStorage {

  private val storage = new MemStorage

  def shared = storage
}
