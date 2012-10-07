/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis

/** An iterable container of elements. Import [[basis.collection.ContainerOps]]
  * to extend this interface with a rich suite of optimized collection operations.
  * 
  * @author Chris Sachs
  * 
  * @define collection  container
  */
trait Container[+A] extends Any with Collection[A] {
  override type Self <: Container[A]
  
  /** Returns a new iterator over the elements of this $collection. */
  def iterator: Iterator[A]
  
  override protected def foreach[U](f: A => U) {
    val xs = iterator
    while (xs.hasNext) f(xs.next())
  }
}
