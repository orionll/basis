/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.containers
package immutable

import basis.collections._
import basis.util._

private[containers] final class ByteArraySeq(array: Array[Byte]) extends ArraySeq[Byte] {
  override def isEmpty: Boolean = array.length == 0
  
  override def length: Int = array.length
  
  override def apply(index: Int): Byte = array(index)
  
  override def update[B >: Byte](index: Int, value: B): ArraySeq[B] = value match {
    case value: Byte =>
      val newArray = array.clone
      newArray(index) = value
      new ByteArraySeq(newArray)
    case _ => super.update(index, value)
  }
  
  override def insert[B >: Byte](index: Int, value: B): ArraySeq[B] = value match {
    case value: Byte =>
      val newArray = new Array[Byte](array.length + 1)
      java.lang.System.arraycopy(array, 0, newArray, 0, index)
      newArray(index) = value
      java.lang.System.arraycopy(array, index, newArray, index + 1, array.length - index)
      new ByteArraySeq(newArray)
    case _ => super.insert(index, value)
  }
  
  override def remove(index: Int): ArraySeq[Byte] = {
    val newArray = new Array[Byte](array.length - 1)
    java.lang.System.arraycopy(array, 0, newArray, 0, index)
    java.lang.System.arraycopy(array, index + 1, newArray, index, newArray.length - index)
    new ByteArraySeq(newArray)
  }
  
  override def :+ [B >: Byte](value: B): ArraySeq[B] = value match {
    case value: Byte =>
      val newArray = new Array[Byte](array.length + 1)
      java.lang.System.arraycopy(array, 0, newArray, 0, array.length)
      newArray(newArray.length) = value
      new ByteArraySeq(newArray)
    case _ => super.:+(value)
  }
  
  override def +: [B >: Byte](value: B): ArraySeq[B] = value match {
    case value: Byte =>
      val newArray = new Array[Byte](array.length + 1)
      newArray(0) = value
      java.lang.System.arraycopy(array, 0, newArray, 1, array.length)
      new ByteArraySeq(newArray)
    case _ => super.+:(value)
  }
}

private[containers] final class ByteArraySeqBuilder extends Builder[Any, Byte, ArraySeq[Byte]] {
  private[this] var array: Array[Byte] = _
  
  private[this] var aliased: Boolean = true
  
  private[this] var length: Int = 0
  
  private[this] def expand(base: Int, size: Int): Int = {
    var n = (base max size) - 1
    n |= n >> 1; n |= n >> 2; n |= n >> 4; n |= n >> 8; n |= n >> 16
    n + 1
  }
  
  private[this] def resize(size: Int) {
    val newArray = new Array[Byte](size)
    if (array != null) java.lang.System.arraycopy(array, 0, newArray, 0, array.length min size)
    array = newArray
  }
  
  private[this] def prepare(size: Int) {
    if (aliased || size > array.length) {
      resize(expand(16, size))
      aliased = false
    }
  }
  
  override def += (value: Byte): this.type = {
    prepare(length + 1)
    array(length) = value
    length += 1
    this
  }
  
  override def expect(count: Int): this.type = {
    if (array == null || length + count > array.length) {
      resize(length + count)
      aliased = false
    }
    this
  }
  
  override def state: ArraySeq[Byte] = {
    if (array == null || length != array.length) resize(length)
    aliased = true
    new ByteArraySeq(array)
  }
  
  override def clear() {
    array = null
    aliased = true
    length = 0
  }
}
