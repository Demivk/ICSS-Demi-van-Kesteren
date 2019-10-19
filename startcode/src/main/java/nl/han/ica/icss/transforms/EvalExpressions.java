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

    /*
    Implementeer de EvalExpressions transformatie.
    Deze transformatie vervangt alle Expression knopen in de AST
    door een Literal knoop met de berekende waarde.

    Expression = operation, variable reference of literal
     */

//    // TODO fix parent.removeChild en parent.addChild
//    private void evaluateExpression(ASTNode node, ASTNode parent) {
//        if (node instanceof Expression) {
//            Expression expression = (Expression) node;
//            if (node instanceof Operation) {
//                Operation operation = (Operation) expression;
//                if (operation.lhs instanceof Operation) {
//                    evaluateExpression(operation.lhs, node);
//                    return;
//                }
//                if (operation.rhs instanceof Operation) {
//                    evaluateExpression(operation.rhs, node);
//                    return;
//                }
//
//                if (operation.lhs instanceof VariableReference) {
//                    operation.lhs = variableValues.getFirst().get(((VariableReference) operation.lhs).name);
//                    evaluateExpression(operation, parent);
//                    return;
//                }
//                if (operation.rhs instanceof VariableReference) {
//                    operation.rhs = variableValues.getFirst().get(((VariableReference) operation.rhs).name);
//                    evaluateExpression(operation, parent);
//                }
//
//                Literal literal = calculateOperation(operation);
//                if(literal != null) {
//                    // Hier gaat het mis!
//                    System.out.println(parent.getClass());
//                    parent.removeChild(node);
//                    parent.addChild(literal);
//                    //replace(parent, node, literal)?
//
//                    if (parent instanceof VariableAssignment) {
//                        variableValues.getFirst().remove(((VariableAssignment) parent).name.name);
//                        variableValues.getFirst().put(((VariableAssignment) parent).name.name, literal);
//                    }
//                    return;
//                }
//            }
//            if (expression instanceof VariableReference) {
//                VariableReference variableReference = (VariableReference) expression;
//                parent.removeChild(variableReference);
//                Literal literal = variableValues.getFirst().get(variableReference.name);
//                parent.addChild(literal);
//            }
//        }
//        for (ASTNode nodes : node.getChildren()) {
//            evaluateExpression(nodes, node);
//        }
//    }
//
//
//    // --- Poging 8000 --- \\
////    private void evaluateOperation(Operation node, ASTNode parent) {
////        Expression value = null;
////
////        if(node.lhs instanceof VariableReference) {
////            VariableReference variableReference = (VariableReference) node.lhs;
////            node.lhs = variableValues.getFirst().get(variableReference.name);
////        }
////        if(node.rhs instanceof VariableReference) {
////            VariableReference variableReference = (VariableReference) node.rhs;
////            node.rhs = variableValues.getFirst().get(variableReference.name);
////        }
////
////        if(node instanceof MultiplyOperation) {
////            if(node.rhs instanceof MultiplyOperation) {
////                evaluateOperation((MultiplyOperation) node.rhs, node);
////            }
////            value = calculateMultiplyOperation(node.lhs, node.rhs);
////            if(node.rhs instanceof MultiplyOperation) {
////                evaluateOperation((Operation) value, parent);
////            }
////        }
////
////        if(node instanceof AddOperation) {
////            if(node.rhs instanceof MultiplyOperation) {
////                evaluateOperation((MultiplyOperation) node.rhs, node);
////            }
////            value = calculateAddOperation(node.lhs, node.rhs);
////        }
////
////        if(node instanceof SubtractOperation) {
////            if(node.rhs instanceof MultiplyOperation) {
////                evaluateOperation((MultiplyOperation) node.rhs, node);
////            }
////            value = calculateSubtractOperation(node.lhs, node.rhs);
////        }
////
////        if(node.rhs instanceof Operation) {
////            evaluateOperation(node, parent);
////        }
////        if(value instanceof Operation) {
////            evaluateOperation((Operation) value, parent);
////        } else {
////            replaceValue(parent, value);
////        }
////    }
//    //
//
//
////    private void evaluateExpression(List<ASTNode> nodes, ASTNode parent) {
////        for (ASTNode node : nodes) {
////            if (node instanceof Expression) {
////                Expression expression = (Expression) node;
////                if (expression instanceof VariableReference) {
////                    VariableReference variableReference = (VariableReference) expression;
////                    parent.removeChild(variableReference);
////                    Literal literal = variableValues.getFirst().get(variableReference.name);
////                    parent.addChild(literal);
////                } else if (expression instanceof Operation) {
////                    Operation operation = (Operation) expression;
////                    evaluateOperation(operation, parent);
////                }
////            }
////            evaluateExpression(node.getChildren(), node);
////        }
////    }
//
////    private void evaluateOperation(Operation operation, ASTNode parent) {
////        if (operation.lhs instanceof Operation) {
////            Operation operationLhs = (Operation) operation.lhs;
////            evaluateOperation(operationLhs, operation); //
////        }
////        if (operation.rhs instanceof Operation) {
////            Operation operationRhs = (Operation) operation.rhs;
////            evaluateOperation(operationRhs, operation); // operation, parent
////        }
////
////        if (operation.lhs instanceof VariableReference) {
////            VariableReference variableReference = (VariableReference) operation.lhs;
////            operation.lhs = variableValues.getFirst().get(variableReference.name);
////        }
////        if (operation.rhs instanceof VariableReference) {
////            VariableReference variableReference = (VariableReference) operation.rhs;
////            operation.rhs = variableValues.getFirst().get(variableReference.name);
////        }
////
////        // Bij height: Height + 2px * 10; wordt addChild niet goed uitgevoerd: multiply wordt niet pixel(20)
////        // Literal value is not saved! Continues with multiply operation instead of pixel(20)
////
//////        System.out.println("Operation: \n" + operation + "\n");
//////        System.out.println("IF INSTANCE OF: \nParent " + parent + " children: " + parent.getChildren() + "\n");
////        parent.removeChild(operation);
//////        System.out.println("AFTER REMOVE: \nParent " + parent + " children: " + parent.getChildren() + "\n");
////        Literal literal = calculateOperation(operation, operation.lhs, operation.rhs);
//////        System.out.println("Literal: \n" + literal + "\n");
////        parent.addChild(literal);
//////        System.out.println("AFTER ADD CHILD: \nParent " + parent + " children: " + parent.getChildren() + "\n");
//////        System.out.println("-=-=-=-=-=-");
////
//////        if(operation instanceof MultiplyOperation || operation instanceof AddOperation || operation instanceof SubtractOperation) {
//////
//////        }
////    }
// // --- end of poging 8000 --- \\
//
//    /**
//     * Executes an operation based on the operation type
//     *
//     * @param operation operation type
//     * @return result of an operation
//     */
//    private Literal calculateOperation(Operation operation) {
//        // Wellicht overbodig?
//        if(operation.lhs instanceof Operation) {
//            operation.lhs = calculateOperation((Operation) operation.lhs);
//        }
//        if(operation.rhs instanceof Operation) {
//            operation.rhs = calculateOperation((Operation) operation.rhs);
//        }
//
//        if (operation instanceof MultiplyOperation) {
//            return calculateMultiplyOperation(operation.lhs, operation.rhs);
//        } else if (operation instanceof AddOperation) {
//            return calculateAddOperation(operation.lhs, operation.rhs);
//        } else if (operation instanceof SubtractOperation) {
//            return calculateSubtractOperation(operation.lhs, operation.rhs);
//        }
//        return null;
//    }

    /*---*/

//    private void evaluateExpression(ASTNode node) {
//        if(node instanceof Declaration) {
//            calculateDeclaration((Declaration) node);
//        }
//        if (node instanceof VariableAssignment) {
//            String name = ((VariableAssignment) node).name.name;
//            Literal literal = calculateExpression(((VariableAssignment) node).expression);
//            variableValues.getLast().put(name, literal); // getLast() ?
//            ((VariableAssignment) node).expression = literal;
//        }
//
//        for(ASTNode child : node.getChildren()) {
//            evaluateExpression(child);
//        }
//    }
//
//    private void calculateDeclaration(Declaration declaration) {
//        declaration.expression = calculateExpression(declaration.expression);
//    }
//
//    private Literal calculateExpression(Expression expression) {
//        if (expression instanceof Operation) {
//            return calculateOperation((Operation) expression);
//        }
//        if (expression instanceof VariableReference) {
//            Literal literal = null;
//            if(variableValues.getFirst().containsKey(((VariableReference) expression).name)) {
//                literal = variableValues.getFirst().get(((VariableReference) expression).name);
//            }
//            return literal;
//        }
//        return null;
//    }
//
//    private Literal calculateOperation(Operation operation) {
//        Literal left = calculateExpression(operation.lhs);
//        Literal right = calculateExpression(operation.rhs);
//
//        if(operation instanceof MultiplyOperation) {
//            return calculateMultiplyOperation(left, right);
//        } else if(operation instanceof AddOperation) {
//            return calculateAddOperation(left, right);
//        } else if(operation instanceof SubtractOperation) {
//            return calculateSubtractOperation(left, right);
//        }
//        return null;
//    }

    /*---*/


    // dit ergens dumpen?
    /*
    Literal literal = calculateOperation(child);
                if(literal != null) {
                    parent.removeChild(child);
                    parent.addChild(literal);

//                    if (parent instanceof VariableAssignment) {
//                        variableValues.getFirst().remove(((VariableAssignment) parent).name.name);
//                        variableValues.getFirst().put(((VariableAssignment) parent).name.name, literal);
//                    }
//                    return;
}
     */

    private void evaluateExpression(ArrayList<ASTNode> children, ASTNode parent) {
        for(ASTNode child : children) {
            if(parent instanceof Declaration) {
                if(child instanceof Operation) {
                    evaluateOperation((Operation) child);
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

    private void evaluateOperation(Expression parent) {
        if(parent instanceof Operation) {
            if(((Operation) parent).rhs instanceof VariableReference) {
                VariableReference variableReference = (VariableReference) ((Operation) parent).rhs;
                if(variableValues.getFirst().containsKey(variableReference.name)) {
                    ((Operation) parent).rhs = variableValues.getFirst().get(variableReference.name);
                }
            }
            if(((Operation) parent).rhs instanceof Operation) {
                for(ASTNode node : ((Operation) parent).rhs.getChildren()) {
                    evaluateOperation((Expression) node);
                }
            }
        }
    }

    private Literal calculateOperation(Operation operation) {

        if(operation instanceof MultiplyOperation) {
            return calculateMultiplyOperation(operation.lhs, operation.rhs);
        } else if(operation instanceof AddOperation) {
            return calculateAddOperation(operation.lhs, operation.rhs);
        } else if (operation instanceof SubtractOperation) {
            return calculateSubtractOperation(operation.lhs, operation.rhs);
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
        if(exLeft instanceof ScalarLiteral) {
            int result = ((ScalarLiteral) exLeft).value - ((ScalarLiteral) exRight).value;
            return new ScalarLiteral(result);
        }
        return null;
    }

    /**
     * [...]
     *
     * @param toBeFound node to find
     */
    private void findAllVariables(ASTNode toBeFound) {
        if (toBeFound instanceof VariableAssignment) {
            String name = ((VariableAssignment) toBeFound).name.name;
            Literal literal = null;

            Expression expression = ((VariableAssignment) toBeFound).expression;
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