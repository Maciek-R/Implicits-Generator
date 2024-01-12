package com.macros

import com.macros.ImplicitsGeneratorDomainTest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.macros.EncoderInstances._
import com.macros.DecoderInstances._

object ImplicitsWrapperSupport {
  @generateAllImplicits(
    Seq("EncoderTypeClass", "DecoderTypeClass"),
    Seq("import com.macros.CaseClassInspector", "import com.macros.ImplicitsGeneratorDomainTest._")
  )
  val allNestedClasses: List[CaseClassDescription] =
    CaseClassInspector.findAllCaseClasses[TestWrapperClass]

  trait ImplicitsWrapper
      extends ImplicitsWrapperSupport.EncoderTypeClassImplicitsCache
      with ImplicitsWrapperSupport.DecoderTypeClassImplicitsCache
}

class ImplicitsGeneratorTest extends AnyFlatSpec with Matchers {
  "it" should "generate cached implicits for all nested classes" in {
    val mainTrait = new ImplicitsWrapperSupport.ImplicitsWrapper {}

    mainTrait.testWrapperClassEncoderTypeClass.isInstanceOf[EncoderTypeClass[TestWrapperClass]] shouldBe true
    mainTrait.testWrapper1EncoderTypeClass.isInstanceOf[EncoderTypeClass[TestWrapper1]] shouldBe true
    mainTrait.testWrapper2EncoderTypeClass.isInstanceOf[EncoderTypeClass[TestWrapper2]] shouldBe true

    mainTrait.testWrapperClassDecoderTypeClass.isInstanceOf[DecoderTypeClass[TestWrapperClass]] shouldBe true
    mainTrait.testWrapper1DecoderTypeClass.isInstanceOf[DecoderTypeClass[TestWrapper1]] shouldBe true
    mainTrait.testWrapper2DecoderTypeClass.isInstanceOf[DecoderTypeClass[TestWrapper2]] shouldBe true
  }
}
