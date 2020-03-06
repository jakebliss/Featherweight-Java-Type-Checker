package testing

import com.jakebg.featherweight_java.{CastExp, Exp, FieldAccessExp, MethodExp, MethodInvocationExp, ObjectCreationExp, Stepper, VariableExp}
import org.scalatest.FunSuite

// Unit tests for semantic step and resolution rules
class SemanticRulesTests extends FunSuite {
  // Test to step from a field access expression should return the first field element (E-ProjNew)
  test("Step from a field lookup expression (fst)") {
    val ast = FieldAccessExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))), "fst")

    val steppedAst = Stepper.step(ast)
    assert(ast === ObjectCreationExp("A"))
  }

  // Test to step from a field access expression should return the second field element (E-ProjNew)
  test("Step from a field lookup expression (snd)") {
    val ast = FieldAccessExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))), "snd")

    val steppedAst = Stepper.step(ast)
    assert(ast === ObjectCreationExp("B"))
  }

  // Test to step from a method invocation expression (E-InvkNew)
  // new Pair(new A(), new B()).setfst(new B())
  //   ->  [newfst -> new B()
  //        this -> new Pair(new A(),new B())]
  //        new Pair(newfst, this.snd)
  test("Step from a field lookup expression") {
    val ast = MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))),
                        "setfst", List(ObjectCreationExp("B")))


    val steppedAst = Stepper.step(ast)
    assert(ast === ObjectCreationExp("Pair", List(VariableExp("newFst"), FieldAccessExp(VariableExp("this"), "snd"))))
  }

  // Test to step from a cast expression (E-CastNew)
  // (Pair)new Pair(new A(), new B())
  //    -> new Pair(new A(), new B())
  test("Step from a cast expression on an object") {
    val ast = CastExp("Pair", ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))))

    val steppedAst = Stepper.step(ast)
    assert(ast === ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))))
  }

  // Test to step from a cast expression on a object creation (E-CastNew)
  // (NotPair)new Pair(new A(), new B())
  //    -> new Pair(new A(), new B())
  test("Get stuck from invalid cast expression on an object") {
    val ast = CastExp("NotPair", ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))))

    val steppedAst = Stepper.step(ast)
    assert(ast === CastExp("NotPair", ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B")))))
  }

  // Test to take a step from a field access expression on a term (E-Field)
  // ((Pair)new Pair(new A(),new B())).snd
  //    -> new Pair(new A(), new B()).snd
  test("Step from a field access expression on a term") {
    val ast = FieldAccessExp(CastExp("Pair", ObjectCreationExp("Pair",
                List(ObjectCreationExp("A"), ObjectCreationExp("B")))), "snd")

    val steppedAst = Stepper.step(ast)
    assert(ast === FieldAccessExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))),
                      "snd"))
  }

  // Test to step from a method invocation expression that contains another method invocation expression (E-Invk-Recv)
  // new Pair(new A(), new B()).setfst(new B()).setsnd(new A())
  //   ->  [newfst -> new B()
  //        this -> new Pair(new A(),new B()).setfst(new B())]
  //        new Pair(newfst, this.snd).setsnd(new A())
  test("Step from a method invocation expression that contains a term as a parameter") {
    val ast = MethodInvocationExp(MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"),
              ObjectCreationExp("B"))),"setfst", List(ObjectCreationExp("B"))), "setsnd",  List(ObjectCreationExp("A")))

    val steppedAst = Stepper.step(ast)
    assert(ast === MethodInvocationExp(ObjectCreationExp("Pair", List(VariableExp("newFst"),
                FieldAccessExp(VariableExp("this"), "snd"))), "setsnd",  List(ObjectCreationExp("A"))))
  }

  // Test to take a step from an object creation expression that contains has a term that needs to be stepped as a
  // parameter (E-New-Arg)
  // new Pair(new A(), new B()).setfst(new Pair(new C(), new B()).fst)
  //   -> new Pair(new A(), new B()).setfst(new C())
  test("Step from a object creation expression that contains a term as a parameter") {
    val ast = MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"),
      ObjectCreationExp("B"))),"setfst", List(FieldAccessExp(
      ObjectCreationExp("Pair", List(ObjectCreationExp("C"), ObjectCreationExp("B"))), "fst")))

    val steppedAst = Stepper.step(ast)
    assert(ast === MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"),
      ObjectCreationExp("B"))),"setfst", List(ObjectCreationExp("C"))))
  }

  // Test to take a step from a method invocation expression that contains has a term that needs to be stepped as a
  // parameter (E-New-Arg)
  // new Pair(new A(), new Pair(new C(), new B()).fst)
  //   -> new Pair(new A(), new C())
  test("Step from a field lookup expression") {
    val ast = ObjectCreationExp("Pair", List(ObjectCreationExp("A"), FieldAccessExp(
              ObjectCreationExp("Pair", List(ObjectCreationExp("C"), ObjectCreationExp("B"))), "fst")))

    val steppedAst = Stepper.step(ast)
    assert(ast === ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("C")))
  }

  // Test to step from a cast expression on a term (E-Cast)
  // (A) new Pair(new A(), new B()).fst
  //    -> (A) (new A())
  test("Step from a cast expression on a term") {
    val ast = CastExp("A", FieldAccessExp(ObjectCreationExp("Pair",
                        List(ObjectCreationExp("A"), ObjectCreationExp("B"))), "fst"))

    val steppedAst = Stepper.step(ast)
    assert(ast === CastExp("A", ObjectCreationExp("A")))
  }
}
