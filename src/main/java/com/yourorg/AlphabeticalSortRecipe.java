package com.yourorg;

import org.openrewrite.ExecutionContext;
import org.openrewrite.NlsRewrite;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.Statement;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class AlphabeticalSortRecipe extends Recipe {
    @Override
    public @NlsRewrite.DisplayName String getDisplayName() {
        return "Alphabetical sort recipe";
    }

    @Override
    public @NlsRewrite.Description String getDescription() {
        return "Sorts given class members in alphabetical order.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaVisitor<ExecutionContext>() {

            @Nonnull
            private Map<String, Statement> members = new TreeMap<>();

            @Override
            public J visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext executionContext) {
                // TODO use proper predicate instead of this horrible hack
                if (classDecl.getKind() != J.ClassDeclaration.Kind.Type.Enum) {
                    super.visitClassDeclaration(classDecl, executionContext);
                    return classDecl.withBody(classDecl.getBody().withStatements(new ArrayList<>(members.values())));
                }   else {
                    members.put(classDecl.getSimpleName(), classDecl);
                    return classDecl;
                }
            }

            @Override
            public J visitVariableDeclarations(J.VariableDeclarations multiVariable, ExecutionContext executionContext) {
                String key = multiVariable.getTypeExpression() + " " + multiVariable.getVariables().get(0);
                members.put(key, multiVariable);
                return multiVariable;
            }

            @Override
            public J visitMethodDeclaration(J.MethodDeclaration method, ExecutionContext executionContext) {
                String parameters = method.getParameters().stream()
                        .filter(p -> !(p instanceof J.Empty))
                        .map(J::print) // TODO use non-deprecated method
                        .collect(Collectors.joining(", "));
                String key = method.getType() + " " + method.getSimpleName() + "(" + parameters + ")";
                members.put(key, method);
                // don't need to visit method internals
                return method;
            }
        };
    }
}
