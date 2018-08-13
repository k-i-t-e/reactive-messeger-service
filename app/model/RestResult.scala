package model

case class RestResult[T](payload: Option[T], status: ResultStatus.Value, message: String = null)

object ResultStatus extends Enumeration {
  val OK, ERROR = Value
}

object RestResult {
  def apply[T](payload: T): RestResult[T] = new RestResult[T](Some(payload), ResultStatus.OK)
  def error[T](payload: T, message: String) = new RestResult[T](Some(payload), ResultStatus.ERROR, message)
  def error[T](message: String) = new RestResult[T](None, ResultStatus.ERROR, message)
}