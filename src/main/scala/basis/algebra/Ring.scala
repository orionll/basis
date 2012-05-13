/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.algebra

trait Ring {
  trait Element extends Any {
    def + (that: Vector): Vector
    
    def unary_- : Vector
    
    def - (that: Vector): Vector
    
    def * (that: Vector): Vector
  }
  
  type Vector <: Element
  
  def zero: Vector
  
  def unit: Vector
}
