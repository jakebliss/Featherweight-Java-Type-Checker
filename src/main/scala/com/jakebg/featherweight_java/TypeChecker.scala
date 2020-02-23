package com.jakebg.featherweight_java

import scala.collection.Map

object TypeChecker {

  def checkType(ast: Exp, gammaMap: Map[VariableExp, Type]) : Type = {
    ast match {

//      case (ast: BinaryExp) =>
//        ast.operator match {
//          case "+" => return additionStep(ast.left, ast.right, gammaMap)
//        }
//      case (ast: ApplicationExp) =>
//        applicationStep(ast.leftExp, ast.rightExp, gammaMap)
//      case (ast: AbstractionExp) =>
//        abstractionStep(ast.parameter, ast.parType, ast.body, gammaMap)
//      case (ast: VariableExp) =>
//        return gammaMap.getOrElse(ast, Type(ExpType.undefined))
//      case (ast: NatNumExp) =>
//        return Type(ExpType.nat)
    }
  }

//    def additionStep(left: Exp, right: Exp, gammaMap: Map[VariableExp, Type]): Type = {
//      (left, right) match {
//        case (left: NatNumExp, right: NatNumExp) =>
//          return Type(ExpType.nat)
//        case (left: NatNumExp, right: Exp) =>
//          if (checkType(right, gammaMap) == Type(ExpType.nat))
//            return Type(ExpType.nat)
//          else
//            return Type(ExpType.undefined)
//        case (left: Exp, right: Exp) =>
//          if (checkType(left, gammaMap) == Type(ExpType.nat) && checkType(right, gammaMap) == Type(ExpType.nat))
//            return Type(ExpType.nat)
//          else
//            return Type(ExpType.undefined)
//      }
//    }
//
//    def abstractionStep(parameter: VariableExp, parType: Type, body: Exp, gammaMap: Map[VariableExp, Type]): Type = {
//      val newGammaMap = gammaMap + (parameter -> parType)
//      (parameter, body) match {
//        case (paramter: VariableExp, body: NatNumExp)  =>
//          return Type(ExpType.arrow, parType, Type(ExpType.nat))
//        case (paramter: VariableExp, body: Exp)  =>
//
//          return Type(ExpType.arrow, parType, checkType(body, newGammaMap))
//        case (parameter: Exp, body: Exp) =>
//          return Type(ExpType.undefined)
//      }
//    }
//
//
//   def applicationStep(leftExp: Exp, rightExp: Exp, gammaMap: Map[VariableExp, Type]): Type = {
//      (leftExp, rightExp) match {
//        case (leftExp: AbstractionExp, rightExp: Exp)  =>
//          val left = checkType(leftExp, gammaMap)
//
////          println(left)
////          println(checkType(rightExp, newGammaMap))
//          if(left.leftType == checkType(rightExp, gammaMap)) {
//            return left.rightType
//          }
//          return Type(ExpType.undefined)
//        case(leftExp: VariableExp, rightExp: Exp) =>
//          val left = gammaMap.getOrElse(leftExp, Type(ExpType.undefined))
//
//          if(left.expType == ExpType.arrow) {
//            if(left.leftType == checkType(rightExp, gammaMap)) {
//              return left.rightType
//            }
//            return Type(ExpType.undefined)
//          } else {
//            return Type(ExpType.undefined)
//          }
//        case (leftExp: ApplicationExp, rightExp: Exp) =>
//          val left = checkType(leftExp, gammaMap)
//
//          if(left.expType == ExpType.arrow) {
//            if(left.leftType == checkType(rightExp, gammaMap)) {
//              return left.rightType
//            }
//            return Type(ExpType.undefined)
//          } else {
//            return Type(ExpType.undefined)
//          }
//        case (leftExp: NatNumExp, rightExp: Exp) =>
//          return Type(ExpType.undefined)
//        case (leftExp: Exp, rightExp: Exp) =>
//          return Type(ExpType.undefined)
//      }
//    }
}
