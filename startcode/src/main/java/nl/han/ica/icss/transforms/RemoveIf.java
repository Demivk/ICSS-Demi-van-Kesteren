package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class RemoveIf implements Transform {

    private LinkedList<HashMap<String, Expression>> variables;

    @Override
    public void apply(AST ast) {
        variables = new LinkedList<>();
        variables.add(new HashMap<>());

//        evaluateIfStatements(ast.root.getChildren(), ast.root);
    }

    private void evaluateIfStatements(List<ASTNode> nodes, ASTNode parent) {
        for(ASTNode node : nodes) {
            if(node instanceof IfClause) {
                IfClause ifClause = (IfClause) node;

                if(ifClause.conditionalExpression instanceof BoolLiteral) {
                    BoolLiteral boolLiteral = (BoolLiteral) ifClause.conditionalExpression;
                    //System.out.println(ifClause.conditionalExpression + " " + boolLiteral.value);
                    if(boolLiteral.value) {
                        //parent.removeChild(node);
                        for(ASTNode bodyNode : ifClause.body) {
                            parent.addChild(bodyNode);
                        }
                        evaluateIfStatements(ifClause.body, parent);
                    } else {
                        System.out.println(parent.getClass());
                        System.out.println("Before: \n" + parent.getChildren());
                        parent.removeChild(node);
                        System.out.println("To remove: \n" + node);
                        System.out.println("After remove: \n" + parent.getChildren());
                    }
                }
            }
            evaluateIfStatements(node.getChildren(), node);
        }
    }
}
