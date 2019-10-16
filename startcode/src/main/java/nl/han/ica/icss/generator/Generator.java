package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

import java.util.ArrayList;

public class Generator {

    private String result = "";

	public String generate(AST ast) {
	    generateResult(ast.root);
        return result + "}";
	}

	private void generateResult(ASTNode node) {
	    if(node instanceof VariableAssignment) {
	        generateVariableAssignmentResult(node);
        }
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

    private void generateVariableAssignmentResult(ASTNode node) {
	    if(node instanceof VariableAssignment) {
            result += ((VariableAssignment) node).name.name + " := ";
            generateLiteralResult(((VariableAssignment) node).expression);
            result += "; \n";
        }
	    node.getChildren().forEach(this::generateVariableAssignmentResult);
    }

    private void generateSelectorResult(Selector node) {
	    if(node instanceof ClassSelector) {
	        result += ((ClassSelector) node).cls + " {";
        } else if(node instanceof IdSelector) {
	        result += ((IdSelector) node).id + " {";
        } else if(node instanceof TagSelector) {
	        result += ((TagSelector) node).tag + " {";
        }
	    result += "\n";
    }

    private void generateBodyResult(ASTNode node) {
	    if(node instanceof Declaration) {
            generateDeclarationResult(node.getChildren());
        }
    }

    private void generateDeclarationResult(ArrayList<ASTNode> nodes) {
	    for(ASTNode node : nodes) {
            if (node instanceof PropertyName) {
                result += "\t" + ((PropertyName) node).name + ": ";
            }
            if (node instanceof Expression) {
                generateLiteralResult(node);
            }
            if(node instanceof VariableReference) {
                result += ((VariableReference) node).name;
            }
        }
        result += ";\n";
    }

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
}
