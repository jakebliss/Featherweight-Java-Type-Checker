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

  classDefinitions = classDefinitions :+ ClassExp("D", "Object", List.empty,
    ConstructorExp("D", List.empty, List.empty, List.empty), List.empty)

  classDefinitions = classDefinitions :+ ClassExp("Name", "Object", List.empty,
    ConstructorExp("Name", List.empty, List.empty, List.empty), List.empty)

  classDefinitions = classDefinitions :+ ClassExp("Animal", "Object", List(("Name", "name")),
    ConstructorExp("Animal", List(("Name", "name")), List.empty, List("name")), List.empty)

  classDefinitions = classDefinitions :+ ClassExp("Pair", "Object", List(("A", "fst"), ("B", "snd")),
    ConstructorExp("Pair", List(("A", "fst"), ("B", "snd")), List.empty, List("fst","snd")),
    List(MethodExp("Pair", "setfst", List(("A", VariableExp("newfst"))), ObjectCreationExp("Pair", List(VariableExp("newfst"),
      FieldAccessExp(VariableExp("this"), "snd")))),
      MethodExp("Pair", "setsnd", List(("B", VariableExp("newsnd"))),
        ObjectCreationExp("Pair", List(FieldAccessExp(VariableExp("this"), "fst"), VariableExp("newsnd"))))))
  TypeChecker.initialize(classDefinitions)

  // Checks that the type checker correctly identifies the type of a variable expression (T-VAR)
  test( "Type check for variable") {
    val ast = VariableExp("x")
    val astType = TypeChecker.checkType(ast, Map(VariableExp("x") -> "A"))
    assert(astType === "A")
  }

  // Checks that the type checker correctly identifies the type of a field access on the first field (T-Field)
  test( "Type check for field access (fst)") {
    val ast = FieldAccessExp(ObjectCreationExp("Pair", List(ObjectCreationExp("A"), ObjectCreationExp("B"))), "fst")
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

  // Checks that the type checker correctly identifies the type of an up cast (T-UCast)
  // (A) new C()
  test( "Type check for up cast") {
    val ast = CastExp("A", ObjectCreationExp("C"))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "A")
  }

  // Checks that the type checker correctly identifies the type of an up cast to the same type(T-UCast)
  // (A) new A()
  test( "Type check for up cast to same type") {
    val ast = CastExp("A", ObjectCreationExp("A"))
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "A")
  }

  // Checks that the type checker correctly identifies the type of an down cast (T-DCast)
  // (C) new A()
  test( "Type check for down cast") {
    val ast = CastExp("C", ObjectCreationExp("A"))
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

  // Checks that the type checker says that the type of a valid class declaration of a subclass is okay (OK)
  test( "Type check for valid class declaration for a subclass") {
    val ast = ClassExp("Dog", "Animal", List(("Name", "name")),
      ConstructorExp("Dog", List(("Name", "name")), List("name"), List("name")), List.empty)
    val astType = TypeChecker.checkType(ast, Map.empty)
    assert(astType === "Dog OK")
  }

//  // Checks that the type checker cannot type a class declaration with a field that is a of a different type than
//  // the superclass field (OK)
//  // class Animal extends Object(Name name) { super(); this.name = name;}
//  // class Dog extends Animal (A name) {Dog(A name) {super(name)} }
//  test( "Type check for class declaration with improper type passed to super class") {
//    val ast = ClassExp("Dog", "Animal", List(("A", "name")),
//      ConstructorExp("Dog", List(("A", "name")), List("name"), List("name")), List.empty)
//    val astType = TypeChecker.checkType(ast, Map.empty)
//    assert(astType === "Undefined")
//  }
}
