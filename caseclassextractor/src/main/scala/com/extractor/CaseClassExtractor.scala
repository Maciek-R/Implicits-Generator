package com.extractor

import magnolia1._
import scala.language.experimental.macros

case class FieldDescription(fieldName: String, fieldType: String)

trait CaseClassExtractor[T] {
  def getCaseClasses(): Either[List[FieldDescription], List[FieldDescription]]
}

object CaseClassExtractor extends CaseClassExtractorDerivation {
  def apply[T](implicit W: CaseClassExtractor[T]): CaseClassExtractor[T] = W

  implicit lazy val intCaseClassExtractor: CaseClassExtractor[Int] =
    emptyCaseClassExtractor

  implicit lazy val strCaseClassExtractor: CaseClassExtractor[String] =
    emptyCaseClassExtractor

  implicit def optCaseClassExtractor[T: CaseClassExtractor]
      : CaseClassExtractor[Option[T]] = () =>
    CaseClassExtractor.apply[T].getCaseClasses()

  implicit def listCaseClassExtractor[T: CaseClassExtractor]
      : CaseClassExtractor[List[T]] = () =>
    CaseClassExtractor.apply[T].getCaseClasses()

  private def emptyCaseClassExtractor[T]: CaseClassExtractor[T] =
    new CaseClassExtractor[T] {
      override def getCaseClasses()
          : Either[List[FieldDescription], List[FieldDescription]] = Left(
        List.empty
      )
    }
}

trait CaseClassExtractorDerivation {
  type Typeclass[T] = CaseClassExtractor[T]

  private def decapitalize(str: String): String =
    str.headOption.map(_.toLower.toString ++ str.tail).getOrElse("")

  def join[T](ctx: CaseClass[CaseClassExtractor, T]): CaseClassExtractor[T] =
    new CaseClassExtractor[T] {
      override def getCaseClasses()
          : Either[List[FieldDescription], List[FieldDescription]] = {
        val typeClassName = ctx.typeName.short
        val elem = FieldDescription(decapitalize(typeClassName), typeClassName)
        val parameters = ctx.parameters.flatMap { param =>
          param.typeclass.getCaseClasses().merge
        }

        Right((elem +: parameters.toList).distinct)
      }
    }

  implicit def gen[T]: CaseClassExtractor[T] = macro Magnolia.gen[T]
}
