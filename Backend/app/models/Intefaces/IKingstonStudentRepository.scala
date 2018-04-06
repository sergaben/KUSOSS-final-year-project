package models.Intefaces

import models.KingstonStudent
import org.joda.time.DateTime

import scala.concurrent.Future

trait IKingstonStudentRepository {

  def insert(kingstonStudent:KingstonStudent):Future[Unit]
  def update(kingstonStudent:KingstonStudent):Future[Unit]
  def delete(kingstonStudent:KingstonStudent):Future[Unit]
  def updateOrInsertToken(nickname: String,email:String,password:String,fromKingston:Boolean,expirationTimeOfUser:Option[DateTime],subject:String,typeOfStudy:String,loginToken:Option[String]):Future[Int]
  def getAll():Future[Seq[KingstonStudent]]
  def getByNickname(nickname:String):Future[Option[KingstonStudent]]
  def getByEmail(email:String):Future[Option[KingstonStudent]]
  def auth(loginToken:String):Future[Option[KingstonStudent]]
}
