package com.jakebg.featherweight_java

import testing.{ASTCreationTests, SemanticRulesTests, TypeRulesTests}

object Driver extends App{
  new (ASTCreationTests).execute()
  new (SemanticRulesTests).execute()
  new (TypeRulesTests).execute()
}
