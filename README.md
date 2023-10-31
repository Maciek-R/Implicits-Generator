# MacroGenerator

This project contains macros which can be helpful with generating additional code.

@generateCachedImplicits(typeClassNames: Seq[String])

* This macro helps generate implicit val cached implicits for declared typeclasses.
* It can be very useful with handling 'method too large error'.
* This error can occur with case classes implemented in project contain many fields and have many nested levels.
* To deal with this problem there should be created intermediate cached implicit values, so the compiler does not have to create whole class at one time, but can store intermediate results

Usage:
 
This code:

    @generateCachedImplicits(Seq("Decoder"))

Should produce:

    case class TestClass(field: String)
    object TestClass {
      implicit lazy val testClassDecoder: Decoder[TestClass] = shapeless.cachedImplicit
    }

Cached implicits are stored in companion object.
This solution has some disadvantages. 
If the project is big and there are a lot of case classes then compilator can have problems with finding appropriate cached implicits.
Compilator first looks for implicits in scope and later it searches in companion objects.
To help compilator find appropriate implicits you will need to explicitly import cached implicits into scope.
Like in above example:

    import TestClass._