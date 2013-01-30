/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2013 Reify It            **
**  |_____/\_____\____/__/\____/      http://basis.reify.it             **
\*                                                                      */

package basis.containers

import basis.collections._
import basis.runtime._
import basis.util._

/** A mutable contiguous `Long` array.
  * 
  * @author Chris Sachs
  * @since  0.0
  */
private[containers] class LongArrayBuffer private (
    private[this] var buffer: Array[Long],
    private[this] var size: Int,
    private[this] var aliased: Boolean)
  extends ArrayBuffer[Long] with Reified {
  
  def this() = this(null, 0, true)
  
  protected override def T: TypeHint[Long] = TypeHint.Long
  
  override def length: Int = size
  
  override def apply(index: Int): Long = {
    if (index < 0 || index >= size) throw new IndexOutOfBoundsException(index.toString)
    buffer(index)
  }
  
  override def update(index: Int, elem: Long) {
    if (index < 0 || index >= size) throw new IndexOutOfBoundsException(index.toString)
    buffer(index) = elem
  }
  
  override def append(elem: Long) {
    var array = buffer
    if (aliased || size + 1 > array.length) {
      array = new Array[Long](expand(size + 1))
      if (buffer != null) java.lang.System.arraycopy(buffer, 0, array, 0, size)
      buffer = array
      aliased = false
    }
    array(size) = elem
    size += 1
  }
  
  override def appendAll(elems: Enumerator[Long]) {
    if (elems.isInstanceOf[ArrayLike[_]]) {
      val xs = elems.asInstanceOf[ArrayLike[Long]]
      val n = xs.length
      var array = buffer
      if (aliased || size + n > array.length) {
        array = new Array[Long](expand(size + n))
        if (buffer != null) java.lang.System.arraycopy(buffer, 0, array, 0, size)
        buffer = array
        aliased = false
      }
      xs.copyToArray(0, array, size, n)
      size += n
    }
    else appendAll(ArrayBuffer.coerce(elems))
  }
  
  override def prepend(elem: Long) {
    var array = buffer
    if (aliased || size + 1 > array.length) array = new Array[Long](expand(1 + size))
    if (buffer != null) java.lang.System.arraycopy(buffer, 0, array, 1, size)
    array(0) = elem
    buffer = array
    size += 1
    aliased = false
  }
  
  override def prependAll(elems: Enumerator[Long]) {
    if (elems.isInstanceOf[ArrayLike[_]]) {
      val xs = elems.asInstanceOf[ArrayLike[Long]]
      val n = xs.length
      var array = buffer
      if (aliased || size + n > array.length) array = new Array[Long](expand(n + size))
      if (buffer != null) java.lang.System.arraycopy(buffer, 0, array, n, size)
      xs.copyToArray(0, array, 0, n)
      buffer = array
      size += n
      aliased = false
    }
    else prependAll(ArrayBuffer.coerce(elems))
  }
  
  override def insert(index: Int, elem: Long) {
    if (index == size) append(elem)
    else if (index == 0) prepend(elem)
    else {
      if (index < 0 || index > size) throw new IndexOutOfBoundsException(index.toString)
      var array = buffer
      if (aliased || size + 1 > array.length) {
        array = new Array[Long](expand(size + 1))
        java.lang.System.arraycopy(buffer, 0, array, 0, index)
      }
      java.lang.System.arraycopy(buffer, index, array, index + 1, size - index)
      array(index) = elem
      buffer = array
      size += 1
      aliased = false
    }
  }
  
  override def insertAll(index: Int, elems: Enumerator[Long]) {
    if (index == size) appendAll(elems)
    else if (index == 0) prependAll(elems)
    else if (elems.isInstanceOf[ArrayLike[_]]) {
      val xs = elems.asInstanceOf[ArrayLike[Long]]
      val n = xs.length
      if (index < 0 || index > size) throw new IndexOutOfBoundsException(index.toString)
      var array = buffer
      if (aliased || size + n > array.length) {
        array = new Array[Long](expand(size + n))
        java.lang.System.arraycopy(buffer, 0, array, 0, index)
      }
      java.lang.System.arraycopy(buffer, index, array, index + n, size - index)
      xs.copyToArray(0, array, index, n)
      buffer = array
      size += n
      aliased = false
    }
    else insertAll(index, ArrayBuffer.coerce(elems))
  }
  
  override def remove(index: Int): Long = {
    if (index < 0 || index >= size) throw new IndexOutOfBoundsException(index.toString)
    var array = buffer
    val x = array(index)
    if (size == 1) clear()
    else {
      if (aliased) {
        array = new Array[Long](expand(size - 1))
        java.lang.System.arraycopy(buffer, 0, array, 0, index)
      }
      java.lang.System.arraycopy(buffer, index + 1, array, index, size - index - 1)
      if (buffer eq array) array(size - 1) = 0L
      size -= 1
      buffer = array
      aliased = false
    }
    x
  }
  
  override def remove(index: Int, count: Int) {
    if (count < 0) throw new IllegalArgumentException("negative count")
    if (index < 0) throw new IndexOutOfBoundsException(index.toString)
    if (index + count > size) throw new IndexOutOfBoundsException((index + count).toString)
    if (size == count) clear()
    else {
      var array = buffer
      if (aliased) {
        array = new Array[Long](expand(size - count))
        java.lang.System.arraycopy(buffer, 0, array, 0, index)
      }
      java.lang.System.arraycopy(buffer, index + count, array, index, size - index - count)
      if (buffer eq array) java.util.Arrays.fill(array, size - count, size, 0L)
      size -= count
      buffer = array
      aliased = false
    }
  }
  
  override def clear() {
    aliased = true
    size = 0
    buffer = null
  }
  
  override def copy: ArrayBuffer[Long] = {
    aliased = true
    new LongArrayBuffer(buffer, size, aliased)
  }
  
  override def copyToArray[B >: Long](index: Int, to: Array[B], offset: Int, count: Int) {
    if (to.isInstanceOf[Array[Long]]) java.lang.System.arraycopy(buffer, index, to, offset, count)
    else super.copyToArray(index, to, offset, count)
  }
  
  override def toArray[B >: Long](implicit B: scala.reflect.ClassTag[B]): Array[B] = {
    if (B == scala.reflect.ClassTag.Long) {
      val array = new Array[Long](size)
      java.lang.System.arraycopy(buffer, 0, array, 0, size)
      array.asInstanceOf[Array[B]]
    }
    else super.toArray
  }
  
  override def toArraySeq: ArraySeq[Long] = {
    if (buffer == null || size != buffer.length) {
      val array = new Array[Long](size)
      if (buffer != null) java.lang.System.arraycopy(buffer, 0, array, 0, size)
      buffer = array
    }
    aliased = true
    new LongArraySeq(buffer)
  }
  
  override def expect(count: Int): this.type = {
    if (buffer == null || size + count > buffer.length) {
      var array = new Array[Long](size + count)
      if (buffer != null) java.lang.System.arraycopy(buffer, 0, array, 0, size)
      buffer = array
    }
    this
  }
  
  protected def defaultSize: Int = 16
  
  override def iterator: Iterator[Long] = new LongArrayBufferIterator(this)
  
  private[this] def expand(size: Int): Int = {
    var n = (defaultSize max size) - 1
    n |= n >> 1; n |= n >> 2; n |= n >> 4; n |= n >> 8; n |= n >> 16
    n + 1
  }
}

private[containers] final class LongArrayBufferIterator private (
    private[this] val b: LongArrayBuffer,
    private[this] var i: Int,
    private[this] var n: Int,
    private[this] var x: Long)
  extends Iterator[Long] {
  
  def this(b: LongArrayBuffer) = this(b, 0, b.length, if (b.length > 0) b(0) else 0L)
  
  override def isEmpty: Boolean = i >= n
  
  override def head: Long = {
    if (i >= n) throw new NoSuchElementException("Head of empty iterator.")
    x
  }
  
  override def step() {
    if (i >= n) throw new UnsupportedOperationException("Empty iterator step.")
    i += 1
    n = b.length
    x = if (i < n) b(i) else 0L
  }
  
  override def dup: Iterator[Long] = new LongArrayBufferIterator(b, i, n, x)
}

private[containers] final class LongArrayBufferBuilder extends LongArrayBuffer {
  override type Scope = ArrayBuffer[_]
  override type State = ArrayBuffer[Long]
  override def state: ArrayBuffer[Long] = copy
  override def toString: String = "ArrayBufferBuilder"+"["+"Long"+"]"
}
