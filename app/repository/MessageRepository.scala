package repository

import java.util.function.Function
import javax.inject.{Inject, Singleton}
import javax.persistence.{EntityManager, EntityManagerFactory, Persistence}

import scala.collection.JavaConverters._
import model.dto.MessageDto
import play.db.jpa.{JPAApi, Transactional}
import util.FunctionUtils

/**
  * Created by Mikhail_Miroliubov on 6/2/2017.
  */
@Singleton
class MessageRepository @Inject() (jpaApi : JPAApi) {

  val entityManagerFactory: EntityManagerFactory = Persistence.createEntityManagerFactory("defaultPersistenceUnit")
  val entityManager: EntityManager = entityManagerFactory.createEntityManager()

  def saveMessage(messageDto: MessageDto) = {
    jpaApi.withTransaction(FunctionUtils.toJavaFunction(
      (entityManager: EntityManager) => entityManager.persist(messageDto)))
  }

  @Transactional
  def loadGlobalMessages() = {
      entityManager.createNamedQuery("Message.getGlobalMessages", classOf[MessageDto])
        .getResultList.asScala.toVector
  }

  @Transactional
  def loadPrivateMessages(sender: String, address: String) = {
    entityManager.createNamedQuery("Message.getPrivateMessages", classOf[MessageDto])
      .setParameter(1, sender)
      .setParameter(2, address)
      .getResultList
      .asScala.toVector
  }

  @Transactional
  def loadLastMessages(user: String) = {
    entityManager.createNamedQuery("Message.getLastMessages", classOf[MessageDto])
      .setParameter(1, user)
      .getResultList
      .asScala
  }
}
