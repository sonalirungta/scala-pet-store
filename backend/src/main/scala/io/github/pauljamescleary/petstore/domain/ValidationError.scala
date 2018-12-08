package io.github.pauljamescleary.petstore.domain

import io.github.pauljamescleary.petstore.domain.users.User
import io.github.pauljamescleary.petstore.shared.domain.pets.Pet

sealed trait ValidationError extends Product with Serializable
case class PetAlreadyExistsError(pet: Pet) extends ValidationError
case object PetNotFoundError extends ValidationError
case object OrderNotFoundError extends ValidationError
case object UserNotFoundError extends ValidationError
case class UserAlreadyExistsError(user: User) extends ValidationError
case class UserAuthenticationFailedError(userName: String) extends ValidationError