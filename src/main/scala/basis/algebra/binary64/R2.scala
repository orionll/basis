/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.algebra
package binary64

/** A concrete 2-dimensional coordinate space over the binary64 `Real` field.
  * 
  * @author Chris Sachs
  * 
  * @define Structure   `R2` space
  */
object R2 extends Vector2Space[Real.type] with RealVectorSpace {
  /** A vector element of this $Structure. */
  final class Element(val x: Real, val y: Real)
    extends super[Vector2Space].Element
      with super[RealVectorSpace].Element {
    
    override def N: Int = 2
    
    override def apply(i: Int): Real = i match {
      case 0 => x
      case 1 => y
      case _ => throw new IndexOutOfBoundsException(i.toString)
    }
    
    override def + (that: Vector): Vector =
      new Vector(x + that.x, y + that.y)
    
    override def unary_- : Vector = new Vector(-x, -y)
    
    override def - (that: Vector): Vector =
      new Vector(x - that.x, y - that.y)
    
    override def :* (scalar: Real): Vector =
      new Vector(x * scalar, y * scalar)
    
    override def *: (scalar: Real): Vector = this :* scalar
    
    override def / (scalar: Real): Vector =
      new Vector(x / scalar, y / scalar)
    
    override def ⋅ (that: Vector): Real =
      x * that.x + y * that.y
    
    override def norm: Real = (x * x + y * y).sqrt
  }
  
  override type Vector = Element
  
  override lazy val zero: Vector = new Vector(0.0, 0.0)
  
  override def apply(coords: Array[Double]): Vector = {
    if (coords.length != 2) throw new DimensionException
    new Vector(coords(0), coords(1))
  }
  
  override def apply(x: Real, y: Real): Vector = new Vector(x, y)
  
  override def ⨯ (that: RealVectorSpace): RealMatrixSpace[that.type, this.type] = {
    if (that.isInstanceOf[R2.type]) R2x2.asInstanceOf[RealMatrixSpace[that.type, this.type]]
    else super.⨯(that)
  }
  
  override def toString: String = "R2"
}
