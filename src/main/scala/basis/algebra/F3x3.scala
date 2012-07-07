/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.algebra

/** An asbtract 3 by 3 matrix space over a field.
  * 
  * @author Chris Sachs
  * 
  * @tparam V   The space of rows and columns.
  * @tparam S   The set of scalars.
  */
trait F3x3[V <: F3[S] with Singleton, S <: Field with Singleton] extends Ring with FMxN[V, V, S] {
  trait Element extends Any with super[Ring].Element with super[FMxN].Element {
    override protected def Matrix: F3x3.this.type = F3x3.this
    
    def _1_1: Scalar
    def _1_2: Scalar
    def _1_3: Scalar
    def _2_1: Scalar
    def _2_2: Scalar
    def _2_3: Scalar
    def _3_1: Scalar
    def _3_2: Scalar
    def _3_3: Scalar
    
    override def M: Int = 3
    override def N: Int = 3
    
    override def apply(k: Int): Scalar = k match {
      case 0 => _1_1
      case 1 => _1_2
      case 2 => _1_3
      case 3 => _2_1
      case 4 => _2_2
      case 5 => _2_3
      case 6 => _3_1
      case 7 => _3_2
      case 8 => _3_3
      case _ => throw new IndexOutOfBoundsException(k.toString)
    }
    
    override def apply(i: Int, j: Int): Scalar = {
      if (i < 0 || i >= 3 || j < 0 || j >= 3)
        throw new IndexOutOfBoundsException("row "+ i +", "+"col "+ j)
      apply(3 * i + j)
    }
    
    def row1: Row = Row(_1_1, _1_2, _1_3)
    def row2: Row = Row(_2_1, _2_2, _2_3)
    def row3: Row = Row(_3_1, _3_2, _3_3)
    
    override def row(i: Int): Row = i match {
      case 0 => row1
      case 1 => row2
      case 2 => row3
      case _ => throw new IndexOutOfBoundsException("row "+ i)
    }
    
    def col1: Col = Col(_1_1, _2_1, _3_1)
    def col2: Col = Col(_1_2, _2_2, _3_2)
    def col3: Col = Col(_1_3, _2_3, _3_3)
    
    override def col(j: Int): Col = j match {
      case 0 => col1
      case 1 => col2
      case 2 => col3
      case _ => throw new IndexOutOfBoundsException("col "+ j)
    }
    
    override def + (that: Matrix): Matrix =
      Matrix(
        _1_1 + that._1_1, _1_2 + that._1_2, _1_3 + that._1_3,
        _2_1 + that._2_1, _2_2 + that._2_2, _2_3 + that._2_3,
        _3_1 + that._3_1, _3_2 + that._3_2, _3_3 + that._3_3)
    
    override def unary_- : Matrix =
      Matrix(
        -_1_1, -_1_2, -_1_3,
        -_2_1, -_2_2, -_2_3,
        -_3_1, -_3_2, -_3_3)
    
    override def - (that: Matrix): Matrix =
      Matrix(
        _1_1 - that._1_1, _1_2 - that._1_2, _1_3 - that._1_3,
        _2_1 - that._2_1, _2_2 - that._2_2, _2_3 - that._2_3,
        _3_1 - that._3_1, _3_2 - that._3_2, _3_3 - that._3_3)
    
    override def :* (scalar: Scalar): Matrix =
      Matrix(
        _1_1 * scalar, _1_2 * scalar, _1_3 * scalar,
        _2_1 * scalar, _2_2 * scalar, _2_3 * scalar,
        _3_1 * scalar, _3_2 * scalar, _3_3 * scalar)
    
    override def *: (scalar: Scalar): Matrix =
      Matrix(
        scalar * _1_1, scalar * _1_2, scalar * _1_3,
        scalar * _2_1, scalar * _2_2, scalar * _2_3,
        scalar * _3_1, scalar * _3_2, scalar * _3_3)
    
    override def :⋅ (vector: Row): Col =
      Col(_1_1 * vector.x + _1_2 * vector.y + _1_3 * vector.z,
          _2_1 * vector.x + _2_2 * vector.y + _2_3 * vector.z,
          _3_1 * vector.x + _3_2 * vector.y + _3_3 * vector.z)
    
    override def ⋅: (vector: Col): Row =
      Row(vector.x * _1_1 + vector.y * _2_1 + vector.z * _3_1,
          vector.x * _1_2 + vector.y * _2_2 + vector.z * _3_2,
          vector.x * _1_3 + vector.y * _2_3 + vector.z * _3_3)
    
    override def * (that: Matrix): Matrix =
      Matrix(
        _1_1 * that._1_1 + _1_2 * that._2_1 + _1_3 * that._3_1,
        _1_1 * that._1_2 + _1_2 * that._2_2 + _1_3 * that._3_2,
        _1_1 * that._1_3 + _1_2 * that._2_3 + _1_3 * that._3_3,
        _2_1 * that._1_1 + _2_2 * that._2_1 + _2_3 * that._3_1,
        _2_1 * that._1_2 + _2_2 * that._2_2 + _2_3 * that._3_2,
        _2_1 * that._1_3 + _2_2 * that._2_3 + _2_3 * that._3_3,
        _3_1 * that._1_1 + _3_2 * that._2_1 + _3_3 * that._3_1,
        _3_1 * that._1_2 + _3_2 * that._2_2 + _3_3 * that._3_2,
        _3_1 * that._1_3 + _3_2 * that._2_3 + _3_3 * that._3_3)
    
    override def inverse(implicit isSquare: V =:= V): Option[Matrix] = {
      val minor_1_1 = _2_2 * _3_3 - _2_3 * _3_2
      val minor_1_2 = _2_1 * _3_3 - _2_3 * _3_1
      val minor_1_3 = _2_1 * _3_2 - _2_2 * _3_1
      val minor_2_1 = _1_2 * _3_3 - _1_3 * _3_2
      val minor_2_2 = _1_1 * _3_3 - _1_3 * _3_1
      val minor_2_3 = _1_1 * _3_2 - _1_2 * _3_1
      val minor_3_1 = _1_2 * _2_3 - _1_3 * _2_2
      val minor_3_2 = _1_1 * _2_3 - _1_3 * _2_1
      val minor_3_3 = _1_1 * _2_2 - _1_2 * _2_1
      
      val det = _1_1 * minor_1_1 - _1_2 * minor_1_2 + _1_3 * minor_1_3
      Some(Matrix(
         minor_1_1 / det, -minor_2_1 / det,  minor_3_1 / det,
        -minor_1_2 / det,  minor_2_2 / det, -minor_3_2 / det,
         minor_1_3 / det, -minor_2_3 / det,  minor_3_3 / det))
    }
    
    override def T: Matrix =
      Matrix(
        _1_1, _2_1, _3_1,
        _1_2, _2_2, _3_2,
        _1_3, _2_3, _3_3)
    
    override def det(implicit isSquare: V =:= V): Scalar = {
      val minor_1_1 = _2_2 * _3_3 - _2_3 * _3_2
      val minor_1_2 = _2_1 * _3_3 - _2_3 * _3_1
      val minor_1_3 = _2_1 * _3_2 - _2_2 * _3_1
      
      _1_1 * minor_1_1 - _1_2 * minor_1_2 + _1_3 * minor_1_3
    }
    
    override def trace(implicit isSquare: V =:= V): Scalar = _1_1 + _2_2 + _3_3
  }
  
  override type Matrix <: Element
  
  /** The type of elements in this $space; equivalent to the type of matrices. */
  override type Value = Matrix
  
  override type Transpose = this.type
  
  override def Transpose: this.type = this
  
  override def Row: V
  override def Col: V
  
  override def Scalar: S
  
  override def M: Int = 3
  override def N: Int = 3
  
  override def zero: Matrix = {
    val z = Scalar.zero
    apply(z, z, z,  z, z, z,  z, z, z)
  }
  
  override def unit: Matrix = {
    val z = Scalar.zero
    val u = Scalar.unit
    apply(u, z, z,  z, u, z,  z, z, u)
  }
  
  override def identity(implicit isSquare: V =:= V): Matrix = unit
  
  def apply(
      _1_1: Scalar, _1_2: Scalar, _1_3: Scalar,
      _2_1: Scalar, _2_2: Scalar, _2_3: Scalar,
      _3_1: Scalar, _3_2: Scalar, _3_3: Scalar): Matrix
  
  override def apply(entries: Scalar*): Matrix = {
    if (entries.length != 9) throw new DimensionException
    apply(entries(0), entries(1), entries(2),
          entries(3), entries(4), entries(5),
          entries(6), entries(7), entries(8))
  }
  
  def rows(row1: Row, row2: Row, row3: Row): Matrix =
    apply(row1.x, row1.y, row1.z,
          row2.x, row2.y, row2.z,
          row3.x, row3.y, row3.z)
  
  override def rows(rows: Row*): Matrix = {
    if (rows.length != 3) throw new DimensionException
    this.rows(rows(0), rows(1), rows(2))
  }
  
  def cols(col1: Col, col2: Col, col3: Col): Matrix =
    apply(col1.x, col2.x, col3.x,
          col1.y, col2.y, col3.y,
          col1.z, col2.z, col3.z)
  
  override def cols(cols: Col*): Matrix = {
    if (cols.length != 3) throw new DimensionException
    this.cols(cols(0), cols(1), cols(2))
  }
}

object F3x3 {
  /** Returns a 3 by 3 matrix space over the given field. */
  def apply(Scalar: Field)(Vector: F3[Scalar.type]): F3x3[Vector.type, Scalar.type] =
    new Space[Vector.type, Scalar.type](Scalar)(Vector)
  
  /** A generic 3 by 3 matrix space over a field.
    * 
    * @tparam V   The space of rows and columns.
    * @tparam S   The set of scalars.
    */
  private final class Space[V <: F3[S] with Singleton, S <: Field with Singleton]
      (val Scalar: S)(Vector: V)
    extends F3x3[V, S] {
    
    final class Element(
        val _1_1: Scalar, val _1_2: Scalar, val _1_3: Scalar,
        val _2_1: Scalar, val _2_2: Scalar, val _2_3: Scalar,
        val _3_1: Scalar, val _3_2: Scalar, val _3_3: Scalar)
      extends super.Element
    
    override type Matrix = Element
    
    override def Row: V = Vector
    override def Col: V = Vector
    
    override lazy val zero: Matrix = super.zero
    override lazy val unit: Matrix = super.unit
    
    override def apply(
        _1_1: Scalar, _1_2: Scalar, _1_3: Scalar,
        _2_1: Scalar, _2_2: Scalar, _2_3: Scalar,
        _3_1: Scalar, _3_2: Scalar, _3_3: Scalar): Matrix =
      new Matrix(
        _1_1, _1_2, _1_3,
        _2_1, _2_2, _2_3,
        _3_1, _3_2, _3_3)
    
    override def toString: String =
      "F3x3"+"("+ Scalar +")"+"("+ Vector +")"
  }
}