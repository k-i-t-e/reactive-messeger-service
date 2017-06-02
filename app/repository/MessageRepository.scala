package repository

import javax.inject.{Inject, Singleton}
import javax.persistence.{EntityManager, EntityManagerFactory, Persistence}
import scala.collection.JavaConverters._

import model.dto.MessageDto
import play.db.jpa.{JPAApi, Transactional}

/**
  * Created by Mikhail_Miroliubov on 6/2/2017.
  */
@Singleton
class MessageRepository @Inject()(val api: JPAApi) {

  val entityManagerFactory: EntityManagerFactory = Persistence.createEntityManagerFactory("defaultPersistenceUnit")
  val entityManager: EntityManager = entityManagerFactory.createEntityManager()

  @Transactional
  def saveMessage(messageDto: MessageDto) = {
    doTransactional(() => entityManager.persist(messageDto))
  }

  @Transactional
  def loadMessagesFor(sender: String) = {
    doTransactional(() => entityManager.createNamedQuery("Message.getAllForSender", classOf[MessageDto])
      .setParameter("sender", sender).getResultList).asScala.toVector
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
