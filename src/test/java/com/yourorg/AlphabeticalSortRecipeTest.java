package com.yourorg;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

public class AlphabeticalSortRecipeTest implements RewriteTest {

    // TODO: add empty lines to the tests. Now the test don't contain empty lines because they seem to be moved together
    //  with statements and assertions fail

    // Assumption: Class members to consider:
    // - constructors
    // - enums
    // - fields (static or not)
    //.- methods (static or not)
    // - initializer blocks (static or not)
    //nested classes (static or not)
    //Assumption: all various types of class members should be sorted together, not sorted fields first, then sorted methods
    //Assumption: sorting is case sensitive, because java is case sensitive and it's possible to have a and  A fields in the same class.
    //Assumption: Javadocs aren't taken into account for sorting, but moved together with the corresponding members.
    //Assumption: Generics are considered when sorting.
    // Assumption: paddings, modifiers and annotations aren't taken into account while sorting

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new AlphabeticalSortRecipe());
    }

    @Test
    void sortFields() {
        rewriteRun(
          //language=java
          java(
            """
              import javax.annotation.Nonnull;
              import javax.annotation.Nullable;
              
              class A {              
                  private String e = "e";
                  public static String b = "b";
                  public Integer intA = 1;
                  private String a = "a";
                  protected String d = "d";
                  @Nonnull
                  static List<String> bList;
                  @Nonnull
                  List<Integer> cList;
                  public Integer inta = 1;
                  private Integer aInt = 1;
                  String c = "c"; 
                  @Nullable
                  List<String> aList;                  
              } 
              """,
            """   
              import javax.annotation.Nonnull;
              import javax.annotation.Nullable;
                         
              class A {              
                  private Integer aInt = 1;
                  public Integer intA = 1;
                  public Integer inta = 1;
                  @Nonnull
                  List<Integer> cList;
                  @Nullable
                  List<String> aList;
                  @Nonnull
                  static List<String> bList;
                  private String a = "a";
                  public static String b = "b";
                  String c = "c";
                  protected String d = "d";
                  private String e = "e";              
              } 
              """
          )
        );
    }


    @Test
    void sortMethods_byName() {
        rewriteRun(
          //language=java
          java(
            """
              import java.util.List;
              
              class A {              
                  private static List<Long> b() {
                      return List.of(1L);
                  }                     
                  private static List<Integer> c() {
                      return List.of(1);
                  }          
                  private static List<Integer> a(int a, long ba) {
                      return List.of(1);
                  }
                  private static List<Integer> a(int a) {
                      return List.of(1);
                  }
                  private static List<Integer> a(int a, long b) {
                      return List.of(1);
                  }
                  private static List<Integer> a(String s) {
                      return List.of(1);
                  }
                  private static List<Integer> a(int a, long b, int c) {
                      return List.of(1);
                  }                  
                  private static List<Integer> a() {
                      return List.of(1);
                  }                              
              } 
              """,
            """    
              import java.util.List;
              
              class A {              
                  private static List<Integer> a() {
                      return List.of(1);
                  } 
                  private static List<Integer> a(String s) {
                      return List.of(1);
                  }
                  private static List<Integer> a(int a) {
                      return List.of(1);
                  }
                  private static List<Integer> a(int a, long b) {
                      return List.of(1);
                  }
                  private static List<Integer> a(int a, long b, int c) {
                      return List.of(1);
                  }
                  private static List<Integer> a(int a, long ba) {
                      return List.of(1);
                  }
                  private static List<Integer> c() {
                      return List.of(1);
                  }   
                  private static List<Long> b() {
                      return List.of(1L);
                  }                                      
              } 
              """
          )
        );
    }

    @Test
    void sortConstructors() {
        rewriteRun(
          //language=java
          java(
            """
              class A {              
                  public A(int b) {}
                  public A(String s) {}
                  public A() {}
                  public A(long a) {}   
                  public A(int a) {} 
                  public A(int a, long b) {}                 
              } 
              """,
            """              
              class A {              
                  public A() {}
                  public A(String s) {}
                  public A(int a) {}
                  public A(int a, long b) {}
                  public A(int b) {}
                  public A(long a) {}                 
              } 
              """
          )
        );
    }

    @Test
    void sortEnums() {
        rewriteRun(
          //language=java
          java(
            """
              public static class Enclosing {
                  public enum C {
                      FIRST, SECOND, THIRD
                  }
                  private enum B {
                      ANOTHER_FIRST, SECOND, THIRD
                  }
                  public enum A {
                      FIRST, SECOND, THIRD
                  }
                  enum AA {
                      ANOTHER_FIRST, SECOND, THIRD
                  }
              }
              """,
            """
              public static class Enclosing {
                  public enum A {
                      FIRST, SECOND, THIRD
                  }
                  enum AA {
                      ANOTHER_FIRST, SECOND, THIRD
                  }
                  private enum B {
                      ANOTHER_FIRST, SECOND, THIRD
                  }
                  public enum C {
                      FIRST, SECOND, THIRD
                  }
              }
              """
          )
        );
    }

    @Disabled("Not implemented")
    @Test
    void sortClasses() {
        rewriteRun(
          //language=java
          java(
            """
              public class Enclosing {
                  public static class D {
                  
                  }
                  public static class A {
              
                  }
                  public static class C {
              
                  }
              }
              """,
            """
              public class Enclosing {
                  public static class A {
                  
                  }
                  public static class C {
              
                  }
                  public static class D {
              
                  }
              }
              """
          )
        );
    }



    @Disabled("Not implemented. In this implementation we will need to take static modifier into account because two blocks can have exactly the same code")
    @Test
    void sortInitBlocks() {
        rewriteRun(
          //language=java
          java(
            """
              public class Enclosing {
                  {
                      System.out.println("second instance init");
                  }
                  static {
                      System.out.println("first class init");
                  }              
                  static {
                      System.out.println("second class init");
                  }  
                  {
                      System.out.println("first instance init");
                  }
              }
              """,
            """
              public class Enclosing {
                  static {
                      System.out.println("first class init");
                  }              
                  static {
                      System.out.println("second class init");
                  }  
                  {
                      System.out.println("first instance init");
                  }
                  {
                      System.out.println("second instance init");
                  }
              }
              """
          )
        );
    }

    @Disabled("Not implemented. In this implementation we will need to take static modifier into account because two blocks can have exactly the same code")
    @Test
    void sortVariousMembers() {
        rewriteRun(
          //language=java
          java(
            """
              public class Enclosing {
                  private String C = "C";
                  private String c = "c";
                  public Enclosing() {
              
                  }
                  public Integer c() {
                      return 1;
                  }
                  enum C {
                      ONE, TWO, THREE
                  }
                  private static class D {}
                  {
                      System.out.println("init instance");
                  }
                  static {
                      System.out.println("init class");
                  }
              }
              """,
            """
              public class Enclosing {
                  static {
                      System.out.println("init class");
                  }
                  {
                      System.out.println("init instance");
                  }
                  private String C = "C";
                  enum C {
                      ONE, TWO, THREE
                  }
                  private String c = "c";
                  private static class D {}
                  public Enclosing() {
              
                  }
                  public Integer c() {
                      return 1;
                  }
              }
              """
          )
        );
    }

}
