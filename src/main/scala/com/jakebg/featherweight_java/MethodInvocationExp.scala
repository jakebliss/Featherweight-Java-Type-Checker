package com.jakebg.featherweight_java

case class MethodInvocationExp(term: Exp, methodName: String, parameter: List[Exp]) extends Exp
