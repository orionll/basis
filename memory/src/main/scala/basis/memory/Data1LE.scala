//      ____              ___
//     / __ | ___  ____  /__/___      A library of building blocks
//    / __  / __ |/ ___|/  / ___|
//   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2014 Reify It
//  |_____/\_____\____/__/\____/      http://basis.reify.it

package basis.memory

import basis.util._

/** Little-endian data backed by a `Byte` array.
  *
  * @author Chris Sachs
  * @since  0.0
  */
private[memory] final class Data1LE(override val words: Array[Byte]) extends Data1 with DataLE {
  override def endian: LittleEndian.type = LittleEndian

  override def copy(size: Long): Data1LE = {
    if (size < 0L || size > Data1LE.MaxSize) throw new DataSizeException(size.toString)
    val words = new Array[Byte](size.toInt)
    java.lang.System.arraycopy(this.words, 0, words, 0, this.words.length min words.length)
    new Data1LE(words)
  }

  override def loadByte(address: Long): Byte =
    words(address.toInt)

  override def storeByte(address: Long, value: Byte): Unit =
    words(address.toInt) = value

  override def loadUnalignedShort(address: Long): Short = {
    val i = address.toInt
    ((words(i)     & 0xFF)       |
     (words(i + 1)         << 8)).toShort
  }

  override def storeUnalignedShort(address: Long, value: Short): Unit = {
    val i = address.toInt
    words(i)     =  value.toByte
    words(i + 1) = (value >> 8).toByte
  }

  override def loadUnalignedInt(address: Long): Int = {
    val i = address.toInt
     (words(i)     & 0xFF)        |
    ((words(i + 1) & 0xFF) <<  8) |
    ((words(i + 2) & 0xFF) << 16) |
     (words(i + 3)         << 24)
  }

  override def storeUnalignedInt(address: Long, value: Int): Unit = {
    val i = address.toInt
    words(i)     =  value.toByte
    words(i + 1) = (value >>  8).toByte
    words(i + 2) = (value >> 16).toByte
    words(i + 3) = (value >> 24).toByte
  }

  override def loadUnalignedLong(address: Long): Long = {
    val i = address.toInt
     (words(i)     & 0xFF).toLong        |
    ((words(i + 1) & 0xFF).toLong <<  8) |
    ((words(i + 2) & 0xFF).toLong << 16) |
    ((words(i + 3) & 0xFF).toLong << 24) |
    ((words(i + 4) & 0xFF).toLong << 32) |
    ((words(i + 5) & 0xFF).toLong << 40) |
    ((words(i + 6) & 0xFF).toLong << 48) |
     (words(i + 7).toLong         << 56)
  }

  override def storeUnalignedLong(address: Long, value: Long): Unit = {
    val i = address.toInt
    words(i)     =  value.toByte
    words(i + 1) = (value >>  8).toByte
    words(i + 2) = (value >> 16).toByte
    words(i + 3) = (value >> 24).toByte
    words(i + 4) = (value >> 32).toByte
    words(i + 5) = (value >> 40).toByte
    words(i + 6) = (value >> 48).toByte
    words(i + 7) = (value >> 56).toByte
  }

  override def toString: String = "Data1LE"+"("+ size +")"
}

/** An allocator for little-endian data backed by a `Byte` array. */
private[memory] object Data1LE extends Allocator with (Long => Data1LE) {
  override def MaxSize: Long = Int.MaxValue.toLong

  override def Endian: Endianness = LittleEndian

  override def alloc[T](count: Long)(implicit T: Struct[T]): Data1LE = apply(T.size * count)

  override def apply(size: Long): Data1LE = {
    if (size < 0L || size > MaxSize) throw new DataSizeException(size.toString)
    val words = new Array[Byte](size.toInt)
    new Data1LE(words)
  }

  override def toString: String = "Data1LE"
}