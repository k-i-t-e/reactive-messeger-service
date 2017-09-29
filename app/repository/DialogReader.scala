package repository

import model.Dialog
import reactivemongo.bson.{BSONDocument, BSONDocumentReader}

object DialogReader extends BSONDocumentReader[Dialog] {
  override def read(bson: BSONDocument): Dialog = {
    val dialog: Option[Dialog] = for {
      from <- bson.getAs[BSONDocument]("_id").get.getAs[String]("from")
      to <- bson.getAs[BSONDocument]("_id").get.getAs[String]("to")
      lastMsg <- bson.getAs[String]("lastMsg").orElse(null);
    } yield Dialog(from, to, if (lastMsg != null) Option(lastMsg) else Option.empty)

    dialog.get
  }
}
