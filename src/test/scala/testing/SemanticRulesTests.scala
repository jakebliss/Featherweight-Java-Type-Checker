package testing

import com.jakebg.featherweight_java.{CastExp, Exp, FieldAccessExp, MethodExp, MethodInvocationExp, ObjectCreationExp, Stepper, VariableExp}
import org.scalatest.FunSuite

// Unit tests for semantic step and resolution rules
class SemanticRulesTests extends FunSuite {
  // Test to step from a field access expression should return the first field element (E-ProjNew)
  test("Step from a field lookup expression (fst)") {
    val ast = FieldAccessExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))), "fst")

    val ast = Stepper.step(ast)
    assert(ast === ObjectCreationExp("A"))
  }

  // Test to step from a field access expression should return the second field element (E-ProjNew)
  test("Step from a field lookup expression (snd)") {
    val ast = FieldAccessExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))), "snd")

    val ast = Stepper.step(ast)
    assert(ast === ObjectCreationExp("B"))
  }

  // Test to step from a method invocation expression (E-InvkNew)
  // new Pair(new A(), new B()).setfst(new B())
  //   ->  [newfst -> new B()
  //        this -> new Pair(new A(),new B())]
  //        new Pair(newfst, this.snd)(E-ProjNew)
  test("Step from a field lookup expression") {
    val ast = MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))),
                        "setfst", List(ObjectCreationExp("B")))


    val ast = Stepper.step(ast)
    assert(ast === ObjectCreationExp("Pair", List(VariableExp("newFst"), FieldAccessExp(VariableExp("this"), "snd"))))
  }

  // Test to step from a cast expression (E-CastNew)
  // (Pair)new Pair(new A(), new B())
  //    -> new Pair(new A(), new B())
  test("Step from a cast expression on an object") {
    val ast = CastExp("Pair", ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))))

    val ast = Stepper.step(ast)
    assert(ast === ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))))
  }

  // Test to step from a cast expression (E-CastNew)
  // (NotPair)new Pair(new A(), new B())
  //    -> new Pair(new A(), new B())
  test("Get stuck from invalid cast expression on an object") {
    val ast = CastExp("NotPair", ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))))

    val ast = Stepper.step(ast)
    assert(ast === CastExp("NotPair", ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B")))))
  }
}
