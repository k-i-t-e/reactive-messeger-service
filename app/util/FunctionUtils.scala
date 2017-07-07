package util

import java.util.function.Function

/**
  * Created by Mikhail_Miroliubov on 7/7/2017.
  */
object FunctionUtils {
  def toJavaFunction[U, V](f: (U) => V): Function[U, V] = new Function[U, V] {
    override def apply(t: U): V = f(t)

    override def compose[T](before: Function[_ >: T, _ <: U]): Function[T, V] = toJavaFunction(f.compose(x => before.apply(x)))

    override def andThen[T](after: Function[_ >: V, _ <: T]): Function[U, T] = toJavaFunction(f.andThen(x => after.apply(x)))
  }
}
