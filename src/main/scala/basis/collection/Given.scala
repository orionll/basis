/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.collection

trait Given[+A] extends Any with Equals with Many[A] {
  override def iterator: Next[A]
  
  override def canEqual(other: Any): Boolean = other.isInstanceOf[Given[_]]
  
  override def equals(other: Any): Boolean = other match {
    case that: Given[_] =>
      val these = this.iterator
      val those = that.iterator
      while (these.hasNext && those.hasNext) if (these.next() != those.next()) return false
      !these.hasNext && !those.hasNext
    case _ => false
  }
  
  override def hashCode: Int = {
    import scala.util.hashing.MurmurHash3._
    var h = 2588263
    var i = 0
    val iter = iterator
    while (iter.hasNext) {
      h = mix(h, iter.next().##)
      i += 1
    }
    finalizeHash(h, i)
  }
}

object Given {
  import scala.language.implicitConversions
  
  @inline implicit def ForGiven[A](self: Given[A]): ForGiven[self.Self, A] =
    new ForGiven[self.Self, A](self)
  
  final class ForGiven[From, A](val __ : Given[A]) extends AnyVal {
    import __.iterator
    
    @inline def foldLeft[B](z: B)(op: (B, A) => B): B = {
      val iter = iterator
      var result = z
      while (iter.hasNext) result = op(result, iter.next())
      result
    }
    
    @inline def reduceLeft[B >: A](op: (B, A) => B): B = {
      val iter = iterator
      if (!iter.hasNext) throw new UnsupportedOperationException
      var result: B = iter.next()
      while (iter.hasNext) result = op(result, iter.next())
      result
    }
    
    @inline def reduceLeftOption[B >: A](op: (B, A) => B): Option[B] = {
      val iter = iterator
      if (!iter.hasNext) return None
      var result: B = iter.next()
      while (iter.hasNext) result = op(result, iter.next())
      Some(result)
    }
    
    def withFilter(p: A => Boolean): Given[A] = new WithFilter(__, p)
    
    @inline def dropWhile(p: A => Boolean)(implicit make: Make[From, A]): make.What = {
      val iter = iterator
      while (iter.hasNext) {
        val x = iter.next()
        if (!p(x)) {
          make += x
          while (iter.hasNext) make += iter.next()
          return make.result
        }
      }
      make.result
    }
    
    @inline def takeWhile(p: A => Boolean)(implicit make: Make[From, A]): make.What = {
      val iter = iterator
      while (iter.hasNext) {
        val x = iter.next()
        if (p(x)) make += x
        else return make.result
      }
      make.result
    }
    
    @inline def span(p: A => Boolean)(implicit makeA: Make[From, A], makeB: Make[From, A]): (makeA.What, makeB.What) = {
      val iter = iterator
      while (iter.hasNext) {
        val x = iter.next()
        if (p(x)) makeA += x
        else {
          makeB += x
          while (iter.hasNext) makeB += iter.next()
          return (makeA.result, makeB.result)
        }
      }
      (makeA.result, makeB.result)
    }
    
    def drop(lower: Int)(implicit make: Make[From, A]): make.What = {
      val iter = iterator
      var i = 0
      while (i < lower && iter.hasNext) { iter.next(); i += 1 }
      while (iter.hasNext) make += iter.next()
      make.result
    }
    
    def take(upper: Int)(implicit make: Make[From, A]): make.What = {
      val iter = iterator
      var i = 0
      while (i < upper && iter.hasNext) { make += iter.next(); i += 1 }
      make.result
    }
    
    def slice(lower: Int, upper: Int)(implicit make: Make[From, A]): make.What = {
      val iter = iterator
      if (lower < upper) {
        var i = 0
        while (i < lower && iter.hasNext) { iter.next(); i += 1 }
        while (i < upper && iter.hasNext) { make += iter.next(); i += 1 }
      }
      make.result
    }
  }
  
  private[basis] final class WithFilter[+A](self: Given[A], p: A => Boolean) extends Given[A] {
    override def iterator: Next[A] = self.iterator filter p
    override def foreach[U](f: A => U): Unit = self.foreach(new Each.Filter(f, p))
  }
}