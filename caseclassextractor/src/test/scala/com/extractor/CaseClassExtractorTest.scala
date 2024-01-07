package com.extractor

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

case class TestClass(a: Int, b: String)

case class TestClass2(a: Int, classOther: ClassOther, b: String)
case class ClassOther(c: String)

case class TestClass3(field4: TestClass4, field5: Option[TestClass5])
case class TestClass4(a: Int, b: Option[String])
case class TestClass5(
    c: Option[Int],
    c6List: List[TestClass6],
    c6ListOther: List[TestClass6]
)
case class TestClass6(f: Option[String])

class CaseClassExtractorTest extends AnyFlatSpec with Matchers {
  "it" should "create typeclass instance" in {
    val caseClassExtractor: CaseClassExtractor[TestClass] =
      CaseClassExtractor.gen
    val result = caseClassExtractor.getCaseClasses()
    result.merge should contain allElementsOf Seq(
      FieldDescription("testClass", "TestClass")
    )
  }

  "it" should "create typeclass instance 2" in {
    val caseClassExtractor: CaseClassExtractor[TestClass2] =
      CaseClassExtractor.gen
    val result = caseClassExtractor.getCaseClasses()
    result.merge should contain allElementsOf Seq(
      FieldDescription("testClass2", "TestClass2"),
      FieldDescription("classOther", "ClassOther")
    )
  }

  "it" should "create typeclass instance 3" in {
    val caseClassExtractor: CaseClassExtractor[TestClass3] =
      CaseClassExtractor.gen
    val result = caseClassExtractor.getCaseClasses()
    result.merge should contain allElementsOf Seq(
      FieldDescription("testClass3", "TestClass3"),
      FieldDescription("testClass4", "TestClass4"),
      FieldDescription("testClass5", "TestClass5"),
      FieldDescription("testClass6", "TestClass6")
    )
    result.merge.length shouldBe 4
  }
}
