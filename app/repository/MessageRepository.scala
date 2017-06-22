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

  def loadMessagesFor(sender: String) = {
    doTransactional(
      () => entityManager.createNamedQuery("Message.getAllForSender", classOf[MessageDto])
        .getResultList //.setParameter("sender", sender)
    ).asScala.toVector
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
