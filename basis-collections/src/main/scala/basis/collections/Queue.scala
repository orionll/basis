/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2013 Reify It            **
**  |_____/\_____\____/__/\____/      http://basis.reify.it             **
\*                                                                      */

package basis.collections

/** A first-in first-out sequence. The `:+` operator puts an element at
  * the `foot` of a queue.
  * 
  * ==Extensions==
  * $Extensions
  * $SequentialOps
  * 
  * @author   Chris Sachs
  * @version  0.1
  * @since    0.1
  * @group    Collections
  * 
  * @groupprio  Measuring     1
  * @groupprio  Decomposing   2
  * @groupprio  Combining     3
  * @groupprio  Traversing    4
  * @groupprio  Classifying   5
  * 
  * @define collection  queue
  */
trait Queue[+A] extends Any with Equals with Family[Queue[_]] with Side[A] {
  /** Returns this $collection with an appended element.
    * @group Combining */
  def :+ [B >: A](elem: B): Queue[B]
}
