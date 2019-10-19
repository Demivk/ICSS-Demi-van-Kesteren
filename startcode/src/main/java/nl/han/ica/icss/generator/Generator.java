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

    private String result = "";
    private LinkedList<HashMap<String,Expression>> variables;

	public String generate(AST ast) {
        variables = new LinkedList<>();
        variables.add(new HashMap<>());
        findAllVariables(ast.root);

	    generateResult(ast.root);
        return result + "}";
	}

    /**
     * Makes the final result by calling
     * generateSelectorResult and generateBodyResult
     *
     * @param node node to start generating the result
     */
	private void generateResult(ASTNode node) {
	    if(node instanceof Selector) {
            if(result.endsWith(";\n")) {
                result += "}\n\n";
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
	        result += ((ClassSelector) selector).cls + " {";
        } else if(selector instanceof IdSelector) {
	        result += ((IdSelector) selector).id + " {";
        } else if(selector instanceof TagSelector) {
	        result += ((TagSelector) selector).tag + " {";
        }
	    result += "\n";
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
                result += "\t" + ((PropertyName) node).name + ": ";
            }
            if (node instanceof Expression) {
                generateLiteralResult(node);
            }
            if(node instanceof VariableReference) {
                generateVariableValueByName(node);
            }
        }
        result += ";\n";
    }

    /**
     * Adds the literal value to the final result
     *
     * @param node node to get the value from
     */
    private void generateLiteralResult(ASTNode node) {
	    if(node instanceof BoolLiteral) {
	        result += ((BoolLiteral) node).value;
        } else if(node instanceof ColorLiteral) {
            result += ((ColorLiteral) node).value;
        } else if(node instanceof PercentageLiteral) {
	        result += ((PercentageLiteral) node).value + "%";
        } else if(node instanceof PixelLiteral) {
	        result += ((PixelLiteral) node).value + "px";
        } else if(node instanceof ScalarLiteral) {
	        result += ((ScalarLiteral) node).value;
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
