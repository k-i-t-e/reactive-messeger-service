package model.dto

import javax.persistence.GenerationType.SEQUENCE
import javax.persistence._

/**
  * Created by Mikhail_Miroliubov on 6/2/2017.
  */
@Entity
@Table(name = "message")
class MessageDto(_text: String, _sender:String) {

  @Id
  @Column(name = "message_id")
  @GeneratedValue(strategy = SEQUENCE, generator = "message_seq_gen")
  @SequenceGenerator(name = "message_seq_gen", sequenceName = "s_message", allocationSize = 1)
  var id: Int = _

  @Column(name = "content")
  var text: String = _text

  @Column(name = "sender")
  var sender: String = _sender

  def this() = this(null, null)
}
