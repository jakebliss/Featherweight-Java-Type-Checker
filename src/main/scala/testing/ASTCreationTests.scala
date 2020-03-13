package testing

import com.jakebg.featherweight_java.{CastExp, ClassExp, ConstructorExp, Exp, FieldAccessExp, MethodExp, MethodInvocationExp, ObjectCreationExp, VariableExp}
import org.scalatest.FunSuite

// These are Unit tests for the creation of an AST
class ASTCreationTests extends FunSuite {
  //Test for the creation of a class declaration expresion
  test("ClassExp Creation") {
    val ast = ClassExp("Pair", "Object", List(("A", "fst"), ("B", "snd")),
      ConstructorExp("Pair", List(("A", "fst"), ("B", "snd")), List.empty, List("A","B")),
      List(MethodExp("Pair", "setfst", List(("Object", VariableExp("newfst"))), ObjectCreationExp("Pair", List(VariableExp("newfst"),
        FieldAccessExp(VariableExp("this"), "snd"))))))

    assert(ast.className === "Pair")
    assert(ast.superClass === "Object")
    assert(ast.parameterList(0) === ("A", "fst"))
    assert(ast.parameterList(1) === ("B", "snd"))
    assert(ast.constructor === ConstructorExp("Pair", List(("A", "fst"), ("B", "snd")), List.empty, List("A","B")))
    assert(ast.methods(0) === MethodExp("Pair", "setfst", List(("Object", VariableExp("newfst"))),
      ObjectCreationExp("Pair", List(VariableExp("newfst"), FieldAccessExp(VariableExp("this"), "snd")))))
  }

  // Test for the creation of a constructor declaration expression
  test("ConstructorExp Creation") {
    val ast = ConstructorExp("Pair", List(("A", "fst"), ("B", "snd")), List.empty, List("A","B"))
    assert(ast.className === "Pair")
    assert(ast.parameterList(0) === ("A", "fst"))
    assert(ast.parameterList(1) === ("B", "snd"))
    assert(ast.superFields === List.empty)
    assert(ast.fields(0) == "A")
    assert(ast.fields(1) == "B")
  }

  // Test for the creation of a method declaration expression
  test("MethodExp Creation") {
    val ast = MethodExp("Pair", "setfst", List(("Object", VariableExp("newfst"))), ObjectCreationExp("Pair", List(VariableExp("newfst"),
      FieldAccessExp(VariableExp("this"), "snd"))))
    assert(ast.className === "Pair")
    assert(ast.methodName === "setfst")
    assert(ast.parameterList(0) === ("Object", VariableExp("newfst")))
    assert(ast.functionBody === ObjectCreationExp("Pair", List(VariableExp("newfst"),
      FieldAccessExp(VariableExp("this"), "snd"))))
  }

  // Test for the creation of a variable expresion
  test("VariableExp Creation") {
    val ast = VariableExp("x")
    assert(ast.name === "x")
  }

  // Test for the creation of an object creation expression
  test("ObjectCreationExp Creation") {
    val ast = ObjectCreationExp("TestClass", List(VariableExp("x")))
    assert(ast.className === "TestClass")
    assert(ast.parameters(0) === VariableExp("x"))
  }

  // Test for the creation of a field access expression
  test("FieldAccessExp Creation") {
    val ast = FieldAccessExp(ObjectCreationExp("TestClass", List(VariableExp("x"))), "parOne")
    assert(ast.term === ObjectCreationExp("TestClass", List(VariableExp("x"))))
    assert(ast.fieldName === "parOne")
  }

  // Test for the creation of a method invocation expression
  test("MethodInvocationExp Creation") {
    val ast = MethodInvocationExp(ObjectCreationExp("TestClass", List(VariableExp("x"))), "testMethod",
                  List(VariableExp("x")))
    assert(ast.term === ObjectCreationExp("TestClass", List(VariableExp("x"))))
    assert(ast.methodName === "testMethod")
    assert(ast.parameters(0) === VariableExp("x"))
  }

  // Test for the creation of cast expression
  test("CastExp Creation") {
    val ast = CastExp("TestClass", VariableExp("x"))
    assert(ast.className === "TestClass")
    assert(ast.term === VariableExp("x"))
  }
}

