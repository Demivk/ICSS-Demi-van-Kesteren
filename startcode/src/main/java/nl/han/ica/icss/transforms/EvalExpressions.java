package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class EvalExpressions implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public EvalExpressions() {
        variableValues = new LinkedList<>();
    }

    /*
    Implementeer de EvalExpressions transformatie. Deze transformatie vervangt
    alle Expression knopen in de AST door een Literal knoop met de berekende waarde.

    Oftewel, hier worden:
    - de variabelen vervangen door de bijbehorende waarden (--> Generator, ipv print vervangen: remove en add map?)
    - berekeningen uitgevoerd
     */

    @Override
    public void apply(AST ast) {
        variableValues = new LinkedList<>();
        variableValues.add(new HashMap<>());

        findAllVariables(ast.root);
        evaluateExpression(ast.root.getChildren(), ast.root);
    }

    private void evaluateExpression(List<ASTNode> nodes, ASTNode parent) {
        for (ASTNode node : nodes) {
            if (node instanceof Expression) {
                Expression expression = (Expression) node;
                if (expression instanceof VariableReference) {
                    VariableReference variableReference = (VariableReference) expression;
                    parent.removeChild(variableReference);
                    Literal literal = variableValues.getFirst().get(variableReference.name);
                    parent.addChild(literal);
                } else if (expression instanceof Operation) {
                    Operation operation = (Operation) expression;
                    evaluateOperation(operation, parent);
                }
            }
            evaluateExpression(node.getChildren(), node);
        }
    }

    private void evaluateOperation(Operation operation, ASTNode parent) {
        if (operation.lhs instanceof Operation) {
            Operation operationLhs = (Operation) operation.lhs;
            evaluateOperation(operationLhs, operation); //
        }
        if (operation.rhs instanceof Operation) {
            Operation operationRhs = (Operation) operation.rhs;
            evaluateOperation(operationRhs, operation); // operation, parent
        }

        if (operation.lhs instanceof VariableReference) {
            VariableReference variableReference = (VariableReference) operation.lhs;
            operation.lhs = variableValues.getFirst().get(variableReference.name);
        }
        if (operation.rhs instanceof VariableReference) {
            VariableReference variableReference = (VariableReference) operation.rhs;
            operation.rhs = variableValues.getFirst().get(variableReference.name);
        }

        // Bij height: Height + 2px * 10; wordt addChild niet goed uitgevoerd: multiply wordt niet pixel(20)
        // Literal value is not saved! Continues with multiply operation instead of pixel(20)

//        System.out.println("Operation: \n" + operation + "\n");
//        System.out.println("IF INSTANCE OF: \nParent " + parent + " children: " + parent.getChildren() + "\n");
        parent.removeChild(operation);
//        System.out.println("AFTER REMOVE: \nParent " + parent + " children: " + parent.getChildren() + "\n");
        Literal literal = calculateOperation(operation, operation.lhs, operation.rhs);
//        System.out.println("Literal: \n" + literal + "\n");
        parent.addChild(literal);
//        System.out.println("AFTER ADD CHILD: \nParent " + parent + " children: " + parent.getChildren() + "\n");
//        System.out.println("-=-=-=-=-=-");

//        if(operation instanceof MultiplyOperation || operation instanceof AddOperation || operation instanceof SubtractOperation) {
//
//        }
    }

    /**
     * Executes an operation based on the operation type
     *
     * @param operation operation type
     * @param exLeft    first expression
     * @param exRight   second expression
     * @return result of an operation
     */
    private Literal calculateOperation(Operation operation, Expression exLeft, Expression exRight) {
        if (operation instanceof MultiplyOperation) {
            return calculateMultiplyOperation(exLeft, exRight);
        } else if (operation instanceof AddOperation) {
            return calculateAddOperation(exLeft, exRight);
        } else if (operation instanceof SubtractOperation) {
            return calculateSubtractOperation(exLeft, exRight);
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
        // Scalar?
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
        // Scalar?
        return null;
    }

    private void findAllVariables(ASTNode node) {
        if (node instanceof VariableAssignment) {
            String name = ((VariableAssignment) node).name.name;
            Literal literal = null;

            Expression expression = ((VariableAssignment) node).expression;
            if (expression instanceof BoolLiteral) {
                literal = new BoolLiteral(((BoolLiteral) expression).value);
            } else if (expression instanceof ColorLiteral) {
                literal = new ColorLiteral(((ColorLiteral) expression).value);
            } else if (expression instanceof PercentageLiteral) {
                literal = new PercentageLiteral(((PercentageLiteral) expression).value);
            } else if (expression instanceof PixelLiteral) {
                literal = new PixelLiteral(((PixelLiteral) expression).value);
            } else if (expression instanceof ScalarLiteral) { // TODO mag dit?
                literal = new ScalarLiteral(((ScalarLiteral) expression).value);
            }
            variableValues.getFirst().put(name, literal);
        }
        node.getChildren().forEach(this::findAllVariables);
    }
}