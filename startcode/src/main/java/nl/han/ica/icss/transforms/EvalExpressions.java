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
        for(ASTNode node : nodes) {
            if (node instanceof Expression) {
                if (node instanceof VariableReference) {
                    parent.removeChild(node);
                    Literal literal = variableValues.getFirst().get(((VariableReference) node).name);
                    parent.addChild(literal);
                }
                if (node instanceof Operation) {
//                    evaluateOperation((Operation) node, parent);
                    Operation operation = (Operation) node;
                    if (operation.lhs instanceof Operation) {
                        System.out.println("LHS = OPERATION");
                        evaluateExpression(operation.lhs.getChildren(), operation.lhs);
                    }
                    if (operation.rhs instanceof Operation) {
                        System.out.println("RHS = OPERATION");
                        evaluateExpression(operation.rhs.getChildren(), operation.rhs);
                    }
                    if (operation.lhs instanceof VariableReference) {
                        operation.lhs = variableValues.getFirst().get(((VariableReference) operation.lhs).name);
                    }
                    if (operation.rhs instanceof VariableReference) {
                        operation.rhs = variableValues.getFirst().get(((VariableReference) operation.rhs).name);
                    }
                    parent.removeChild(node);
                    Literal literal = calculateOperation(operation, operation.lhs, operation.rhs);
                    parent.addChild(literal);
                }
            }
            // TODO null
            evaluateExpression(node.getChildren(), node);
        }
    }

    private void evaluateOperation(Operation operation, ASTNode parent) {
        if(operation instanceof MultiplyOperation) {
            parent.removeChild(operation);
            Literal literal = calculateMultiplyOperation(operation.lhs, operation.rhs);
            parent.addChild(literal);
        }
        if(operation.lhs instanceof Operation) {
            evaluateOperation(operation, operation.lhs);
        }
        if(operation.rhs instanceof Operation) {
            evaluateOperation(operation, operation.rhs);
        }
        if(operation instanceof AddOperation) {
            parent.removeChild(operation);
            Literal literal = calculateAddOperation(operation.lhs, operation.rhs);
            parent.addChild(literal);
        }
        if(operation instanceof SubtractOperation) {
            parent.removeChild(operation);
            Literal literal = calculateSubtractOperation(operation.lhs, operation.rhs);
            parent.addChild(literal);
        }
    }

    /**
     * Executes an operation based on the operation type
     * @param operation operation type
     * @param exLeft first expression
     * @param exRight second expression
     * @return result of an operation
     */
    private Literal calculateOperation(Operation operation, Expression exLeft, Expression exRight) {
        if(operation instanceof MultiplyOperation) {
            return calculateMultiplyOperation(exLeft, exRight);
        } else if(operation instanceof AddOperation) {
            return calculateAddOperation(exLeft, exRight);
        } else if(operation instanceof SubtractOperation) {
            return calculateSubtractOperation(exLeft, exRight);
        }
        return null;
    }

    /**
     * Executes a multiply operation
     * @param exLeft first expression
     * @param exRight second expression
     * @return result of exLeft * exRight
     */
    private Literal calculateMultiplyOperation(Expression exLeft, Expression exRight) {
        if(exLeft instanceof ScalarLiteral) {
            if(exRight instanceof PercentageLiteral) {
                int result = ((ScalarLiteral) exLeft).value * ((PercentageLiteral) exRight).value;
                return new PercentageLiteral(result);
            }
            if (exRight instanceof PixelLiteral) {
                int result = ((ScalarLiteral) exLeft).value * ((PixelLiteral) exRight).value;
                return new PixelLiteral(result);
            }
        }
        if(exRight instanceof ScalarLiteral) {
            if(exLeft instanceof PercentageLiteral) {
                int result = ((PercentageLiteral) exLeft).value * ((ScalarLiteral) exRight).value;
                return new PercentageLiteral(result);
            }
            if(exLeft instanceof PixelLiteral) {
                int result = ((PixelLiteral) exLeft).value * ((ScalarLiteral) exRight).value;
                return new PixelLiteral(result);
            }
        }
        return null;
    }

    /**
     * Executes an add operation
     * @param exLeft first expression
     * @param exRight second expression
     * @return result of exLeft + exRight
     */
    private Literal calculateAddOperation(Expression exLeft, Expression exRight) {
        if(exLeft instanceof PercentageLiteral) {
            int result = ((PercentageLiteral) exLeft).value + ((PercentageLiteral) exRight).value;
            return new PercentageLiteral(result);
        }
        if(exLeft instanceof PixelLiteral) {
            int result = ((PixelLiteral) exLeft).value + ((PixelLiteral) exRight).value;
            return new PixelLiteral(result);
        }
        // Scalar?
        return null;
    }

    /**
     * Executes a subtract operation
     * @param exLeft first expression
     * @param exRight second expression
     * @return result of exLeft - exRight
     */
    private Literal calculateSubtractOperation(Expression exLeft, Expression exRight) {
        if(exLeft instanceof PercentageLiteral) {
            int result = ((PercentageLiteral) exLeft).value - ((PercentageLiteral) exRight).value;
            return new PercentageLiteral(result);
        }
        if(exLeft instanceof PixelLiteral) {
            int result = ((PixelLiteral) exLeft).value - ((PixelLiteral) exRight).value;
            return new PixelLiteral(result);
        }
        // Scalar?
        return null;
    }


    private void findAllVariables(ASTNode node) {
        if(node instanceof VariableAssignment) {
            String name = ((VariableAssignment) node).name.name;
            Literal literal = null;

            Expression expression = ((VariableAssignment) node).expression;
            if(expression instanceof BoolLiteral) {
                literal = new BoolLiteral(((BoolLiteral) expression).value);
            } else if(expression instanceof ColorLiteral) {
                literal = new ColorLiteral(((ColorLiteral) expression).value);
            } else if(expression instanceof PercentageLiteral) {
                literal = new PercentageLiteral(((PercentageLiteral) expression).value);
            } else if(expression instanceof PixelLiteral) {
                literal = new PixelLiteral(((PixelLiteral) expression).value);
            } else if(expression instanceof ScalarLiteral) { // TODO mag dit?
                literal = new ScalarLiteral(((ScalarLiteral) expression).value);
            }
            variableValues.getFirst().put(name, literal);
        }
        node.getChildren().forEach(this::findAllVariables);
    }
//    private void findDeclarationVariables(ASTNode toBeFound) {
//        if(toBeFound instanceof Declaration) {
//            VariableReference variableReference = findDeclarationVariable(toBeFound.getChildren());;
//            if(variableReference != null) {
//                toBeFound.removeChild(variableReference);
//                if(variableValuesMap.containsKey(variableReference.name)) {
//                    toBeFound.addChild(variableValuesMap.get(variableReference.name));
//                }
//            }
//        }
//        toBeFound.getChildren().forEach(this::findDeclarationVariables);
//    }
//
//    private VariableReference findDeclarationVariable(ArrayList<ASTNode> toBeFound) {
//        for(ASTNode n : toBeFound) {
//            if(n instanceof VariableReference) {
//                return (VariableReference) n;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Looks up all the Variable Assignments and puts the reference and expression
//     * in the hashmap variableTypes
//     * @param toBeFound
//     */
//    private void findAllVariables(ASTNode toBeFound) {
//        if (toBeFound instanceof VariableAssignment) {
//            String name = ((VariableAssignment) toBeFound).name.name;
//            Expression expression = ((VariableAssignment) toBeFound).expression;
//            variableValuesMap.put(name, expression);
//        }
//        toBeFound.getChildren().forEach(this::findAllVariables);
//    }
}
