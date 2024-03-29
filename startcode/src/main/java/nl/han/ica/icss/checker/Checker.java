package nl.han.ica.icss.checker;

import java.util.HashMap;
import java.util.LinkedList;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.*;
import nl.han.ica.icss.transforms.AllowedAttributes;

public class Checker {

    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        variableTypes.add(new HashMap<>());

        findAllVariables(ast.root);
        checkAST(ast.root);
    }

    /**
     * Executes all check methods
     *
     * @param node node to start checking
     */
    private void checkAST(ASTNode node) {
        checkAllowedStyleAttributes(node);  // works
        checkUndefinedVariables(node);      // works
        // checkOperationsAreAllowed(node);    // does not work with multiple operands
        checkNoColorsInOperation(node);     // works
        checkDeclarationValuesValid(node);  // works
        checkIfConditionIsBoolean(node);    // works
        checkNoBooleansInOperation(node);   // works

        node.getChildren().forEach(this::checkAST);
    }

    /**
     * BP04
     * Eis: Alleen de stijlattributen color,background-color, width en height zijn toegestaan.
     * <p>
     * Checks if the property name is allowed,
     * else sets an error
     *
     * @param toBeChecked node to check
     */
    private void checkAllowedStyleAttributes(ASTNode toBeChecked) {
        if (toBeChecked.getChildren().size() != 1) {
            if (toBeChecked instanceof PropertyName) {
                if (!((PropertyName) toBeChecked).name.equals(AllowedAttributes.COLOR.attribute) && !((PropertyName) toBeChecked).name.equals(AllowedAttributes.BACKGROUNDCOLOR.attribute) &&
                        !((PropertyName) toBeChecked).name.equals(AllowedAttributes.WIDTH.attribute) && !((PropertyName) toBeChecked).name.equals(AllowedAttributes.HEIGHT.attribute)) {
                    toBeChecked.setError(((PropertyName) toBeChecked).name + " is not an allowed style attribute.");
                }
            }
            toBeChecked.getChildren().forEach(this::checkAllowedStyleAttributes);
        }
    }

    /**
     * CH01
     * Eis: "Controleer of er geen variabelen worden gebruikt die niet gedefineerd zijn."
     * <p>
     * Checks if a variable exists in variableType,
     * else sets an error
     *
     * @param toBeChecked node to check
     */
    private void checkUndefinedVariables(ASTNode toBeChecked) {
        if (toBeChecked.getChildren().size() != 1) {
            if (toBeChecked instanceof VariableReference) {
                String name = ((VariableReference) toBeChecked).name;
                if (!variableTypes.getFirst().containsKey(name)) {
                    toBeChecked.setError("Unknown variable: " + name + " not defined.");
                }
            }
        }
    }

    /**
     * CH02
     * Eis: "Controleer of de operanden van de operaties plus en min van gelijk type zijn en
     * dat vermenigvuldigen enkel met scalaire waarden gebeurd. Je mag geen pixels bij
     * percentages optellen bijvoorbeeld."
     * <p>
     * Checks if add and subtract operations are done by the same operands,
     * checks if multiplying operations are done by scalaire operands,
     * else sets an error
     *
     * @param toBeChecked node to check
     */
    private void checkOperationsAreAllowed(ASTNode toBeChecked) {
        if (toBeChecked.getChildren().size() != 1) {
            if (toBeChecked instanceof Operation) {
                if (toBeChecked instanceof AddOperation || toBeChecked instanceof SubtractOperation) {
                    if (((Operation) toBeChecked).lhs instanceof VariableReference) {
                        if (variableTypes.getFirst().containsKey(((VariableReference) ((Operation) toBeChecked).lhs).name)) {
                            if (variableTypes.getFirst().get(((VariableReference) ((Operation) toBeChecked).lhs).name) != resolveExpressionType(((Operation) toBeChecked).rhs)) {
                                toBeChecked.setError("The operand types must be the same.");
                            }
                        }
                    } else if (((Operation) toBeChecked).rhs instanceof VariableReference) {
                        if (variableTypes.getFirst().containsKey(((VariableReference) ((Operation) toBeChecked).rhs).name)) {
                            if (variableTypes.getFirst().get(((VariableReference) ((Operation) toBeChecked).rhs).name) != resolveExpressionType(((Operation) toBeChecked).lhs)) {
                                toBeChecked.setError("The operand types must be the same.");
                            }
                        }
                    } else if (((Operation) toBeChecked).lhs instanceof VariableReference && ((Operation) toBeChecked).rhs instanceof VariableReference) {
                        if (variableTypes.getFirst().containsKey(((VariableReference) ((Operation) toBeChecked).lhs).name) && variableTypes.getFirst().containsKey(((VariableReference) ((Operation) toBeChecked).rhs).name)) {
                            if (variableTypes.getFirst().get(((VariableReference) ((Operation) toBeChecked).lhs).name) != variableTypes.getFirst().get(((VariableReference) ((Operation) toBeChecked).rhs).name)) {
                                toBeChecked.setError("The operand types must be the same.");
                            }
                        }
                    } else if (resolveExpressionType(((Operation) toBeChecked).lhs) != resolveExpressionType(((Operation) toBeChecked).rhs)) {
                        toBeChecked.setError("The operand types must be the same.");
                    }
                } else if (toBeChecked instanceof MultiplyOperation) {
                    if ((resolveExpressionType(((MultiplyOperation) toBeChecked).lhs) != ExpressionType.SCALAR && resolveExpressionType(((MultiplyOperation) toBeChecked).rhs) != ExpressionType.SCALAR) ||
                            (resolveExpressionType(((MultiplyOperation) toBeChecked).lhs) == ExpressionType.SCALAR && resolveExpressionType(((MultiplyOperation) toBeChecked).rhs) == ExpressionType.SCALAR)) {
                        toBeChecked.setError("The multiply operation needs one scalar type.");
                    }
                }
            }
        }
    }

    /**
     * CH03
     * Eis: "Controleer of er geen kleuren worden gebruikt in operaties (plus, min en keer)."
     * <p>
     * Checks if colors are not used in operations,
     * else sets an error
     *
     * @param toBeChecked node to check
     */
    private void checkNoColorsInOperation(ASTNode toBeChecked) {
        if (toBeChecked.getChildren().size() != 1) {
            if (toBeChecked instanceof Operation) {
                if (((Operation) toBeChecked).lhs instanceof VariableReference) {
                    if (variableTypes.getFirst().get(((VariableReference) ((Operation) toBeChecked).lhs).name) == ExpressionType.COLOR) {
                        toBeChecked.setError("Operations cannot be performed with colors.");
                    }
                }
                if (((Operation) toBeChecked).rhs instanceof VariableReference) {
                    if (variableTypes.getFirst().get(((VariableReference) ((Operation) toBeChecked).rhs).name) == ExpressionType.COLOR) {
                        toBeChecked.setError("Operations cannot be performed with colors.");
                    }
                }
                if (((Operation) toBeChecked).lhs instanceof ColorLiteral || ((Operation) toBeChecked).rhs instanceof ColorLiteral) {
                    toBeChecked.setError("Operations cannot be performed with colors.");
                }
            }
        }
    }

    /**
     * CH04
     * Eis: "Controleer of bij declaraties het type van de waarde klopt bij de stijleigenschap.
     * Declaraties zoals width: #ff0000 of color: 12px zijn natuurlijk onzin."
     * <p>
     * Checks if a property gets the right (allowed) value,
     * else sets an error
     *
     * @param toBeChecked node to check
     */
    private void checkDeclarationValuesValid(ASTNode toBeChecked) {
        if (toBeChecked.getChildren().size() != 1) {
            if (toBeChecked instanceof Declaration) {
                if (((Declaration) toBeChecked).property.name.equals(AllowedAttributes.COLOR.attribute) || ((Declaration) toBeChecked).property.name.equals(AllowedAttributes.BACKGROUNDCOLOR.attribute)) {
                    if (((Declaration) toBeChecked).expression instanceof Operation) {
                        toBeChecked.setError("Operations are not allowed with colors.");
                    }
                    if (((Declaration) toBeChecked).expression instanceof VariableReference) {
                        if (variableTypes.getFirst().containsKey(((VariableReference) ((Declaration) toBeChecked).expression).name)) {
                            if (variableTypes.getFirst().get(((VariableReference) ((Declaration) toBeChecked).expression).name) != ExpressionType.COLOR) {
                                toBeChecked.setError("Variable at color attribute must have a color value.");
                            }
                        }
                    } else if (resolveExpressionType(((Declaration) toBeChecked).expression) != ExpressionType.COLOR) {
                        toBeChecked.setError("Color attribute must have a color value.");
                    }
                }
                if (((Declaration) toBeChecked).property.name.equals(AllowedAttributes.WIDTH.attribute) || ((Declaration) toBeChecked).property.name.equals(AllowedAttributes.HEIGHT.attribute)) {
                    if (((Declaration) toBeChecked).expression instanceof Operation) {
                        if (checkOperationResultType((Operation) ((Declaration) toBeChecked).expression) != ExpressionType.PIXEL && checkOperationResultType((Operation) ((Declaration) toBeChecked).expression) != ExpressionType.PERCENTAGE) {
                            toBeChecked.setError("Operation at size attribute must have a pixel or percentage value.");
                        }
                    }
                    if (((Declaration) toBeChecked).expression instanceof VariableReference) {
                        if (variableTypes.getFirst().containsKey(((VariableReference) ((Declaration) toBeChecked).expression).name)) {
                            if (variableTypes.getFirst().get(((VariableReference) ((Declaration) toBeChecked).expression).name) != ExpressionType.PIXEL && variableTypes.getFirst().get(((VariableReference) ((Declaration) toBeChecked).expression).name) != ExpressionType.PERCENTAGE) {
                                toBeChecked.setError("Variable at size attribute must have a pixel or percentage value.");
                            }
                        }
                    } else if (resolveExpressionType(((Declaration) toBeChecked).expression) != ExpressionType.PIXEL && resolveExpressionType(((Declaration) toBeChecked).expression) != ExpressionType.PERCENTAGE) {
                        toBeChecked.setError("Size attribute must have a pixel or percentage value.");
                    }
                }
            }
        }
    }

    /**
     * CH05
     * Eis: "Controleer of de conditie bij een if-statement van het type boolean is
     * (zowel bij een variabele-referentie als een boolean literal)."
     * <p>
     * Checks if the condition is a boolean,
     * else sets an error
     *
     * @param toBeChecked node to check
     */
    private void checkIfConditionIsBoolean(ASTNode toBeChecked) {
        if (toBeChecked.getChildren().size() != 1) {
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
        }
    }

    /**
     * EU01
     * Eigen eis: "Controleer of er geen booleans worden gebruikt in operaties (plus, min en keer)"
     * Lijkt op CH03
     * <p>
     * Checks if booleans are not used in operations,
     * else sets an error
     *
     * @param toBeChecked node to check
     */
    private void checkNoBooleansInOperation(ASTNode toBeChecked) {
        if (toBeChecked.getChildren().size() != 1) {
            if (toBeChecked instanceof Operation) {
                if (((Operation) toBeChecked).lhs instanceof VariableReference) {
                    if (variableTypes.getFirst().get(((VariableReference) ((Operation) toBeChecked).lhs).name) == ExpressionType.BOOL) {
                        toBeChecked.setError("Operations cannot be performed with booleans.");
                    }
                }
                if (((Operation) toBeChecked).rhs instanceof VariableReference) {
                    if (variableTypes.getFirst().get(((VariableReference) ((Operation) toBeChecked).rhs).name) == ExpressionType.BOOL) {
                        toBeChecked.setError("Operations cannot be performed with booleans.");
                    }
                }
                if (((Operation) toBeChecked).lhs instanceof BoolLiteral || ((Operation) toBeChecked).rhs instanceof BoolLiteral) {
                    toBeChecked.setError("Operations cannot be performed with booleans.");
                }
            }
        }
    }

    /**
     * Looks up all the Variable Assignments and puts the reference and expression
     * in the hashmap variableTypes
     *
     * @param toBeFound node to check
     */
    private void findAllVariables(ASTNode toBeFound) {
        if (toBeFound instanceof VariableAssignment) {
            String name = ((VariableAssignment) toBeFound).name.name;
            ExpressionType expressionType = resolveExpressionType(((VariableAssignment) toBeFound).expression);
            variableTypes.getFirst().put(name, expressionType);
        }
        toBeFound.getChildren().forEach(this::findAllVariables);
    }

    /**
     * Returns the ExpressionType (type of literal) of the given expression.
     *
     * @param expression expression to check
     * @return ExpressionType or UNDEFINED
     */
    private ExpressionType resolveExpressionType(Expression expression) {
        if (expression instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        } else if (expression instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else if (expression instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        } else if (expression instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        } else if (expression instanceof ScalarLiteral) {
            return ExpressionType.SCALAR;
        } else if (expression instanceof Operation) {
            return checkOperationResultType((Operation) expression);
        } else if (expression instanceof VariableReference) {
            if (variableTypes.getFirst().containsKey(((VariableReference) expression).name)) {
                return variableTypes.getFirst().get(((VariableReference) expression).name);
            }
        }
        return ExpressionType.UNDEFINED;
    }

    /**
     * Recursively gets the left and right ExpressionType,
     * checks which type of operation it is and returns the type
     *
     * @param operation operation to check
     * @return ExpressionType or UNDEFINED
     */
    private ExpressionType checkOperationResultType(Operation operation) {
        ExpressionType left = resolveExpressionType(operation.lhs);
        ExpressionType right = resolveExpressionType(operation.rhs);

        if (operation instanceof MultiplyOperation) {
            if (left == ExpressionType.SCALAR) {
                if (right == ExpressionType.PIXEL) {
                    return ExpressionType.PIXEL;
                }
                if (right == ExpressionType.PERCENTAGE) {
                    return ExpressionType.PERCENTAGE;
                }
            } else if (right == ExpressionType.SCALAR) {
                if (left == ExpressionType.PIXEL) {
                    return ExpressionType.PIXEL;
                }
                if (left == ExpressionType.PERCENTAGE) {
                    return ExpressionType.PERCENTAGE;
                }
            }
        }
        if (operation instanceof AddOperation || operation instanceof SubtractOperation) {
            if (left == ExpressionType.PIXEL) {
                if(left == right) {
                    return ExpressionType.PIXEL;
                }
            }
            if (right == ExpressionType.PERCENTAGE) {
                if(left == right) {
                    return ExpressionType.PERCENTAGE;
                }
            }
        }
        return ExpressionType.UNDEFINED;
    }
}
