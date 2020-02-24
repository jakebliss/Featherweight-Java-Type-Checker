package testing

import org.scalatest.FunSuite

// Unit tests for semantic step and resolution rules
class SemanticRulesTests extends FunSuite {
//  // Test to resolve an addition expression to its sum (ADD3)
//  test( "Resolve addition expression") {
//    val ast = BinaryExp("+", NatNumExp(3), NatNumExp(4))
//    val astVal = Stepper.step(ast).asInstanceOf[NatNumExp].value
//    assert(astVal === 7)
//  }
//
//  // Test to take a step from an add expression with one value (ADD2)
//  // and one arithmetic expression where ae is on right side
//  test( "Step addition expression with one ae (ae right)") {
//    val ast = BinaryExp("+", NatNumExp(3), BinaryExp("+", NatNumExp(2), NatNumExp(3)))
//    val steppedAst = Stepper.step(ast)
//    assert(steppedAst === BinaryExp("+", NatNumExp(3), NatNumExp(5)))
//  }
//
//  // Test to take a step from an add expression with one value expression (ADD2)
//  // and one arithmetic expression where ae is on left side
//  test( "Step addition expression with one ae (ae left)") {
//    val ast = BinaryExp("+", BinaryExp("+", NatNumExp(2), NatNumExp(3)), NatNumExp(6))
//    val steppedAst = Stepper.step(ast)
//    assert(steppedAst === BinaryExp("+", NatNumExp(5), NatNumExp(6)))
//  }
//
//  // Test to take a step from an add expression with a abstraction on the left and an binary expression on the right (ADD2)
//  test( "Step addition left: abstraction, right: binary expression") {
//    val ast = BinaryExp("+", ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat),
//      BinaryExp("+", VariableExp("x"), NatNumExp(6))), NatNumExp(3)),
//      BinaryExp("+", NatNumExp(1), NatNumExp(2)))
//
//    val steppedAst = Stepper.step(ast)
//    assert(steppedAst === BinaryExp("+", BinaryExp("+", NatNumExp(3), NatNumExp(6)),
//      BinaryExp("+", NatNumExp(1), NatNumExp(2))))
//  }
//
//  // Test to take a step from an add expression with an add expression on the left and right (ADD1)
//  test( "Step addition expression left: binary, right: binary") {
//    val ast = BinaryExp("+", BinaryExp("+", NatNumExp(2), NatNumExp(3)),
//                              BinaryExp("+", NatNumExp(6), NatNumExp(8)))
//    val steppedAst = Stepper.step(ast)
//    assert(steppedAst === BinaryExp("+", NatNumExp(5), BinaryExp("+", NatNumExp(6), NatNumExp(8))))
//  }
//
//  // Test to take a step from an application expression on the left and an addition expression on the right (ADD1)
//  test( "Step addition expression left: application, right: binary") {
//    val ast = BinaryExp("+", ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat),
//                        BinaryExp("+", VariableExp("x"), NatNumExp(4))), NatNumExp(9)),
//                            BinaryExp("+", NatNumExp(3), NatNumExp(2)))
//
//    val steppedAst = Stepper.step(ast)
//    assert(steppedAst === BinaryExp("+", BinaryExp("+", NatNumExp(9), NatNumExp(4)),
//                            BinaryExp("+", NatNumExp(3), NatNumExp(2))))
//  }
//
//  // Test to take a step from an application expression with a abstraction on the left and a natural number on the right.
//  // Test should replace all instances of x with 4 (APP3)
//  test("Step application expression with natural number replacement") {
//    val ast = ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat),
//      BinaryExp("+", VariableExp("x"), NatNumExp(6))), NatNumExp(4))
//
//    val steppedAst = Stepper.step(ast)
//    assert(steppedAst === BinaryExp("+", NatNumExp(4), NatNumExp(6)))
//  }
//
//  // Test to take a step from an application expression with a abstraction on the left and the right.
//  // Test should replace all instances of x with abstraction on right (APP3)
//  test("Step application expression with abstraction replacement") {
//    val ast = ApplicationExp(AbstractionExp(VariableExp("x"),
//                              Type(ExpType.arrow, Type(ExpType.nat), Type(ExpType.nat)),
//                                ApplicationExp(VariableExp("x"), NatNumExp(3))),
//                            AbstractionExp(VariableExp("y"), Type(ExpType.nat), BinaryExp("+", VariableExp("y"), NatNumExp(8))))
//
//    val steppedAst = Stepper.step(ast)
//    assert(steppedAst === ApplicationExp(AbstractionExp(VariableExp("y"), Type(ExpType.nat),
//                            BinaryExp("+", VariableExp("y"), NatNumExp(8))), NatNumExp(3)))
//  }
//
//  // Test to take a step from an application expression with a abstraction on the left and a natural number on the right
//  // where there are multiple instances of the variable to replace (APP3)
//  test("Step application expression with multiple natural number replacement") {
//    val ast = ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat),
//      BinaryExp("+", VariableExp("x"), BinaryExp("+", NatNumExp(2), VariableExp("x")))), NatNumExp(4))
//
//    val steppedAst = Stepper.step(ast)
//    assert(steppedAst === BinaryExp("+", NatNumExp(4), BinaryExp("+", NatNumExp(2), NatNumExp(4))))
//  }
//
//
//  // Test to take a step from an application expression with a abstraction on the left and a natural number on the right
//  // where the variable is reassigned further down the tree (APP3)
//  test("Step application expression with variable reassignment") {
//    val ast = ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat),
//              BinaryExp("+", VariableExp("x"), ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat),
//                BinaryExp("+", VariableExp("x"), NatNumExp(1))), NatNumExp(10)))), NatNumExp(4))
//
//    val steppedAst = Stepper.step(ast)
//    assert(steppedAst === BinaryExp("+", NatNumExp(4), ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat),
//                            BinaryExp("+", VariableExp("x"), NatNumExp(1))), NatNumExp(10))))
//  }
//
//  // Test to resolve an application expression with a abstraction on the left and a natural number on the right
//  // where the variable is reassigned further down the tree
//  test("Resolve application expression with variable reassignment") {
//    val ast = ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat),
//      BinaryExp("+", VariableExp("x"), ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat),
//        BinaryExp("+", VariableExp("x"), NatNumExp(1))), NatNumExp(10)))), NatNumExp(4))
//
//    val astVal = Stepper.resolveAST(ast).asInstanceOf[NatNumExp].value
//    assert(astVal === 15)
//  }
//
//  // Test to take a step from an application with a value on the left side and an expression on the right side (APP2)
//  test("Step application expression with lhs: value, rhs: expression") {
//    val ast = ApplicationExp(AbstractionExp(VariableExp("x"), parType = Type(ExpType.nat),
//              BinaryExp("+", VariableExp("x"), NatNumExp(3))),  BinaryExp("+", NatNumExp(2), NatNumExp(1)))
//
//    val steppedAst = Stepper.step(ast)
//    assert(steppedAst === ApplicationExp(AbstractionExp(VariableExp("x"), parType = Type(ExpType.nat),
//      BinaryExp("+", VariableExp("x"), NatNumExp(3))),  NatNumExp(3)))
//  }
//
//  // Test to take a step from an application with a expression on the left side and on the right side (APP1)
//  test("Step application expression with lhs: expression, rhs: expression") {
//    val ast = ApplicationExp(ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat),
//                AbstractionExp(VariableExp("y"), Type(ExpType.nat),
//                  BinaryExp("+", VariableExp("x"), VariableExp("y")))), NatNumExp(6)), NatNumExp(8))
//
//    val steppedAst = Stepper.step(ast)
//    assert(steppedAst === ApplicationExp(AbstractionExp(VariableExp("y"), Type(ExpType.nat),
//                            BinaryExp("+", NatNumExp(6), VariableExp("y"))), NatNumExp(8)))
//  }
//
//  // Test to resolve an application with an addition expression as its body
//  test("Resolve application expression with simple addition body") {
//    val ast = ApplicationExp(AbstractionExp(VariableExp("x"), Type(ExpType.nat),
//              BinaryExp("+", VariableExp("x"), NatNumExp(6))), NatNumExp(4))
//
//    val astVal = Stepper.resolveAST(ast).asInstanceOf[NatNumExp].value
//    assert(astVal === 10)
//  }
}
