package com.jakebg.featherweight_java

case class MethodExp(className: String, methodName: String, parameterList: List[(String, String)],
                     functionBody: Exp)
