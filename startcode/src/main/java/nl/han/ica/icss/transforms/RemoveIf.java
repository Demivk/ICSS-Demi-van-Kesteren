package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class RemoveIf implements Transform {

    private LinkedList<HashMap<String, Expression>> variables;

    @Override
    public void apply(AST ast) {
        variables = new LinkedList<>();
        variables.add(new HashMap<>());

        evaluateIfStatements(ast.root.getChildren(), ast.root);
    }

    private void evaluateIfStatements(List<ASTNode> nodes, ASTNode parent) {
        for(ASTNode node : nodes) {
            if(node instanceof IfClause) {
                evaluateIfClause((IfClause) node, parent);
            }
            if(node.getChildren() != null) {
                evaluateIfStatements(node.getChildren(), node);
            }
        }
    }

    private List<ASTNode> evaluateIfClause(IfClause ifClause, ASTNode parent) {
        List<ASTNode> values = new ArrayList<>();
        if(((BoolLiteral) ifClause.conditionalExpression).value) {
            for(ASTNode node : ifClause.body) {
                if(node instanceof IfClause) {
                    values.addAll(evaluateIfClause((IfClause) node, ifClause));
                } else {
                    values.add(node);
                }
            }
        }
        removeIfClause(ifClause, parent);
        if(!(parent instanceof IfClause)) {
            for(ASTNode node : values) {
                parent.addChild(node);
            }
        }
        return values;
    }

    private void removeIfClause(IfClause ifClause, ASTNode parent) {
        if(parent instanceof Stylerule) {
            Stylerule stylerule = (Stylerule) parent;
            int index = stylerule.body.indexOf(ifClause);
            stylerule.body.remove(index);
        } else {
            parent.removeChild(ifClause);
        }
    }
}
