package com.mrscalalib.macros

import cats.data.NonEmptyList

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

case class CaseClassDescription(fieldName: String, fieldType: String)

/** Macro is returning all nested case class names at compile time for given Type
  *
  * Example:
  * {{{
  * case class TestClassMain(t1: TestClass1, t2: Option[TestClass2])
  * case class TestClass1(t11: List[TestClass11])
  * case class TestClass11(str: String)
  * case class TestClass2(int: Int)
  *
  * CaseClassInspector.findAllCaseClasses[TestClassMain]
  * }}}
  * It should return:
  * {{{
  * List(
  *   CaseClassDescription("testClassMain", "TestClassMain"),
  *   CaseClassDescription("testClass1", "TestClass1"),
  *   CaseClassDescription("testClass11", "TestClass11"),
  *   CaseClassDescription("testClass2", "TestClass2")
  * )
  * }}}
  */
object CaseClassInspector {

  def findAllCaseClasses[T]: List[CaseClassDescription] =
    macro findAllCaseClassesImpl[T]

  def findAllCaseClassesImpl[T: c.WeakTypeTag](
      c: blackbox.Context
  ): c.Expr[List[CaseClassDescription]] = {
    import c.universe._

    implicit val liftCaseClassDescription = new Liftable[CaseClassDescription] {
      override def apply(ccd: CaseClassDescription): c.universe.Tree =
        q"_root_.com.mrscalalib.macros.CaseClassDescription(${ccd.fieldName}, ${ccd.fieldType})"
    }

    def isOneOfTypedClasses(tpe: Type): Boolean = {
      List(
        weakTypeOf[Option[_]],
        weakTypeOf[List[_]],
        weakTypeOf[NonEmptyList[_]]
      ).exists(tpe <:< _)
    }

    def getAllNestedCaseClasses(tpe: Type): List[CaseClassDescription] = {
      if (isOneOfTypedClasses(tpe)) {
        tpe.typeArgs.headOption
          .map(getAllNestedCaseClasses)
          .getOrElse(List.empty)
      } else {
        val symbol = tpe.typeSymbol
        if (symbol.isClass && symbol.asClass.isCaseClass) {
          val params =
            symbol.asClass.primaryConstructor.asMethod.paramLists.flatten
          val innerTypes = params.flatMap(param => getAllNestedCaseClasses(param.typeSignature))
          val typeName = symbol.typeSignature.typeSymbol.name.toString
          CaseClassDescription(decapitalize(typeName), typeName) +: innerTypes
        } else {
          List.empty
        }
      }
    }

    val tpe = weakTypeOf[T]
    val result = getAllNestedCaseClasses(tpe).distinct

    c.Expr[List[CaseClassDescription]](q"$result")
  }

  private def decapitalize(str: String) =
    str.headOption.map(_.toLower.toString ++ str.tail).getOrElse("")
}
