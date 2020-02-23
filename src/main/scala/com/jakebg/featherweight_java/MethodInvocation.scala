package com.jakebg.featherweight_java

case class MethodInvocation(term: Exp, method: MethodExp, parameter: List[Exp]) extends Exp
