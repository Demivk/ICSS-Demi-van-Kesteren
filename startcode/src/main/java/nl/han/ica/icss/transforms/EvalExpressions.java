package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.LinkedList;

public class EvalExpressions implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public EvalExpressions() {
        variableValues = new LinkedList<>();
    }

    /*
    Implementeer de EvalExpressions transformatie. Deze transformatie vervangt
    alle Expression knopen in de AST door een Literal knoop met de berekende waarde.
     */

    @Override
    public void apply(AST ast) {
        variableValues = new LinkedList<>();
        variableValues.add(new HashMap<>());
//        walkAST(ast.root);
    }

//    private void walkAST(ASTNode node) {
//        if(node.getChildren().size() != 1) {
//            if(node instanceof Declaration) {
//                calculateDeclaration((Declaration) node);
//            } else if(node instanceof VariableAssignment) {
//                calculateVariableAssignment((VariableAssignment) node);
//            }
//        }
//    }
//
//    private void calculateDeclaration(Declaration declaration) {
//        declaration.expression = calculateExpression(declaration.expression);
//    }
//
//    private void calculateVariableAssignment(VariableAssignment variableAssignment) {
//        String varName = variableAssignment.name.name;
//        Literal value = calculateExpression(variableAssignment.expression);
//
//        HashMap<String, Literal> map = variableValues.getLast();
//        map.put(varName, value);
//        variableAssignment.expression = value;
//    }
//
//    private Literal calculateExpression(Expression expression) {
//        if(expression instanceof Operation) {
//            return calculateOperation((Operation)expression);
//        } else if(expression instanceof VariableReference) {
//            VariableReference variableReference = (VariableReference)expression;
//            Literal value = null;
//            for(HashMap<String, Literal> map : variableValues) {
//                if(map.containsKey(variableReference.name)) {
//                    value = map.get(variableReference.name);
//                }
//            }
//            return value;
//        } else {
//            return (Literal)expression;
//        }
//    }
//
//    private Literal calculateOperation(Operation operation) {
//        Literal lhs = calculateExpression(operation.lhs);
//        Literal rhs = calculateExpression(operation.rhs);
//
//        if(operation instanceof AddOperation) {
//            return calculateAddOperation(lhs, rhs);
//        } else if(operation instanceof MultiplyOperation) {
//            return calculateMultiplyOperation(lhs, rhs);
//        } else {
//            return calculateSubtractOperation(lhs, rhs);
//        }
//    }
//
//    private Literal calculateAddOperation(Literal lhs, Literal rhs) {
//        int result;
//
//        if(lhs instanceof PercentageLiteral) {
//            result = ((PercentageLiteral) lhs).value + ((PercentageLiteral) rhs).value;
//            return new PercentageLiteral(result);
//        } else if(lhs instanceof PixelLiteral) {
//            result = ((PixelLiteral) lhs).value + ((PixelLiteral) rhs).value;
//            return new PixelLiteral(result);
//        } else {
//            result = ((ScalarLiteral) lhs).value + ((ScalarLiteral) rhs).value;
//            return new ScalarLiteral(result);
//        }
//    }
//
//    private Literal calculateMultiplyOperation(Literal lhs, Literal rhs) {
//        int result;
//        Literal scalarSide, nonScalarSide;
//
//        if(lhs instanceof ScalarLiteral) {
//            scalarSide = lhs;
//            nonScalarSide = rhs;
//        } else {
//            scalarSide = rhs;
//            nonScalarSide = lhs;
//        }
//
//        if(nonScalarSide instanceof PercentageLiteral) {
//            result = ((PercentageLiteral) nonScalarSide).value * ((ScalarLiteral) scalarSide).value;
//            return new PercentageLiteral(result);
//        } else if(nonScalarSide instanceof PixelLiteral) {
//            result = ((PixelLiteral) nonScalarSide).value * ((ScalarLiteral) scalarSide).value;
//            return new PixelLiteral(result);
//        } /*else {
//            result = ((ScalarLiteral) nonScalarSide).value * ((ScalarLiteral) scalarSide).value;
//            return new ScalarLiteral(result);
//        }*/
//        return null;
//
//    }
//
//    private Literal calculateSubtractOperation(Literal lhs, Literal rhs) {
//        int result;
//
//        if(lhs instanceof PercentageLiteral) {
//            result = ((PercentageLiteral) lhs).value - ((PercentageLiteral) rhs).value;
//            return new PercentageLiteral(result);
//        } else if(lhs instanceof PixelLiteral) {
//            result = ((PixelLiteral) lhs).value - ((PixelLiteral) rhs).value;
//            return new PixelLiteral(result);
//        } else {
//            result = ((ScalarLiteral) lhs).value - ((ScalarLiteral) rhs).value;
//            return new ScalarLiteral(result);
//        }
//    }
}
