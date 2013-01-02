/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2013 Reify It            **
**  |_____/\_____\____/__/\____/      http://basis.reify.it             **
\*                                                                      */

package basis.containers

import basis.collections._

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers

class ArrayMapSpec
  extends FunSpec
    with ShouldMatchers
    with MapFactoryBehaviors
    with MapBehaviors {
  
  override def suiteName = "ArrayMap specification"
  
  it should behave like GenericMapFactory(ArrayMap)
  
  it should behave like TraversableMap(ArrayMap)
}
