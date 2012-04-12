/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.algebra

trait VectorR4[V <: VectorR4[V]] extends VectorF4[V, Real] with VectorRN[V] {
  def Space: R4 {
    type Vector = V
  }
  
  override def + (that: V): V =
    Space(this(0) + that(0), this(1) + that(1), this(2) + that(2), this(3) + that(3))
  
  override def unary_- : V =
    Space(-this(0), -this(1), -this(2), -this(3))
  
  override def - (that: V): V =
    Space(this(0) - that(0), this(1) - that(1), this(2) - that(2), this(3) - that(3))
  
  override def :* (scalar: Double): V =
    Space(this(0) * scalar, this(1) * scalar, this(2) * scalar, this(3) * scalar)
  
  override def *: (scalar: Double): V = this :* scalar
  
  override def ⋅ (that: V): Real =
    new Real(this(0) * that(0) + this(1) * that(1) + this(2) * that(2) + this(3) * that(3))
}
