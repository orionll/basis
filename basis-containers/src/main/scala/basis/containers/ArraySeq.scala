/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.containers

import basis.collections._
import basis.util._

/** A contiguous array.
  * 
  * @groupprio  Examining     -5
  * @groupprio  Copying       -4
  * @groupprio  Iterating     -3
  * @groupprio  Traversing    -2
  * @groupprio  Classifying   -1
  * 
  * @define collection  array sequence
  */
abstract class ArraySeq[@specialized(Specializable.Primitives) +A]
  extends Equals with Family[ArraySeq[A]] with IndexedSeq[A] {
  
  override def apply(index: Int): A
  
  /** Copies elementes from this $collection to an array slice.
    * 
    * @param  xs      the destination array.
    * @param  start   the offset to copy to in the destination array.
    * @param  count   the maximum number of elements to copy.
    * @group  Copying
    */
  def copyToArray[B >: A](xs: Array[B], start: Int, count: Int) {
    var i = 0
    var j = start
    val n = count min (xs.length - start) min length
    while (i < n) {
      xs(j) = this(i)
      i += 1
      j += 1
    }
  }
  
  /** Copies elementes from this $collection to an array offset.
    * 
    * @param  xs      the destination array.
    * @param  start   the offset to copy to in the destination array.
    * @group  Copying
    */
  def copyToArray[B >: A](xs: Array[B], start: Int) {
    var i = 0
    var j = start
    val n = (xs.length - start) min length
    while (i < n) {
      xs(j) = this(i)
      i += 1
      j += 1
    }
  }
  
  /** Copies elementes from this $collection to an array.
    * 
    * @param  xs  the destination array.
    * @group  Copying
    */
  def copyToArray[B >: A](xs: Array[B]) {
    var i = 0
    val n = xs.length min length
    while (i < n) {
      xs(i) = this(i)
      i += 1
    }
  }
  
  protected override def stringPrefix: String = "ArraySeq"
}

object ArraySeq extends SeqFactory[ArraySeq] {
  import scala.reflect.ClassTag
  
  implicit override def Builder[A](implicit A: ClassTag[A])
    : Builder[Any, A] { type State = ArraySeq[A] } = (A match {
    case ClassTag.Byte    => new ByteArraySeqBuilder
    case ClassTag.Short   => new ShortArraySeqBuilder
    case ClassTag.Int     => new IntArraySeqBuilder
    case ClassTag.Long    => new LongArraySeqBuilder
    case ClassTag.Float   => new FloatArraySeqBuilder
    case ClassTag.Double  => new DoubleArraySeqBuilder
    case ClassTag.Boolean => new BitArraySeqBuilder
    case _                => new RefArraySeqBuilder[A]
  }).asInstanceOf[Builder[Any, A] { type State = ArraySeq[A] }]
  
  override def toString: String = "ArraySeq"
}
