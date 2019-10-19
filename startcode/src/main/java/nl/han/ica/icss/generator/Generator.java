package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Generator {

    private StringBuilder stringBuilder;
    private LinkedList<HashMap<String,Expression>> variables;

	public String generate(AST ast) {
        variables = new LinkedList<>();
        variables.add(new HashMap<>());
        stringBuilder = new StringBuilder();

        findAllVariables(ast.root);
	    generateResult(ast.root);

        return stringBuilder.toString() + "}";
	}

    /**
     * Makes the final result by calling
     * generateSelectorResult and generateBodyResult
     *
     * @param node node to start generating the result
     */
	private void generateResult(ASTNode node) {
	    if(node instanceof Selector) {
            if(stringBuilder.toString().endsWith(";\n")) {
                stringBuilder.append("}\n\n");
            }
	        generateSelectorResult((Selector) node);
        }
	    if(node instanceof Declaration) {
	        generateBodyResult(node);
        }
	    node.getChildren().forEach(this::generateResult);
    }

    /**
     * Checks the selector type of the node and adds it to the final result
     * @param selector node to check the type of
     */
    private void generateSelectorResult(Selector selector) {
	    if(selector instanceof ClassSelector) {
	        stringBuilder.append(((ClassSelector) selector).cls).append(" {");
        } else if(selector instanceof IdSelector) {
	        stringBuilder.append(((IdSelector) selector).id).append(" {");
        } else if(selector instanceof TagSelector) {
	        stringBuilder.append(((TagSelector) selector).tag).append(" {");
        }
	    stringBuilder.append("\n");
    }

    /**
     * Calls generateDeclarationResult if the node is a Declaration
     *
     * @param node node to check
     */
    private void generateBodyResult(ASTNode node) {
	    if(node instanceof Declaration) {
            generateDeclarationResult(node.getChildren());
        }
    }

    /**
     * Adds the declaration to the final result
     *
     * @param nodes list of nodes to check
     */
    private void generateDeclarationResult(ArrayList<ASTNode> nodes) {
	    for(ASTNode node : nodes) {
            if (node instanceof PropertyName) {
                stringBuilder.append("\t").append(((PropertyName) node).name).append(": ");
            }
            if (node instanceof Expression) {
                generateLiteralResult(node);
            }
            if(node instanceof VariableReference) {
                generateVariableValueByName(node);
            }
        }
        stringBuilder.append(";\n");
    }

    /**
     * Adds the literal value to the final result
     * Because BoolLiteral and ScalarLiteral are not used
     * in the final CSS file, they are not included
     *
     * @param node node to get the value from
     */
    private void generateLiteralResult(ASTNode node) {
	    if(node instanceof ColorLiteral) {
            stringBuilder.append(((ColorLiteral) node).value);
        } else if(node instanceof PercentageLiteral) {
	        stringBuilder.append(((PercentageLiteral) node).value).append("%");
        } else if(node instanceof PixelLiteral) {
	        stringBuilder.append(((PixelLiteral) node).value).append("px");
        }
    }

    /**
     * Retrieves the value of a variable and calls generateLiteralResult
     *
     * @param node node to get the value from
     */
    private void generateVariableValueByName(ASTNode node) {
	    if(node instanceof VariableReference) {
            if (variables.getFirst().containsKey(((VariableReference) node).name)) {
                generateLiteralResult(variables.getFirst().get(((VariableReference) node).name));
            }
        }
    }

    /**
     * Adds the variable name and expression to the hashmap in variables
     *
     * @param toBeFound node to find
     */
    private void findAllVariables(ASTNode toBeFound) {
        if (toBeFound instanceof VariableAssignment) {
            String name = ((VariableAssignment) toBeFound).name.name;
            Expression expression = ((VariableAssignment) toBeFound).expression;
            variables.getFirst().put(name, expression);
        }
        toBeFound.getChildren().forEach(this::findAllVariables);
    }
}
