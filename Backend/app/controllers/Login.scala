package controllers

import java.util.UUID

import database.KingstonStudentRepositoryImpl
import javax.inject.Inject
import models.KingstonStudent
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents, Result}
import utils.FutureO

import scala.concurrent.{ExecutionContext, Future}

class Login @Inject()(cc:ControllerComponents, kingstonStudentRepositoryImpl: KingstonStudentRepositoryImpl)
                     (implicit executionContext:ExecutionContext) extends AbstractController(cc){

  case class LoginRequest(nickname:String,password:String)

  implicit val LoginRequestReads: Reads[LoginRequest] = (
    (JsPath \ "nickname").read[String] and
      (JsPath \ "matchPassword").read[String]
    )(LoginRequest.apply _)

  implicit val LoginRequestWrites = new Writes[LoginRequest] {
    def writes(loginRequest: LoginRequest): JsObject = Json.obj(
      "nickname" -> loginRequest.nickname,
      "matchPassword" -> loginRequest.password
    )
  }
  def validateJson[A:Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )
  // TODO - add authentication token in login request and send it as json




  def login = Action.async(validateJson[LoginRequest]){ implicit req =>
    // get the nickname and password from frontend
    // get the nickname and password from database
    // check that the password matched the one in the database
    // if it matches then send true otherwise send false
    val tokenLoginAsString:String = UUID.randomUUID().toString
    val tokenLoginAsOption:Option[String] = Option(tokenLoginAsString)

//    val userExist = for {
//      user <- FutureO(kingstonStudentRepositoryImpl.getByNickname(req.body.nickname))
//
//    } yield user
//    val loginCompleted:Future[Result] = for{
//      firstResult <- kingstonStudentRepositoryImpl.getByNickname(req.body.nickname)
//      userWithPass <- firstResult.getOrElse(Ok(Json.obj("status" -> "OK","Authenticated" -> false,"nickname" -> "NONE","token"->"NONE")))
//      userAuthenticated = checkPasswordValidation(req.body.password,userWithPass.password)
//    }
    kingstonStudentRepositoryImpl.getByNickname(req.body.nickname).flatMap{
      case Some(newStudent) =>Future.successful(
        if(checkPasswordValidation(req.body.password, newStudent.password)){
          val query = kingstonStudentRepositoryImpl.updateOrInsertToken(
            newStudent.nickname, newStudent.email, newStudent.password, newStudent.fromKingston, newStudent.expirationTimeOfUser, newStudent.subject, newStudent.typeOfStudy, tokenLoginAsOption
          )
          kingstonStudentRepositoryImpl.runUpdateOrInsertToken(query).foreach(updated => Ok(Json.obj("status" -> "OK","Authenticated" -> false,"nickname" -> "NONE","token"->tokenLoginAsString,"response"->updated))).asInstanceOf[Result]
//          val result = for {
//            affectedRows <-
//            )
//          } yield affectedRows
        }else{
          Ok(Json.obj("status" -> "OK","Authenticated" -> false,"nickname" -> "NONE"))
        }
      )
//        .map{
//          case 0 => Ok(Json.obj("status" -> "OK","Authenticated" -> false,"nickname" -> "NONE","token"->tokenLoginAsString))
//          case n => Ok(Json.obj("status" -> "OK","Authenticated" -> false,"nickname" -> "NONE","token"->n))
//        }
      case other => Future.successful(Ok(Json.obj("status" -> "NOT_FOUND", "error" -> "user does not exist")))
    }
//    val authInPlace = for {
//      auth <- FutureO(kingstonStudentRepositoryImpl.auth(tokenLoginAsString))
//    } yield auth
////    ,"authToken"->Json.toJsFieldJsValueWrapper(token)
//
//    val createOrUpdateTokenLogin = for {
//      token <- kingstonStudentRepositoryImpl.updateToken(req.body.nickname,tokenLoginAsOption)
//    }yield token

//    userExist.future.flatMap {
//      case Some(newStudent) =>
//        Future.successful(
//          if (checkPasswordValidation(req.body.password, newStudent.password)) {
//
//            val result = for {
//              affectedRows <- kingstonStudentRepositoryImpl.runUpdateOrInsertToken(
//                kingstonStudentRepositoryImpl.updateOrInsertToken(
//                  newStudent.nickname, newStudent.email, newStudent.password, newStudent.fromKingston, newStudent.expirationTimeOfUser, newStudent.subject, newStudent.typeOfStudy, tokenLoginAsOption
//                )
//              )
//            } yield affectedRows
//
////            result.flatMap{
////              case 0 => Future.successful(Ok(Json.obj("status" -> "OK", "authenticated" -> true,
////                "nickname" -> newStudent.nickname)))
////              case n => Future.successful(Ok(Json.obj("status" -> "OK", "authenticated" -> true, "data" -> n)))
////            }
//          }else
//            Ok(Json.obj("status" -> "OK","Authenticated" -> false,"nickname" -> "NONE"))
//        )
////        Ok(Json.obj("status" -> "OK", "authenticated" -> true,
////                    "nickname" -> newStudent.nickname))
////                    else
////                    Ok(Json.obj("status" -> "OK",
////                                "Authenticated" -> false,
////                                "nickname" -> "NONE")))
////        rowsAffected match{
////          case 0 => KStudents += KingstonStudent(nickname,email,password,fromKingston,expirationTimeOfUser,subject,typeOfStudy,loginToken)
////          case 1 => DBIO.successful(1)
////          case other => DBIO.failed(new RuntimeException(
////            s"Expected 0 or 1 change, not $other for $nickname , $email ,$password, $fromKingston , $expirationTimeOfUser, $subject, $typeOfStudy"))
////        }
//      case other => Future.successful(Ok(Json.obj("status" -> "NOT_FOUND", "error" -> "user does not exist")))
//    }

  }

  def checkPasswordValidation(passwordFromFrontEnd:String,passwordFromDatabase:String): Boolean ={
    val foundStudent = BCrypt.checkpw(passwordFromFrontEnd,passwordFromDatabase)
    foundStudent
  }

  def checkAuth(auth:FutureO[KingstonStudent],updatedToken:Future[Int]): Future[Any] ={

    auth.future.flatMap{
      case Some(authentication) => Future.successful(s"$authentication")
      case other =>Future.successful(createOrUpdateToken(updatedToken).toString)
    }
  }

  def createOrUpdateToken(checkIfTokenWasUpdated:Future[Int]): Int={
    val updated = for{
      token <- checkIfTokenWasUpdated
    }yield token
    val newToken = updated.foreach( updatedToken => if(updatedToken>0) updatedToken else -1)
    newToken.toString.toInt

  }
}
