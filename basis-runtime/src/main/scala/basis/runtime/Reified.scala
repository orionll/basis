/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://basis.reify.it             **
\*                                                                      */

package basis.runtime

/** A unary parametric type with a hint about its type parameter.
  * `Reified` protects its type hint so as not to clutter implementation
  * classes' APIs. Pattern match against [[Reified$ Reified]] to access
  * an instance's type hint. */
trait Reified[T] extends Any {
  /** Returns a hint about this instance's type parameter. */
  protected def T: TypeHint[T]
}

/** An extractor for unary reified types. */
object Reified {
  /** Returns `true` if an instance has a matching reified type parameter. */
  def apply[T](any: Any)(implicit T: TypeHint[T]): Boolean =
    any.isInstanceOf[Reified[_]] && any.asInstanceOf[Reified[_]].T == T
  
  /** Extracts the type hint from a unary reified type. */
  def unapply[T](any: Reified[T]): Some[TypeHint[T]] = Some(any.T)
}
