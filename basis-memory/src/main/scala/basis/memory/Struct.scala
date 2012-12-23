/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://basis.reify.it             **
\*                                                                      */

package basis.memory

import basis.runtime._
import basis.util._

import scala.annotation.implicitNotFound

/** A typeclass for binary data structures.
  * 
  * ==Frames==
  * A struct's `alignment`, `offset` and `size` constitute its ''frame''; the
  * alignment must evenly divide the offset and size. Structs must never access
  * more than `size - 1` bytes beyond a given address, and should assume proper
  * alignment of provided addresses.
  * 
  * @tparam T   the struct instance type.
  */
@implicitNotFound("No available Struct for type ${T}.")
abstract class Struct[@specialized(Byte, Short, Int, Long, Float, Double, Boolean) T] extends TypeHint[T] {
  /** Returns the power-of-two alignment of this struct's frame. This alignment
    * must evenly divide all storage addresses used for values of this type. */
  def alignment: Long
  
  /** Returns the size in bytes of this struct's frame. The struct's alignment
    * must evenly divide this size. */
  def size: Long
  
  /** Loads a struct instance from data.
    * 
    * @param  data      the data to load from.
    * @param  address   the aligned address in `data` to load from.
    * @return the loaded instance.
    */
  def load(data: Data, address: Long): T
  
  /** Stores a struct instance as data.
    * 
    * @param  data      the data to store to.
    * @param  address   the aligned address in `data` to store to.
    * @param  value     the instance to store.
    */
  def store(data: Data, address: Long, value: T): Unit
}

/** A factory for builtin implicit structs.
  * 
  * @groupprio  Generic     -4
  * @groupprio  Aligned     -3
  * @groupprio  Unaligned   -2
  * @groupprio  Composite   -1
  */
object Struct {
  /** Returns the implicit struct for a given instance type.
    * @group Generic */
  def apply[T](implicit T: Struct[T]): T.type = T
  
  /** A `Byte` struct.
    * @group Aligned */
  implicit object PackedByte extends Struct[Byte] {
    override def alignment: Long = 1L
    override def size: Long = 1L
    override def load(data: Data, address: Long): Byte = data.loadByte(address)
    override def store(data: Data, address: Long, value: Byte): Unit = data.storeByte(address, value)
    override def toString: String = "PackedByte"
  }
  
  /** An unaligned `Short` struct.
    * @group Unaligned */
  object PackedShort extends Struct[Short] {
    override def alignment: Long = 1L
    override def size: Long = 2L
    override def load(data: Data, address: Long): Short = data.loadUnalignedShort(address)
    override def store(data: Data, address: Long, value: Short): Unit = data.storeUnalignedShort(address, value)
    override def toString: String = "PackedShort"
  }
  
  /** An unaligned `Int` struct.
    * @group Unaligned */
  object PackedInt extends Struct[Int] {
    override def alignment: Long = 1L
    override def size: Long = 4L
    override def load(data: Data, address: Long): Int = data.loadUnalignedInt(address)
    override def store(data: Data, address: Long, value: Int): Unit = data.storeUnalignedInt(address, value)
    override def toString: String = "PackedInt"
  }
  
  /** An unaligned `Long` struct.
    * @group Unaligned */
  object PackedLong extends Struct[Long] {
    override def alignment: Long = 1L
    override def size: Long = 8L
    override def load(data: Data, address: Long): Long = data.loadUnalignedLong(address)
    override def store(data: Data, address: Long, value: Long): Unit = data.storeUnalignedLong(address, value)
    override def toString: String = "PackedLong"
  }
  
  /** An unaligned `Float` struct.
    * @group Unaligned */
  object PackedFloat extends Struct[Float] {
    override def alignment: Long = 1L
    override def size: Long = 4L
    override def load(data: Data, address: Long): Float = data.loadUnalignedFloat(address)
    override def store(data: Data, address: Long, value: Float): Unit = data.storeUnalignedFloat(address, value)
    override def toString: String = "PackedFloat"
  }
  
  /** An unaligned `Double` struct.
    * @group Unaligned */
  object PackedDouble extends Struct[Double] {
    override def alignment: Long = 1L
    override def size: Long = 8L
    override def load(data: Data, address: Long): Double = data.loadUnalignedDouble(address)
    override def store(data: Data, address: Long, value: Double): Unit = data.storeUnalignedDouble(address, value)
    override def toString: String = "PackedDouble"
  }
  
  /** A `Boolean` struct.
    * @group Aligned */
  implicit object PackedBoolean extends Struct[Boolean] {
    override def alignment: Long = 1L
    override def size: Long = 1L
    override def load(data: Data, address: Long): Boolean = decode(data.loadByte(address))
    override def store(data: Data, address: Long, value: Boolean): Unit = data.storeByte(address, encode(value))
    @inline private[this] def decode(value: Byte): Boolean = value == 0
    @inline private[this] def encode(value: Boolean): Byte = if (value) 0.toByte else -1.toByte
    override def toString: String = "PackedBoolean"
  }
  
  /** An aligned `Short` struct.
    * @group Aligned */
  implicit object PaddedShort extends Struct[Short] {
    override def alignment: Long = 2L
    override def size: Long = 2L
    override def load(data: Data, address: Long): Short = data.loadShort(address)
    override def store(data: Data, address: Long, value: Short): Unit = data.storeShort(address, value)
    override def toString: String = "PaddedShort"
  }
  
  /** An aligned `Int` struct.
    * @group Aligned */
  implicit object PaddedInt extends Struct[Int] {
    override def alignment: Long = 4L
    override def size: Long = 4L
    override def load(data: Data, address: Long): Int = data.loadInt(address)
    override def store(data: Data, address: Long, value: Int): Unit = data.storeInt(address, value)
    override def toString: String = "PaddedInt"
  }
  
  /** An aligned `Long` struct.
    * @group Aligned */
  implicit object PaddedLong extends Struct[Long] {
    override def alignment: Long = 8L
    override def size: Long = 8L
    override def load(data: Data, address: Long): Long = data.loadLong(address)
    override def store(data: Data, address: Long, value: Long): Unit = data.storeLong(address, value)
    override def toString: String = "PaddedLong"
  }
  
  /** An aligned `Float` struct.
    * @group Aligned */
  implicit object PaddedFloat extends Struct[Float] {
    override def alignment: Long = 4L
    override def size: Long = 4L
    override def load(data: Data, address: Long): Float = data.loadFloat(address)
    override def store(data: Data, address: Long, value: Float): Unit = data.storeFloat(address, value)
    override def toString: String = "PaddedFloat"
  }
  
  /** An aligned `Double` struct.
    * @group Aligned */
  implicit object PaddedDouble extends Struct[Double] {
    override def alignment: Long = 8L
    override def size: Long = 8L
    override def load(data: Data, address: Long): Double = data.loadDouble(address)
    override def store(data: Data, address: Long, value: Double): Unit = data.storeDouble(address, value)
    override def toString: String = "PaddedDouble"
  }
  
  /** A struct for 2-tuples of struct instances.
    * @group Composite */
  final class Record2
      [@specialized(Int, Long, Double, Boolean) T1, @specialized(Int, Long, Double, Boolean) T2]
      (implicit T1: Struct[T1], T2: Struct[T2])
    extends Struct[(T1, T2)] {
    private[this] val offset2: Long = align(T1.size, T2.alignment)
    override val alignment: Long = T1.alignment max T2.alignment
    override val size: Long = align(offset2 + T2.size, alignment)
    override def load(data: Data, address: Long): (T1, T2) = (
      T1.load(data, address),
      T2.load(data, address + offset2)
    )
    override def store(data: Data, address: Long, tuple: (T1, T2)) {
      T1.store(data, address,           tuple._1)
      T2.store(data, address + offset2, tuple._2)
    }
    override def toString: String = "("+ T1 +", "+ T2 +")"
  }
  
  /** Returns a struct for a 2-tuple of struct instances.
    * @group Composite */
  implicit def Record2
      [@specialized(Int, Long, Double, Boolean) T1, @specialized(Int, Long, Double, Boolean) T2]
      (implicit T1: Struct[T1], T2: Struct[T2])
    : Record2[T1, T2] = new Record2
  
  /** A struct for 3-tuples of struct instances.
    * @group Composite */
  final class Record3[T1, T2, T3]
      (implicit T1: Struct[T1], T2: Struct[T2], T3: Struct[T3])
    extends Struct[(T1, T2, T3)] {
    private[this] val offset2: Long = align(T1.size, T2.alignment)
    private[this] val offset3: Long = align(offset2 + T2.size, T3.alignment)
    override val alignment: Long = T1.alignment max T2.alignment max T3.alignment
    override val size: Long = align(offset3 + T3.size, alignment)
    override def load(data: Data, address: Long): (T1, T2, T3) = (
      T1.load(data, address),
      T2.load(data, address + offset2),
      T3.load(data, address + offset3)
    )
    override def store(data: Data, address: Long, tuple: (T1, T2, T3)) {
      T1.store(data, address,           tuple._1)
      T2.store(data, address + offset2, tuple._2)
      T3.store(data, address + offset3, tuple._3)
    }
    override def toString: String = "("+ T1 +", "+ T2 +", "+ T3 +")"
  }
  
  /** Returns a struct for a 3-tuple of struct instances.
    * @group Composite */
  implicit def Record3[T1, T2, T3]
      (implicit T1: Struct[T1], T2: Struct[T2], T3: Struct[T3])
    : Record3[T1, T2, T3] = new Record3
  
  /** A struct for 4-tuples of struct instances.
    * @group Composite */
  final class Record4[T1, T2, T3, T4]
      (implicit T1: Struct[T1], T2: Struct[T2], T3: Struct[T3], T4: Struct[T4])
    extends Struct[(T1, T2, T3, T4)] {
    private[this] val offset2: Long = align(T1.size, T2.alignment)
    private[this] val offset3: Long = align(offset2 + T2.size, T3.alignment)
    private[this] val offset4: Long = align(offset3 + T3.size, T4.alignment)
    override val alignment: Long = T1.alignment max T2.alignment max T3.alignment max T4.alignment
    override val size: Long = align(offset4 + T4.size, alignment)
    override def load(data: Data, address: Long): (T1, T2, T3, T4) = (
      T1.load(data, address),
      T2.load(data, address + offset2),
      T3.load(data, address + offset3),
      T4.load(data, address + offset4)
    )
    override def store(data: Data, address: Long, tuple: (T1, T2, T3, T4)) {
      T1.store(data, address,           tuple._1)
      T2.store(data, address + offset2, tuple._2)
      T3.store(data, address + offset3, tuple._3)
      T4.store(data, address + offset4, tuple._4)
    }
    override def toString: String = "("+ T1 +", "+ T2 +", "+ T3 +", "+ T4 +")"
  }
  
  /** Returns a struct for a 4-tuple of struct instances.
    * @group Composite */
  implicit def Record4[T1, T2, T3, T4]
      (implicit T1: Struct[T1], T2: Struct[T2], T3: Struct[T3], T4: Struct[T4])
    : Record4[T1, T2, T3, T4] = new Record4
}
