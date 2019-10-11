package nl.han.ica.icss.checker;

import java.util.HashMap;
import java.util.LinkedList;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.types.*;

public class Checker {

    private LinkedList<HashMap<String,ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        variableTypes.add(new HashMap<>());

        for(ASTNode node : ast.root.getChildren()) {
            checkBP04(node);
            checkCH01(node);
            checkCH02(node);
            checkCH03(node);
            checkCH04(node);
//            checkCH05(node);
        }
    }

    // TODO optimaliseren: lijst maken van allowed attributes?
    /**
     * Eis: Alleen de stijlattributen color,background-color, width en height zijn toegestaan.
     * Checks if the property name is allowed,
     * else sets an error
     * @param toBeChecked
     */
    private void checkBP04(ASTNode toBeChecked) {
        if(toBeChecked.getChildren().size() != 1) {
            if(toBeChecked instanceof PropertyName) {
                if(!((PropertyName) toBeChecked).name.equals("color") && !((PropertyName) toBeChecked).name.equals("background-color") &&
                   !((PropertyName) toBeChecked).name.equals("width") && !((PropertyName) toBeChecked).name.equals("height")) {
                    toBeChecked.setError(((PropertyName) toBeChecked).name + " is not an allowed style attribute.");
                }
            }
            toBeChecked.getChildren().forEach(this::checkBP04);
        }
    }

    /**
     * Eis: "Controleer of er geen variabelen worden gebruikt die niet gedefineerd zijn."
     *
     * Checks if a variable gets a value,
     * else sets an error
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
     *
     * Checks if add and subtract operations are done by the same operands,
     * checks if multiplying operations are done by scalaire operands,
     * else sets an error
     * @param toBeChecked
     */
    private void checkCH02(ASTNode toBeChecked) {
        if(toBeChecked.getChildren().size() != 1) {
            if(toBeChecked instanceof Operation) {
                if(resolveExpressionType(((Operation) toBeChecked).lhs) != resolveExpressionType(((Operation) toBeChecked).rhs)) {
                    toBeChecked.setError("The operand types must be the same.");
                }
                if(toBeChecked instanceof MultiplyOperation) {
                    if(resolveExpressionType(((MultiplyOperation) toBeChecked).lhs) == ExpressionType.BOOL ||
                       resolveExpressionType(((MultiplyOperation) toBeChecked).lhs) == ExpressionType.COLOR) {
                        toBeChecked.setError("The multiply operand can only be used with scalaire values.");
                    }
                }
            }
            toBeChecked.getChildren().forEach(this::checkCH02);
        }
    }

    /**
     * Eis: "Controleer of er geen kleuren worden gebruikt in operaties (plus, min en keer)."
     *
     * Checks if colors are not used in operations,
     * else sets an error
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
     *
     * Checks if a property gets the right (allowed) value,
     * else sets an error
     * @param toBeChecked
     */
    private void checkCH04(ASTNode toBeChecked) {
        if(toBeChecked.getChildren().size() != 1) {
            if(toBeChecked instanceof Declaration) {
                System.out.println(((Declaration) toBeChecked).property + " " + ((Declaration) toBeChecked).expression);
                if(((Declaration) toBeChecked).property.name.equals("color") || ((Declaration) toBeChecked).property.name.equals("background-color")) {
                    if(resolveExpressionType(((Declaration) toBeChecked).expression) != ExpressionType.COLOR) {
                        toBeChecked.setError("Color attribute must have a color value.");
                    }
                }
                if(((Declaration) toBeChecked).property.name.equals("width") || ((Declaration) toBeChecked).property.name.equals("height")) {
                    if(resolveExpressionType(((Declaration) toBeChecked).expression) != ExpressionType.PIXEL && resolveExpressionType(((Declaration) toBeChecked).expression) != ExpressionType.PERCENTAGE) {
                        toBeChecked.setError("Size attribute must have a pixel or percentage value.");
                    }
                }
            }
            toBeChecked.getChildren().forEach(this::checkCH04);
        }
    }

    /**
     * Eis: "Controleer of de conditie bij een if-statement van het type boolean is
     * (zowel bij een variabele-referentie als een boolean literal)."
     * @param toBeChecked
     */
    private void checkCH05(ASTNode toBeChecked) {
        if(toBeChecked.getChildren().size() != 1) {
            if(toBeChecked instanceof IfClause) {
                // if(resolveExpressionType(((IfClause) toBeChecked).getConditionalExpression()) != ExpressionType.BOOL)
                if(!(((IfClause) toBeChecked).getConditionalExpression() instanceof BoolLiteral)) {
                    toBeChecked.setError("If-statement requires a boolean expression.");
                }
                if(((IfClause) toBeChecked).getConditionalExpression() instanceof VariableReference) {
                    // Verkrijg assignment, haal daar de expressie uit en check het

                    System.out.println("It's a var ref");

                }
            }
            toBeChecked.getChildren().forEach(this::checkCH05);
        }
    }

    /**
     * Returns the ExpressionType (type of literal) of the given expression.
     * @param expression
     * @return ExpressionType or null
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
