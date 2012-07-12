/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.collection

trait Indexing[+Scope, +A] extends Any with Iterating[Scope, A] with Indexable[A] {
  override def length: Int
  
  override def apply(index: Int): A
  
  def reversed(implicit builder: Collector[Scope, A]): builder.Product = {
    var i = length - 1
    while (i >= 0) {
      builder += apply(i)
      i -= 1
    }
    builder.result
  }
  
  override def map[B](f: A => B)(implicit builder: Collector[Scope, B]): builder.Product = {
    var i = 0
    val limit = length
    while (i < limit) {
      builder += f(apply(i))
      i += 1
    }
    builder.result
  }
  
  override def flatMap[B](f: A => Incremental[B])(implicit builder: Collector[Scope, B]): builder.Product = {
    var i = 0
    val limit = length
    while (i < limit) {
      builder ++= f(apply(i))
      i += 1
    }
    builder.result
  }
  
  override def filter(p: A => Boolean)(implicit builder: Collector[Scope, A]): builder.Product = {
    var i = 0
    val limit = length
    while (i < limit) {
      val item = apply(i)
      if (p(item)) builder += item
      i += 1
    }
    builder.result
  }
  
  override def collect[B](q: PartialFunction[A, B])(implicit builder: Collector[Scope, B]): builder.Product = {
    var i = 0
    val limit = length
    while (i < limit) {
      val item = apply(i)
      if (q.isDefinedAt(item)) builder += q(item)
      i += 1
    }
    builder.result
  }
  
  override def drop(lower: Int)(implicit builder: Collector[Scope, A]): builder.Product = {
    var i = math.max(0, lower)
    val limit = length
    while (i < limit) {
      builder += apply(i)
      i += 1
    }
    builder.result
  }
  
  override def take(upper: Int)(implicit builder: Collector[Scope, A]): builder.Product = {
    var i = 0
    val limit = math.min(upper, length)
    while (i < limit) {
      builder += apply(i)
      i += 1
    }
    builder.result
  }
  
  override def slice(lower: Int, upper: Int)(implicit builder: Collector[Scope, A]): builder.Product = {
    var i = math.max(0, lower)
    val limit = math.min(upper, length)
    while (i < limit) {
      builder += apply(i)
      i += 1
    }
    builder.result
  }
  
  override def eagerly: Indexing[Scope, A] = this
}

object Indexing {
  abstract class Abstractly[+Scope, +A] extends Indexable.Abstractly[A] with Indexing[Scope, A]
  
  final class Projecting[+Scope, +A](self: Indexable[A]) extends Abstractly[Scope, A] {
    override def length: Int = self.length
    override def apply(index: Int): A = self.apply(index)
  }
}