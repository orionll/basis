//      ____              ___
//     / __ | ___  ____  /__/___      A library of building blocks
//    / __  / __ |/ ___|/  / ___|
//   / /_/ / /_/ /\__ \/  /\__ \      (c) 2012-2014 Reify It
//  |_____/\_____\____/__/\____/      http://basis.reify.it

package basis.collections

import org.scalatest._

trait CollectionBehaviors { this: FunSpec =>
  import CollectionEnablers._
  import CollectionGenerators._
  import Matchers._

  def GenericCollection[CC[X] <: Collection[X]](CC: generic.CollectionFactory[CC]) = describe(s"Generic $CC collections") {
    it("should have an empty collection") {
      CC.empty shouldBe empty
    }

    it("should build and traverse unary collections") {
      val xs = (CC.Builder[String] += "unit").state
      var q = false
      xs.traverse {
        case "unit" if !q => q = true
        case "unit" => fail("Traversed expected element more than once")
        case elem => fail(s"Traversed unexpected element $elem")
      }
      withClue("traversed element") (q should be (true))
    }

    it("should build and traverse n-ary collections") {
      var n = 2
      while (n <= 1024) withClue(s"sum of first $n integers") {
        val ns = CC.range(1, n)
        var sum = 0
        ns.traverse(sum += _)
        sum should equal (n * (n + 1) / 2)
        n += 1
      }
    }
  }

  def GenericCollectionBuilder[CC[X] <: Collection[X]](CC: generic.CollectionFactory[CC]) = describe(s"Generic $CC builders") {
    def concat(i: Int, j: Int, alias: Boolean): Unit = withClue(s"[1..$i] ++ [${i + 1}..${i + j}]") {
      val xs = CC.range(1, i)
      val ys = CC.range(i + 1, i + j)

      val b = CC.Builder[Int]
      b.appendAll(xs)
      if (alias) b.state
      b.appendAll(ys)
      val ns = b.state

      var sum = 0L
      ns.traverse(sum += _)
      val n = (i + j).toLong
      sum should equal (n * (n + 1L) / 2L)
    }

    def concatSmall(alias: Boolean): Unit = {
      var n = 2
      while (n <= (1 << 10) + 1) {
        var i = 1
        var j = n - 1
        while (i < n) {
          concat(i, j, alias)
          i += 1
          j -= 1
        }
        n += 1
      }
    }

    def concatLarge(alias: Boolean): Unit = {
      var i = 2
      while (i <= 20) {
        val k = 1 << i
        concat(1, k, alias)
        concat(k, 1, alias)
        concat(k, k, alias)
        i += 1
      }
    }

    it("should concatenate small collections") {
      concatSmall(alias = false)
    }

    it("should concatenate large collections") {
      concatLarge(alias = false)
    }

    it("should concatenate small collections with intermediate aliasing") {
      concatSmall(alias = true)
    }

    it("should concatenate large collections with intermediate aliasing") {
      concatLarge(alias = true)
    }
  }
}
