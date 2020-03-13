package testing

import com.jakebg.featherweight_java.{CastExp, ClassExp, ConstructorExp, Exp, FieldAccessExp, MethodExp, MethodInvocationExp, ObjectCreationExp, Stepper, VariableExp}
import org.scalatest.FunSuite

// Unit tests for semantic step and resolution rules
class SemanticRulesTests extends FunSuite {
  var classDefinitions = List[ClassExp]()
  classDefinitions = classDefinitions :+ ClassExp("A", "Object", List.empty,
    ConstructorExp("A", List.empty, List.empty, List.empty), List.empty)

  classDefinitions = classDefinitions :+ ClassExp("B", "Object", List.empty,
    ConstructorExp("B", List.empty, List.empty, List.empty), List.empty)

  classDefinitions = classDefinitions :+ ClassExp("Noise", "Object", List.empty,
    ConstructorExp("Noise", List.empty, List.empty, List.empty), List.empty)

  classDefinitions = classDefinitions :+ ClassExp("Bark", "Object", List.empty,
    ConstructorExp("Bark", List.empty, List.empty, List.empty), List.empty)

  classDefinitions = classDefinitions :+ ClassExp("Animal", "Object", List(("Object", "name")),
    ConstructorExp("Animal", List(("Object", "name")), List.empty, List("name")),
    List(MethodExp("Object", "makenoise", List.empty, ObjectCreationExp("Noise")),
          MethodExp("Object", "getname", List.empty, FieldAccessExp(VariableExp("this"), "name"))))

  classDefinitions = classDefinitions :+ ClassExp("Dog", "Animal", List(("Object", "name")),
    ConstructorExp("Dog", List(("Object", "name")), List("name"), List("name")),
    List(MethodExp("Object", "makenoise", List.empty, ObjectCreationExp("Bark"))))

  classDefinitions = classDefinitions :+ ClassExp("Pair", "Object", List(("Object", "fst"), ("Object", "snd")),
    ConstructorExp("Pair", List(("Object", "fst"), ("Object", "snd")), List.empty, List("fst","snd")),
    List(MethodExp("Pair", "setfst", List(("Object", VariableExp("newfst"))), ObjectCreationExp("Pair", List(VariableExp("newfst"),
          FieldAccessExp(VariableExp("this"), "snd")))),
         MethodExp("Pair", "setsnd", List(("Object", VariableExp("newsnd"))),
           ObjectCreationExp("Pair", List(FieldAccessExp(VariableExp("this"), "fst"), VariableExp("newsnd")))),
        MethodExp("Pair", "setboth", List(("Object", VariableExp("newfst")), ("Object", VariableExp("newsnd"))),
          ObjectCreationExp("Pair", List(VariableExp("newfst"), VariableExp("newsnd"))))))
  Stepper.initialize(classDefinitions)

  classDefinitions = classDefinitions :+ ClassExp("Kenel", "Object",
    List(("Dog", "fst"), ("Dog", "snd"), ("Dog", "trd"), ("Dog", "frth")),
    ConstructorExp("Dog",  List(("Dog", "fst"), ("Dog", "snd"), ("Dog", "trd"), ("Dog", "frth")), List.empty,
      List("fst", "snd", "trd", "frth")), List.empty)

  // Test to step from a field access expression should return the first field element (E-ProjNew)
  test("Step from a field lookup expression (fst)") {
    val ast = FieldAccessExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))), "fst")

    val steppedAst = Stepper.step(ast)
    assert(steppedAst === ObjectCreationExp("A"))
  }

  // Test to step from a field access expression should return the second field element (E-ProjNew)
  test("Step from a field lookup expression (snd)") {
    val ast = FieldAccessExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))), "snd")

    val steppedAst = Stepper.step(ast)
    assert(steppedAst === ObjectCreationExp("B"))
  }

  // Test to step from a method invocation expression (E-InvkNew)
  // new Pair(new A(), new B()).setfst(new A())
  //   ->  [newfst -> new A()
  //        this -> new Pair(new A(),new B())]
  //        new Pair(new A(), new Pair(new A(),new B()).snd)
  test("Step from a method call expression") {
    val ast = MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))),
                        "setfst", List(ObjectCreationExp("B")))


    val steppedAst = Stepper.step(ast)
    assert(steppedAst === ObjectCreationExp("Pair", List(ObjectCreationExp("B"),
      FieldAccessExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))), "snd"))))
  }

  // Resolve a method invocation expression (E-InvkNew)
  // new Pair(new A(), new B()).setfst(new A())
  //   -> new Pair(new A(), new B()))
  test("Resolve a method call expression") {
    val ast = MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))),
      "setfst", List(ObjectCreationExp("B")))


    val resolvedAst = Stepper.resolveAST(ast)
    assert(resolvedAst === ObjectCreationExp("Pair", List(ObjectCreationExp("B"), ObjectCreationExp("B"))))
  }

  // Test to step from a method invocation expression on an overloaded method (E-InvkNew)
  // new Dog(new A()).makenoise()
  //   ->  [ this -> new Dog(new a()]
  //        new Bark ()
  test("Step from a method call expression on an overloaded method") {
    val ast = MethodInvocationExp(ObjectCreationExp("Dog", List(ObjectCreationExp("A"))), "makenoise", List.empty)

    val steppedAst = Stepper.step(ast)
    assert(steppedAst === ObjectCreationExp("Bark"))
  }

  // Test to step from a method invocation expression on a method that comes from super class(E-InvkNew)
  // new Dog(new A()).getname()
  //   ->  [ this -> new Dog(new a()]
  //        new Dog(new A()).name
  test("Step from a method call expression from super class") {
    val ast = MethodInvocationExp(ObjectCreationExp("Dog", List(ObjectCreationExp("A"))), "getname", List.empty)

    val steppedAst = Stepper.step(ast)
    assert(steppedAst === FieldAccessExp(ObjectCreationExp("Dog", List(ObjectCreationExp("A"))), "name"))
  }

  // Resolve a method invocation expression on a method that comes from super class(E-InvkNew)
  // new Dog(new A()).getname()
  //   ->  [ this -> new Dog(new a()]
  //        new A()
  test("Resolve a method call expression from super class") {
    val ast = MethodInvocationExp(ObjectCreationExp("Dog", List(ObjectCreationExp("A"))), "getname", List.empty)

    val resolvedAst = Stepper.resolveAST(ast)
    assert(resolvedAst === ObjectCreationExp("A"))
  }

  // Test to step from a cast expression (E-CastNew)
  // (Pair)new Pair(new A(), new B())
  //    -> new Pair(new A(), new B())
  test("Step from a cast expression on an object") {
    val ast = CastExp("Pair", ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))))

    val steppedAst = Stepper.step(ast)
    assert(steppedAst === ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))))
  }

  // Test to step from a cast expression (E-CastNew)
  // (Animal) new Dog(new A())
  //    -> new Animal(new A())
  test("Step from a cast expression to a super type on an object") {
    val ast = CastExp("Animal", ObjectCreationExp("Dog", List(ObjectCreationExp("A"))))

    val steppedAst = Stepper.step(ast)
    assert(steppedAst === ObjectCreationExp("Animal", List(ObjectCreationExp("A"))))
  }

  // Test to step from a cast expression on a object creation (E-CastNew)
  // (NotPair)new Pair(new A(), new B())
  //    -> new Pair(new A(), new B())
  test("Get stuck from invalid cast expression on an object") {
    val ast = CastExp("NotPair", ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))))

    val steppedAst = Stepper.step(ast)
    assert(steppedAst === CastExp("NotPair", ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B")))))
  }

  // Test to take a step from a field access expression on a term (E-Field)
  // ((Pair)new Pair(new A(),new B())).snd
  //    -> new Pair(new A(), new B()).snd
  test("Step from a field access expression on a term") {
    val ast = FieldAccessExp(CastExp("Pair", ObjectCreationExp("Pair",
                List(ObjectCreationExp("A"), ObjectCreationExp("B")))), "snd")

    val steppedAst = Stepper.step(ast)
    assert(steppedAst === FieldAccessExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))),
                      "snd"))
  }

  // Test to take a step from a field access expression on a field access term (E-Field)
  // (new Pair(new A(),Pair(new B(), new A())).snd.snd
  //    -> new Pair(new A(), new B()).snd
  test("Step from a field access expression on a field access term") {
    val ast = FieldAccessExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), FieldAccessExp(
        ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))), "snd"))), "snd")

    val steppedAst = Stepper.step(ast)
    assert(steppedAst === FieldAccessExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))),
      "snd"))
  }

  // Test to step from a field access expression on a method invocation term (E-Field)
  // new Pair(new A(), new B()).setfst(new A()).fst
  //   ->  [newfst -> new A()
  //        this -> new Pair(new A(),new B())]
  //        new Pair(new A(), new Pair(new A(),new B()).snd).fst
  test("Step from a field access expression on a method invocation term") {
    val ast = FieldAccessExp(MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))),
      "setfst", List(ObjectCreationExp("B"))), "fst")


    val steppedAst = Stepper.step(ast)
    assert(steppedAst === FieldAccessExp(ObjectCreationExp("Pair", List(ObjectCreationExp("B"),
      FieldAccessExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))), "snd"))), "fst"))
  }

  // Test to resolve a field access expression on a method invocation term (E-Field)
  // new Pair(new A(), new B()).setfst(new B()).fst
  //   -> new B()
  test("Resolve field access expression on a method invocation term") {
    val ast = FieldAccessExp(MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))),
      "setfst", List(ObjectCreationExp("B"))), "fst")


    val steppedAst = Stepper.resolveAST(ast)
    assert(steppedAst === ObjectCreationExp("B"))
  }

  // Test to step from a method invocation expression that contains another method invocation expression (E-Invk-Recv)
  // new Pair(new A(), new B()).setfst(new A()).setsnd(new B())
  //   ->  [newfst -> new A()
  //        this -> new Pair(new A(),new B())]
  //        new Pair(new A(), new Pair(new A(),new B()).snd).setsnd(new B()),
  test("Step from a method invocation expression that contains a term as a parameter") {
    val ast = MethodInvocationExp(MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"),
      ObjectCreationExp("B"))),"setfst", List(ObjectCreationExp("A"))), "setsnd",  List(ObjectCreationExp("B")))

    val steppedAst = Stepper.step(ast)
    assert(steppedAst === MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"),
      FieldAccessExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"),
        ObjectCreationExp("B"))), "snd"))), "setsnd",  List(ObjectCreationExp("B"))))
  }

  // Test to resolve a method invocation expression that contains another method invocation expression (E-Invk-Recv)
  // new Pair(new A(), new B()).setfst(new A()).setsnd(new B())
  //   ->  [newfst -> new A()
  //        this -> new Pair(new A(),new B())]
  //        new Pair(new A(), new B())
  test("Resolve a method invocation expression that contains a term as a parameter") {
    val ast = MethodInvocationExp(MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"),
      ObjectCreationExp("B"))),"setfst", List(ObjectCreationExp("A"))), "setsnd",  List(ObjectCreationExp("B")))

    val resolvedAst = Stepper.resolveAST(ast)
    assert(resolvedAst === ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))))
  }

  // Test to take a step from an object creation expression that contains has a term that needs to be stepped as a
  // parameter (E-Invk-Arg)
  // new Pair(new A(), new B()).setfst(new Pair(new C(), new B()).fst)
  //   -> new Pair(new A(), new B()).setfst(new C())
  test("Step from a object creation expression that contains a term as a parameter") {
    val ast = MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"),
      ObjectCreationExp("B"))),"setfst", List(FieldAccessExp(
      ObjectCreationExp("Pair", List(ObjectCreationExp("C"), ObjectCreationExp("B"))), "fst")))

    val steppedAst = Stepper.step(ast)
    assert(steppedAst === MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"),
      ObjectCreationExp("B"))),"setfst", List(ObjectCreationExp("C"))))
  }

  // Test to take a step from an object creation expression that contains has a term that needs to be stepped as a
  // parameter (E-Invk-Arg)
  // new Pair(new A(), new B()).setboth(new B(), new Pair(new B(), new C()).snd)
  //   -> new Pair(new A(), new B()).setboth(new B(), new C())
  test("Step from a object creation expression that contains a term as the second parameter") {
    val ast = MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"),
      ObjectCreationExp("B"))),"setboth", List(ObjectCreationExp("B"), FieldAccessExp(
      ObjectCreationExp("Pair", List(ObjectCreationExp("B"), ObjectCreationExp("C"))), "snd")))

    val steppedAst = Stepper.step(ast)
    assert(steppedAst === MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"),
      ObjectCreationExp("B"))),"setboth", List(ObjectCreationExp("B"), ObjectCreationExp("C"))))
  }

  // Test to resolve an object creation expression that contains has a term that needs to be stepped as a
  // parameter (E-Invk-Arg)
  // new Pair(new A(), new B()).setboth(new B(), new Pair(new B(), new C()).snd)
  //   -> new Pair(new B(), new C())
  test("Resolve an object creation expression that contains a term as the second parameter") {
    val ast = MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"),
      ObjectCreationExp("B"))),"setboth", List(ObjectCreationExp("B"), FieldAccessExp(
      ObjectCreationExp("Pair", List(ObjectCreationExp("B"), ObjectCreationExp("C"))), "snd")))

    val resolvedAst = Stepper.resolveAST(ast)
    assert(resolvedAst === ObjectCreationExp("Pair", List(ObjectCreationExp("B"), ObjectCreationExp("C"))))
  }

  // Test to take a step from a method invocation expression that contains has a term that needs to be stepped as a
  // parameter (E-New-Arg)
  // new Pair(new A(), new Pair(new C(), new B()).fst)
  //   -> new Pair(new A(), new C())
  test("Step from a object creations expression with a feild that needs to be resolved") {
    val ast = ObjectCreationExp("Pair", List(ObjectCreationExp("A"), FieldAccessExp(
      ObjectCreationExp("Pair", List(ObjectCreationExp("C"), ObjectCreationExp("B"))), "fst")))

    val steppedAst = Stepper.step(ast)
    assert(steppedAst === ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("C"))))
  }

  // Test to take a step from a method invocation expression that contains has a term that needs to be stepped as a
  // parameter (E-New-Arg)
  // new Kenel(new Dog(), new Dog(), new Pair(new Dog(), new Dog()).fst, new Dog())
  //   -> new Kenel(new Dog(), new Dog(), new Dog(), new Dog())
  test("Step from a object creations expression with a field that needs to be resolved (lots of args)") {
    val ast = ObjectCreationExp("Kenel", List(ObjectCreationExp("Dog"), ObjectCreationExp("Dog"),
      FieldAccessExp(ObjectCreationExp("Pair", List(ObjectCreationExp("Dog"),ObjectCreationExp("Dog"))), "fst"),
      ObjectCreationExp("Dog")))

    val steppedAst = Stepper.step(ast)
    assert(steppedAst === ObjectCreationExp("Kenel", List(ObjectCreationExp("Dog"), ObjectCreationExp("Dog"),
      ObjectCreationExp("Dog"), ObjectCreationExp("Dog"))))
  }

  // Test to step from a cast expression on a field access term (E-Cast)
  // (A) new Pair(new A(), new B()).fst
  //    -> (A) (new A())
  test("Step from a cast expression on a field access term") {
    val ast = CastExp("A", FieldAccessExp(ObjectCreationExp("Pair",
      List(ObjectCreationExp("A"), ObjectCreationExp("B"))), "fst"))

    val steppedAst = Stepper.step(ast)
    assert(steppedAst === CastExp("A", ObjectCreationExp("A")))
  }

  // Test to step from a cast expression on a method invocation term(E-Cast)
  // (Object) new Dog(new A()).getname()
  //    -> (Object) new Dog(new A()).name
  test("Step from a cast expression on a method invocation term") {
    val ast = CastExp("Object", MethodInvocationExp(ObjectCreationExp("Dog", List(ObjectCreationExp("A"))), "getname", List.empty))

    val steppedAst = Stepper.step(ast)
    assert(steppedAst === CastExp("Object", FieldAccessExp(ObjectCreationExp("Dog", List(ObjectCreationExp("A"))), "name")))
  }

  // Test to resolve a cast expression on a method invocation term(E-Cast)
  // (Object) new Dog(new A()).getname()
  //    -> new Object()
  test("Resolve a cast expression on a method invocation term") {
    val ast = CastExp("Object", MethodInvocationExp(ObjectCreationExp("Dog", List(ObjectCreationExp("A"))), "getname", List.empty))

    val resolveAst = Stepper.resolveAST(ast)
    assert(resolveAst === ObjectCreationExp("Object"))
  }

  // Test to step from a cast expression on a cast term(E-Cast)
  // (Object) (Animal) new Dog(new A())
  //    -> (Object) new Animal(new A())
  test("Step from a cast expression on a cast expression") {
    val ast = CastExp("Object", CastExp("Animal", ObjectCreationExp("Dog", List(ObjectCreationExp("A")))))

    val steppedAst = Stepper.step(ast)
    assert(steppedAst === CastExp("Object", ObjectCreationExp("Animal", List(ObjectCreationExp("A")))))
  }

  // Test to resolve a cast expression on a cast term(E-Cast)
  // (Object) (Animal) new Dog(new A())
  //    -> new Object()
  test("Resolve a cast expression on a cast expression") {
    val ast = CastExp("Object", CastExp("Animal", ObjectCreationExp("Dog", List(ObjectCreationExp("A")))))

    val resolvedAst = Stepper.resolveAST(ast)
    assert(resolvedAst === ObjectCreationExp("Object", List(ObjectCreationExp("A"))))
  }

}
