//      ____              ___
//     / __ | ___  ____  /__/___      A library of building blocks
//    / __  / __ |/ ___|/  / ___|
//   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2015 Chris Sachs
//  |_____/\_____\____/__/\____/      http://basis.reify.it

package basis.collections
package sequential

import basis._
import scala.reflect.macros._

private[sequential] abstract class IteratorMacros(override val c: blackbox.Context) extends TraverserMacros(c) {
  import c.{ Expr, WeakTypeTag }
  import c.universe.{ Traverser => _, _ }

  override def these: Expr[Iterator[_]]

  def foreach[A, U](f: Expr[A => U]): Expr[Unit] = Expr[Unit](q"""{
    val xs = $these
    while (!xs.isEmpty) {
      $f(xs.head)
      xs.step()
    }
  }""")

  def foldLeft[A, B : WeakTypeTag](z: Expr[B])(op: Expr[(B, A) => B]): Expr[B] = Expr[B](q"""{
    val xs = $these
    var r = $z
    while (!xs.isEmpty) {
      r = $op(r, xs.head)
      xs.step()
    }
    r
  }""")

  def reduceLeft[A, B >: A](op: Expr[(B, A) => B])(implicit B: WeakTypeTag[B]): Expr[B] = Expr[B](q"""{
    val xs = $these
    if (xs.isEmpty) throw new _root_.java.lang.UnsupportedOperationException("empty reduce")
    else {
      var r = xs.head: $B
      xs.step()
      while (!xs.isEmpty) {
        r = $op(r, xs.head)
        xs.step()
      }
      r
    }
  }""")

  def mayReduceLeft[A, B >: A](op: Expr[(B, A) => B])(implicit B: WeakTypeTag[B]): Expr[Maybe[B]] = Expr[Maybe[B]](q"""{
    val xs = $these
    if (xs.isEmpty) _root_.basis.Trap
    else {
      var r = xs.head: $B
      xs.step()
      while (!xs.isEmpty) {
        r = $op(r, xs.head)
        xs.step()
      }
      _root_.basis.Bind(r)
    }
  }""")

  def find[A : WeakTypeTag](p: Expr[A => Boolean]): Expr[Maybe[A]] = {
    implicit val MaybeA = MaybeTag[A]
    Expr[Maybe[A]](q"""{
      val xs = $these
      var r = _root_.basis.Trap: $MaybeA
      while (!xs.isEmpty && {
        val x = xs.head
        !$p(x) && { xs.step(); true } || { r = _root_.basis.Bind(x); false }
      }) ()
      r
    }""")
  }

  def forall[A](p: Expr[A => Boolean]): Expr[Boolean] = Expr[Boolean](q"""{
    val xs = $these
    var r = true
    while (!xs.isEmpty && ($p(xs.head) && { xs.step(); true } || { r = false; false })) ()
    r
  }""")

  def exists[A](p: Expr[A => Boolean]): Expr[Boolean] = Expr[Boolean](q"""{
    val xs = $these
    var r = false
    while (!xs.isEmpty && (!$p(xs.head) && { xs.step(); true } || { r = true; false })) ()
    r
  }""")

  def count[A](p: Expr[A => Boolean]): Expr[Int] = Expr[Int](q"""{
    val xs = $these
    var t = 0
    while (!xs.isEmpty) {
      if ($p(xs.head)) t += 1
      xs.step()
    }
    t
  }""")

  def choose[A, B : WeakTypeTag](q: Expr[PartialFunction[A, B]]): Expr[Maybe[B]] = {
    implicit val MaybeB = MaybeTag[B]
    Expr[Maybe[B]](q"""{
      val xs = $these
      var r = _root_.basis.Trap: $MaybeB
      val f = $q
      while (!xs.isEmpty && {
        val x = xs.head
        f.isDefinedAt(x) && { r = _root_.basis.Bind(f.applyOrElse(x, _root_.scala.PartialFunction.empty)); false } || { xs.step(); true }
      }) ()
      r
    }""")
  }

  def collect[A, B](q: Expr[PartialFunction[A, B]])(builder: Expr[Builder[B]]): Expr[builder.value.State] = {
    implicit val builderType = BuilderTypeTag(builder)
    implicit val builderState = BuilderStateTag(builder)
    Expr[builder.value.State](q"""{
      val xs = $these
      val b = $builder: $builderType
      val f = $q
      while (!xs.isEmpty) {
        val x = xs.head
        if (f.isDefinedAt(x)) b.append(f.applyOrElse(x, _root_.scala.PartialFunction.empty))
        xs.step()
      }
      b.state
    }""")
  }

  def map[A, B](f: Expr[A => B])(builder: Expr[Builder[B]]): Expr[builder.value.State] = {
    implicit val builderType = BuilderTypeTag(builder)
    implicit val builderState = BuilderStateTag(builder)
    Expr[builder.value.State](q"""{
      val xs = $these
      val b = $builder: $builderType
      while (!xs.isEmpty) {
        b.append($f(xs.head))
        xs.step()
      }
      b.state
    }""")
  }

  def flatMap[A, B](f: Expr[A => Traverser[B]])(builder: Expr[Builder[B]]): Expr[builder.value.State] = {
    implicit val builderType = BuilderTypeTag(builder)
    implicit val builderState = BuilderStateTag(builder)
    Expr[builder.value.State](q"""{
      val xs = $these
      val b = $builder: $builderType
      while (!xs.isEmpty) {
        b.appendAll($f(xs.head))
        xs.step()
      }
      b.state
    }""")
  }

  def filter[A](p: Expr[A => Boolean])(builder: Expr[Builder[A]]): Expr[builder.value.State] = {
    implicit val builderType = BuilderTypeTag(builder)
    implicit val builderState = BuilderStateTag(builder)
    Expr[builder.value.State](q"""{
      val xs = $these
      val b = $builder: $builderType
      while (!xs.isEmpty) {
        val x = xs.head
        if ($p(x)) b.append(x)
        xs.step()
      }
      b.state
    }""")
  }

  def dropWhile[A](p: Expr[A => Boolean])(builder: Expr[Builder[A]]): Expr[builder.value.State] = {
    implicit val builderType = BuilderTypeTag(builder)
    implicit val builderState = BuilderStateTag(builder)
    Expr[builder.value.State](q"""{
      val xs = $these
      val b = $builder: $builderType
      while (!xs.isEmpty && {
        val x = xs.head
        xs.step()
        $p(x) || { b.append(x); false }
      }) ()
      while (!xs.isEmpty) {
        b.append(xs.head)
        xs.step()
      }
      b.state
    }""")
  }

  def takeWhile[A](p: Expr[A => Boolean])(builder: Expr[Builder[A]]): Expr[builder.value.State] = {
    implicit val builderType = BuilderTypeTag(builder)
    implicit val builderState = BuilderStateTag(builder)
    Expr[builder.value.State](q"""{
      val xs = $these
      val b = $builder: $builderType
      while (!xs.isEmpty && {
        val x = xs.head
        $p(x) && { b.append(x); xs.step(); true }
      }) ()
      b.state
    }""")
  }

  def span[A](p: Expr[A => Boolean])(builder1: Expr[Builder[A]], builder2: Expr[Builder[A]]): Expr[(builder1.value.State, builder2.value.State)] = {
    implicit val builder1Type = BuilderTypeTag(builder1)
    implicit val builder2Type = BuilderTypeTag(builder2)
    implicit val builder1State = BuilderStateTag(builder1)
    implicit val builder2State = BuilderStateTag(builder2)
    Expr[(builder1.value.State, builder2.value.State)](q"""{
      val xs = $these
      val b1 = $builder1: $builder1Type
      val b2 = $builder2: $builder2Type
      while (!xs.isEmpty && {
        val x = xs.head
        xs.step()
        if ($p(x)) { b1.append(x); true } else { b2.append(x); false }
      }) ()
      while (!xs.isEmpty) {
        b2.append(xs.head)
        xs.step()
      }
      (b1.state: $builder1State, b2.state: $builder2State)
    }""")
  }

  def drop[A](lower: Expr[Int])(builder: Expr[Builder[A]]): Expr[builder.value.State] = {
    implicit val builderType = BuilderTypeTag(builder)
    implicit val builderState = BuilderStateTag(builder)
    Expr[builder.value.State](q"""{
      val xs = $these
      var i = 0
      val n = $lower
      val b = $builder: $builderType
      while (i < n && !xs.isEmpty) {
        xs.step()
        i += 1
      }
      while (!xs.isEmpty) {
        b.append(xs.head)
        xs.step()
      }
      b.state
    }""")
  }

  def take[A](upper: Expr[Int])(builder: Expr[Builder[A]]): Expr[builder.value.State] = {
    implicit val builderType = BuilderTypeTag(builder)
    implicit val builderState = BuilderStateTag(builder)
    Expr[builder.value.State](q"""{
      val xs = $these
      var i = 0
      val n = $upper
      val b = $builder: $builderType
      while (i < n && !xs.isEmpty) {
        b.append(xs.head)
        xs.step()
        i += 1
      }
      b.state
    }""")
  }

  def slice[A](lower: Expr[Int], upper: Expr[Int])(builder: Expr[Builder[A]]): Expr[builder.value.State] = {
    implicit val builderType = BuilderTypeTag(builder)
    implicit val builderState = BuilderStateTag(builder)
    Expr[builder.value.State](q"""{
      val xs = $these
      var i = 0
      var n = $lower
      val b = $builder: $builderType
      while (i < n && !xs.isEmpty) {
        xs.step()
        i += 1
      }
      n = $upper
      while (i < n && !xs.isEmpty) {
        b.append(xs.head)
        xs.step()
        i += 1
      }
      b.state
    }""")
  }

  def zip[A, B](those: Expr[Iterator[B]])(builder: Expr[Builder[(A, B)]])(implicit A: WeakTypeTag[A], B: WeakTypeTag[B]): Expr[builder.value.State] = {
    implicit val builderType = BuilderTypeTag(builder)
    implicit val builderState = BuilderStateTag(builder)
    Expr[builder.value.State](q"""{
      val xs = $these
      val ys = $those
      val b = $builder: $builderType
      while (!xs.isEmpty && !ys.isEmpty) {
        b.append((xs.head: $A, ys.head: $B))
        xs.step()
        ys.step()
      }
      b.state
    }""")
  }

  def zipContainer[A : WeakTypeTag, B : WeakTypeTag](those: Expr[Container[B]])(builder: Expr[Builder[(A, B)]]): Expr[builder.value.State] =
    zip[A, B](Expr[Iterator[B]](q"$those.iterator"))(builder)
}
