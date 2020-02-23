package com.jakebg.featherweight_java

case class ClassExp(className: String, superClass: String, variableList: List[(String, String)],
                    constructor: ConstructorExp, methods: List[MethodExp])
