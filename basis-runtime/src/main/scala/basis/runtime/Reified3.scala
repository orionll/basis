/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://basis.reify.it             **
\*                                                                      */

package basis.runtime

/** A ternary parametric type with hints about its type parameters.
  * `Reified3` protects its type hints so as not to clutter implementation
  * classes' APIs. Pattern match against [[Reified3$ Reified3]] to access
  * an instance's type hints. */
trait Reified3[T1, T2, T3] extends Any {
  /** Returns a hint about this instance's first type parameter. */
  protected def T1: TypeHint[T1]
  
  /** Returns a hint about this instance's second type parameter. */
  protected def T2: TypeHint[T2]
  
  /** Returns a hint about this instance's third type parameter. */
  protected def T3: TypeHint[T3]
}

/** An extractor for ternary reified types. */
object Reified3 {
  /** Returns `true` if an instance has matching reified type parameters. */
  def apply[T1, T2, T3](any: Any)(implicit T1: TypeHint[T1], T2: TypeHint[T2], T3: TypeHint[T3]): Boolean =
    any.isInstanceOf[Reified3[_, _, _]] && {
      val its = any.asInstanceOf[Reified3[_, _, _]]
      its.T1 == T1 && its.T2 == T2 && its.T3 == T3
    }
  
  /** Extracts the type hints from a ternary reified type. */
  def unapply[T1, T2, T3](any: Reified3[T1, T2, T3]): Some[(TypeHint[T1], TypeHint[T2], TypeHint[T3])] =
    Some((any.T1, any.T2, any.T3))
}
