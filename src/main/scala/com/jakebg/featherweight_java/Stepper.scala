package com.jakebg.featherweight_java

object Stepper {

  def resolveAST(ast: Exp): Exp = {
    var steppedAST = ast
    var x = 0
    while(!steppedAST.isInstanceOf[NatNumExp]) {
      steppedAST = step(steppedAST)
      x += 1
    }
    return steppedAST
  }

  def step(ast: Exp) : Exp = {
    ast match {

//      case (ast: BinaryExp) =>
//        ast.operator match {
//          case "+" => return additionStep(ast.left, ast.right)
//        }
//
//      case (ast: ApplicationExp) =>
//        return applicationStep(ast.leftExp, ast.rightExp)

      case (ast: Exp) =>
        return ast
    }
  }

//  def additionStep(left: Exp, right: Exp) : Exp = {
//    (left, right) match {
//      case (left: NatNumExp, right: NatNumExp)  =>
//        // ADD 3
//        return NatNumExp(left.value + right.value)
//      case (left: NatNumExp, right: Exp) =>
//        // ADD 2
//        return BinaryExp("+", left, step(right))
//      case (left: AbstractionExp, right: Exp) =>
//        // ADD 2
//        return BinaryExp("+", left, step(right))
//      case(left: Exp, right: Exp) =>
//        // ADD 1
//        return BinaryExp("+", step(left), right)
//    }
//  }
//
//  def applicationStep(left: Exp, right: Exp): Exp = {
//    (left, right) match {
//      case (left: AbstractionExp, right: NatNumExp)  =>
//        // APP3
//        return(substituteVariable(left.body, left.parameter, right))
//      case (left: AbstractionExp, right: AbstractionExp)  =>
//        // APP3
//        return(substituteVariable(left.body, left.parameter, right))
//      case (left: AbstractionExp, right: Exp) =>
//        // APP2
//        return ApplicationExp(left, step(right))
//      case(left: Exp, right: Exp) =>
//        // APP1
//        return ApplicationExp(step(left), right)
//    }
//  }
//
//  def substituteVariable(ast: Exp, variable: VariableExp, value: Exp): Exp = {
//    ast match {
//      case ast: NatNumExp  =>
//        return ast
//      case ast: VariableExp =>
//        if(ast == variable) {
//          return value
//        }
//        return ast
//      case ast: AbstractionExp =>
//        if(ast.parameter == variable) {
//          return ast
//        } else {
//          return AbstractionExp(ast.parameter, ast.parType, substituteVariable(ast.body, variable, value))
//        }
//      case ast: ApplicationExp =>
//        return ApplicationExp(substituteVariable(ast.leftExp, variable, value),
//                                substituteVariable(ast.rightExp, variable, value))
//      case ast: BinaryExp =>
//        return BinaryExp(ast.operator, substituteVariable(ast.left, variable, value),
//                                        substituteVariable(ast.right, variable, value))
//    }
//  }

}
