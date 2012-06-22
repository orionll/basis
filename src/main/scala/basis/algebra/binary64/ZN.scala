/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.algebra
package binary64

/** A concrete ''N''-dimensional coordinate space over the binary64 `Integer` field.
  * 
  * @author Chris Sachs
  * 
  * @define Structure   `ZN` space
  */
class ZN(override val N: Int) extends IntegerVectorSpace {
  /** A vector element of this $Structure. */
  final class Element private[ZN] (coords: Array[Long]) extends super.Element {
    if (coords.length != Vector.N) throw new DimensionException
    
    override def N: Int = coords.length
    
    override def apply(i: Int): Integer = coords(i)
  }
  
  override type Vector = Element
  
  override lazy val zero: Vector = super.zero
  
  override def apply(coords: Array[Long]): Vector = new Vector(coords)
  
  override def toString: String = "ZN"+"("+ N + ")"
}
