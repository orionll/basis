/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2013 Reify It            **
**  |_____/\_____\____/__/\____/      http://basis.reify.it             **
\*                                                                      */

package basis.control

/** An exception signalling a break in control flow.
  * 
  * @author Chris Sachs
  * @group  Imperative
  */
class Break extends Throwable(null, null, false, false) {
  override def toString: String = "Break"
}