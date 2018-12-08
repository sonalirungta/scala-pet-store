package io.github.pauljamescleary.petstore.domain.pets

import scala.language.higherKinds
import cats.data.NonEmptyList
import io.github.pauljamescleary.petstore.shared.domain.pets.{Pet, PetStatus}

trait PetRepositoryAlgebra[F[_]] {

  def create(pet: Pet): F[Pet]

  def update(pet: Pet) : F[Option[Pet]]

  def get(id: Long): F[Option[Pet]]

  def delete(id: Long): F[Option[Pet]]

  def findByNameAndCategory(name: String, category: String): F[Set[Pet]]

  def list(pageSize: Int, offset: Int): F[List[Pet]]

  def findByStatus(status: NonEmptyList[PetStatus]): F[List[Pet]]

  def findByTag(tags: NonEmptyList[String]): F[List[Pet]]
}
