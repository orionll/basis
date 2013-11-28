//      ____              ___
//     / __ | ___  ____  /__/___      A library of building blocks
//    / __  / __ |/ ___|/  / ___|
//   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2014 Reify It
//  |_____/\_____\____/__/\____/      http://basis.reify.it

package basis.memory

import basis.util._

trait Loader {
  /** Returns the internal byte order.
    * @group General */
  def endian: Endianness

  /** Returns `true` if this supports volatile semantics; returns `false`
    * if volatile operations do not guarantee coherency.
    * @group General */
  def isCoherent: Boolean = false

  /** Returns `true` if this can load the next `offset` addresses.
    * @group General */
  def canLoad(offset: Long): Boolean

  /** Loads a single byte.
    *
    * @param  offset  the relative address to load.
    * @return the loaded `Byte` value.
    * @group  Aligned
    */
  def loadByte(offset: Long): Byte

  /** Loads a 2-byte `endian` ordered word as a native-endian `Short` value.
    * Truncates `offset` to 2-byte alignment.
    *
    * @param  offset  the 2-byte aligned relative address to load.
    * @return the loaded `Short` value.
    * @group  Aligned
    */
  def loadShort(offset: Long): Short =
    loadUnalignedShort(offset & -2L)

  /** Loads a 4-byte `endian` ordered word as a native-endian `Int` value.
    * Truncates `offset` to 4-byte alignment.
    *
    * @param  offset  the 4-byte aligned relative address to load.
    * @return the loaded `Int` value.
    * @group  Aligned
    */
  def loadInt(offset: Long): Int =
    loadUnalignedInt(offset & -4L)

  /** Loads an 8-byte `endian` ordered word as a native-endian `Long` value.
    * Truncates `offset` to 8-byte alignment.
    *
    * @param  offset  the 8-byte aligned relative address to load.
    * @return the loaded `Long` value.
    * @group  Aligned
    */
  def loadLong(offset: Long): Long =
    loadUnalignedLong(offset & -8L)

  /** Loads a 4-byte `endian` ordered word as a native-endian `Float` value.
    * Truncates `offset` to 4-byte alignment.
    *
    * @param  offset  the 4-byte aligned relative address to load.
    * @return the loaded `Float` value.
    * @group  Aligned
    */
  def loadFloat(offset: Long): Float =
    loadUnalignedFloat(offset & -4L)

  /** Loads an 8-byte `endian` ordered word as a native-endian `Double` value.
    * Truncates `offset` to 8-byte alignment.
    *
    * @param  offset  the 8-byte aligned relative address to load.
    * @return the loaded `Double` value.
    * @group  Aligned
    */
  def loadDouble(offset: Long): Double =
    loadUnalignedDouble(offset & -8L)

  /** Loads a 2-byte `endian` ordered word as a native-endian `Short` value.
    *
    * @param  offset  the unaligned relative address to load.
    * @return the loaded `Short` value.
    * @group  Unaligned
    */
  def loadUnalignedShort(offset: Long): Short = {
    if (endian eq BigEndian) {
      ((loadByte(offset)              << 8) |
       (loadByte(offset + 1L) & 0xFF)).toShort
    }
    else if (endian eq LittleEndian) {
      ((loadByte(offset)      & 0xFF)       |
       (loadByte(offset + 1L)         << 8)).toShort
    }
    else throw new MatchError(endian)
  }

  /** Loads a 4-byte `endian` ordered word as a native-endian `Int` value.
    *
    * @param  offset  the unaligned relative address to load.
    * @return the loaded `Int` value.
    * @group  Unaligned
    */
  def loadUnalignedInt(offset: Long): Int = {
    if (endian eq BigEndian) {
       (loadByte(offset)              << 24) |
      ((loadByte(offset + 1L) & 0xFF) << 16) |
      ((loadByte(offset + 2L) & 0xFF) <<  8) |
       (loadByte(offset + 3L) & 0xFF)
    }
    else if (endian eq LittleEndian) {
       (loadByte(offset)      & 0xFF)        |
      ((loadByte(offset + 1L) & 0xFF) <<  8) |
      ((loadByte(offset + 2L) & 0xFF) << 16) |
       (loadByte(offset + 3L)         << 24)
    }
    else throw new MatchError(endian)
  }

  /** Loads an 8-byte `endian` ordered word as a native-endian `Long` value.
    *
    * @param  offset  the unaligned relative address to load.
    * @return the loaded `Long` value.
    * @group  Unaligned
    */
  def loadUnalignedLong(offset: Long): Long = {
    if (endian eq BigEndian) {
       (loadByte(offset).toLong              << 56) |
      ((loadByte(offset + 1L) & 0xFF).toLong << 48) |
      ((loadByte(offset + 2L) & 0xFF).toLong << 40) |
      ((loadByte(offset + 3L) & 0xFF).toLong << 32) |
      ((loadByte(offset + 4L) & 0xFF).toLong << 24) |
      ((loadByte(offset + 5L) & 0xFF).toLong << 16) |
      ((loadByte(offset + 6L) & 0xFF).toLong <<  8) |
       (loadByte(offset + 7L) & 0xFF).toLong
    }
    else if (endian eq LittleEndian) {
       (loadByte(offset)      & 0xFF).toLong        |
      ((loadByte(offset + 1L) & 0xFF).toLong <<  8) |
      ((loadByte(offset + 2L) & 0xFF).toLong << 16) |
      ((loadByte(offset + 3L) & 0xFF).toLong << 24) |
      ((loadByte(offset + 4L) & 0xFF).toLong << 32) |
      ((loadByte(offset + 5L) & 0xFF).toLong << 40) |
      ((loadByte(offset + 6L) & 0xFF).toLong << 48) |
       (loadByte(offset + 7L).toLong         << 56)
    }
    else throw new MatchError(endian)
  }

  /** Loads a 4-byte `endian` ordered word as a native-endian `Float` value.
    *
    * @param  offset  the unaligned relative address to load.
    * @return the loaded `Float` value.
    * @group  Unaligned
    */
  def loadUnalignedFloat(offset: Long): Float =
    loadUnalignedInt(offset).toFloatBits

  /** Loads an 8-byte `endian` ordered word as a native-endian `Double` value.
    *
    * @param  offset  the unaligned relative address to load.
    * @return the loaded `Double` value.
    * @group  Unaligned
    */
  def loadUnalignedDouble(offset: Long): Double =
    loadUnalignedLong(offset).toDoubleBits

  /** Loads a single byte with volatile semantics if `isCoherent`.
    *
    * @param  offset  the relative address to load.
    * @return the loaded `Byte` value.
    * @group  Volatile
    */
  def loadVolatileByte(offset: Long): Byte =
    loadByte(offset)

  /** Loads a 2-byte `endian` ordered word as a native-endian `Short` value with
    * volatile semantics if `isCoherent`. Truncates `offset` to 2-byte alignment.
    *
    * @param  offset  the 2-byte aligned relative address to load.
    * @return the loaded `Short` value.
    * @group  Volatile
    */
  def loadVolatileShort(offset: Long): Short =
    loadShort(offset)

  /** Loads a 4-byte `endian` ordered word as a native-endian `Int` value with
    * volatile semantics if `isCoherent`. Truncates `offset` to 4-byte alignment.
    *
    * @param  offset  the 4-byte aligned relative address to load.
    * @return the loaded `Int` value.
    * @group  Volatile
    */
  def loadVolatileInt(offset: Long): Int =
    loadInt(offset)

  /** Loads an 8-byte `endian` ordered word as a native-endian `Long` value with
    * volatile semantics if `isCoherent`. Truncates `offset` to 8-byte alignment.
    *
    * @param  offset  the 8-byte aligned relative address to load.
    * @return the loaded `Long` value.
    * @group  Volatile
    */
  def loadVolatileLong(offset: Long): Long =
    loadLong(offset)

  /** Loads a 4-byte `endian` ordered word as a native-endian `Float` value with
    * volatile semantics if `isCoherent`. Truncates `offset` to 4-byte alignment.
    *
    * @param  offset  the 4-byte aligned relative address to load.
    * @return the loaded `Float` value.
    * @group  Volatile
    */
  def loadVolatileFloat(offset: Long): Float =
    loadFloat(offset)

  /** Loads an 8-byte `endian` ordered word as a native-endian `Double` value with
    * volatile semantics if `isCoherent`. Truncates `offset` to 8-byte alignment.
    *
    * @param  offset  the 8-byte aligned relative address to load.
    * @return the loaded `Double` value.
    * @group  Volatile
    */
  def loadVolatileDouble(offset: Long): Double =
    loadDouble(offset)

  /** Loads an instance from a struct value.
    *
    * @tparam T       the instance type to load.
    * @param  offset  the aligned relative address to load.
    * @param  T       the implicit struct type to load.
    * @return the loaded instance.
    * @group  Compound
    */
  def load[T](offset: Long)(implicit T: Struct[T]): T =
    T.load(this, offset)

  /** Loads a sequence of struct values into a new array.
    *
    * @tparam T       the instance type to load.
    * @param  offset  the aligned relative address to load.
    * @param  count   the number of values to load.
    * @param  T       the implicit struct type to load.
    * @return the loaded array of instance values.
    * @group  Aggregate
    */
  def loadArray[T](offset: Long, count: Int)(implicit T: Struct[T]): Array[T] = {
    val array = T.newArray(count)
    loadToArray[T](offset, array, 0, count)
    array
  }

  /** Copies a sequence of loaded struct values to an array slice.
    *
    * @tparam T       the instance type to load.
    * @param  offset  the aligned relative address to load.
    * @param  array   the array to copy to.
    * @param  start   the offset to copy to in the array.
    * @param  count   the number of values to copy.
    * @param  T       the implicit struct type to load.
    * @group  Aggregate
    */
  def loadToArray[T](offset: Long, array: Array[T], start: Int, count: Int)(implicit T: Struct[T]): Unit = {
    val end = start + count
    var p = offset
    var i = start
    while (i < end) {
      array(i) = T.load(this, p)
      p += T.size
      i += 1
    }
  }
}