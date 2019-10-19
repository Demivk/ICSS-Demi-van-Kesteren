package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class RemoveIf implements Transform {

    private LinkedList<HashMap<String, Expression>> variables;

    @Override
    public void apply(AST ast) {
        variables = new LinkedList<>();
        variables.add(new HashMap<>());
        findAllVariables(ast.root);

        evaluateIfStatements(ast.root.getChildren(), ast.root);
    }

    /**
     * For every child, if the child is an IfClause, evaluate the child
     *
     * @param children children of the parent
     * @param parent node to evaluate
     */
    private void evaluateIfStatements(List<ASTNode> children, ASTNode parent) {
        for(ASTNode child : children) {
            if(child instanceof IfClause) {
                evaluateIfClause((IfClause) child, parent);
            }
            evaluateIfStatements(child.getChildren(), child);
        }
    }

    /**
     * Checks if the condition is true or false.
     * If true, keep the body,
     * If false, remove it.
     *
     * @param ifClause ifClause to evaluate
     * @param parent parent of the ifClause
     * @return list of node to keep
     */
    private List<ASTNode> evaluateIfClause(IfClause ifClause, ASTNode parent) {
        List<ASTNode> values = new ArrayList<>();

        if(ifClause.conditionalExpression instanceof BoolLiteral || ifClause.conditionalExpression instanceof VariableReference) {
            if (getBoolean(ifClause.conditionalExpression).value) {
                for (ASTNode node : ifClause.body) {
                    if (node instanceof IfClause) {
                        values.addAll(evaluateIfClause((IfClause) node, ifClause));
                    } else {
                        values.add(node);
                    }
                }
            }
        }
        removeIfClause(ifClause, parent);
        if(!(parent instanceof IfClause)) {
            for(ASTNode node : values) {
                parent.addChild(node);
            }
        }
        return values;
    }

    /**
     * Checks if the node is a BoolLiteral or VariableReference
     * and returns the value (true or false) of the node.
     *
     * @param node node to get the value from
     * @return value of the node or null
     */
    private BoolLiteral getBoolean(ASTNode node) {
        if(node instanceof BoolLiteral) {
            return (BoolLiteral) node;
        }
        if(node instanceof VariableReference) {
            VariableReference variableReference = (VariableReference) node;
            if(variables.getFirst().containsKey(variableReference.name)) {
                return (BoolLiteral) variables.getFirst().get(variableReference.name);
            }
        }
        return null;
    }

    /**
     * Removes the ifClause from the parent.
     *
     * @param ifClause ifClause to remove
     * @param parent parent of the IfClause
     */
    private void removeIfClause(IfClause ifClause, ASTNode parent) {
        if(parent instanceof Stylerule) {
            Stylerule stylerule = (Stylerule) parent;
            int index = stylerule.body.indexOf(ifClause);
            stylerule.body.remove(index);
        } else {
            parent.removeChild(ifClause);
        }
    }

    /**
     * Adds the variable name and expression to the hashmap in variables
     *
     * @param toBeFound VariableAssignment to find
     */
    private void findAllVariables(ASTNode toBeFound) {
        if(toBeFound instanceof VariableAssignment) {
            String name = ((VariableAssignment) toBeFound).name.name;
            Expression expression = ((VariableAssignment) toBeFound).expression;
            variables.getFirst().put(name, expression);
        }
        toBeFound.getChildren().forEach(this::findAllVariables);
    }
}
