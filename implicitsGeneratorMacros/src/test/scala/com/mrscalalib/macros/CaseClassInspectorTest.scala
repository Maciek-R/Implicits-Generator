package com.mrscalalib.macros

import cats.data.NonEmptyList
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

case class TestClassMain(t1: TestClass1, t2: Option[TestClass2])
case class TestClass1(t11: List[TestClass11])
case class TestClass11(str: String)
case class TestClass2(int: NonEmptyList[Int], ints: NonEmptyList[TestClass3])
case class TestClass3(int: Int)

case class TestClassOther(
    i1: TestClassOther1,
    i1Opt: Option[TestClassOther1],
    str: TestClassOther2,
    str2: List[TestClassOther2]
)
case class TestClassOther1(int: Int)
case class TestClassOther2(str: String)

class CaseClassInspectorTest extends AnyFlatSpec with Matchers {
  "it" should "find all nested case class names" in {
    val result = CaseClassInspector.findAllCaseClasses[TestClassMain]
    result should contain allElementsOf
      List(
        CaseClassDescription("testClassMain", "TestClassMain"),
        CaseClassDescription("testClass1", "TestClass1"),
        CaseClassDescription("testClass2", "TestClass2"),
        CaseClassDescription("testClass11", "TestClass11"),
        CaseClassDescription("testClass3", "TestClass3")
      )
    result.length shouldBe 5
  }

  "it" should "find all nested case classes without duplicates" in {
    val result = CaseClassInspector.findAllCaseClasses[TestClassOther]
    result should contain allElementsOf
      List(
        CaseClassDescription("testClassOther", "TestClassOther"),
        CaseClassDescription("testClassOther1", "TestClassOther1"),
        CaseClassDescription("testClassOther2", "TestClassOther2")
      )
    result.length shouldBe 3
  }
}
