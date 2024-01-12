package com.mrscalalib.macros.generator

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

trait Encoder[T]

object Encoder {
  implicit lazy val encTestResponse1: Encoder[TestResponse1] =
    new Encoder[TestResponse1] {}
  implicit lazy val encTestResponse3: Encoder[TestResponse3] =
    new Encoder[TestResponse3] {}
  implicit lazy val encTestResponse4: Encoder[TestResponse4] =
    new Encoder[TestResponse4] {}
  implicit lazy val encTestResponse5: Encoder[TestResponse5] =
    new Encoder[TestResponse5] {}
}

trait Decoder[T]

object Decoder {
  implicit lazy val decTestResponse1: Decoder[TestResponse1] =
    new Decoder[TestResponse1] {}
  implicit lazy val decTestResponse2: Decoder[TestResponse2] =
    new Decoder[TestResponse2] {}
  implicit lazy val decTestResponse3: Decoder[TestResponse3] =
    new Decoder[TestResponse3] {}
}

@generateCachedImplicits(Seq("Encoder"))
case class TestResponse1(field: String)

@generateCachedImplicits(Seq("Decoder"))
case class TestResponse2(field: String)

@generateCachedImplicits(Seq("Decoder", "Encoder"))
case class TestResponse3(field: String)

@generateCachedImplicits(Seq("Encoder"))
final case class TestResponse4(field: String)

@generateCachedImplicits(Seq("Encoder"))
case class TestResponse5(field: String)

object TestResponse5 {
  def testMethod = "test"
}

class GeneratorCachedImplicitsTest extends AnyFlatSpec with Matchers {
  "it" should "create cached implicit for Encoder type class" in {
    TestResponse1.testResponse1Encoder
      .isInstanceOf[Encoder[TestResponse1]] shouldBe true
  }

  "it" should "create cached implicit for Decoder type class" in {
    TestResponse2.testResponse2Decoder
      .isInstanceOf[Decoder[TestResponse2]] shouldBe true
  }

  "it" should "create cached implicits for Encoder and Decoder type class" in {
    TestResponse3.testResponse3Encoder
      .isInstanceOf[Encoder[TestResponse3]] shouldBe true
    TestResponse3.testResponse3Decoder
      .isInstanceOf[Decoder[TestResponse3]] shouldBe true
  }

  "it" should "for final case class create cached implicit for Encoder type class" in {
    TestResponse4.testResponse4Encoder
      .isInstanceOf[Encoder[TestResponse4]] shouldBe true
  }

  "it" should "create cached implicit for Encoder type class and not modify companion object" in {
    TestResponse5.testResponse5Encoder
      .isInstanceOf[Encoder[TestResponse5]] shouldBe true
    TestResponse5.testMethod shouldBe "test"
  }
}
