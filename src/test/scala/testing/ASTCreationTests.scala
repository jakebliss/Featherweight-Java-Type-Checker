package testing

import com.jakebg.featherweight_java.{CastExp, ClassExp, ConstructorExp, Exp, FieldAccessExp, MethodExp, MethodInvocationExp, ObjectCreationExp, VariableExp}
import org.scalatest.FunSuite

// These are Unit tests for the creation of an AST
class ASTCreationTests extends FunSuite {
  //Test for the creation of a class declaration expresion
  test("ClassExp Creation") {
    val ast = ClassExp("TestClass", "SuperClass", List(("Object", "value")),
      ConstructorExp("TestClass", List(("Object", "value")), List("testField")),
      List(MethodExp("TestClass", "testMethod", List(("Object", "value")), VariableExp("x"))))
    assert(ast.className === "TestClass")
    assert(ast.superClass === "SuperClass")
    assert(ast.parameterList(0) === ("Object", "value"))
    assert(ast.constructor === ConstructorExp("TestClass", List(("Object", "value")), List("testField")))
    assert(ast.methods(0) === MethodExp("TestClass", "testMethod", List(("Object", "value")), VariableExp("x")))
  }

  // Test for the creation of a constructor declaration expression
  test("ConstructorExp Creation") {
    val ast = ConstructorExp("TestClass", List(("Object", "value")), List("testField"))
    assert(ast.className === "TestClass")
    assert(ast.parameterList(0) === ("Object", "value"))
    assert(ast.fields(0) == "testField")
  }

  // Test for the creation of a method declaration expression
  test("MethodExp Creation") {
    val ast = MethodExp("TestClass", "testMethod", List(("Object", "value")), VariableExp("x"))
    assert(ast.className === "TestClass")
    assert(ast.methodName === "testMethod")
    assert(ast.parameterList(0) === ("Object", "value"))
    assert(ast.functionBody === VariableExp("x"))
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
    assert(ast.parameter(0) === VariableExp("x"))
  }

  // Test for the creation of cast expression
  test("CastExp Creation") {
    val ast = CastExp("TestClass", VariableExp("x"))
    assert(ast.className === "TestClass")
    assert(ast.term === VariableExp("x"))
  }
}

