package api

import java.util.UUID

import scalaz._
import Scalaz._
import api.AuctionsApi.PostInput
import api.LotsApi.{LimitedResultRequest, PostInput}
import entities.AuctionId
import persistence.AuctionService

import scala.util.Try

object InputValidator {
  type ErrorMsg = String
  type VNel[A] = ValidationNel[ErrorMsg, A]

  val auctionIdErrorMsg = "Invalid auction Id. Please provide a valid UUID."
  val auctionNotFoundErrorMsg = "Invalid auction Id. Auction does not exist."
  val auctionDataErrorMsg = "Auction data cannot be empty."
  val lotDataErrorMsg = "Lot data cannot be empty."
  val limitErrorMsg = "Limit should be a value between 0 and 100 (right inclusive)."
  val offsetErrorMsg = "Offset should be a value greater than 0."
}

trait InputValidator {

  import InputValidator._

  implicit val service: AuctionService

  def validUUIDString(uuid: String): Option[String] = Try(UUID.fromString(uuid).toString).toOption

  def validData(data: String): Option[String] = if (data.trim.length > 0) Some(data) else None

  def validateGetLotsInput(input: LotsApi.LimitedResultRequest[AuctionId]): VNel[LimitedResultRequest[AuctionId]] = {
    Apply[VNel].apply(
      validUUIDString(input.resourceId).toSuccessNel(auctionIdErrorMsg)
    ) {
      _ => input
    }
  }

  def validatePostLotsInput(input: LotsApi.PostInput): VNel[LotsApi.PostInput] = {
    (
      validUUIDString(input.auctionId).toSuccessNel(auctionIdErrorMsg) |@|
      service.getAuction(input.auctionId).toSuccessNel(auctionNotFoundErrorMsg) |@|
      validData(input.lotData).toSuccessNel(lotDataErrorMsg)
    ) {
      (_, _, _) => input
    }
  }

  def validatePostAuctionInput(input: AuctionsApi.PostInput): VNel[AuctionsApi.PostInput] = {
    Apply[VNel].apply(
      validData(input.data).toSuccessNel(auctionDataErrorMsg)
    ) {
      _ => input
    }
  }
}
