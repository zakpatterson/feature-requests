package h4sm.featurerequests
package infrastructure.repository.persistent

import java.time.Instant

import cats.Monad
import cats.data.OptionT
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.postgres.syntax.monaderror._
import db.domain._
import db.sql._
import domain.requests._

class RequestRepositoryInterpreter[M[_]: Monad](xa: Transactor[M]) extends RequestRepositoryAlgebra[M] {
  def insert(r: Feature): M[Unit] = requests.insert(r).run.as(()).transact(xa)

  def select: M[List[(Feature, FeatureId, Instant)]] = requests.select.to[List].transact(xa)

  def byId(id: FeatureId): OptionT[M, (Feature, FeatureId, Instant)] =
    OptionT(requests.selectById(id).option.transact(xa))

  def insertGetId(a: Feature): OptionT[M, FeatureId] = OptionT {
    (requests.insertGetId(a).map(_.some).onUniqueViolation {
      HC.rollback.as(none[FeatureId])
    }).transact(xa)
  }

  def selectWithVoteCounts: M[List[VotedFeature]] = requests.selectAllWithVoteCounts.to[List].transact(xa)
}