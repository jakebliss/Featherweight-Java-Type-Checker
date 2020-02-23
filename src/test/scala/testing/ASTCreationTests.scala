package testing

// These are Unit tests for the creation of an AST
class ASTCreationTests extends FunSuite {
  // Test for the creation of a natural number expresion
  test("NatNum Creation") {
    val ast = NatNumExp(5)
    assert(ast.value === 5)
  }

  // Test to make sure that we can only create natural numbers
  test("NatNum Must Be Greater Than Zero") {
    assertThrows[IllegalArgumentException] {
      val ast = NatNumExp(0)
    }
  }

  // Test for the creation of a binary expression
  test( "BinaryExp Creation") {
    val ast = BinaryExp("+", NatNumExp(3), NatNumExp(4))
    assert(ast.operator === "+")
    assert(ast.left === NatNumExp(3))
    assert(ast.right === NatNumExp(4))
  }

  // Test for the creation of an abstraction expression
  test("AbstractionExp Creation") {
    val ast = AbstractionExp(VariableExp("x"), Type(ExpType.nat), BinaryExp("+", VariableExp("x"), NatNumExp(1)))
    assert(ast.parameter === VariableExp("x"))
    assert(ast.parType === Type(ExpType.nat))
    assert(ast.body === BinaryExp("+", VariableExp("x"), NatNumExp(1)))
  }

  // Test for the creation of an application expresion
  test("ApplicationExp Creation") {
    val ast = ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat),
              BinaryExp("+", VariableExp("x"), NatNumExp(1))), NatNumExp(2))
    assert(ast.leftExp === AbstractionExp(VariableExp("x"), Type(ExpType.nat),
                        BinaryExp("+", VariableExp("x"), NatNumExp(1))))
    assert(ast.rightExp ===  NatNumExp(2))
  }

  // Test for the creation of a variable expresion
  test("VariableExp Creation") {
    val ast = VariableExp("x")
    assert(ast.name === "x")
  }
}

