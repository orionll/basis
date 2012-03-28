/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.algebra

import basis.memory._
import basis.util.MurmurHash._

/** An integer modeled by a `Long` value.
  * 
  * @author Chris Sachs
  * 
  * @constructor  Creates an `Integer` value from a `Long` value.
  * @param  value   The `Long` value.
  * 
  * @define Element   Integer
  * @define element   `Integer` value
  * @define scalar    `Integer` value
  */
final class Integer(protected val value: Long) extends OrderedRing[Integer] {
  def + (that: Integer): Integer = new Integer(value + that.value)
  
  def + (n: Int): Integer = new Integer(value + n)
  
  def unary_- : Integer = new Integer(-value)
  
  def - (that: Integer): Integer = new Integer(value - that.value)
  
  def - (n: Int): Integer = new Integer(value - n)
  
  def * (that: Integer): Integer = new Integer(value * that.value)
  
  def * (n: Int): Integer = new Integer(value * n)
  
  def pow(n: Int): Integer = new Integer(math.pow(value, n).toLong)
  
  /** Returns the greatest common divisor of this $element and another $element. */
  def gcd(that: Integer): Integer = {
    var a = math.abs(value)
    var b = math.abs(that.value)
    while (b != 0L) {
      val t = b
      b = a % b
      a = t
    }
    new Integer(a)
  }
  
  def abs: Integer = if (value >= 0L) this else -this
  
  def min(that: Integer): Integer = if (value <= that.value) this else that
  
  def max(that: Integer): Integer = if (value >= that.value) this else that
  
  def < (that: Integer): Boolean = value < that.value
  
  def <= (that: Integer): Boolean = value <= that.value
  
  def >= (that: Integer): Boolean = value >= that.value
  
  def > (that: Integer): Boolean = value > that.value
  
  override def equals(other: Any): Boolean = other match {
    case that: Integer => value == that.value
    case _ => false
  }
  
  override def hashCode: Int = hash(value)
  
  override def toString: String = value.toString
  
  /** Converts this `Integer` value to an `Int` value. */
  def toInt: Int = value.toInt
  
  /** Returns the `Long` value of this `Integer` value. */
  def toLong: Long = value
  
  /** Converts this `Integer` value to a `Float` value. */
  def toFloat: Float = value.toFloat
  
  /** Converts this `Integer` value to a `Double` value. */
  def toDouble: Double = value.toDouble
}

/** Contains factory methods for `Integer` values. Serves as a struct for `Integer` values.
  * Contains implicit conversions between `Long` values and `Integer` values. */
object Integer extends Struct1[Long, Integer] {
  /** The additive identity of the `Integer` ring. */
  val Zero: Integer = new Integer(0L)
  
  /** The multiplicative identity of the `Integer` ring. */
  val One: Integer = new Integer(1L)
  
  def apply(value: Long): Integer = new Integer(value)
  
  def unapply(integer: Integer): Some[Long] = Some(integer.value)
  
  /** Implicitly converts a `Long` value to an `Integer` value. */
  implicit def box(value: Long): Integer = new Integer(value)
  
  /** Implicitly converts an `Integer` value to a `Long` value. */
  implicit def unbox(integer: Integer): Long = integer.value
  
  def load(data: Data, address: Long): Integer =
    new Integer(data.loadLong(address))
  
  def store(data: Data, address: Long, integer: Integer): Unit =
    data.storeLong(address, integer.value)
  
  implicit def struct: this.type = this
  
  override def toString: String = "Integer"
}
