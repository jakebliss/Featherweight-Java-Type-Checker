package testing

import org.scalatest.FunSuite

// Unit tests for type rule checkers
class TypeRulesTests extends FunSuite {
//  // Checks that the type checker correctly identifies the type of a natural number expression (NUM)
//  test( "Type check for natural number expression") {
//    val ast = NatNumExp(5)
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === Type(ExpType.nat))
//  }
//
//  // Checks that the type checker correctly identifies the type of a binary addition expression (ADD)
//  test( "Type check for binary addition expression") {
//    val ast = BinaryExp("+", NatNumExp(3), NatNumExp(4))
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === Type(ExpType.nat))
//  }
//
//  // Checks that the type checker correctly identifies the type of a variable expression that holds a natural number (VAR)
//  test( "Type check for natural number variable") {
//    val ast = VariableExp("x")
//    val astType = TypeChecker.checkType(ast, Map(VariableExp("x") -> Type(ExpType.nat)))
//    assert(astType === Type(ExpType.nat))
//  }
//
//  // Checks that the type checker correctly identifies the type of a variable expression that holds an arrow (VAR)
//  test( "Type check for arrow variable") {
//    val ast = VariableExp("x")
//    val astType = TypeChecker.checkType(ast, Map(VariableExp("x") -> Type(ExpType.arrow, Type(ExpType.nat), Type(ExpType.nat))))
//    assert(astType === Type(ExpType.arrow, Type(ExpType.nat), Type(ExpType.nat)))
//  }
//
//  // Checks that the type checker correctly identifies the type of an abstraction the parameter is of type nat and the
//  // expression body returns a natural number (ABS)
//  test( "Type check for abstraction (nat->nat)") {
//    val ast = AbstractionExp(VariableExp("x"), Type(ExpType.nat), BinaryExp("+", VariableExp("x"), NatNumExp(1)))
//    val astType = TypeChecker.checkType(ast, Map(VariableExp("x") -> Type(ExpType.nat)))
//    assert(astType === Type(ExpType.arrow, Type(ExpType.nat), Type(ExpType.nat)))
//  }
//
//  // Checks that the type checker correctly identifies the type of an application where e1 is of type nat -> nat
//  // and e2 is of type nat (APP)
//  test( "Type check for realistic arrow variable") {
//    val ast = ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat),
//      BinaryExp("+", VariableExp("x"), NatNumExp(1))), NatNumExp(2))
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === Type(ExpType.nat))
//  }
//
//  // Checks that the type checker correctly identifies the type of an add expression with one value
//  // and one arithmetic expression where ae is on right side
//  test( "Type check addition expression with one ae (ae right)") {
//    val ast = BinaryExp("+", NatNumExp(3), BinaryExp("+", NatNumExp(2), NatNumExp(3)))
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === Type(ExpType.nat))
//  }
//
//  // Checks that the type checker correctly identifies the type of an add expression with one value expression
//  // and one arithmetic expression where ae is on left side
//  test( "Type check addition expression with one ae (ae left)") {
//    val ast = BinaryExp("+", BinaryExp("+", NatNumExp(2), NatNumExp(3)), NatNumExp(6))
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === Type(ExpType.nat))
//  }
//
//  // Checks that the type checker correctly identifies the type of an add expression with a application on the left
//  // and an binary expression on the right
//  test( "Type check addition left: abstraction, right: binary expression") {
//    val ast = BinaryExp("+", ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat),
//      BinaryExp("+", VariableExp("x"), NatNumExp(6))), NatNumExp(3)),
//      BinaryExp("+", NatNumExp(1), NatNumExp(2)))
//
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === Type(ExpType.nat))
//  }
//
//  // Checks that the type checker correctly identifies the type of an add expression with an add expression on the
//  // left and right
//  test( "Type check addition expression left: binary, right: binary") {
//    val ast = BinaryExp("+", BinaryExp("+", NatNumExp(2), NatNumExp(3)),
//      BinaryExp("+", NatNumExp(6), NatNumExp(8)))
//
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === Type(ExpType.nat))
//  }
//
//  // Checks that the type checker correctly identifies the type of an addition expression with an application expression
//  // on the left and an addition expression on the right
//  test( "Type check addition expression left: application, right: binary") {
//    val ast = BinaryExp("+", ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat),
//      BinaryExp("+", VariableExp("x"), NatNumExp(4))), NatNumExp(9)),
//      BinaryExp("+", NatNumExp(3), NatNumExp(2)))
//
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === Type(ExpType.nat))
//  }
//
//  // Checks that the type checker correctly identifies the type of an application expression with a abstraction on the
//  // left and a natural number on the right.
//  test("Type check application with natural number replacement") {
//    val ast = ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat),
//      BinaryExp("+", VariableExp("x"), NatNumExp(6))), NatNumExp(4))
//
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === Type(ExpType.nat))
//  }
//
//  // Checks that the type checker correctly identifies the type of an application expression with a abstraction on the
//  // left and the right.
//  test("Type check application with abstraction replacement") {
//    val ast = ApplicationExp(AbstractionExp(VariableExp("x"),
//      Type(ExpType.arrow, Type(ExpType.nat), Type(ExpType.nat)),
//      ApplicationExp(VariableExp("x"), NatNumExp(3))),
//      AbstractionExp(VariableExp("y"), Type(ExpType.nat), BinaryExp("+", VariableExp("y"), NatNumExp(8))))
//
//    val steppedAst = Stepper.step(ast)
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === Type(ExpType.nat))
//  }
//
//  // Checks that the type checker correctly identifies the type of an application expression with a abstraction on the
//  // left and a natural number on the right where the variable is reassigned further down the tree
//  test("Type check application expression with variable reassignment") {
//    val ast = ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat),
//      BinaryExp("+", VariableExp("x"), ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat),
//        BinaryExp("+", VariableExp("x"), NatNumExp(1))), NatNumExp(10)))), NatNumExp(4))
//
//    val steppedAst = Stepper.step(ast)
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === Type(ExpType.nat))
//  }
//
//  // Checks that the type checker correctly identifies the type of an application with a value on the left side and an
//  // expression on the right side
//  test("Type check application expression with lhs: value, rhs: expression") {
//    val ast = ApplicationExp(AbstractionExp(VariableExp("x"), parType = Type(ExpType.nat),
//      BinaryExp("+", VariableExp("x"), NatNumExp(3))),  BinaryExp("+", NatNumExp(2), NatNumExp(1)))
//
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === Type(ExpType.nat))
//  }
//
//  // Checks that the type checker correctly identifies the type of an application with a expression on the left side
//  // and on the right side
//  test("Type check application expression with lhs: expression, rhs: expression") {
//    val ast = ApplicationExp(ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat),
//      AbstractionExp(VariableExp("y"), Type(ExpType.nat),
//        BinaryExp("+", VariableExp("x"), VariableExp("y")))), NatNumExp(6)), NatNumExp(8))
//
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === Type(ExpType.nat))
//  }
//
//  //TODO: Write failure tests
//  // Checks that the type check fails if the variable is not in the map
//  test( "Type check for unmapped variable") {
//    val ast = VariableExp("x")
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === Type(ExpType.undefined))
//  }
//
//  // Checks that an add expression with a natural number on the left and an abstraction on the right will not be well typed
//  test("Type check for addition expression with abstraction operand on right") {
//    val ast = BinaryExp("+", NatNumExp(5), AbstractionExp(VariableExp("x"), Type(ExpType.nat), NatNumExp(3)))
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === Type(ExpType.undefined))
//  }
//
//  // Checks that an add expression with a natural number on the right and an abstraction on the left will not be well typed
//  test("Type check for addition expression with abstraction operand on left") {
//    val ast = BinaryExp("+", AbstractionExp(VariableExp("x"), Type(ExpType.nat), NatNumExp(3)), NatNumExp(5))
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === Type(ExpType.undefined))
//  }
//
//  // Checks that an add expression with a abstraction on the right and left will not be well typed
//  test("Type check for addition expression with abstraction operand on both sides") {
//    val ast = BinaryExp("+", AbstractionExp(VariableExp("x"), Type(ExpType.nat), NatNumExp(3)),
//                              AbstractionExp(VariableExp("y"), Type(ExpType.nat), NatNumExp(4)))
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === Type(ExpType.undefined))
//  }
//
//  // Checks that an application with a left expression that has a type of nat will not be well typed
//  test("Type check for application with nat number on left") {
//    val ast = ApplicationExp(NatNumExp(3), NatNumExp(4))
//
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === Type(ExpType.undefined))
//  }
//
//  // Checks that an application with a right type that mismatches the parameter type on the left will not be well typed
//  test("Type check for application with parameter right type mismatch") {
//    val ast = ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat), NatNumExp(3)),
//                              AbstractionExp(VariableExp("y"), Type(ExpType.nat), NatNumExp(4)))
//
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === Type(ExpType.undefined))
//  }
}
