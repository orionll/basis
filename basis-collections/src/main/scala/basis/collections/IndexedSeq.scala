/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.collections

import basis.util._

/** An indexed sequence of elements.
  * 
  * @groupprio  Examining   -3
  * @groupprio  Iterating   -2
  * @groupprio  Traversing  -1
  */
trait IndexedSeq[@specialized(Byte, Short, Int, Long, Float, Double, Boolean) +A]
  extends Any with Family[IndexedSeq[A]] with Seq[A] {
  
  /** Returns the element at `index`.
    * @group Examining */
  def apply(index: Int): A
  
  override def isEmpty: Boolean = length == 0
  
  override def iterator: Iterator[A] = new IndexedSeqIterator(this, 0, length)
  
  protected override def foreach[@specialized(Unit) U](f: A => U) {
    var i = 0
    val n = length
    while (i < n) {
      f(apply(i))
      i += 1
    }
  }
}

private[collections] final class IndexedSeqIterator[@specialized(Byte, Short, Int, Long, Float, Double, Boolean) +A]
    (xs: IndexedSeq[A], from: Int, until: Int)
  extends Iterator[A] {
  
  private[this] var upper: Int = (0 max upper) min xs.length
  private[this] var lower: Int = (0 max lower) min upper
  private[this] var index: Int = lower
  
  override def isEmpty: Boolean = index >= upper
  
  override def head: A = if (isEmpty) Done.head else xs(index)
  
  override def step(): Unit = if (isEmpty) Done.step() else index += 1
  
  override def dup: Iterator[A] = new IndexedSeqIterator(xs, index, upper)
}
