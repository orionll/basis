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

/** A mutable contiguous `Short` array.
  * 
  * @author Chris Sachs
  * @since  0.0
  */
private[containers] class ShortArrayBuffer private (
    private[this] var buffer: Array[Short],
    private[this] var size: Int,
    private[this] var aliased: Boolean)
  extends ArrayBuffer[Short] with Reified {
  
  def this() = this(null, 0, true)
  
  protected override def T: TypeHint[Short] = TypeHint.Short
  
  override def length: Int = size
  
  override def apply(index: Int): Short = {
    if (index < 0 || index >= size) throw new IndexOutOfBoundsException(index.toString)
    buffer(index)
  }
  
  override def update(index: Int, elem: Short) {
    if (index < 0 || index >= size) throw new IndexOutOfBoundsException(index.toString)
    buffer(index) = elem
  }
  
  override def append(elem: Short) {
    var array = buffer
    if (aliased || size + 1 > array.length) {
      array = new Array[Short](expand(size + 1))
      if (buffer != null) java.lang.System.arraycopy(buffer, 0, array, 0, size)
      buffer = array
      aliased = false
    }
    array(size) = elem
    size += 1
  }
  
  override def appendAll(elems: Enumerator[Short]) {
    if (elems.isInstanceOf[ArrayLike[_]]) {
      val xs = elems.asInstanceOf[ArrayLike[Short]]
      val n = xs.length
      var array = buffer
      if (aliased || size + n > array.length) {
        array = new Array[Short](expand(size + n))
        if (buffer != null) java.lang.System.arraycopy(buffer, 0, array, 0, size)
        buffer = array
        aliased = false
      }
      xs.copyToArray(0, array, size, n)
      size += n
    }
    else appendAll(ArrayBuffer.coerce(elems))
  }
  
  override def prepend(elem: Short) {
    var array = buffer
    if (aliased || size + 1 > array.length) array = new Array[Short](expand(1 + size))
    if (buffer != null) java.lang.System.arraycopy(buffer, 0, array, 1, size)
    array(0) = elem
    buffer = array
    size += 1
    aliased = false
  }
  
  override def prependAll(elems: Enumerator[Short]) {
    if (elems.isInstanceOf[ArrayLike[_]]) {
      val xs = elems.asInstanceOf[ArrayLike[Short]]
      val n = xs.length
      var array = buffer
      if (aliased || size + n > array.length) array = new Array[Short](expand(n + size))
      if (buffer != null) java.lang.System.arraycopy(buffer, 0, array, n, size)
      xs.copyToArray(0, array, 0, n)
      buffer = array
      size += n
      aliased = false
    }
    else prependAll(ArrayBuffer.coerce(elems))
  }
  
  override def insert(index: Int, elem: Short) {
    if (index == size) append(elem)
    else if (index == 0) prepend(elem)
    else {
      if (index < 0 || index > size) throw new IndexOutOfBoundsException(index.toString)
      var array = buffer
      if (aliased || size + 1 > array.length) {
        array = new Array[Short](expand(size + 1))
        java.lang.System.arraycopy(buffer, 0, array, 0, index)
      }
      java.lang.System.arraycopy(buffer, index, array, index + 1, size - index)
      array(index) = elem
      buffer = array
      size += 1
      aliased = false
    }
  }
  
  override def insertAll(index: Int, elems: Enumerator[Short]) {
    if (index == size) appendAll(elems)
    else if (index == 0) prependAll(elems)
    else if (elems.isInstanceOf[ArrayLike[_]]) {
      val xs = elems.asInstanceOf[ArrayLike[Short]]
      val n = xs.length
      if (index < 0 || index > size) throw new IndexOutOfBoundsException(index.toString)
      var array = buffer
      if (aliased || size + n > array.length) {
        array = new Array[Short](expand(size + n))
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
  
  override def remove(index: Int): Short = {
    if (index < 0 || index >= size) throw new IndexOutOfBoundsException(index.toString)
    var array = buffer
    val x = array(index)
    if (size == 1) clear()
    else {
      if (aliased) {
        array = new Array[Short](expand(size - 1))
        java.lang.System.arraycopy(buffer, 0, array, 0, index)
      }
      java.lang.System.arraycopy(buffer, index + 1, array, index, size - index - 1)
      if (buffer eq array) array(size - 1) = 0.toShort
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
        array = new Array[Short](expand(size - count))
        java.lang.System.arraycopy(buffer, 0, array, 0, index)
      }
      java.lang.System.arraycopy(buffer, index + count, array, index, size - index - count)
      if (buffer eq array) java.util.Arrays.fill(array, size - count, size, 0.toShort)
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
  
  override def copy: ArrayBuffer[Short] = {
    aliased = true
    new ShortArrayBuffer(buffer, size, aliased)
  }
  
  override def copyToArray[B >: Short](index: Int, to: Array[B], offset: Int, count: Int) {
    if (to.isInstanceOf[Array[Short]]) java.lang.System.arraycopy(buffer, index, to, offset, count)
    else super.copyToArray(index, to, offset, count)
  }
  
  override def toArray[B >: Short](implicit B: scala.reflect.ClassTag[B]): Array[B] = {
    if (B == scala.reflect.ClassTag.Short) {
      val array = new Array[Short](size)
      java.lang.System.arraycopy(buffer, 0, array, 0, size)
      array.asInstanceOf[Array[B]]
    }
    else super.toArray
  }
  
  override def toArraySeq: ArraySeq[Short] = {
    if (buffer == null || size != buffer.length) {
      val array = new Array[Short](size)
      if (buffer != null) java.lang.System.arraycopy(buffer, 0, array, 0, size)
      buffer = array
    }
    aliased = true
    new ShortArraySeq(buffer)
  }
  
  override def expect(count: Int): this.type = {
    if (buffer == null || size + count > buffer.length) {
      var array = new Array[Short](size + count)
      if (buffer != null) java.lang.System.arraycopy(buffer, 0, array, 0, size)
      buffer = array
    }
    this
  }
  
  protected def defaultSize: Int = 16
  
  override def iterator: Iterator[Short] = new ShortArrayBufferIterator(this)
  
  private[this] def expand(size: Int): Int = {
    var n = (defaultSize max size) - 1
    n |= n >> 1; n |= n >> 2; n |= n >> 4; n |= n >> 8; n |= n >> 16
    n + 1
  }
}

private[containers] final class ShortArrayBufferIterator private (
    private[this] val b: ShortArrayBuffer,
    private[this] var i: Int,
    private[this] var n: Int,
    private[this] var x: Short)
  extends Iterator[Short] {
  
  def this(b: ShortArrayBuffer) = this(b, 0, b.length, if (b.length > 0) b(0) else 0.toShort)
  
  override def isEmpty: Boolean = i >= n
  
  override def head: Short = {
    if (i >= n) throw new NoSuchElementException("Head of empty iterator.")
    x
  }
  
  override def step() {
    if (i >= n) throw new UnsupportedOperationException("Empty iterator step.")
    i += 1
    n = b.length
    x = if (i < n) b(i) else 0.toShort
  }
  
  override def dup: Iterator[Short] = new ShortArrayBufferIterator(b, i, n, x)
}

private[containers] final class ShortArrayBufferBuilder extends ShortArrayBuffer {
  override type Scope = ArrayBuffer[_]
  override type State = ArrayBuffer[Short]
  override def state: ArrayBuffer[Short] = copy
  override def toString: String = "ArrayBufferBuilder"+"["+"Short"+"]"
}
