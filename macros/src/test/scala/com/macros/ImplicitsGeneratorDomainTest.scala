package com.macros

import com.macros.ImplicitsGeneratorDomainTest.{TestWrapper1, TestWrapper2, TestWrapperClass}

object ImplicitsGeneratorDomainTest {
  case class TestWrapperClass(t1: TestWrapper1, t2: Option[TestWrapper2])
  case class TestWrapper1(i: Int)
  case class TestWrapper2(str: String)
}

trait EncoderTypeClass[T]
trait DecoderTypeClass[T]

object EncoderTypeClass {
  def dummyApply[T] = new EncoderTypeClass[T] {}
}
object DecoderTypeClass {
  def dummyApply[T] = new DecoderTypeClass[T] {}
}

object EncoderInstances {
  implicit lazy val testWrapperClassEncoder: EncoderTypeClass[TestWrapperClass] =
    EncoderTypeClass.dummyApply
  implicit lazy val testWrapper1Encoder: EncoderTypeClass[TestWrapper1] =
    EncoderTypeClass.dummyApply
  implicit lazy val testWrapper2Encoder: EncoderTypeClass[TestWrapper2] =
    EncoderTypeClass.dummyApply
}

object DecoderInstances {
  implicit lazy val testWrapperClassDecoder: DecoderTypeClass[TestWrapperClass] =
    DecoderTypeClass.dummyApply
  implicit lazy val testWrapper1Decoder: DecoderTypeClass[TestWrapper1] =
    DecoderTypeClass.dummyApply
  implicit lazy val testWrapper2Decoder: DecoderTypeClass[TestWrapper2] =
    DecoderTypeClass.dummyApply
}
