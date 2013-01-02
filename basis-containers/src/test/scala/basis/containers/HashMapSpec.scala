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

class HashMapSpec
  extends FunSpec
    with ShouldMatchers
    with MapFactoryBehaviors
    with MapBehaviors {
  
  override def suiteName = "HashMap specification"
  
  it should behave like GenericMapFactory(HashMap)
  
  it should behave like TraversableMap(HashMap)
}
