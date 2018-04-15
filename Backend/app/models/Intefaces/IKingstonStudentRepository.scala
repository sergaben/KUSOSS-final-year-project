package models.Intefaces

import models.KingstonStudent

import scala.concurrent.Future

trait IKingstonStudentRepository {

  def insert(kingstonStudent:KingstonStudent):Future[Unit]
  def updateTokenToNull(nickname :String):Future[Int]
  def delete(kingstonStudent:KingstonStudent):Future[Unit]
  def updateOrInsertToken(id:Option[Int],nickname: String,email:String,password:String,subject:String,typeOfStudy:String,tokenToBeInserted:Option[String]):Future[Option[KingstonStudent]]
  def getAll():Future[Seq[KingstonStudent]]
  def getByNickname(nickname:String):Future[Option[KingstonStudent]]
  def getByEmail(email:String):Future[Option[KingstonStudent]]
  def auth(loginToken:String):Future[Option[KingstonStudent]]
}
