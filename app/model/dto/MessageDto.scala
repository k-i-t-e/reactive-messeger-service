package model.dto

/**
  * Created by Mikhail_Miroliubov on 6/2/2017.
  */
class MessageDto(_text: String, _sender:String, _address: String) {

  var text: String = _text

  var from: String = _sender

  var to: String = _address

  def this() = this(null, null, null)
}
