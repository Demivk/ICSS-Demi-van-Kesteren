package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
// TODO javadoc
// TODO varref/varass naar transformer?
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

	private void generateResult(ASTNode node) {
	    if(node instanceof Selector) {
            if(result.endsWith(";\n")) {
                result += "}\n\n";
            }
	        generateSelectorResult((Selector) node);
        }
//	    if(node instanceof IfClause) {
//            generateIfClauseResult(node);
//        }
	    if(node instanceof Declaration) {
	        generateBodyResult(node);
        }
	    node.getChildren().forEach(this::generateResult);
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
                generateVariableValueByName(node);
            }
//            if(node instanceof Operation) {
//                generateOperationResult(node);
//            }
        }
        result += ";\n";
    }

//    private void generateIfClauseResult(ASTNode node) {
//	    if(node instanceof IfClause) {
//	        result += "IFFF";
//	        result += ((IfClause) node).conditionalExpression; // varref
//	        generateVariableValueByName(node);
//	        result += "\n";
//	        // als conditie in if = true
//            // zet dat als result anders niet
//	        node.getChildren().forEach(this::generateIfClauseResult);
//        }
//    }

//    private void generateOperationResult(ASTNode node) {
//	    if(node instanceof Operation) {
//	        if(node instanceof MultiplyOperation) {
//	            result += "MULTIPLY\n";
//            }
//	        if(node instanceof AddOperation) {
//                result += "ADD\n";
//            }
//	        if(node instanceof SubtractOperation) {
//                result += "SUBTRACT\n";
//            }
//        }
//	    node.getChildren().forEach(this::generateOperationResult);
//    }

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

    private void generateVariableValueByName(ASTNode node) {
	    if(node instanceof VariableReference) {
            if (variables.getFirst().containsKey(((VariableReference) node).name)) {
                generateLiteralResult(variables.getFirst().get(((VariableReference) node).name));
            }
        }
    }

    private void findAllVariables(ASTNode node) {
        if (node instanceof VariableAssignment) {
            String name = ((VariableAssignment) node).name.name;
            Expression expression = ((VariableAssignment) node).expression;
            variables.getFirst().put(name, expression);
        }
        node.getChildren().forEach(this::findAllVariables);
    }
}
