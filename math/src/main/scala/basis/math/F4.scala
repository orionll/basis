//      ____              ___
//     / __ | ___  ____  /__/___      A library of building blocks
//    / __  / __ |/ ___|/  / ___|
//   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2015 Chris Sachs
//  |_____/\_____\____/__/\____/      http://basis.reify.it

package basis.math

/** An abstract 4-dimensional vector space over a ring.
  *
  * @author   Chris Sachs
  * @version  0.1
  * @since    0.0
  * @group    VectorSpaces
  */
trait F4 extends FN { F4 =>
  override type Vector <: VectorF4

  override def dim: Int = 4

  override def zero: Vector = {
    val z = Scalar.zero
    apply(z, z, z, z)
  }

  /** Returns a new vector with 𝑥, 𝑦, 𝑧 and 𝑤 coordinates. */
  def apply(x: Scalar, y: Scalar, z: Scalar, w: Scalar): Vector

  override def apply(coords: Array[Scalar]): Vector = {
    if (coords.length != 4) throw new DimensionException
    apply(coords(0), coords(1), coords(2), coords(3))
  }

  /** Extracts the 𝑥, 𝑦, 𝑧 and 𝑤 coordinates from a vector. */
  def unapply(vector: Vector): Option[(Scalar, Scalar, Scalar, Scalar)] =
    Some((vector.x, vector.y, vector.z, vector.w))

  trait VectorF4 extends Any with VectorFN {
    /** Returns the 𝑥-coordinate of this $vector. */
    def x: Scalar

    /** Returns the 𝑦-coordinate of this $vector. */
    def y: Scalar

    /** Returns the 𝑧-coordinate of this $vector. */
    def z: Scalar

    /** Returns the 𝑤-coordinate of this $vector. */
    def w: Scalar

    override def dim: Int = 4

    override def apply(i: Int): Scalar = i match {
      case 0 => x
      case 1 => y
      case 2 => z
      case 3 => w
      case _ => throw new IndexOutOfBoundsException(i.toString)
    }

    override def + (that: Vector): Vector =
      F4(x + that.x, y + that.y, z + that.z, w + that.w)

    override def unary_- : Vector =
      F4(-x, -y, -z, -w)

    override def - (that: Vector): Vector =
      F4(x - that.x, y - that.y, z - that.z, w - that.w)

    override def :* (scalar: Scalar): Vector =
      F4(x * scalar, y * scalar, z * scalar, w * scalar)

    override def *: (scalar: Scalar): Vector =
      F4(scalar * x, scalar * y, scalar * z, scalar * w)

    override def ∘ (that: Vector): Vector =
      F4(x * that.x, y * that.y, z * that.z, w * that.w)

    override def ⋅ (that: Vector): Scalar =
      x * that.x + y * that.y + z * that.z + w * that.w
  }
}
