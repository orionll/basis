//      ____              ___
//     / __ | ___  ____  /__/___      A library of building blocks
//    / __  / __ |/ ___|/  / ___|
//   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2015 Chris Sachs
//  |_____/\_____\____/__/\____/      http://basis.reify.it

package basis.collections
package immutable

import basis._
import basis.collections.generic._
import basis.collections.mutable._
import scala.reflect._

private[collections] final class DoubleArraySeq(array: Array[Double]) extends ArraySeq[Double] {
  override def erasure: ClassTag[_] = ClassTag.Double

  override def length: Int = array.length

  override def apply(index: Int): Double = array(index)

  override def update[B >: Double](index: Int, elem: B): ArraySeq[B] = {
    if (elem.isInstanceOf[Double]) {
      if (index < 0 || index >= length) throw new IndexOutOfBoundsException(index.toString)
      val newArray = new Array[Double](length)
      java.lang.System.arraycopy(array, 0, newArray, 0, newArray.length)
      newArray(index) = elem.asInstanceOf[Double]
      new DoubleArraySeq(newArray).asInstanceOf[ArraySeq[B]]
    }
    else super.update(index, elem)
  }

  override def append[B >: Double](elem: B): ArraySeq[B] = {
    if (elem.isInstanceOf[Double]) {
      val newArray = new Array[Double](length + 1)
      java.lang.System.arraycopy(array, 0, newArray, 0, length)
      newArray(newArray.length) = elem.asInstanceOf[Double]
      new DoubleArraySeq(newArray)
    }
    else super.append(elem)
  }

  override def appendAll[B >: Double](elems: Traverser[B]): ArraySeq[B] = {
    if (elems.isInstanceOf[ArrayLike[_]] && elems.asInstanceOf[ArrayLike[_]].erasure == ClassTag.Double) {
      val xs = elems.asInstanceOf[ArrayLike[Double]]
      val n = xs.length
      val newArray = new Array[Double](length + n)
      java.lang.System.arraycopy(array, 0, newArray, 0, length)
      xs.copyToArray(0, newArray, length, n)
      new DoubleArraySeq(newArray)
    }
    else super.appendAll(elems)
  }

  override def prepend[B >: Double](elem: B): ArraySeq[B] = {
    if (elem.isInstanceOf[Double]) {
      val newArray = new Array[Double](1 + length)
      newArray(0) = elem.asInstanceOf[Double]
      java.lang.System.arraycopy(array, 0, newArray, 1, length)
      new DoubleArraySeq(newArray)
    }
    else super.prepend(elem)
  }

  override def prependAll[B >: Double](elems: Traverser[B]): ArraySeq[B] = {
    if (elems.isInstanceOf[ArrayLike[_]] && elems.asInstanceOf[ArrayLike[_]].erasure == ClassTag.Double) {
      val xs = elems.asInstanceOf[ArrayLike[Double]]
      val n = xs.length
      val newArray = new Array[Double](n + length)
      xs.copyToArray(0, newArray, 0, n)
      java.lang.System.arraycopy(array, 0, newArray, n, length)
      new DoubleArraySeq(newArray)
    }
    else super.prependAll(elems)
  }

  override def insert[B >: Double](index: Int, elem: B): ArraySeq[B] = {
    if (elem.isInstanceOf[Double]) {
      if (index < 0 || index > length) throw new IndexOutOfBoundsException(index.toString)
      val newArray = new Array[Double](length + 1)
      java.lang.System.arraycopy(array, 0, newArray, 0, index)
      newArray(index) = elem.asInstanceOf[Double]
      java.lang.System.arraycopy(array, index, newArray, index + 1, length - index)
      new DoubleArraySeq(newArray)
    }
    else super.insert(index, elem)
  }

  override def insertAll[B >: Double](index: Int, elems: Traverser[B]): ArraySeq[B] = {
    if (elems.isInstanceOf[ArrayLike[_]] && elems.asInstanceOf[ArrayLike[_]].erasure == ClassTag.Double) {
      val xs = elems.asInstanceOf[ArrayLike[Double]]
      val n = xs.length
      if (index < 0 || index > length) throw new IndexOutOfBoundsException(index.toString)
      val newArray = new Array[Double](length + n)
      java.lang.System.arraycopy(array, 0, newArray, 0, index)
      xs.copyToArray(0, newArray, index, n)
      java.lang.System.arraycopy(array, index, newArray, index + n, length - index)
      new DoubleArraySeq(newArray)
    }
    else super.insertAll(index, elems)
  }

  override def remove(index: Int): ArraySeq[Double] = {
    if (index < 0 || index >= length) throw new IndexOutOfBoundsException(index.toString)
    val newArray = new Array[Double](length - 1)
    java.lang.System.arraycopy(array, 0, newArray, 0, index)
    java.lang.System.arraycopy(array, index + 1, newArray, index, newArray.length - index)
    new DoubleArraySeq(newArray)
  }

  override def remove(index: Int, count: Int): ArraySeq[Double] = {
    if (count < 0) throw new IllegalArgumentException("negative count")
    if (index < 0) throw new IndexOutOfBoundsException(index.toString)
    if (index + count > length) throw new IndexOutOfBoundsException((index + count).toString)
    val newArray = new Array[Double](length - count)
    java.lang.System.arraycopy(array, 0, newArray, 0, index)
    java.lang.System.arraycopy(array, index + count, newArray, index, newArray.length - index)
    new DoubleArraySeq(newArray)
  }

  override def copyToArray[B >: Double](index: Int, to: Array[B], offset: Int, count: Int): Unit = {
    if (to.isInstanceOf[Array[Double]]) java.lang.System.arraycopy(array, index, to, offset, count)
    else super.copyToArray(index, to, offset, count)
  }

  override def toArray[B >: Double](implicit B: ClassTag[B]): Array[B] = {
    if (B == ClassTag.Double) {
      val newArray = new Array[Double](length)
      java.lang.System.arraycopy(array, 0, newArray, 0, newArray.length)
      newArray.asInstanceOf[Array[B]]
    }
    else super.toArray
  }

  override def iterator: Iterator[Double] = new DoubleArraySeqIterator(array)
}

private[collections] final class DoubleArraySeqIterator
    (array: Array[Double], private[this] var i: Int, n: Int)
  extends Iterator[Double] {

  def this(array: Array[Double]) = this(array, 0, array.length)

  override def isEmpty: Boolean = i >= n

  override def head: Double = {
    if (i >= n) Iterator.empty.head
    array(i)
  }

  override def step(): Unit = {
    if (i >= n) Iterator.empty.step()
    i += 1
  }

  override def dup: Iterator[Double] = new DoubleArraySeqIterator(array, i, n)
}

private[collections] final class DoubleArraySeqBuilder extends DoubleArrayBuffer with State[ArraySeq[Double]] {
  override def state: ArraySeq[Double] = toArraySeq
  override def toString: String = "ArraySeq"+"."+"Builder"+"["+"Double"+"]"+"()"
}
