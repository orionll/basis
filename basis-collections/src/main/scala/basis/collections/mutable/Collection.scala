/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.collections
package mutable

/** A mutable collection.
  * 
  * @groupprio  Examining     -3
  * @groupprio  Traversing    -2
  * @groupprio  Classifying   -1
  */
trait Collection[A]
  extends Any
    with Mutable
    with Family[Collection[A]]
    with traversable.Collection[A]
