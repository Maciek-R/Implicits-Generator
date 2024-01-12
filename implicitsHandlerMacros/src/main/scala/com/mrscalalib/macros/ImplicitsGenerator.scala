package com.mrscalalib.macros

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.collection.immutable.{AbstractSeq, LinearSeq}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

@compileTimeOnly("enable macro paradise to expand macro annotations")
class generateAllImplicits(typeClassNames: Seq[String], imports: Seq[String])
    extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ImplicitsGenerator.impl
}

object ImplicitsGenerator {

  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    def createImplicitVals(
        typeClassName: String,
        caseClassDescriptions: Seq[CaseClassDescription]
    ) = {
      caseClassDescriptions.map { caseClassDescription =>
        val implicitName =
          TermName(s"${caseClassDescription.fieldName}$typeClassName")
        val implicitTypeName = TypeName(caseClassDescription.fieldType)
        q"implicit lazy val $implicitName: ${TypeName(typeClassName)}[$implicitTypeName] = shapeless.cachedImplicit"
      }
    }

    def createTraitsWithImplicits(
        caseClassDescriptions: Seq[CaseClassDescription],
        typeClassNames: Seq[String]
    ): Seq[Tree] = {
      typeClassNames.map { typeClassName =>
        val implicitVals =
          createImplicitVals(typeClassName, caseClassDescriptions)
        val traitName = TypeName(s"${typeClassName}ImplicitsCache")
        q"trait $traitName {..$implicitVals}"
      }
    }

    val (typeClassNames, imports) = c.prefix.tree match {
      case q"new generateAllImplicits(..$values)" =>
        values match {
          case typeClassNames :: importsTree :: Nil =>
            val typeClasses = c.eval[Seq[String]](c.Expr(typeClassNames))
            val imports = c.eval[Seq[String]](c.Expr(importsTree))
            (typeClasses, imports)
          case _ =>
            c.abort(
              c.enclosingPosition,
              "'generateAllImplicits' macro takes 2 arguments of Seq[String] types"
            )
        }
      case _ => c.abort(c.enclosingPosition, "Macro not supported error")
    }

    val tree = annottees.map(_.tree).toList match {
      case declaration :: Nil =>
        val expression = declaration.children.last

        val importsTree = imports.map(c.parse(_))
        val caseClassDescriptionsTree =
          q"""
              ..$importsTree
              import com.macros.CaseClassDescription
              ..$expression
           """

        val caseClassDescriptions = c
          .eval[List[CaseClassDescription]](c.Expr(caseClassDescriptionsTree))
          .distinct

        val traits =
          createTraitsWithImplicits(caseClassDescriptions, typeClassNames)

        val allTraits = List(declaration) ++ traits
        q"..$allTraits"
      case _ =>
        c.abort(
          c.enclosingPosition,
          "generateImplicits macro can be applied only on val declaration"
        )
    }

    c.Expr(q"..$tree")
  }
}
