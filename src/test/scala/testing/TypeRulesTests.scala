package testing

import com.jakebg.featherweight_java.{CastExp, ClassExp, ConstructorExp, FieldAccessExp, MethodExp, MethodInvocationExp, ObjectCreationExp, Stepper, TypeChecker, VariableExp}
import org.scalatest.FunSuite

// Unit tests for type rule checkers
class TypeRulesTests extends FunSuite {
  var classDefinitions = List[ClassExp]()
  classDefinitions = classDefinitions :+ ClassExp("A", "Object", List.empty,
    ConstructorExp("A", List.empty, List.empty, List.empty),
    List(MethodExp("B", "newvalue", List.empty, ObjectCreationExp("B"))))

  classDefinitions = classDefinitions :+ ClassExp("B", "Object", List.empty,
    ConstructorExp("B", List.empty, List.empty, List.empty), List.empty)

  classDefinitions = classDefinitions :+ ClassExp("C", "A", List.empty,
    ConstructorExp("C", List.empty, List.empty, List.empty),
    List(MethodExp("D", "newvalue", List.empty, ObjectCreationExp("D"))))

  classDefinitions = classDefinitions :+ ClassExp("D", "A", List.empty,
    ConstructorExp("D", List.empty, List.empty, List.empty), List.empty)

  classDefinitions = classDefinitions :+ ClassExp("Name", "Object", List.empty,
    ConstructorExp("Name", List.empty, List.empty, List.empty), List.empty)

  classDefinitions = classDefinitions :+ ClassExp("Animal", "Object", List(("Name", "name")),
    ConstructorExp("Animal", List(("Name", "name")), List.empty, List("name")), List.empty)

  classDefinitions = classDefinitions :+ ClassExp("Dog", "Animal", List(("Object", "name")),
    ConstructorExp("Dog", List(("Object", "name")), List("name"), List("name")),
    List(MethodExp("Object", "makenoise", List.empty, ObjectCreationExp("Bark"))))

  classDefinitions = classDefinitions :+ ClassExp("Pair", "Object", List(("A", "fst"), ("B", "snd")),
    ConstructorExp("Pair", List(("A", "fst"), ("B", "snd")), List.empty, List("fst","snd")),
    List(MethodExp("Pair", "setfst", List(("A", VariableExp("newfst"))), ObjectCreationExp("Pair", List(VariableExp("newfst"),
      FieldAccessExp(VariableExp("this"), "snd")))),
      MethodExp("Pair", "setsnd", List(("B", VariableExp("newsnd"))),
        ObjectCreationExp("Pair", List(FieldAccessExp(VariableExp("this"), "fst"), VariableExp("newsnd"))))))

  classDefinitions = classDefinitions :+ ClassExp("Kenel", "Object",
    List(("Dog", "fst"), ("Dog", "snd"), ("Dog", "trd"), ("Dog", "frth")),
    ConstructorExp("Kenel",  List(("Dog", "fst"), ("Dog", "snd"), ("Dog", "trd"), ("Dog", "frth")), List.empty,
      List("fst", "snd", "trd", "frth")), List.empty)

  TypeChecker.initialize(classDefinitions)

  // Checks that the type checker correctly identifies the type of a variable expression (T-VAR)
  test( "Type check for variable") {
    val ast = VariableExp("x")
    val astType = TypeChecker.checkType(ast, Map(VariableExp("x") -> "A"))
    assert(astType === "A")
  }

  // Checks that the type checker cannot type a variable expression that does not have a mapping(T-VAR)
  test( "Type check for variable with no mapping") {
    val ast = VariableExp("x")
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "Undefined")
  }

  // Checks that the type checker correctly identifies the type of a field access on the first field (T-Field)
  test( "Type check for field access (fst)") {
    val ast = FieldAccessExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))), "fst")
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "A")
  }

  // Checks the type of a field access expression on a cast term (T-Field)
  // ((Pair)new Pair(new A(),new B())).snd
  //    -> new Pair(new A(), new B()).snd
  test("Type check field access expression on a cast term") {
    val ast = FieldAccessExp(CastExp("Pair", ObjectCreationExp("Pair",
      List(ObjectCreationExp("A"), ObjectCreationExp("B")))), "snd")

    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "B")
  }

  // Checks the type of a field access expression on a field access term (T-Field)
  // (new Pair(new A(),Pair(new B(), new A())).snd.snd
  //    -> new Pair(new A(), new B()).snd
  test("Type check field access expression on a field access term") {
    val ast = FieldAccessExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), FieldAccessExp(
      ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))), "snd"))), "snd")

    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "B")
  }

  // Checks the type of a  field access expression on a method invocation term (T-Field)
  // new Pair(new A(), new B()).setfst(new A()).fst
  //   ->  [newfst -> new A()
  //        this -> new Pair(new A(),new B())]
  //        new Pair(new A(), new Pair(new A(),new B()).snd).fst
  test("Type check field access expression on a method invocation term") {
    val ast = FieldAccessExp(MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))),
      "setfst", List(ObjectCreationExp("A"))), "fst")

    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "A")
  }

  // Checks that the type checker correctly identifies the type of a field access for the second field (T-Field)
  test( "Type check for field access (snd)") {
    val ast = FieldAccessExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))), "snd")
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "B")
  }

  // Checks that the type checker correctly identifies the type of a method invocation (T-Invk)
  // new Pair(new A(), new B()).setfst(new A())
  test( "Type check for method invocation") {
    val ast = MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))),
      "setfst", List(ObjectCreationExp("A")))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "Pair")
  }

  // Checks that the type checker correctly identifies the type of a method invocation with a subtype argument (T-Invk)
  // new Pair(new A(), new B()).setfst(new C())
  test( "Type check for method invocation with subtype argument") {
    val ast = MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))),
      "setfst", List(ObjectCreationExp("C")))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "Pair")
  }

  // Checks that the type checker correctly identifies the type of a method invocation with a non-subtype argument(T-Invk)
  // new Pair(new A(), new B()).setfst(new B())
  test( "Type check for method invocation with non-subtype argument") {
    val ast = MethodInvocationExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))),
      "setfst", List(ObjectCreationExp("B")))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "Undefined")
  }

  // Checks that the type checker correctly identifies the type of a object creation (T-New)
  // new Pair(new A(), new B())
  test( "Type check for object creation") {
    val ast = ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B")))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "Pair")
  }

  // Checks that the type checker correctly identifies the type of a object creation with a subtype argument (T-New)
  // new Pair(new C(), new B())
  test( "Type check for object creation with subtype parameter") {
    val ast = ObjectCreationExp("Pair", List(ObjectCreationExp("C"), ObjectCreationExp("B")))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "Pair")
  }

  // Checks that the type checker cannot type an object creation with a parameter that is not a subtype (T-New)
  // new Pair(new A(), new B())
  test( "Type check for object creation with non-subtype parameter") {
    val ast = ObjectCreationExp("Pair", List(ObjectCreationExp("B"), ObjectCreationExp("B")))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "Undefined")
  }

  // Checks that the type checker correctly identifies the type of a method invocation expression that contains has
  // a term that needs to be stepped as a parameter (T-New)
  // new Pair(new A(), new Pair(new A(), new B()).snd)
  //   -> new Pair(new A(), new B())
  test("Type check object creation expression with a feild that needs to be resolved") {
    val ast = ObjectCreationExp("Pair", List(ObjectCreationExp("A"), FieldAccessExp(
      ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))), "snd")))

    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "Pair")
  }

  // Checks that the type checker correctly identifies the type of a method invocation expression that contains
  // has a term that needs to be stepped as a parameter (T-New)
  // new Kenel(new Dog(), new Dog(), new Dog(), new Dog())
  //   -> new Kenel(new Dog(), new Dog(), new Dog(), new Dog())
  test("Type check object creations expression with a field that needs to be resolved (lots of args)") {
    val ast = ObjectCreationExp("Kenel", List(ObjectCreationExp("Dog", List(ObjectCreationExp("A"))),
      ObjectCreationExp("Dog", List(ObjectCreationExp("A"))), ObjectCreationExp("Dog", List(ObjectCreationExp("A"))),
      ObjectCreationExp("Dog", List(ObjectCreationExp("A")))))

    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "Kenel")
  }

  // Checks that the type checker cannot type a method invocation expression that contains
  // a paramter that is the wrong type (T-New)
  // new Kenel(new Dog(), new Dog(), new Pair(new Dog(), new Dog()).fst, new Dog())
  //   -> new Kenel(new Dog(), new Dog(), new Dog(), new Dog())
  test("Type check object creation expression with an incorrect parameter type") {
    val ast = ObjectCreationExp("Kenel", List(ObjectCreationExp("Dog"), ObjectCreationExp("Dog"),
      ObjectCreationExp("A"), ObjectCreationExp("Dog")))

    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "Undefined")
  }

  // Checks that the type checker correctly identifies the type of an up cast (T-UCast)
  // (A) new C()
  test( "Type check for up cast") {
    val ast = CastExp("A", ObjectCreationExp("C"))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "A")
  }

  // Checks that the type checker correctly identifies the type of an multilevel up cast (T-UCast)
  // (A) new C()
  test( "Type check for mu;ti-level up cast") {
    val ast = CastExp("Object", ObjectCreationExp("C"))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "Object")
  }

  // Checks that the type checker correctly identifies the type of an up cast to the same type(T-UCast)
  // (A) new A()
  test( "Type check for up cast to same type") {
    val ast = CastExp("A", ObjectCreationExp("A"))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "A")
  }

  // Checks that the type checker correctly identifies the type of an down cast (T-DCast)
  // (C) new Object()
  test( "Type check for multi-leveldown cast") {
    val ast = CastExp("C", ObjectCreationExp("Object"))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "C")
  }

  // Checks that the type checker stupid casts a cast with unrelated types (T-SCast)
  // (B) new C()
  test( "Type check for up cast non super type") {
    val ast = CastExp("B", ObjectCreationExp("C"))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "B")
  }

  // Checks that the type checker says that the type of a valid method is okay (M OK in C)
  // Pair setfst(A newfst) {
  //  return new Pair(newfst, this.snd);
  // }
  test( "Type check for valid method declaration") {
    val gammaMap = Map(VariableExp("this") -> "Pair")
    val ast = MethodExp("Pair", "setfst", List(("A", VariableExp("newfst"))),
      ObjectCreationExp("Pair", List(VariableExp("newfst"), FieldAccessExp(VariableExp("this"), "snd"))))
    val astType = TypeChecker.checkType(ast, gammaMap)
    assert(astType === "OK in Pair")
  }

  // Checks that the type checker cannot type a method with a return type that does not exist (M OK in C)
  // Elephant getelephant() () {
  //  return new Elephant();
  // }
  test( "Type check for method declaration with non-existant return type") {
    val ast = MethodExp("Elephant", "getelephant", List.empty, ObjectCreationExp("Elephant"))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "Undefined")
  }

  // Checks that the type checker cannot type a method declaration with mismatch in return type(M OK in C)
  // A makeA() { return new B(); }
  test( "Type check for invalid method declaration (incompatible return type)") {
    val gammaMap = Map(VariableExp("this") -> "A")
    val ast = MethodExp("A", "makeA", List(), ObjectCreationExp("B"))
    val astType = TypeChecker.checkType(ast, gammaMap)
    assert(astType === "Undefined")
  }

  // Checks that the type checker cannot type a method declaration with an improperly overloaded method(M OK in C)
  // A newvalue() { return new A(); }
  // D newvalue() { return new D(); }
  test( "Type check for invalid method declaration (improperly overloaded method)") {
    val gammaMap = Map(VariableExp("this") -> "D")
    val ast = MethodExp("D", "newvalue", List.empty, ObjectCreationExp("D"))
    val astType = TypeChecker.checkType(ast, gammaMap)
    assert(astType === "Undefined")
  }

  // Checks that the type checker correctly identifies the type of an overloaded method(M OK in C)
  // B newvalue() { return new B(); }
  // B newvalue() { return new Pair(new A(), new B()).snd; }
  test( "Type check for a method declaration with a properly overloaded method") {
    val gammaMap = Map(VariableExp("this") -> "D")
    val ast = MethodExp("B", "newvalue", List.empty, FieldAccessExp(ObjectCreationExp("Pair",
      List(ObjectCreationExp("A"), ObjectCreationExp("B"))), "snd"))
    val astType = TypeChecker.checkType(ast, gammaMap)
    assert(astType === "OK in D")
  }

  // Checks that the type checker says that the type of a valid class declaration is okay (OK)
  test( "Type check for valid class declaration") {
    val ast = ClassExp("Pair", "Object", List(("A", "fst"), ("B", "snd")),
      ConstructorExp("Pair", List(("A", "fst"), ("B", "snd")), List.empty, List("fst","snd")),
      List(MethodExp("Pair", "setfst", List(("A", VariableExp("newfst"))), ObjectCreationExp("Pair", List(VariableExp("newfst"),
        FieldAccessExp(VariableExp("this"), "snd")))),
        MethodExp("Pair", "setsnd", List(("B", VariableExp("newsnd"))),
          ObjectCreationExp("Pair", List(FieldAccessExp(VariableExp("this"), "fst"), VariableExp("newsnd"))))))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "Pair OK")
  }

  // Checks that the type checker cannot type a class declaration with wrong number of fields  (OK)
  test( "Type check for class declaration with wrong number of fields") {
    val ast = ClassExp("Pair", "Object", List(("A", "fst"), ("B", "snd")),
      ConstructorExp("Pair", List(("A", "fst"), ("B", "snd")), List.empty, List("fst")),
      List(MethodExp("Pair", "setfst", List(("A", VariableExp("newfst"))), ObjectCreationExp("Pair", List(VariableExp("newfst"),
        FieldAccessExp(VariableExp("this"), "snd")))),
        MethodExp("Pair", "setsnd", List(("B", VariableExp("newsnd"))),
          ObjectCreationExp("Pair", List(FieldAccessExp(VariableExp("this"), "fst"), VariableExp("newsnd"))))))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "Undefined")
  }

  // Checks that the type checker cannot type a class declaration with wrong number of parameters (OK)
  test( "Type check for valid class declaration with wrong number of parameters") {
    val ast = ClassExp("Pair", "Object", List(("A", "fst")),
      ConstructorExp("Pair", List(("A", "fst")), List.empty, List("fst", "snd")),
      List(MethodExp("Pair", "setfst", List(("A", VariableExp("newfst"))), ObjectCreationExp("Pair", List(VariableExp("newfst"),
        FieldAccessExp(VariableExp("this"), "snd")))),
        MethodExp("Pair", "setsnd", List(("B", VariableExp("newsnd"))),
          ObjectCreationExp("Pair", List(FieldAccessExp(VariableExp("this"), "fst"), VariableExp("newsnd"))))))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "Undefined")
  }

  // Checks that the type checker cannot type a class declaration with mismatch in paramter fields and constructor
  // fields (OK)
  test( "Type check for class declaration with parameter/constructor field mismatch") {
    val ast = ClassExp("Pair", "Object", List(("A", "fst"), ("B", "trd")),
      ConstructorExp("Pair", List(("A", "fst"), ("B", "snd")), List.empty, List("fst", "snd")),
      List(MethodExp("Pair", "setfst", List(("A", VariableExp("newfst"))), ObjectCreationExp("Pair", List(VariableExp("newfst"),
        FieldAccessExp(VariableExp("this"), "snd")))),
        MethodExp("Pair", "setsnd", List(("B", VariableExp("newsnd"))),
          ObjectCreationExp("Pair", List(FieldAccessExp(VariableExp("this"), "fst"), VariableExp("newsnd"))))))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "Undefined")
  }

  // Checks that the type checker cannot type a subclass with the wrong type of super field (OK)
  test( "Type check for class declaration with wrong type of super class field") {
    val ast = ClassExp("Cat", "Animal", List(("Object", "age")),
      ConstructorExp("Cat", List(("Object", "name")), List("age"), List("age")),
      List(MethodExp("Object", "makenoise", List.empty, ObjectCreationExp("Meow"))))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "Undefined")
  }

  // Checks that the type checker cannot type a class declaration with a bad method declaration (OK)
  test( "Type check for class declaration with bad method") {
    val ast = ClassExp("Pair", "Object", List(("A", "fst"), ("B", "snd")),
      ConstructorExp("Pair", List(("A", "fst"), ("B", "snd")), List.empty, List("fst","snd")),
      List(MethodExp("Pair", "setfst", List(("A", VariableExp("newfst"))), ObjectCreationExp("A")),
        MethodExp("Pair", "setsnd", List(("B", VariableExp("newsnd"))),
          ObjectCreationExp("Pair", List(FieldAccessExp(VariableExp("this"), "fst"), VariableExp("newsnd"))))))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "Undefined")
  }

}
