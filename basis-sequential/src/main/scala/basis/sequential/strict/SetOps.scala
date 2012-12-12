/*      ____              ___                                           *\
**     / __ | ___  ____  /__/___      A library of building blocks      **
**    / __  / __ |/ ___|/  / ___|                                       **
**   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012 Chris Sachs              **
**  |_____/\_____\____/__/\____/      http://www.scalabasis.com/        **
\*                                                                      */

package basis.sequential
package strict

import basis.collections._

/** Strictly evaluated set operations.
  * 
  * @groupprio  Mapping     -3
  * @groupprio  Filtering   -2
  * @groupprio  Combining   -1
  */
final class SetOps[+A, +From] {
  /** Returns the applications of a partial function to each element in this
    * set for which the function is defined.
    * 
    * @param  q         the partial function to filter and map elements.
    * @param  builder   the implicit accumulator for mapped elements.
    * @return the accumulated elements filtered and mapped by `q`.
    * @group  Mapping
    */
  def collect[B](q: PartialFunction[A, B])(implicit builder: Builder[From, B]): builder.State =
    macro ContainerOps.collect[A, B]
  
  /** Returns the applications of a function to each element in this set.
    * 
    * @param  f         the function to apply to each element.
    * @param  builder   the implicit accumulator for mapped elements.
    * @return the accumulated elements mapped by `f`.
    * @group  Mapping
    */
  def map[B](f: A => B)(implicit builder: Builder[From, B]): builder.State =
    macro ContainerOps.map[A, B]
  
  /** Returns the concatenation of all elements returned by a function applied
    * to each element in this set.
    * 
    * @param  f         the enumerator-yielding function to apply to each element.
    * @param  builder   the implicit accumulator for flattened elements.
    * @return the concatenation of all accumulated elements produced by `f`.
    * @group  Mapping
    */
  def flatMap[B](f: A => Enumerator[B])(implicit builder: Builder[From, B]): builder.State =
    macro ContainerOps.flatMap[A, B]
  
  /** Returns all elements in this set that satisfy a predicate.
    * 
    * @param  p         the predicate to test elements against.
    * @param  builder   the implicit accumulator for filtered elements.
    * @return the accumulated elements filtered by `p`.
    * @group  Filtering
    */
  def filter(p: A => Boolean)(implicit builder: Builder[From, A]): builder.State =
    macro ContainerOps.filter[A]
  
  /** Returns all elements following the longest prefix of this set
    * for which each element satisfies a predicate.
    * 
    * @param  p         the predicate to test elements against.
    * @param  builder   the implicit accumulator for non-dropped elements.
    * @return the suffix of accumulated elements beginning with the first
    *         element to not satisfy `p`.
    * @group  Filtering
    */
  def dropWhile(p: A => Boolean)(implicit builder: Builder[From, A]): builder.State =
    macro ContainerOps.dropWhile[A]
  
  /** Returns the longest prefix of this set for which each element
    * satisfies a predicate.
    * 
    * @param  p         the predicate to test elements against.
    * @param  builder   the implicit accumulator for taken elements.
    * @return the longest prefix of accumulated elements preceding the first
    *         element to not satisfy `p`.
    * @group  Filtering
    */
  def takeWhile(p: A => Boolean)(implicit builder: Builder[From, A]): builder.State =
    macro ContainerOps.takeWhile[A]
  
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
  //  macro ContainerOps.span[A]
  
  /** Returns all elements in this set following a prefix up to some length.
    * 
    * @param  lower     the length of the prefix to drop; also the inclusive
    *                   lower bound for indexes of elements to keep.
    * @param  builder   the accumulator for non-dropped elements.
    * @return all but the first `lower` accumulated elements.
    * @group  Filtering
    */
  def drop(lower: Int)(implicit builder: Builder[From, A]): builder.State =
    macro ContainerOps.drop[A]
  
  /** Returns a prefix of this set up to some length.
    * 
    * @param  upper     the length of the prefix to take; also the exclusive
    *                   upper bound for indexes of elements to keep.
    * @param  builder   the accumulator for taken elements.
    * @return up to the first `upper` accumulated elements.
    * @group  Filtering
    */
  def take(upper: Int)(implicit builder: Builder[From, A]): builder.State =
    macro ContainerOps.take[A]
  
  /** Returns an interval of elements in this set.
    * 
    * @param  lower     the inclusive lower bound for indexes of elements to keep.
    * @param  upper     the exclusive upper bound for indexes of elements to keep.
    * @param  builder   the accumulator for kept elements.
    * @return the accumulated elements with indexes greater than or equal to
    *         `lower` and less than `upper`.
    * @group  Filtering
    */
  def slice(lower: Int, upper: Int)(implicit builder: Builder[From, A]): builder.State =
    macro ContainerOps.slice[A]
  
  /** Returns the concatenation of this and another set.
    * 
    * @param  those     the set to append to these elements.
    * @param  builder   the accumulator for concatenated elements.
    * @return the accumulated elements of both set.
    * @group  Combining
    */
  def ++ [B >: A](those: Set[B])(implicit builder: Builder[From, B]): builder.State =
    macro ContainerOps.++[B]
}
