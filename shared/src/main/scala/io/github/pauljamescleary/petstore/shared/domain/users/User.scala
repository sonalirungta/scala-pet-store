package io.github.pauljamescleary.petstore.shared.domain.users

import io.github.pauljamescleary.petstore.shared.JsonSerializers._

case class User(
    userName: String,
    firstName: String,
    lastName: String,
    email: String,
    hash: String,
    phone: String,
    id: Option[Long] = None
)

object User {
  implicit val decodeUser = deriveDecoder[User]
  implicit val encodeUser = deriveEncoder[User]
}