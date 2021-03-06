//      ____              ___
//     / __ | ___  ____  /__/___      A library of building blocks
//    / __  / __ |/ ___|/  / ___|
//   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2015 Chris Sachs
//  |_____/\_____\____/__/\____/      http://basis.reify.it

package basis.collections
package immutable

import basis._
import basis.collections.generic._
import basis.util._
import scala.annotation._
import scala.annotation.unchecked._

final class HashTrieSet[+A] private[collections] (
    private[collections] val treeMap: Int,
    private[collections] val leafMap: Int,
    slots: Array[AnyRef])
  extends Equals with Immutable with Family[HashTrieSet[_]] with Subset[A] {

  import HashTrieSet.{ VOID, LEAF, TREE, KNOT }

  override def isEmpty: Boolean = slotMap == 0

  override def size: Int = {
    var t = 0
    var i = 0
    var treeMap = this.treeMap
    var leafMap = this.leafMap
    while ((treeMap | leafMap) != 0) {
      ((leafMap & 1 | (treeMap & 1) << 1): @switch) match {
        case VOID => ()
        case LEAF => t += 1; i += 1
        case TREE => t += treeAt(i).size; i += 1
        case KNOT => t += knotAt(i).size; i += 1
      }
      treeMap >>>= 1
      leafMap >>>= 1
    }
    t
  }

  override def contains(elem: A @uncheckedVariance): Boolean = contains(elem, elem.##, 0)

  override def + [B >: A](elem: B): HashTrieSet[B] = update(elem, elem.##, 0)

  override def - (elem: A @uncheckedVariance): HashTrieSet[A] = remove(elem, elem.##, 0)

  private def slotMap: Int = treeMap | leafMap

  private def choose(hash: Int, shift: Int): Int = 1 << ((hash >>> shift) & 0x1F)

  private def select(branch: Int): Int = (slotMap & (branch - 1)).countSetBits

  private def follow(branch: Int): Int =
    (if ((leafMap & branch) != 0) 1 else 0) | (if ((treeMap & branch) != 0) 2 else 0)

  private[collections] def leafAt(index: Int): A =
    slots(index).asInstanceOf[A]

  private def getLeaf(branch: Int): A =
    slots(select(branch)).asInstanceOf[A]

  private def setLeaf[B >: A](branch: Int, leaf: B): this.type = {
    slots(select(branch)) = leaf.asInstanceOf[AnyRef]
    this
  }

  private[collections] def treeAt(index: Int): HashTrieSet[A] =
    slots(index).asInstanceOf[HashTrieSet[A]]

  private def getTree(branch: Int): HashTrieSet[A] =
    slots(select(branch)).asInstanceOf[HashTrieSet[A]]

  private def setTree[B >: A](branch: Int, tree: HashTrieSet[B]): this.type = {
    slots(select(branch)) = tree
    this
  }

  private[collections] def knotAt(index: Int): ArraySet[A] =
    slots(index).asInstanceOf[ArraySet[A]]

  private def getKnot(branch: Int): ArraySet[A] =
    slots(select(branch)).asInstanceOf[ArraySet[A]]

  private def setKnot[B >: A](branch: Int, knot: ArraySet[B]): this.type = {
    slots(select(branch)) = knot
    this
  }

  private def isUnary: Boolean = treeMap == 0 && leafMap.countSetBits == 1

  private def unaryElement: A = slots(0).asInstanceOf[A]

  private def remap(treeMap: Int, leafMap: Int): HashTrieSet[A] = {
    var oldSlotMap = this.treeMap | this.leafMap
    var newSlotMap = treeMap | leafMap
    if (oldSlotMap == newSlotMap) new HashTrieSet(treeMap, leafMap, this.slots.clone)
    else {
      var i = 0
      var j = 0
      val size = newSlotMap.countSetBits
      if (size == 0) {
        return HashTrieSet.empty
      }
      val slots = new Array[AnyRef](size)
      while (newSlotMap != 0) {
        if ((oldSlotMap & newSlotMap & 1) == 1) slots(j) = this.slots(i)
        if ((oldSlotMap & 1) == 1) i += 1
        if ((newSlotMap & 1) == 1) j += 1
        oldSlotMap >>>= 1
        newSlotMap >>>= 1
      }
      new HashTrieSet(treeMap, leafMap, slots)
    }
  }

  @tailrec private def contains(elem: A @uncheckedVariance, elemHash: Int, shift: Int): Boolean = {
    val branch = choose(elemHash, shift)
    (follow(branch): @switch) match {
      case VOID => false
      case LEAF => elem == getLeaf(branch)
      case TREE => getTree(branch).contains(elem, elemHash, shift + 5)
      case KNOT => getKnot(branch).contains(elem)
    }
  }

  private def update[B >: A](elem: B, elemHash: Int, shift: Int): HashTrieSet[B] = {
    val branch = choose(elemHash, shift)
    (follow(branch): @switch) match {
      case VOID => remap(treeMap, leafMap | branch).setLeaf(branch, elem)
      case LEAF =>
        val leaf = getLeaf(branch)
        val leafHash = leaf.##
        if (elemHash == leafHash && elem == leaf) this
        else if (elemHash != leafHash)
          remap(treeMap | branch, leafMap ^ branch).
            setTree(branch, merge(leaf, leafHash, elem, elemHash, shift + 5))
        else remap(treeMap | branch, leafMap).setKnot(branch, ArraySet(leaf, elem))
      case TREE =>
        val oldTree = getTree(branch)
        val newTree = oldTree.update(elem, elemHash, shift + 5)
        if (oldTree eq newTree) this
        else remap(treeMap, leafMap).setTree(branch, newTree)
      case KNOT =>
        val oldKnot = getKnot(branch)
        val newKnot = oldKnot + elem
        if (oldKnot eq newKnot) this
        else remap(treeMap, leafMap).setKnot(branch, newKnot)
    }
  }

  private def remove(elem: A @uncheckedVariance, elemHash: Int, shift: Int): HashTrieSet[A] = {
    val branch = choose(elemHash, shift)
    (follow(branch): @switch) match {
      case VOID => this
      case LEAF =>
        if (elem != getLeaf(branch)) this
        else remap(treeMap, leafMap ^ branch)
      case TREE =>
        val oldTree = getTree(branch)
        val newTree = oldTree.remove(elem, elemHash, shift + 5)
        if (oldTree eq newTree) this
        else if (newTree.isEmpty) remap(treeMap ^ branch, leafMap)
        else if (newTree.isUnary) remap(treeMap ^ branch, leafMap | branch).setLeaf(branch, newTree.unaryElement)
        else remap(treeMap, leafMap).setTree(branch, newTree)
      case KNOT =>
        val oldKnot = getKnot(branch)
        val newKnot = oldKnot - elem
        if (oldKnot eq newKnot) this
        else if (newKnot.isEmpty) remap(treeMap ^ branch, leafMap)
        else if (newKnot.isUnary) remap(treeMap ^ branch, leafMap | branch).setLeaf(branch, newKnot.unaryElement)
        else remap(treeMap, leafMap).setKnot(branch, newKnot)
    }
  }

  private def merge[B >: A](elem0: B, hash0: Int, elem1: B, hash1: Int, shift: Int): HashTrieSet[B] = {
    // assume(hash0 != hash1)
    val branch0 = choose(hash0, shift)
    val branch1 = choose(hash1, shift)
    val slotMap = branch0 | branch1
    if (branch0 == branch1) {
      val slots = new Array[AnyRef](1)
      slots(0) = merge(elem0, hash0, elem1, hash1, shift + 5)
      new HashTrieSet(slotMap, 0, slots)
    }
    else {
      val slots = new Array[AnyRef](2)
      if (((branch0 - 1) & branch1) == 0) {
        slots(0) = elem0.asInstanceOf[AnyRef]
        slots(1) = elem1.asInstanceOf[AnyRef]
      }
      else {
        slots(0) = elem1.asInstanceOf[AnyRef]
        slots(1) = elem0.asInstanceOf[AnyRef]
      }
      new HashTrieSet(0, slotMap, slots)
    }
  }

  override def traverse(f: A => Unit): Unit = {
    var i = 0
    var treeMap = this.treeMap
    var leafMap = this.leafMap
    while ((treeMap | leafMap) != 0) {
      ((leafMap & 1 | (treeMap & 1) << 1): @switch) match {
        case VOID => ()
        case LEAF => f(leafAt(i)); i += 1
        case TREE => treeAt(i) traverse f; i += 1
        case KNOT => knotAt(i) traverse f; i += 1
      }
      treeMap >>>= 1
      leafMap >>>= 1
    }
  }

  override def iterator: Iterator[A] = new HashTrieSetIterator(this)

  protected override def stringPrefix: String = "HashTrieSet"
}

/** A factory for [[HashTrieSet hash sets]].
  * @group Containers */
object HashTrieSet extends SetFactory[HashTrieSet] {
  private[this] val Empty = new HashTrieSet[Nothing](0, 0, new Array[AnyRef](0))
  override def empty[A]: HashTrieSet[A] = Empty

  override def from[A](elems: Traverser[A]): HashTrieSet[A] = {
    if (elems.isInstanceOf[HashTrieSet[_]]) elems.asInstanceOf[HashTrieSet[A]]
    else super.from(elems)
  }

  implicit override def Builder[A]: Builder[A] with State[HashTrieSet[A]] =
    new HashTrieSetBuilder[A]

  override def toString: String = "HashTrieSet"

  private[collections] final val VOID = 0
  private[collections] final val LEAF = 1
  private[collections] final val TREE = 2
  private[collections] final val KNOT = 3
}

private[collections] final class HashTrieSetIterator[+A](
    nodes: Array[AnyRef], private[this] var depth: Int,
    stack: Array[Int], private[this] var stackPointer: Int)
  extends Iterator[A] {

  import HashTrieSet.{ VOID, LEAF, TREE, KNOT }

  def this(tree: HashTrieSet[A]) = {
    this(new Array[AnyRef](7), 0, new Array[Int](21), 0)
    node = tree
    i = 0
    treeMap = tree.treeMap
    leafMap = tree.leafMap
  }

  private[this] def node: AnyRef = nodes(depth)
  private[this] def node_=(node: AnyRef): Unit = nodes(depth) = node

  private[this] def i: Int = stack(stackPointer)
  private[this] def i_=(index: Int): Unit = stack(stackPointer) = index

  private[this] def treeMap: Int = stack(stackPointer + 1)
  private[this] def treeMap_=(treeMap: Int): Unit = stack(stackPointer + 1) = treeMap

  private[this] def leafMap: Int = stack(stackPointer + 2)
  private[this] def leafMap_=(leafMap: Int): Unit = stack(stackPointer + 2) = leafMap

  private[this] def follow: Int = leafMap & 1 | (treeMap & 1) << 1

  private[this] def push(tree: HashTrieSet[A]): Unit = {
    depth += 1
    node = tree

    stackPointer += 3
    i = 0
    treeMap = tree.treeMap
    leafMap = tree.leafMap
  }

  private[this] def push(knot: ArraySet[A]): Unit = {
    depth += 1
    node = knot

    stackPointer += 3
    i = 0
  }

  private[this] def pop(): Unit = {
    node = null
    depth -= 1

    i = 0
    treeMap = 0
    leafMap = 0
    stackPointer -= 3

    i += 1
    treeMap >>>= 1
    leafMap >>>= 1
  }

  @tailrec override def isEmpty: Boolean = node match {
    case node: HashTrieSet[A] =>
      if ((treeMap | leafMap) != 0) (follow: @switch) match {
        case VOID =>
          treeMap >>>= 1
          leafMap >>>= 1
          isEmpty
        case LEAF => false
        case TREE =>
          push(node.treeAt(i))
          isEmpty
        case KNOT =>
          push(node.knotAt(i))
          isEmpty
      }
      else if (depth > 0) { pop(); isEmpty }
      else true
    case node: ArraySet[A] =>
      if (i < node.size) false
      else { pop(); isEmpty }
  }

  @tailrec override def head: A = node match {
    case node: HashTrieSet[A] =>
      if ((treeMap | leafMap) != 0) (follow: @switch) match {
        case VOID =>
          treeMap >>>= 1
          leafMap >>>= 1
          head
        case LEAF => node.leafAt(i)
        case TREE =>
          push(node.treeAt(i))
          head
        case KNOT =>
          push(node.knotAt(i))
          head
      }
      else if (depth > 0) { pop(); head }
      else Iterator.empty.head
    case node: ArraySet[A] =>
      if (i < node.size) node.elementAt(i)
      else { pop(); head }
  }

  @tailrec override def step(): Unit = node match {
    case node: HashTrieSet[A] =>
      val slotMap = treeMap | leafMap
      if (slotMap != 0) {
        if ((slotMap & 1) == 1) i += 1
        treeMap >>>= 1
        leafMap >>>= 1
      }
      else if (depth > 0) { pop(); step() }
      else Iterator.empty.step()
    case node: ArraySet[A] =>
      if (i < node.size) i += 1
      else { pop(); step() }
  }

  override def dup: Iterator[A] =
    new HashTrieSetIterator(nodes.clone, depth, stack.clone, stackPointer)
}

private[collections] final class HashTrieSetBuilder[A] extends Builder[A] with State[HashTrieSet[A]] {
  private[this] var these: HashTrieSet[A] = HashTrieSet.empty[A]

  override def append(elem: A): Unit = these += elem

  override def appendAll(elems: Traverser[A]): Unit = {
    if (these.isEmpty && elems.isInstanceOf[HashTrieSet[_]])
      these = elems.asInstanceOf[HashTrieSet[A]]
    else super.appendAll(elems)
  }

  override def expect(count: Int): this.type = this

  override def state: HashTrieSet[A] = these

  override def clear(): Unit = these = HashTrieSet.empty[A]

  override def toString: String = "HashTrieSet"+"."+"Builder"
}
