package nl.han.ica.icss.checker;

import java.util.HashMap;
import java.util.LinkedList;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.types.*;

public class Checker { // TODO vragen of dit ook hoort te werken in de preview (daar alleen g4 toch?)

    private LinkedList<HashMap<String,ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        variableTypes.add(new HashMap<>());

        for(ASTNode node : ast.root.getChildren()) {
            checkCH01(node);
            checkCH02(node);
            checkCH03(node);
//            checkCH04(node);
//            checkCH05(node);
        }
    }

    /**
     * Eis: "Controleer of er geen variabelen worden gebruikt die niet gedefineerd zijn."
     * @param toBeChecked
     */
    private void checkCH01(ASTNode toBeChecked) {
        if(toBeChecked.getChildren().size() != 1) {
            if(toBeChecked instanceof VariableAssignment) {
                 variableTypes.getFirst().put(((VariableAssignment)toBeChecked).name.name, resolveExpressionType(((VariableAssignment) toBeChecked).expression));
            } else if(toBeChecked instanceof Declaration) {
                if(((Declaration) toBeChecked).expression instanceof VariableReference && !variableTypes.getFirst().containsKey(((VariableReference) ((Declaration)toBeChecked).expression).name)) {
                    toBeChecked.setError("Unknown variable " + ((VariableReference) ((Declaration) toBeChecked).expression).name + " not defined.");
                }
            }
            toBeChecked.getChildren().forEach(this::checkCH01);
        }
    }

    /**
     * Eis: "Controleer of de operanden van de operaties plus en min van gelijk type zijn en
     * dat vermenigvuldigen enkel met scalaire waarden gebeurd. Je mag geen pixels bij
     * percentages optellen bijvoorbeeld."
     * @param toBeChecked
     */
    private void checkCH02(ASTNode toBeChecked) {
        if(toBeChecked.getChildren().size() != 1) {
            if(toBeChecked instanceof Operation) {
                if(resolveExpressionType(((Operation) toBeChecked).lhs) != resolveExpressionType(((Operation) toBeChecked).rhs)) {
                    toBeChecked.setError("The operand types must be the same.");
                }
                if(toBeChecked instanceof MultiplyOperation) {
                    if(resolveExpressionType(((MultiplyOperation) toBeChecked).lhs) == ExpressionType.BOOL || resolveExpressionType(((MultiplyOperation) toBeChecked).lhs) == ExpressionType.COLOR) {
                        toBeChecked.setError("The multiply operand can only be used with scalaire values.");
                    }
                }
            }
            toBeChecked.getChildren().forEach(this::checkCH02);
        }
    }

    /**
     * Eis: "Controleer of er geen kleuren worden gebruikt in operaties (plus, min en keer)."
     * @param toBeChecked
     */
    private void checkCH03(ASTNode toBeChecked) {
        if(toBeChecked.getChildren().size() != 1) {
            if(toBeChecked instanceof Operation) {
                if(((Operation) toBeChecked).lhs instanceof ColorLiteral || ((Operation) toBeChecked).rhs instanceof ColorLiteral) {
                    toBeChecked.setError("Operations cannot be performed with colors.");
                }
            }
            toBeChecked.getChildren().forEach(this::checkCH03);
        }
    }

    /**
     * Eis: "Controleer of bij declaraties het type van de waarde klopt bij de stijleigenschap.
     * Declaraties zoals width: #ff0000 of color: 12px zijn natuurlijk onzin."
     * @param toBeChecked
     */
    private void checkCH04(ASTNode toBeChecked) {

    }

    /**
     * Eis: "Controleer of de conditie bij een if-statement van het type boolean is
     * (zowel bij een variabele-referentie als een boolean literal)."
     * @param toBeChecked
     */
    private void checkCH05(ASTNode toBeChecked) {

    }

    /**
     * Returns the ExpressionType (type of literal) of the given expression.
     * @param expression
     * @return ExpressionType
     */
    private ExpressionType resolveExpressionType(Expression expression) {
        if(expression instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        } else if(expression instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else if(expression instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        } else if(expression instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        } else if(expression instanceof ScalarLiteral) {
            return ExpressionType.SCALAR;
        } else {
            return null;
        }
    }
}
