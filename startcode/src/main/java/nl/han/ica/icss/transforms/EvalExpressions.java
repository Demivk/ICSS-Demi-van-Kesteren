package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class EvalExpressions implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public EvalExpressions() {
        variableValues = new LinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new LinkedList<>();
        variableValues.add(new HashMap<>());
        findAllVariables(ast.root);

        evaluateExpression(ast.root.getChildren(), ast.root);
    }

    /**
     * For every child, decides the type of the parent,
     * then checks if the child is an Operation (then sets the parent's expression to calculateExpression(child))
     * or if the child is a VariableReference (sets the parent's expression to the value if exists)
     *
     * @param children children of the parent
     * @param parent node to evaluate
     */
    private void evaluateExpression(ArrayList<ASTNode> children, ASTNode parent) {
        for(ASTNode child : children) {
            if(parent instanceof Declaration) {
                Declaration declaration = (Declaration) parent;
                if(child instanceof Operation) {
                    declaration.expression = calculateExpression((Expression) child);
                }
                if(child instanceof VariableReference) {
                    VariableReference variableReference = (VariableReference) child;
                    if(variableValues.getFirst().containsKey(variableReference.name)) {
                        ((Declaration) parent).expression = variableValues.getFirst().get(variableReference.name);
                    }
                }
            }
            evaluateExpression(child.getChildren(), child);
        }
    }

    /**
     * Decides the type of expression,
     * returns the retrieved variable value if the expression is a VariableReference,
     * returns the solution of the expression if it is an Operation
     * returns the expression if it is a Literal
     * or returns null
     *
     * @param expression expression to calculate
     * @return value of VariableReference, solution of Operation, Literal or null
     */
    private Literal calculateExpression(Expression expression) {
        if(expression instanceof VariableReference) {
            if(variableValues.getFirst().containsKey(((VariableReference) expression).name)) {
                return variableValues.getFirst().get(((VariableReference) expression).name);
            }
        }
        if(expression instanceof Operation) {
            return calculateOperation((Operation) expression);
        }
        if(expression instanceof Literal) {
            return (Literal) expression;
        }
        return null;
    }

    /**
     * Sets the left and right part of the operation to a
     * literal using calculateExpression(Expression expression),
     * decides the type of calculation and
     * returns the result of the calculation
     *
     * @param operation operation to solve
     * @return literal with calculated value
     */
    private Literal calculateOperation(Operation operation) {
        Literal left = calculateExpression(operation.lhs);
        Literal right = calculateExpression(operation.rhs);

        if(operation instanceof MultiplyOperation) {
            return calculateMultiplyOperation(left, right);
        } else if(operation instanceof AddOperation) {
            return calculateAddOperation(left, right);
        } else if (operation instanceof SubtractOperation) {
            return calculateSubtractOperation(left, right);
        }
        return null;
    }

    /**
     * Executes a multiply operation
     *
     * @param exLeft  first expression
     * @param exRight second expression
     * @return result of exLeft * exRight
     */
    private Literal calculateMultiplyOperation(Expression exLeft, Expression exRight) {
        if (exLeft instanceof ScalarLiteral) {
            if (exRight instanceof PercentageLiteral) {
                int result = ((ScalarLiteral) exLeft).value * ((PercentageLiteral) exRight).value;
                return new PercentageLiteral(result);
            }
            if (exRight instanceof PixelLiteral) {
                int result = ((ScalarLiteral) exLeft).value * ((PixelLiteral) exRight).value;
                return new PixelLiteral(result);
            }
        }
        if (exRight instanceof ScalarLiteral) {
            if (exLeft instanceof PercentageLiteral) {
                int result = ((PercentageLiteral) exLeft).value * ((ScalarLiteral) exRight).value;
                return new PercentageLiteral(result);
            }
            if (exLeft instanceof PixelLiteral) {
                int result = ((PixelLiteral) exLeft).value * ((ScalarLiteral) exRight).value;
                return new PixelLiteral(result);
            }
        }
        if (exLeft instanceof ScalarLiteral && exRight instanceof ScalarLiteral) {
            int result = ((ScalarLiteral) exLeft).value * ((ScalarLiteral) exRight).value;
            return new ScalarLiteral(result);
        }
        return null;
    }

    /**
     * Executes an add operation
     *
     * @param exLeft  first expression
     * @param exRight second expression
     * @return result of exLeft + exRight
     */
    private Literal calculateAddOperation(Expression exLeft, Expression exRight) {
        if (exLeft instanceof PercentageLiteral) {
            int result = ((PercentageLiteral) exLeft).value + ((PercentageLiteral) exRight).value;
            return new PercentageLiteral(result);
        }
        if (exLeft instanceof PixelLiteral) {
            int result = ((PixelLiteral) exLeft).value + ((PixelLiteral) exRight).value;
            return new PixelLiteral(result);
        }
        if(exLeft instanceof ScalarLiteral) {
            int result = ((ScalarLiteral) exLeft).value + ((ScalarLiteral) exRight).value;
            return new ScalarLiteral(result);
        }
        return null;
    }

    /**
     * Executes a subtract operation
     *
     * @param exLeft  first expression
     * @param exRight second expression
     * @return result of exLeft - exRight
     */
    private Literal calculateSubtractOperation(Expression exLeft, Expression exRight) {
        if (exLeft instanceof PercentageLiteral) {
            int result = ((PercentageLiteral) exLeft).value - ((PercentageLiteral) exRight).value;
            return new PercentageLiteral(result);
        }
        if (exLeft instanceof PixelLiteral) {
            int result = ((PixelLiteral) exLeft).value - ((PixelLiteral) exRight).value;
            return new PixelLiteral(result);
        }
        if(exLeft instanceof ScalarLiteral) {
            int result = ((ScalarLiteral) exLeft).value - ((ScalarLiteral) exRight).value;
            return new ScalarLiteral(result);
        }
        return null;
    }

    /**
     * Adds the variable name and the retrieved value to the hashmap in variableValues.
     *
     * @param toBeFound VariableAssignment to find
     */
    private void findAllVariables(ASTNode toBeFound) {
        if (toBeFound instanceof VariableAssignment) {
            String name = ((VariableAssignment) toBeFound).name.name;
            Literal literal = null;

            Expression expression = ((VariableAssignment) toBeFound).expression;
//            if(expression instanceof Literal) {
//                literal = (Literal) expression;
//            }
            if (expression instanceof BoolLiteral) {
                literal = new BoolLiteral(((BoolLiteral) expression).value);
            } else if (expression instanceof ColorLiteral) {
                literal = new ColorLiteral(((ColorLiteral) expression).value);
            } else if (expression instanceof PercentageLiteral) {
                literal = new PercentageLiteral(((PercentageLiteral) expression).value);
            } else if (expression instanceof PixelLiteral) {
                literal = new PixelLiteral(((PixelLiteral) expression).value);
            } else if (expression instanceof ScalarLiteral) {
                literal = new ScalarLiteral(((ScalarLiteral) expression).value);
            }
            variableValues.getFirst().put(name, literal);
        }
        toBeFound.getChildren().forEach(this::findAllVariables);
    }
}