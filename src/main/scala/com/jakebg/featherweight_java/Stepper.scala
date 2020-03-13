package com.jakebg.featherweight_java

import scala.collection.mutable
import scala.collection.mutable.Map

object Stepper {
  var classMap = Map("Object" -> List.empty[(String,String)])
  var methodBodyMap = Map[(String, String), (List[VariableExp], Exp)]()
  var classDefinitions: List[ClassExp] = _

  def initialize (classDefList: List[ClassExp]): Unit = {
    classDefinitions = classDefList

    for (classDef <- classDefinitions) {
      classMap = classMap + (classDef.className -> classDef.parameterList)

      for (method <- classDef.methods) {
        var parameters = List[VariableExp]()
        for (parameter <- method.parameterList) {
          parameters = parameters :+ parameter.asInstanceOf[(String, VariableExp)]._2
        }
        methodBodyMap = methodBodyMap + ((method.methodName, classDef.className) -> (parameters, method.functionBody))
      }
    }
  }

  def resolveAST(ast: Exp): Exp = {
    var steppedAST = step(ast)
    while(!steppedAST.isInstanceOf[ObjectCreationExp]) {
      steppedAST = step(steppedAST)
    }
    steppedAST = step(steppedAST)
    return steppedAST
  }

  def step(ast: Exp) : Exp = {
    ast match {

      case (ast: FieldAccessExp) =>
        return fieldAccessStep(ast.term, ast.fieldName)

      case (ast: MethodInvocationExp) =>
        return methodInvocationStep(ast.term, ast.methodName, ast.parameters)

      case (ast: CastExp) =>
        return castStep(ast.term, ast.className)

      case (ast: ObjectCreationExp) =>
        return objectCreationStep(ast)
    }
  }

  def fieldAccessStep(term: Exp, fieldName: String) : Exp = {
    (term) match {
      case (term: ObjectCreationExp) =>
        getFields(term.className) match {
          case Some(fieldList) =>
            var index = 0

            for (field <- fieldList) {
              if (field.asInstanceOf[(String, String)]._2 == fieldName) {
                return term.parameters(index)
              }
              index += 1
            }
            println ("Field not found")
            return null
          case None =>
            println ("Invalid class name")
            return null
        }
      case (term: Exp) => {
        return FieldAccessExp(step(term), fieldName)
      }
    }
  }

  def methodInvocationStep(term: Exp, methodName: String,  parameters: List[Exp]): Exp = {
    (term) match {
      case (term: FieldAccessExp) =>
        return MethodInvocationExp(step(term), methodName, parameters)

      case (term: MethodInvocationExp) =>
        return MethodInvocationExp(step(term), methodName, parameters)

      case (term: CastExp) =>
        return MethodInvocationExp(step(term), methodName, parameters)

      case (term: ObjectCreationExp) =>
        if(!parameters.isEmpty) {
          var newParameters = List[Exp]()
          for(parameter <- parameters) {
            newParameters = newParameters :+ step(parameter)
          }
          if(parameters != newParameters) {
            return MethodInvocationExp(term, methodName, newParameters)
          }
        }

        getMethod(methodName, term.className) match {
          case Some(method) =>
            val methodTuple = method.asInstanceOf[(List[VariableExp], Exp)]
            var variableList = methodTuple._1
            variableList = variableList
            var body = methodTuple._2

            for (index <- 0 until variableList.size) {
              body = substituteVariables(body, variableList(index), parameters(index))
            }
            body = substituteVariables(body, VariableExp("this"), term)
            return body
          case None =>
            println("Method does not exist")
            return null;
        }

      case (term: Exp) => {
        return term
      }
    }
  }

  def objectCreationStep(term: ObjectCreationExp): Exp = {
    if (term.parameters.isEmpty){
      return term
    }
    else {
      var newParameters = List[Exp]()
      for(parameter <- term.parameters) {
        newParameters = newParameters :+ step(parameter)
      }
      return ObjectCreationExp(term.className, newParameters)
    }
  }

  def castStep(term: Exp, castClassName: String): Exp = {
    (term) match {
      case(term: ObjectCreationExp) =>
        if(checkIfSubtype(term.className, castClassName)) {
          return ObjectCreationExp(castClassName, term.parameters)
        }
        else {
          println("Cast failed")
          return CastExp(castClassName, term)
        }

      case(term: Exp) =>
        return CastExp(castClassName, step(term))
    }
  }

  def substituteVariables(ast: Exp, variable: VariableExp, value: Exp): Exp = {
    ast match {
      case ast: ObjectCreationExp  =>
        var newParameters = List[Exp]()
        for(parameter <- ast.parameters) {
          newParameters = newParameters :+ substituteVariables(parameter, variable, value)
        }
        return ObjectCreationExp(ast.className, newParameters)

      case ast: VariableExp =>
        if(ast == variable) {
          return value
        }
        return ast
      case ast: FieldAccessExp =>
        return FieldAccessExp(substituteVariables(ast.term, variable, value), ast.fieldName)
      case ast: MethodInvocationExp =>
        var newParameters = List[Exp]()
        for(parameter <- ast.parameters) {
          newParameters = newParameters :+ substituteVariables(parameter, variable, value)
        }
        return MethodInvocationExp(substituteVariables(ast.term, variable, value), ast.methodName, newParameters)
      case ast: CastExp =>
        return CastExp(ast.className, substituteVariables(ast.term, variable, value))
    }
  }

  def checkIfSubtype(originalClass: String, castClass: String): Boolean = {
    if(originalClass == castClass) {
      return true;
    }

    var queue = mutable.Queue(originalClass)

    while(!queue.isEmpty) {
      val curClass = queue.dequeue()
      for (classDef <- classDefinitions) {
        if (classDef.className == curClass)
          if (classDef.superClass == castClass) {
            return true
          }
          else {
            queue.enqueue(classDef.superClass)
          }
      }
    }
    return false
  }

  def getFields(className: String): Option[List[(String, String)]] = {
    return classMap.get(className)
  }

  def getMethod(methodName: String, className: String): Option[(List[VariableExp], Exp)] = {
      methodBodyMap.get((methodName, className)) match {
        case Some(method) =>
          return Some(method)
        case None =>
          for(classDef <- classDefinitions) {
            if(classDef.className == className) {
              return getMethod(methodName, classDef.superClass)
            }
          }
          return None
      }
  }
}
