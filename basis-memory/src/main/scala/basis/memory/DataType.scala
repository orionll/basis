/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.memory

import scala.annotation.implicitNotFound
import scala.reflect.ClassTag

/** A data storage strategy. Each data type is either a [[RefType]] or a [[ValType]].
  * 
  * @tparam T   the modeled instance type.
  */
@implicitNotFound("${T} has no implicit DataType.")
sealed abstract class DataType[T]

/** Fundamental data types. Provides implicit value types for primitives and
  * tuples of value types, as well as a fallback reference type.
  * 
  * @groupname  generic     Generic data type provider
  * @groupprio  generic     -5
  * 
  * @groupname  reference   Fallback reference type provider
  * @groupprio  reference   -4
  * 
  * @groupname  aligned     Aligned primitive value type providers
  * @groupprio  aligned     -3
  * 
  * @groupname  unaligned   Unaligned primitive value type providers
  * @groupprio  unaligned   -2
  * 
  * @groupname  composite   Compsite value type providers
  * @groupprio  composite   -1
  */
object DataType extends ValTypes {
  /** Returns the implicit `DataType` of a given Scala type.
    * @group generic */
  def apply[T](implicit datatype: DataType[T]): datatype.type = datatype
}

/** A by-reference data storage strategy.
  * 
  * @tparam T   the modeled instance type.
  */
@implicitNotFound("${T} has no implicit RefType.")
sealed abstract class RefType[T] extends DataType[T]

/** Reference types.
  * 
  * @groupname  generic   Generic reference type provider
  * @groupprio  generic   -5
  */
object RefType {
  private[this] object Reference extends RefType[Nothing] {
    override def toString: String = "RefType"
  }
  
  /** Implicitly provides a `RefType` for any given Scala type.
    * @group generic */
  implicit def apply[T]: RefType[T] = Reference.asInstanceOf[RefType[T]]
}

/** A by-value data storage strategy. Value types define an isomorphism
  * between fixed-length byte sequences and class instances.
  * 
  * ==Frames==
  * A value type's `alignment`, `offset` and `size` constitute its ''frame'';
  * the alignment must evenly divide the offset and size. Value types must
  * never access more than `size - 1` bytes beyond a given address. Assume
  * proper alignment of provided addresses.
  * 
  * @tparam T   the modeled instance type.
  */
@implicitNotFound("${T} has no implicit ValType.")
abstract class ValType[T](implicit val tag: ClassTag[T]) extends DataType[T] {
  /** Returns the power-of-two alignment of this type's frame. The alignment
    * must evenly divide all addresses used to store this type's values. */
  def alignment: Long
  
  /** Returns the size in bytes of this type's frame. The type's alignmemt
    * must evenly divide this size. */
  def size: Long
  
  /** Loads an instance from a data value.
    * 
    * @param  data      the data to load from.
    * @param  address   the aligned address in `data` to load from.
    * @return the loaded instance.
    */
  def load(data: Data, address: Long): T
  
  /** Stores an instance as a data value.
    * 
    * @param  data      the data to store to.
    * @param  address   the aligned address in `data` to store to.
    * @param  value     the instance to store.
    */
  def store(data: Data, address: Long, value: T): Unit
}

/** Fundamental value types.
  * 
  * @groupname  generic     Generic value type provider
  * @groupprio  generic     -4
  * 
  * @groupname  aligned     Algined primitive value types
  * @groupprio  aligned     -3
  * 
  * @groupname  unaligned   Unaligned primitive value types
  * @groupprio  unaligned   -2
  * 
  * @groupname  composite   Composite value types
  * @groupprio  composite   -1
  */
object ValType {
  import java.lang.Math.max
  
  /** Returns the implicit `ValType` of a given Scala type.
    * @group generic */
  def apply[T](implicit valtype: ValType[T]): valtype.type = valtype
  
  /** A `Byte` value type.
    * @group aligned */
  object PackedByte extends ValType[Byte] {
    override def alignment: Long = 1L
    override def size: Long = 1L
    override def load(data: Data, address: Long): Byte =
      data.loadByte(address)
    override def store(data: Data, address: Long, value: Byte): Unit =
      data.storeByte(address, value)
    override def toString: String = "PackedByte"
  }
  
  /** An unaligned `Short` value type.
    * @group unaligned */
  object PackedShort extends ValType[Short] {
    override def alignment: Long = 1L
    override def size: Long = 2L
    override def load(data: Data, address: Long): Short =
      data.loadUnalignedShort(address)
    override def store(data: Data, address: Long, value: Short): Unit =
      data.storeUnalignedShort(address, value)
    override def toString: String = "PackedShort"
  }
  
  /** An unaligned `Int` value type.
    * @group unaligned */
  object PackedInt extends ValType[Int] {
    override def alignment: Long = 1L
    override def size: Long = 4L
    override def load(data: Data, address: Long): Int =
      data.loadUnalignedInt(address)
    override def store(data: Data, address: Long, value: Int): Unit =
      data.storeUnalignedInt(address, value)
    override def toString: String = "PackedInt"
  }
  
  /** An unaligned `Long` value type.
    * @group unaligned */
  object PackedLong extends ValType[Long] {
    override def alignment: Long = 1L
    override def size: Long = 8L
    override def load(data: Data, address: Long): Long =
      data.loadUnalignedLong(address)
    override def store(data: Data, address: Long, value: Long): Unit =
      data.storeUnalignedLong(address, value)
    override def toString: String = "PackedLong"
  }
  
  /** An unaligned `Float` value type.
    * @group unaligned */
  object PackedFloat extends ValType[Float] {
    override def alignment: Long = 1L
    override def size: Long = 4L
    override def load(data: Data, address: Long): Float =
      data.loadUnalignedFloat(address)
    override def store(data: Data, address: Long, value: Float): Unit =
      data.storeUnalignedFloat(address, value)
    override def toString: String = "PackedFloat"
  }
  
  /** An unaligned `Double` value type.
    * @group unaligned */
  object PackedDouble extends ValType[Double] {
    override def alignment: Long = 1L
    override def size: Long = 8L
    override def load(data: Data, address: Long): Double =
      data.loadUnalignedDouble(address)
    override def store(data: Data, address: Long, value: Double): Unit =
      data.storeUnalignedDouble(address, value)
    override def toString: String = "PackedDouble"
  }
  
  /** A `Boolean` value type.
    * @group aligned */
  object PackedBoolean extends ValType[Boolean] {
    override def alignment: Long = 1L
    override def size: Long = 1L
    override def load(data: Data, address: Long): Boolean =
      data.loadByte(address) == 0
    override def store(data: Data, address: Long, value: Boolean): Unit =
      data.storeByte(address, if (value) 0.toByte else -1.toByte)
    override def toString: String = "PackedBoolean"
  }
  
  /** An aligned `Short` value type.
    * @group aligned */
  object PaddedShort extends ValType[Short] {
    override def alignment: Long = 2L
    override def size: Long = 2L
    override def load(data: Data, address: Long): Short =
      data.loadShort(address)
    override def store(data: Data, address: Long, value: Short): Unit =
      data.storeShort(address, value)
    override def toString: String = "PaddedShort"
  }
  
  /** An aligned `Int` value type.
    * @group aligned */
  object PaddedInt extends ValType[Int] {
    override def alignment: Long = 4L
    override def size: Long = 4L
    override def load(data: Data, address: Long): Int =
      data.loadInt(address)
    override def store(data: Data, address: Long, value: Int): Unit =
      data.storeInt(address, value)
    override def toString: String = "PaddedInt"
  }
  
  /** An aligned `Long` value type.
    * @group aligned */
  object PaddedLong extends ValType[Long] {
    override def alignment: Long = 8L
    override def size: Long = 8L
    override def load(data: Data, address: Long): Long =
      data.loadLong(address)
    override def store(data: Data, address: Long, value: Long): Unit =
      data.storeLong(address, value)
    override def toString: String = "PaddedLong"
  }
  
  /** An aligned `Float` value type.
    * @group aligned */
  object PaddedFloat extends ValType[Float] {
    override def alignment: Long = 4L
    override def size: Long = 4L
    override def load(data: Data, address: Long): Float =
      data.loadFloat(address)
    override def store(data: Data, address: Long, value: Float): Unit =
      data.storeFloat(address, value)
    override def toString: String = "PaddedFloat"
  }
  
  /** An aligned `Double` value type.
    * @group aligned */
  object PaddedDouble extends ValType[Double] {
    override def alignment: Long = 8L
    override def size: Long = 8L
    override def load(data: Data, address: Long): Double =
      data.loadDouble(address)
    override def store(data: Data, address: Long, value: Double): Unit =
      data.storeDouble(address, value)
    override def toString: String = "PaddedDouble"
  }
  
  /** A struct for tuples with two value-typed members.
    * @group composite */
  final class Record2[T1, T2](
      field1: ValType[T1], field2: ValType[T2])
    extends ValType[(T1, T2)] {
    private[this] val offset2: Long =
      align(field1.size, field2.alignment)
    override val alignment: Long =
      max(field1.alignment, field2.alignment)
    override val size: Long =
      align(offset2 + field2.size, alignment)
    override def load(data: Data, address: Long): (T1, T2) =
      (field1.load(data, address),
       field2.load(data, address + offset2))
    override def store(data: Data, address: Long, tuple: (T1, T2)) {
      field1.store(data, address,           tuple._1)
      field2.store(data, address + offset2, tuple._2)
    }
    override def toString: String =
      "Record2"+"("+ field1 +", "+ field2 +")"
  }
  
  /** A struct for tuples with three value-typed members.
    * @group composite */
  final class Record3[T1, T2, T3](
      field1: ValType[T1], field2: ValType[T2],
      field3: ValType[T3])
    extends ValType[(T1, T2, T3)] {
    private[this] val offset2: Long =
      align(field1.size, field2.alignment)
    private[this] val offset3: Long =
      align(offset2 + field2.size, field3.alignment)
    override val alignment: Long =
      max(max(field1.alignment, field2.alignment), field3.alignment)
    override val size: Long =
      align(offset3 + field3.size, alignment)
    override def load(data: Data, address: Long): (T1, T2, T3) =
      (field1.load(data, address),
       field2.load(data, address + offset2),
       field3.load(data, address + offset3))
    override def store(data: Data, address: Long, tuple: (T1, T2, T3)) {
      field1.store(data, address,           tuple._1)
      field2.store(data, address + offset2, tuple._2)
      field3.store(data, address + offset3, tuple._3)
    }
    override def toString: String =
      "Record3"+"("+ field1 +", "+ field2 +", "+ field3 +")"
  }
  
  /** A struct for tuples with four value-typed members.
    * @group composite */
  final class Record4[T1, T2, T3, T4](
      field1: ValType[T1], field2: ValType[T2],
      field3: ValType[T3], field4: ValType[T4])
    extends ValType[(T1, T2, T3, T4)] {
    private[this] val offset2: Long =
      align(field1.size, field2.alignment)
    private[this] val offset3: Long =
      align(offset2 + field2.size, field3.alignment)
    private[this] val offset4: Long =
      align(offset3 + field3.size, field4.alignment)
    override val alignment: Long =
      max(max(max(
        field1.alignment, field2.alignment),
        field3.alignment), field4.alignment)
    override val size: Long =
      align(offset4 + field4.size, alignment)
    override def load(data: Data, address: Long): (T1, T2, T3, T4) =
      (field1.load(data, address),
       field2.load(data, address + offset2),
       field3.load(data, address + offset3),
       field4.load(data, address + offset4))
    override def store(data: Data, address: Long, tuple: (T1, T2, T3, T4)) {
      field1.store(data, address,           tuple._1)
      field2.store(data, address + offset2, tuple._2)
      field3.store(data, address + offset3, tuple._3)
      field4.store(data, address + offset4, tuple._4)
    }
    override def toString: String =
      "Record4"+"("+ field1 +", "+ field2 +", "+ field3 +", "+ field4 +")"
  }
}

/** Implicit reference types.
  * 
  * @groupname  reference   Fallback reference type provider
  * @groupprio  reference   -4
  */
private[memory] class RefTypes {
  /** Implicitly provides a fallback reference type.
    * @group reference */
  implicit def Reference[T]: RefType[T] = RefType[T]
}

/** Implicit value types for primitives and tuples.
  * 
  * @groupname  aligned     Aligned primitive value type providers
  * @groupprio  aligned     -3
  * 
  * @groupname  unaligned   Unaligned primitive value type providers
  * @groupprio  unaligned   -2
  * 
  * @groupname  composite   Compsite value type providers
  * @groupprio  composite   -1
  */
private[memory] class ValTypes extends RefTypes {
  /** Implicitly provides a `Byte` value type.
    * @group aligned */
  implicit def PackedByte: ValType.PackedByte.type = ValType.PackedByte
  
  /** Returns an unaligned `Short` value type.
    * @group unaligned */
  def PackedShort: ValType.PackedShort.type = ValType.PackedShort
  
  /** Returns an unaligned `Int` value type.
    * @group unaligned */
  def PackedInt: ValType.PackedInt.type = ValType.PackedInt
  
  /** Returns an unaligned `Long` value type.
    * @group unaligned */
  def PackedLong: ValType.PackedLong.type = ValType.PackedLong
  
  /** Returns an unaligned `Float` value type.
    * @group unaligned */
  def PackedFloat: ValType.PackedFloat.type = ValType.PackedFloat
  
  /** Returns an unaligned `Double` value type.
    * @group unaligned */
  def PackedDouble: ValType.PackedDouble.type = ValType.PackedDouble
  
  /** Implicitly provides a `Boolean` value type.
    * @group aligned */
  implicit def PackedBoolean: ValType.PackedBoolean.type = ValType.PackedBoolean
  
  /** Implicitly provides an aligned `Short` value type.
    * @group aligned */
  implicit def PaddedShort: ValType.PaddedShort.type = ValType.PaddedShort
  
  /** Implicitly provies an aligned `Int` value type.
    * @group aligned */
  implicit def PaddedInt: ValType.PaddedInt.type = ValType.PaddedInt
  
  /** Implicitly provides an aligned `Long` value type.
    * @group aligned */
  implicit def PaddedLong: ValType.PaddedLong.type = ValType.PaddedLong
  
  /** Implicitly provides an aligned `Float` value type.
    * @group aligned */
  implicit def PaddedFloat: ValType.PaddedFloat.type = ValType.PaddedFloat
  
  /** Implicitly provides an aligned `Double` value type.
    * @group aligned */
  implicit def PaddedDouble: ValType.PaddedDouble.type = ValType.PaddedDouble
  
  /** Implicitly provides a struct for tuples with two value-typed members.
    * @group composite */
  implicit def Record2[T1, T2](
      implicit field1: ValType[T1], field2: ValType[T2])
    : ValType.Record2[T1, T2] =
    new ValType.Record2(field1, field2)
  
  /** Implicitly provides a struct for tuples with three value-typed members.
    * @group composite */
  implicit def Record3[T1, T2, T3](
      implicit field1: ValType[T1], field2: ValType[T2],
               field3: ValType[T3])
    : ValType.Record3[T1, T2, T3] =
    new ValType.Record3(field1, field2, field3)
  
  /** Implicitly provides a struct for tuples with four value-typed members.
    * @group composite */
  implicit def Record4[T1, T2, T3, T4](
      implicit field1: ValType[T1], field2: ValType[T2],
               field3: ValType[T3], field4: ValType[T4])
    : ValType.Record4[T1, T2, T3, T4] =
    new ValType.Record4(field1, field2, field3, field4)
}