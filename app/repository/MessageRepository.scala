package repository

import javax.inject.Singleton
import javax.persistence.{EntityManager, EntityManagerFactory, Persistence}
import scala.collection.JavaConverters._

import model.dto.MessageDto

/**
  * Created by Mikhail_Miroliubov on 6/2/2017.
  */
@Singleton
class MessageRepository {

  val entityManagerFactory: EntityManagerFactory = Persistence.createEntityManagerFactory("defaultPersistenceUnit")
  val entityManager: EntityManager = entityManagerFactory.createEntityManager()

  def saveMessage(messageDto: MessageDto) = {
    doTransactional(() => entityManager.persist(messageDto))
  }

  def loadGlobalMessages() = {
    doTransactional(
      () => entityManager.createNamedQuery("Message.getGlobalMessages", classOf[MessageDto])
        .getResultList
    ).asScala.toVector
  }

  def loadPrivateMessages(sender: String, address: String) = {
    doTransactional(
      () => entityManager.createNamedQuery("Message.getPrivateMessages", classOf[MessageDto])
        .setParameter(1, sender)
        .setParameter(2, address)
        .getResultList
    ).asScala.toVector
  }

  def loadLastMessages(user: String) = {
    doTransactional(
      () => entityManager.createNamedQuery("Message.getLastMessages", classOf[MessageDto])
        .setParameter(1, user)
        .getResultList
    ).asScala
  }

  private def doTransactional[T](f: () => T) = {
    entityManager.getTransaction.begin()
    try {
      val res = f.apply
      entityManager.getTransaction.commit()
      res
    } catch {
      case e: Throwable =>
        entityManager.getTransaction.rollback()
        throw e
    }
  }
}
