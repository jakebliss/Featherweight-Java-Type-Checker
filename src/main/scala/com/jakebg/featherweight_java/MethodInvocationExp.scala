package com.jakebg.featherweight_java

case class MethodInvocationExp(term: Exp, methodName: String, parameters: List[Exp]) extends Exp
