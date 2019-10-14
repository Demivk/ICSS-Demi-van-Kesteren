package nl.han.ica.icss.checker;

import java.util.HashMap;
import java.util.LinkedList;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.*;

public class Checker {

    private LinkedList<HashMap<String,ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        variableTypes.add(new HashMap<>());

        for(ASTNode node : ast.root.getChildren()) {
//            checkAllowedStyleAttributes(node); // works
//            checkUndefinedVariables(node); // works
//            checkOperationsAreAllowed(node); // does not work with variables
//            checkNoColorsInOperation(node); // works
//            checkDeclarationValuesValid(node); // does not work with operations with variables
//            checkIfConditionIsBoolean(node); // works
//            checkNoBooleansInOperation(node); // works
        }
    }

    // TODO optimaliseren: lijst maken van allowed attributes?
    /**
     * BP04
     * Eis: Alleen de stijlattributen color,background-color, width en height zijn toegestaan.
     * Checks if the property name is allowed,
     * else sets an error
     * @param toBeChecked
     */
    private void checkAllowedStyleAttributes(ASTNode toBeChecked) {
        if(toBeChecked.getChildren().size() != 1) {
            if(toBeChecked instanceof PropertyName) {
                if(!((PropertyName) toBeChecked).name.equals("color") && !((PropertyName) toBeChecked).name.equals("background-color") &&
                   !((PropertyName) toBeChecked).name.equals("width") && !((PropertyName) toBeChecked).name.equals("height")) {
                    toBeChecked.setError(((PropertyName) toBeChecked).name + " is not an allowed style attribute.");
                }
            }
            toBeChecked.getChildren().forEach(this::checkAllowedStyleAttributes);
        }
    }

    /**
     * CH01
     * Eis: "Controleer of er geen variabelen worden gebruikt die niet gedefineerd zijn."
     *
     * Checks if a variable gets a value,
     * else sets an error
     * @param toBeChecked
     */
    private void checkUndefinedVariables(ASTNode toBeChecked) {
        if(toBeChecked.getChildren().size() != 1) {
            if(toBeChecked instanceof VariableAssignment) {
                 variableTypes.getFirst().put(((VariableAssignment)toBeChecked).name.name, resolveExpressionType(((VariableAssignment) toBeChecked).expression));
            } else if(toBeChecked instanceof Declaration) {
                if(((Declaration) toBeChecked).expression instanceof VariableReference && !variableTypes.getFirst().containsKey(((VariableReference) ((Declaration)toBeChecked).expression).name)) {
                    toBeChecked.setError("Unknown variable " + ((VariableReference) ((Declaration) toBeChecked).expression).name + " not defined.");
                }
            } else if(toBeChecked instanceof IfClause) {
                if(((IfClause) toBeChecked).conditionalExpression instanceof VariableReference && !variableTypes.getFirst().containsKey(((VariableReference) ((IfClause) toBeChecked).conditionalExpression).name)) {
                    toBeChecked.setError("Unknown variable " + ((VariableReference) ((IfClause) toBeChecked).conditionalExpression).name + " not defined.");
                }
            }
            toBeChecked.getChildren().forEach(this::checkUndefinedVariables);
        }

//        if(toBeChecked.getChildren().size() != 1) {
//            if(toBeChecked instanceof VariableReference) {
//                String name = ((VariableReference) toBeChecked).name;
//                boolean exists = false;
//                for(HashMap<String, ExpressionType> h : variableTypes) {
//                    if (h.containsKey(name)) {
//                        exists = true;
//                        break;
//                    }
//                }
//                if(!exists) {
//                    toBeChecked.setError("Variable " + ((VariableReference) toBeChecked).name + " not defined");
//                }
//            }
//            toBeChecked.getChildren().forEach(this::checkUndefinedVariables);
//        }
    }

    // TODO fix werkend met variabelen (lvl3 werkt niet (lvl1 zou dan ook niet horen te werken))
    /**
     * CH02
     * Eis: "Controleer of de operanden van de operaties plus en min van gelijk type zijn en
     * dat vermenigvuldigen enkel met scalaire waarden gebeurd. Je mag geen pixels bij
     * percentages optellen bijvoorbeeld."
     *
     * Checks if add and subtract operations are done by the same operands,
     * checks if multiplying operations are done by scalaire operands,
     * else sets an error
     * @param toBeChecked
     */
    private void checkOperationsAreAllowed(ASTNode toBeChecked) {
        if(toBeChecked.getChildren().size() != 1) {

//            if(toBeChecked instanceof VariableAssignment) {
//                variableTypes.getFirst().put(((VariableAssignment) toBeChecked).name.name, resolveExpressionType(((VariableAssignment) toBeChecked).expression));
//            }

            if(toBeChecked instanceof Operation) {
                if(toBeChecked instanceof AddOperation || toBeChecked instanceof SubtractOperation) {
                    // if(((Operation) toBeChecked).lhs instanceof VariableReference)
                    //      get value, do checks
                    // else
                    if(resolveExpressionType(((Operation) toBeChecked).lhs) != resolveExpressionType(((Operation) toBeChecked).rhs)) {
                        toBeChecked.setError("The operand types must be the same.");
                    }
                } else if(toBeChecked instanceof MultiplyOperation) {
                    if((resolveExpressionType(((MultiplyOperation) toBeChecked).lhs) != ExpressionType.SCALAR && resolveExpressionType(((MultiplyOperation) toBeChecked).rhs) != ExpressionType.SCALAR) ||
                       (resolveExpressionType(((MultiplyOperation) toBeChecked).lhs) == ExpressionType.SCALAR && resolveExpressionType(((MultiplyOperation) toBeChecked).rhs) == ExpressionType.SCALAR)) {
                        toBeChecked.setError("The multiply operation needs one scalar type.");
                    }
                }
            }
            toBeChecked.getChildren().forEach(this::checkOperationsAreAllowed);
        }
    }

    /**
     * CH03
     * Eis: "Controleer of er geen kleuren worden gebruikt in operaties (plus, min en keer)."
     *
     * Checks if colors are not used in operations,
     * else sets an error
     * @param toBeChecked
     */
    private void checkNoColorsInOperation(ASTNode toBeChecked) {
        if(toBeChecked.getChildren().size() != 1) {
            if(toBeChecked instanceof Operation) {
                if(((Operation) toBeChecked).lhs instanceof ColorLiteral || ((Operation) toBeChecked).rhs instanceof ColorLiteral) {
                    toBeChecked.setError("Operations cannot be performed with colors.");
                }
            }
            toBeChecked.getChildren().forEach(this::checkNoColorsInOperation);
        }
    }

    // TODO fix werkend met variablen (lvl 2 werkt niet met optellen) + optimaliseren
    /**
     * CH04
     * Eis: "Controleer of bij declaraties het type van de waarde klopt bij de stijleigenschap.
     * Declaraties zoals width: #ff0000 of color: 12px zijn natuurlijk onzin."
     *
     * Checks if a property gets the right (allowed) value,
     * else sets an error
     * @param toBeChecked
     */
    private void checkDeclarationValuesValid(ASTNode toBeChecked) {
        if(toBeChecked.getChildren().size() != 1) {
            if(toBeChecked instanceof VariableAssignment) {
                String name = ((VariableAssignment)toBeChecked).name.name;
                ExpressionType expressionType = resolveExpressionType(((VariableAssignment) toBeChecked).expression);
                variableTypes.getFirst().put(name, expressionType);
            }

            if(toBeChecked instanceof Declaration) {
                if(((Declaration) toBeChecked).property.name.equals("color") || ((Declaration) toBeChecked).property.name.equals("background-color")) {
                    if(((Declaration) toBeChecked).expression instanceof VariableReference) {
                        if(variableTypes.getFirst().containsKey(((VariableReference) ((Declaration) toBeChecked).expression).name)) {
                            if(variableTypes.getFirst().get(((VariableReference) ((Declaration) toBeChecked).expression).name) != ExpressionType.COLOR) {
                                toBeChecked.setError("Variable at color attribute must have a color value.");
                            }
                        }
                    } else if(resolveExpressionType(((Declaration) toBeChecked).expression) != ExpressionType.COLOR) {
                        toBeChecked.setError("Color attribute must have a color value.");
                    }
                }
                if(((Declaration) toBeChecked).property.name.equals("width") || ((Declaration) toBeChecked).property.name.equals("height")) {
                    if(((Declaration) toBeChecked).expression instanceof VariableReference) {
                        if(variableTypes.getFirst().containsKey(((VariableReference) ((Declaration) toBeChecked).expression).name)) {
                            if(variableTypes.getFirst().get(((VariableReference) ((Declaration) toBeChecked).expression).name) != ExpressionType.PIXEL && variableTypes.getFirst().get(((VariableReference) ((Declaration) toBeChecked).expression).name) != ExpressionType.PERCENTAGE) {
                                toBeChecked.setError("Variable at size attribute must have a pixel or percentage value.");
                            }
                        }
                    } else if(resolveExpressionType(((Declaration) toBeChecked).expression) != ExpressionType.PIXEL && resolveExpressionType(((Declaration) toBeChecked).expression) != ExpressionType.PERCENTAGE) {
                        toBeChecked.setError("Size attribute must have a pixel or percentage value.");
                    }
                }
            }
            toBeChecked.getChildren().forEach(this::checkDeclarationValuesValid);
        }
    }

    /**
     * CH05
     * Eis: "Controleer of de conditie bij een if-statement van het type boolean is
     * (zowel bij een variabele-referentie als een boolean literal)."
     * @param toBeChecked
     */
    private void checkIfConditionIsBoolean(ASTNode toBeChecked) {
        if (toBeChecked.getChildren().size() != 1) {
            if (toBeChecked instanceof VariableAssignment) {
                String name = ((VariableAssignment) toBeChecked).name.name;
                ExpressionType expressionType = resolveExpressionType(((VariableAssignment) toBeChecked).expression);
                variableTypes.getFirst().put(name, expressionType);
            }

            if (toBeChecked instanceof IfClause) {
                if (((IfClause) toBeChecked).getConditionalExpression() instanceof VariableReference) {
                    if (variableTypes.getFirst().containsKey(((VariableReference) ((IfClause) toBeChecked).getConditionalExpression()).name)) {
                        if (variableTypes.getFirst().get(((VariableReference) ((IfClause) toBeChecked).getConditionalExpression()).name) != ExpressionType.BOOL) {
                            toBeChecked.setError("If-statement requires a variable with a boolean expression");
                        }
                    }
                } else if (!(((IfClause) toBeChecked).getConditionalExpression() instanceof BoolLiteral)) {
                    toBeChecked.setError("If-statement requires a boolean expression.");
                }
            }
            toBeChecked.getChildren().forEach(this::checkIfConditionIsBoolean);
        }
    }

    /**
     * EU01
     * Eigen eis: "Controleer of er geen booleans worden gebruikt in operaties (plus, min en keer)"
     * Lijkt op CH03
     * Checks if booleans are not used in operations,
     * else sets an error
     * @param toBeChecked
     */
    private void checkNoBooleansInOperation(ASTNode toBeChecked) {
        if(toBeChecked.getChildren().size() != 1) {
            if(toBeChecked instanceof Operation) {
                if(((Operation) toBeChecked).lhs instanceof BoolLiteral || ((Operation) toBeChecked).rhs instanceof BoolLiteral) {
                    toBeChecked.setError("Operations cannot be performed with booleans.");
                }
            }
            toBeChecked.getChildren().forEach(this::checkNoBooleansInOperation);
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
