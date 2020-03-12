package com.jakebg.featherweight_java

import com.jakebg.featherweight_java.Stepper.{classMap, methodBodyMap}

import scala.collection.{Map, mutable}

object TypeChecker {
  var classMap = Map("Object" -> List.empty[(String,String)])
  var methodBodyMap = Map[(String, String), (List[VariableExp], Exp)]()
  var methodTypeMap = Map[(String, String), (List[String], String)]()
  var classDefinitions: List[ClassExp] = _

  def initialize (classDefList: List[ClassExp]): Unit = {
    classDefinitions = classDefList

    for (classDef <- classDefinitions) {
      classMap = classMap + (classDef.className -> classDef.parameterList)

      for (method <- classDef.methods) {
        var parameters = List[VariableExp]()
        var parametersTypes = List[String]()
        for (parameter <- method.parameterList) {
          parameters = parameters :+ parameter.asInstanceOf[(String, VariableExp)]._2
          parametersTypes = parametersTypes :+ parameter.asInstanceOf[(String, VariableExp)]._1
        }
        methodBodyMap = methodBodyMap + ((method.methodName, classDef.className) -> (parameters, method.functionBody))
        methodTypeMap = methodTypeMap + ((method.methodName, classDef.className) -> (parametersTypes, method.className))
      }
    }
  }

  def checkType(ast: Exp, gammaMap: Map[VariableExp, String]) : String = {
    ast match {

      case (ast: VariableExp) =>
        return gammaMap.getOrElse(ast, "Undefined")

      case (ast: FieldAccessExp) =>
        return fieldAccessStep(ast.term, ast.fieldName, gammaMap)

      case (ast: MethodInvocationExp) =>
        return methodInvocationStep(ast.term, ast.methodName, ast.parameters, gammaMap)

      case (ast: ObjectCreationExp) =>
        return objectCreationStep(ast.className, ast.parameters, gammaMap)

      case (ast: CastExp) =>
        return castStep(ast.term, ast.className, gammaMap)

      case(ast: ClassExp) =>
        return checkClassDeclaration(ast.className, ast.superClass, ast.parameterList, ast.constructor, ast.methods, gammaMap)

      case(ast:MethodExp) =>
        return checkMethodDeclaration(ast.className, ast.methodName, ast.parameterList, ast.functionBody, gammaMap)
    }
  }

    def fieldAccessStep(term: Exp, fieldName: String, gammaMap: Map[VariableExp, String]): String = {
      val termType = checkType(term, gammaMap)

      getFields(termType) match {
        case Some(fieldList) =>
          for (field <- fieldList) {
            if (field.asInstanceOf[(String, String)]._2 == fieldName) {
              return field.asInstanceOf[(String, String)]._1
            }
          }
          return "Undefined"
        case None =>
          return "Undefined"
      }
    }

  def objectCreationStep(className: String, parameters: List[Exp], gammaMap: Map[VariableExp, String]): String = {
    var parameterTypes = List[String]()

    for (parameter <- parameters) {
      parameterTypes = parameterTypes :+ checkType(parameter, gammaMap)
    }

    getFields(className) match {
      case Some(fieldList) =>
        if (parameterTypes.size == fieldList.size) {
          for (index <- 0 until fieldList.size) {
            if(!checkIfSubtype(parameterTypes(index), fieldList(index)._1)) {
              return "Undefined"
            }
          }
          return className
        }
        else {
            return "Undefined"
        }
      case None =>
        return "Undefined"
    }
  }

  def methodInvocationStep(term: Exp, methodName: String, parameters: List[Exp],
                           gammaMap: Map[VariableExp, String]): String = {
    val termType = checkType(term, gammaMap)
    var parameterTypes = List[String]()

    for (parameter <- parameters) {
      parameterTypes = parameterTypes :+ checkType(parameter, gammaMap)
    }

    getMethodType(methodName, termType) match {
      case Some((fieldTypes, returnType)) =>
        if (parameterTypes.size == fieldTypes.size) {
          for (index <- 0 until fieldTypes.size) {
            if(!checkIfSubtype(parameterTypes(index), fieldTypes(index))) {
              return "Undefined"
            }
          }
          return returnType
        }
        else {
          return "Undefined"
        }
      case None =>
        return "Undefined"
    }
  }

  def castStep(term: Exp, className: String, gammaMap: Map[VariableExp, String]): String = {
    val termType = checkType(term, gammaMap)

    if(checkIfSubtype(termType, className)) {
      return className
    }
    else if(checkIfSubtype(className, termType)) {
      return className
    }
    else {
      println("Stupid Warning")
      return className
    }
  }

  def checkMethodDeclaration(returnType: String, methodName: String, parameterList: List[(String, VariableExp)],
                             body: Exp, gammaMap: Map[VariableExp, String]): String = {
    var newGammaMap = gammaMap
    var parameterTypes = List[String]()
    for(parameter <- parameterList) {
      newGammaMap = newGammaMap + (parameter._2 -> parameter._1)
      parameterTypes = parameterTypes :+ parameter._1
    }

    val bodyType = checkType(body, newGammaMap)
    val thisType = gammaMap.getOrElse(VariableExp("this"), null)

    for(classDef <- classDefinitions) {
      if(classDef.className == thisType) {
        if(checkIfSubtype(bodyType,returnType)) {
          if (checkOverride(methodName, thisType, parameterTypes, returnType)) {
            return "OK in " + thisType
          }
        }
        else {
          return "Undefined"
        }
      }
    }
    return "Undefined"
  }

  def checkClassDeclaration(className: String, superClassName: String, parameterList: List[(String, String)],
                            constructor: ConstructorExp, methods: List[MethodExp], gammaMap: Map[VariableExp, String]): String = {
    var newGammaMap = gammaMap
    newGammaMap = newGammaMap + (VariableExp("this") -> className)

    getFields(superClassName) match {
      case Some(fieldList) =>
        if ((parameterList.size + fieldList.size) == (constructor.superFields.size + constructor.fields.size)) {
          for (index <- 0 until fieldList.size) {
            if(fieldList(index)._2 != constructor.superFields(index)) {
              println("super field check failed")
              return "Undefined"
            }
          }
          for (index <- 0 until constructor.parameterList.size) {
            if(parameterList(index)._2 != constructor.fields(index)) {
              println("parameter check failed")
              return "Undefined"
            }
          }
          for (method <- methods) {
            if(checkMethodDeclaration(method.className, method.methodName, method.parameterList,
                                     method.functionBody, newGammaMap) != "OK in " + className) {
              println(checkMethodDeclaration(method.className, method.methodName, method.parameterList,
                method.functionBody, newGammaMap))
              println("method check failed")
              return "Undefined"
            }
          }
          return className + " OK"
        }
        else {
          return "Undefined"
        }
      case None =>
        return "Undefined"
    }
  }


  def checkIfSubtype(subClass: String, superClass: String): Boolean = {
    if(subClass == superClass) {
      return true;
    }

    var queue = mutable.Queue(subClass)

    while(!queue.isEmpty) {
      val curClass = queue.dequeue()
      for (classDef <- classDefinitions) {
        if (classDef.className == curClass)
          if (classDef.superClass == superClass) {
            return true
          }
          else {
            queue.enqueue(classDef.superClass)
          }
      }
    }
    return false
  }

  def checkOverride(methodName: String, superClassType: String, parameters: List[String], returnType: String): Boolean = {
    getMethodType(methodName, superClassType) match {
      case Some((supFieldTypes, supReturnType)) =>
        if (supFieldTypes.size == parameters.size) {
          for (index <- 0 until supFieldTypes.size) {
            if(supFieldTypes(index) != parameters(index)) {
              return false
            }
          }
          if(supReturnType == returnType) {
            return true
          }
        }
        return false
      case None =>
        return false
    }
  }

  def getFields(className: String): Option[List[(String, String)]] = {
    return classMap.get(className)
  }

  def getMethod(methodName: String, className: String): Option[(List[VariableExp], Exp)] = {
    return methodBodyMap.get((methodName, className))
  }

  def getMethodType(methodName: String, className: String): Option[(List[String], String)] = {
    return methodTypeMap.get((methodName, className))
  }
}
