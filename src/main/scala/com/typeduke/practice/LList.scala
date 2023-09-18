package com.typeduke.practice

import scala.annotation.tailrec
import com.typeduke.part3fp.AnonymousFunctions.doubler

/* Exercise 1
 * LList extension
 *
 * 1. Generic trait `Predicate[T]` with a little method `test(T) => Boolean`
 * 2. Generic trait `Transformer[A, B]` with a method `transform(A) => B`
 * 3. LList:
 *    - `map(transformer) => LList`
 *    - `filter(predicate) => LList`
 *    - `flatMap(transformer from A to LList[B]) => LList[B]`
 *
 * `class EvenPredicate extends Predicate[Int]`
 * `class StringToIntTransformer extends Transformer[String, Int]`
 *
 * `[1, 2, 3].map(n * 2) = [2, 4, 6]`
 * `[1, 2, 3, 4].filter(n % 2) = [2, 4]`
 * `[1, 2, 3].flatMap(n => [n, n + 1]) => (1, 2, 2, 3, 3, 4)`
 */

/* Exercise 2
 * Case classes
 *
 * Find out where case classes make sense for `LList` and apply the respective changes.
 */

/* Exercise 3
 * Implement a `find` method.
 */

/* Exercise 4
 * Replace `Predicate` and `Transformer` with appropriate function types.
 */

/* Exercise 5
 * Replace all `FunctionN` instantiations with lambdas.
 */

// Singly linked list
abstract class LList[A] {
  def head: A
  def tail: LList[A]
  def isEmpty: Boolean
  def add(element: A): LList[A] = Cons(element, this)

  infix def ++(other: LList[A]): LList[A]

  def map[B](transformer: A => B): LList[B]
  def filter(predicate: A => Boolean): LList[A]
  def flatMap[B](transformer: A => LList[B]): LList[B]
}

case class Empty[A]() extends LList[A] {
  override def head: A = throw new NoSuchElementException
  override def tail: LList[A] = throw new NoSuchElementException
  override def isEmpty: Boolean = true

  override def ++(other: LList[A]): LList[A] = other

  override def map[B](transformer: A => B): LList[B] = Empty()
  override def filter(predicate: A => Boolean): LList[A] = this
  override def flatMap[B](transformer: A => LList[B]): LList[B] = Empty()

  override def toString: String = "[]"
}

case class Cons[A](override val head: A, override val tail: LList[A]) extends LList[A] {
  override def isEmpty: Boolean = false

  override def ++(other: LList[A]): LList[A] =
    Cons(this.head, this.tail ++ other)

  override def map[B](transformer: A => B): LList[B] =
    Cons(transformer(this.head), this.tail.map(transformer))

  override def filter(predicate: A => Boolean): LList[A] =
    if (predicate(this.head)) Cons(this.head, this.tail.filter(predicate))
    else this.tail.filter(predicate)

  override def flatMap[B](transformer: A => LList[B]): LList[B] =
    transformer(this.head) ++ this.tail.flatMap(transformer)

  override def toString(): String = {
    @tailrec
    def concatenateElements(remainder: LList[A], acc: String): String =
      if (remainder.isEmpty) acc
      else concatenateElements(remainder.tail, s"$acc, ${remainder.head}")

    s"[${concatenateElements(this.tail, s"${this.head}")}]"
  }
}

object LList {
  def find[A](list: LList[A], predicate: A => Boolean): A =
    if (list.isEmpty) throw new NoSuchElementException
    else if (predicate(list.head)) list.head
    else find(list.tail, predicate)
}

// The following traits have been replaced with function types:
// `Predicate[T]` has been replaced with `T => Boolean`.
// trait Predicate[T] {
//   def test(element: T): Boolean
// }
//
// `Transformer[A, B]` has been replaced with `A = B`.
// trait Transformer[A, B] {
//   def transform(element: A): B
// }

object LListTest {
  def main(args: Array[String]): Unit = {
    val empty = Empty[Int]()

    println(empty)
    println(empty.isEmpty)

    val firstThreeNumbers = Cons(1, Cons(2, Cons(3, empty)))
    val firstThreeNumbers2 = empty.add(1).add(2).add(3)

    println(firstThreeNumbers)
    println(firstThreeNumbers2)
    println(firstThreeNumbers2.isEmpty)

    val someStrings = Cons("dog", Cons("cat", Empty()))

    println(someStrings)

    // The following functions have been replaced with in-place lambdas:
    // val evenPredicate = new Function1[Int, Boolean] {
    //   override def apply(element: Int): Boolean = element % 2 == 0
    // }
    //
    // val doubler = new Function1[Int, Int] {
    //   override def apply(element: Int): Int = element * 2
    // }
    //
    // val doublerList = new Function1[Int, LList[Int]] {
    //   override def apply(element: Int): LList[Int] =
    //     Cons(element, Cons(element * 2, Empty()))
    // }
    //
    // val stringToIntTransformer = new Function1[String, Int] {
    //   override def apply(element: String): Int = element.toInt
    // }

    // `map` tests
    // val numbersDoubled = firstThreeNumbers.map(doubler)
    val numbersDoubled2 = firstThreeNumbers.map(x => x * 2)
    val numbersDoubled3 = firstThreeNumbers.map(_ * 2)
    println(numbersDoubled3)

    // val numbersNested = firstThreeNumbers.map(doublerList)
    val numbersNested2 = firstThreeNumbers.map(elem => Cons(elem, Cons(elem * 2, Empty())))
    println(numbersNested2)

    // `filter` tests
    // val onlyEvenNumbers = firstThreeNumbers.filter(evenPredicate)
    val onlyEvenNumbers2 = firstThreeNumbers.filter(elem => elem % 2 == 0)
    val onlyEvenNumbers3 = firstThreeNumbers.filter(_ % 2 == 0)
    println(onlyEvenNumbers3)

    // `concatenation` tests
    val listInBothWays = firstThreeNumbers ++ firstThreeNumbers2
    println(listInBothWays)

    // `flatMap` tests
    // val flattenedList = firstThreeNumbers.flatMap(doublerList)
    val flattenedList2 = firstThreeNumbers.flatMap(elem => Cons(elem, Cons(elem * 2, Empty())))
    println(flattenedList2)

    // `find` tests
    // println(LList.find(firstThreeNumbers, evenPredicate)) // 2
    println(LList.find(firstThreeNumbers, _ % 2 == 0)) // 2
    // Should throw a `NoSuchElementException`
    // println(LList.find(firstThreeNumbers, new Predicate[Int] {
    //   override def test(element: Int): Boolean = element > 5
    // }))
  }
}
