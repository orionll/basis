/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.sequential
package strict

import basis.collections._
import basis.collections.traversable._

/** Strictly evaluated indexed sequence operations.
  * 
  * @groupprio  Mapping     -3
  * @groupprio  Filtering   -2
  * @groupprio  Combining   -1
  */
final class IndexedSeqOps[+A, +From] {
  /** Returns the applications of a partial function to each element in this
    * sequence for which the function is defined.
    * 
    * @param  q         the partial function to filter and map elements.
    * @param  builder   the implicit accumulator for mapped elements.
    * @return the accumulated elements filtered and mapped by `q`.
    * @group  Mapping
    */
  def collect[B](q: PartialFunction[A, B])(implicit builder: Builder[From, B]): builder.State =
    macro IndexedSeqOps.collect[A, B]
  
  /** Returns the applications of a function to each element in this sequence.
    * 
    * @param  f         the function to apply to each element.
    * @param  builder   the implicit accumulator for mapped elements.
    * @return the accumulated elements mapped by `f`.
    * @group  Mapping
    */
  def map[B](f: A => B)(implicit builder: Builder[From, B]): builder.State =
    macro IndexedSeqOps.map[A, B]
  
  /** Returns the concatenation of all elements returned by a function applied
    * to each element in this sequence.
    * 
    * @param  f         the enumerator-yielding function to apply to each element.
    * @param  builder   the implicit accumulator for flattened elements.
    * @return the concatenation of all accumulated elements produced by `f`.
    * @group  Mapping
    */
  def flatMap[B](f: A => Enumerator[B])(implicit builder: Builder[From, B]): builder.State =
    macro IndexedSeqOps.flatMap[A, B]
  
  /** Returns all elements in this sequence that satisfy a predicate.
    * 
    * @param  p         the predicate to test elements against.
    * @param  builder   the implicit accumulator for filtered elements.
    * @return the accumulated elements filtered by `p`.
    * @group  Filtering
    */
  def filter(p: A => Boolean)(implicit builder: Builder[From, A]): builder.State =
    macro IndexedSeqOps.filter[A]
  
  /** Returns all elements following the longest prefix of this sequence
    * for which each element satisfies a predicate.
    * 
    * @param  p         the predicate to test elements against.
    * @param  builder   the implicit accumulator for non-dropped elements.
    * @return the suffix of accumulated elements beginning with the first
    *         element to not satisfy `p`.
    * @group  Filtering
    */
  def dropWhile(p: A => Boolean)(implicit builder: Builder[From, A]): builder.State =
    macro IndexedSeqOps.dropWhile[A]
  
  /** Returns the longest prefix of this sequence for which each element
    * satisfies a predicate.
    * 
    * @param  p         the predicate to test elements against.
    * @param  builder   the implicit accumulator for taken elements.
    * @return the longest prefix of accumulated elements preceding the first
    *         element to not satisfy `p`.
    * @group  Filtering
    */
  def takeWhile(p: A => Boolean)(implicit builder: Builder[From, A]): builder.State =
    macro IndexedSeqOps.takeWhile[A]
  
  /** Returns a (prefix, suffix) pair with the prefix being the longest one for
    * which each element satisfies a predicate, and the suffix beginning with
    * the first element to not satisfy the predicate.
    * 
    * @param  p           the predicate to test elements against.
    * @param  builder1    the implicit accumulator for prefix elements.
    * @param  builder2    the implicit accumilator for suffix elements.
    * @return the pair of accumulated prefix and suffix elements.
    * @group  Filtering
    */
  //FIXME: SI-6447
  //def span(p: A => Boolean)
  //    (implicit builder1: Builder[From, A], builder2: Builder[From, A])
  //  : (builder1.State, builder2.State) =
  //  macro IndexedSeqOps.span[A]
  
  /** Returns all elements in this sequence following a prefix up to some length.
    * 
    * @param  lower     the length of the prefix to drop; also the inclusive
    *                   lower bound for indexes of elements to keep.
    * @param  builder   the accumulator for non-dropped elements.
    * @return all but the first `lower` accumulated elements.
    * @group  Filtering
    */
  def drop(lower: Int)(implicit builder: Builder[From, A]): builder.State =
    macro IndexedSeqOps.drop[A]
  
  /** Returns a prefix of this sequence up to some length.
    * 
    * @param  upper     the length of the prefix to take; also the exclusive
    *                   upper bound for indexes of elements to keep.
    * @param  builder   the accumulator for taken elements.
    * @return up to the first `upper` accumulated elements.
    * @group  Filtering
    */
  def take(upper: Int)(implicit builder: Builder[From, A]): builder.State =
    macro IndexedSeqOps.take[A]
  
  /** Returns an interval of elements in this sequence.
    * 
    * @param  lower     the inclusive lower bound for indexes of elements to keep.
    * @param  upper     the exclusive upper bound for indexes of elements to keep.
    * @param  builder   the accumulator for kept elements.
    * @return the accumulated elements with indexes greater than or equal to
    *         `lower` and less than `upper`.
    * @group  Filtering
    */
  def slice(lower: Int, upper: Int)(implicit builder: Builder[From, A]): builder.State =
    macro IndexedSeqOps.slice[A]
  
  /** Returns the reverse of this sequence.
    * 
    * @param  builder   the accumulator for reversed elements.
    * @return the elements in this sequence in reverse order.
    * @group  Combining
    */
  def reverse(implicit builder: Builder[From, A]): builder.State =
    macro IndexedSeqOps.reverse[A]
  
  /** Returns pairs of elements from this and another sequence.
    * 
    * @param  those     the sequence whose elements to pair with these elements.
    * @param  builder   the accumulator for paired elements.
    * @return the accumulated pairs of corresponding elements.
    * @group  Combining
    */
  def zip[B](those: IndexedSeq[B])(implicit builder: Builder[From, (A, B)]): builder.State =
    macro IndexedSeqOps.zip[A, B]
  
  /** Returns the concatenation of this and another sequence.
    * 
    * @param  those     the sequence to append to these elements.
    * @param  builder   the accumulator for concatenated elements.
    * @return the accumulated elements of both sequences.
    * @group  Combining
    */
  def ++ [B >: A](those: IndexedSeq[B])(implicit builder: Builder[From, B]): builder.State =
    macro IndexedSeqOps.++[B]
}

private[strict] object IndexedSeqOps {
  import scala.collection.immutable.{::, Nil}
  import scala.reflect.macros.Context
  import basis.util.IntOps
  
  private def unApply[A : c.WeakTypeTag](c: Context): c.Expr[IndexedSeq[A]] = {
    import c.{Expr, mirror, prefix, typeCheck, weakTypeOf, WeakTypeTag}
    import c.universe._
    val Apply(_, sequence :: Nil) = prefix.tree
    val IndexedSeqTag =
      WeakTypeTag[IndexedSeq[A]](
        appliedType(
          mirror.staticClass("basis.collections.traversable.IndexedSeq").toType,
          weakTypeOf[A] :: Nil))
    Expr(typeCheck(sequence, IndexedSeqTag.tpe))(IndexedSeqTag)
  }
  
  def collect[A : c.WeakTypeTag, B : c.WeakTypeTag]
      (c: Context)
      (q: c.Expr[PartialFunction[A, B]])
      (builder: c.Expr[Builder[_, B]])
    : c.Expr[builder.value.State] =
    new IndexedSeqMacros[c.type](c).collect[A, B](unApply[A](c))(q)(builder)
  
  def map[A : c.WeakTypeTag, B : c.WeakTypeTag]
      (c: Context)
      (f: c.Expr[A => B])
      (builder: c.Expr[Builder[_, B]])
    : c.Expr[builder.value.State] =
    new IndexedSeqMacros[c.type](c).map[A, B](unApply[A](c))(f)(builder)
  
  def flatMap[A : c.WeakTypeTag, B : c.WeakTypeTag]
      (c: Context)
      (f: c.Expr[A => Enumerator[B]])
      (builder: c.Expr[Builder[_, B]])
    : c.Expr[builder.value.State] =
    new IndexedSeqMacros[c.type](c).flatMap[A, B](unApply[A](c))(f)(builder)
  
  def filter[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
      (builder: c.Expr[Builder[_, A]])
    : c.Expr[builder.value.State] =
    new IndexedSeqMacros[c.type](c).filter[A](unApply[A](c))(p)(builder)
  
  def dropWhile[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
      (builder: c.Expr[Builder[_, A]])
    : c.Expr[builder.value.State] =
    new IndexedSeqMacros[c.type](c).dropWhile[A](unApply[A](c))(p)(builder)
  
  def takeWhile[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
      (builder: c.Expr[Builder[_, A]])
    : c.Expr[builder.value.State] =
    new IndexedSeqMacros[c.type](c).takeWhile[A](unApply[A](c))(p)(builder)
  
  def span[A : c.WeakTypeTag]
      (c: Context)
      (p: c.Expr[A => Boolean])
      (builder1: c.Expr[Builder[_, A]], builder2: c.Expr[Builder[_, A]])
    : c.Expr[(builder1.value.State, builder2.value.State)] =
    new IndexedSeqMacros[c.type](c).span[A](unApply[A](c))(p)(builder1, builder2)
  
  def drop[A : c.WeakTypeTag]
      (c: Context)
      (lower: c.Expr[Int])
      (builder: c.Expr[Builder[_, A]])
    : c.Expr[builder.value.State] =
    new IndexedSeqMacros[c.type](c).drop[A](unApply[A](c))(lower)(builder)
  
  def take[A : c.WeakTypeTag]
      (c: Context)
      (upper: c.Expr[Int])
      (builder: c.Expr[Builder[_, A]])
    : c.Expr[builder.value.State] =
    new IndexedSeqMacros[c.type](c).take[A](unApply[A](c))(upper)(builder)
  
  def slice[A : c.WeakTypeTag]
      (c: Context)
      (lower: c.Expr[Int], upper: c.Expr[Int])
      (builder: c.Expr[Builder[_, A]])
    : c.Expr[builder.value.State] =
    new IndexedSeqMacros[c.type](c).slice[A](unApply[A](c))(lower, upper)(builder)
  
  def reverse[A : c.WeakTypeTag]
      (c: Context)
      (builder: c.Expr[Builder[_, A]])
    : c.Expr[builder.value.State] =
    new IndexedSeqMacros[c.type](c).reverse[A](unApply[A](c))(builder)
  
  def zip[A : c.WeakTypeTag, B : c.WeakTypeTag]
      (c: Context)
      (those: c.Expr[IndexedSeq[B]])
      (builder: c.Expr[Builder[_, (A, B)]])
    : c.Expr[builder.value.State] =
    new IndexedSeqMacros[c.type](c).zip[A, B](unApply[A](c), those)(builder)
  
  def ++ [A : c.WeakTypeTag]
      (c: Context)
      (those: c.Expr[IndexedSeq[A]])
      (builder: c.Expr[Builder[_, A]])
    : c.Expr[builder.value.State] =
    new IndexedSeqMacros[c.type](c).++[A](unApply[A](c), those)(builder)
}