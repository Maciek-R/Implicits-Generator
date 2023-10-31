package com.macros.generator

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.internal.Flags.{CASE, FINAL}
import scala.reflect.macros.whitebox

/**
 *
 * @param typeClassNames - names of type classes for which should be created cached implicits
 *
 * This macro creates lazy val cached implicit instance for given type classes
 * It can be used only on case classes or final case classes
 * This macro can be useful when dealing with big case classes and compilation throws 'method too large error'
 * To avoid this error there should be created intermediate cached values
 *
 * Given:
 * {{{
 *   @generateCachedImplicits(Seq("Decoder"))
 *   case class TestClass(field: String)
 * }}}
 * This should produce after compilation:
 * {{{
 *   case class TestClass(field: String)
 *   object TestClass {
 *     implicit lazy val testClassDecoder: Decoder[TestClass] = shapeless.cachedImplicit
 *   }
 * }}}
 *
 */

@compileTimeOnly("enable macro paradise to expand macro annotations")
class generateCachedImplicits(typeClassNames: Seq[String]) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro GeneratorCachedImplicits.impl
}

object GeneratorCachedImplicits {
  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val typeClassNames = c.prefix.tree match {
      case q"new generateCachedImplicits($typeClassNames)" => c.eval[Seq[String]](c.Expr(typeClassNames))
      case _ => c.abort(c.enclosingPosition, "Arguments used in macro should be of type Seq[String]")
    }

    def decapitalize(str: String) = {
      str.headOption.map(_.toLower.toString).getOrElse("") ++ str.tail
    }

    def createImplicitVal(className: String, typeClassName: String) = {
      val fieldName = TermName(decapitalize(s"${className}$typeClassName"))
      q"implicit lazy val $fieldName: ${TypeName(typeClassName)}[${TypeName(className)}] = shapeless.cachedImplicit"
    }

    def createCachedImplicits(className: String): Seq[Tree] = {
      typeClassNames.map(createImplicitVal(className, _))
    }

    def createCompanionObject(className:String): c.Tree = {
      val cachedImplicitVals = createCachedImplicits(className)
      val objectName = TermName(className)
      q"object $objectName {..$cachedImplicitVals}"
    }

    def enrichCompanionObject(companionObject: Tree): c.Tree = {
      companionObject match {
        case q"object $name extends { ..$extended } with ..$withed { $self => ..$stats }" =>
          val implicitVals = createCachedImplicits(name.toString())
          val newStats = stats ++ implicitVals
          q"object $name extends { ..$extended } with ..$withed { $self => ..$newStats }"
        case _ => c.abort(c.enclosingPosition, s"$companionObject is not a companion object")
      }
    }

    val tree = annottees.map(_.tree).toList match {
      case (caseClass @ q"${mods: ModifiersApi} class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$_ } with ..$_ { $self => ..$stats }") :: Nil
        if mods.flags == CASE || mods.flags == FINAL + CASE =>
        val companionObject = createCompanionObject(tpname.toString())

        val trees = List(caseClass, companionObject)
        q"..$trees"
      case (caseClass@q"${mods: ModifiersApi} class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$_ } with ..$_ { $self => ..$stats }") :: companionObject :: Nil
        if mods.flags == CASE || mods.flags == FINAL + CASE =>
        val enrichedCompanionObject = enrichCompanionObject(companionObject)

        val trees = List(caseClass, enrichedCompanionObject)
        q"..$trees"
      case _ =>
        c.abort(c.enclosingPosition, "generateCachedImplicits macro can be applied only on case classes")
    }
    c.Expr(tree)
  }
}
