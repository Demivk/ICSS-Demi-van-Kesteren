package nl.han.ica.icss.parser;


import java.util.Stack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;


/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

    //Accumulator attributes:
    private AST ast;


    //Use this to keep track of the parent nodes when recursively traversing the ast
    private Stack<ASTNode> currentContainer;


    public ASTListener() {
        ast = new AST();
        currentContainer = new Stack<>();
    }
    public AST getAST() {
        return ast;
    }

    // STYLESHEET
    /**
     * Creates a new Stylesheet,
     * pushes it to the stack
     * and sets the root to the new stylesheet
     * @param ctx
     */
    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
        Stylesheet stylesheet = new Stylesheet();
        ast.root = stylesheet;
        currentContainer.push(stylesheet);
    }

    /**
     * Pops the item at the top of the stack (Stylesheet)
     * @param ctx
     */
    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
        currentContainer.pop();
    }

    // VARIABLES
    /**
     * Creates a new VariableAssignment,
     * adds it as a child to the item at the top of the stack
     * and pushes it to the stack
     * @param ctx
     */
    @Override
    public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        VariableAssignment variableAssignment = new VariableAssignment();
        currentContainer.peek().addChild(variableAssignment);
        currentContainer.push(variableAssignment);
    }

    /**
     * Pops the item at the top of the stack (VariableAssignment)
     * @param ctx
     */
    @Override
    public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        currentContainer.pop();
    }

    /**
     * Creates a new VariableReference
     * and adds it as a child to the item at the top of the stack.
     * Since it is the lowest node in the tree, it can be added to the tree instantly.
     * @param ctx
     */
    @Override
    public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
        VariableReference variableReference = new VariableReference(ctx.getText());
        currentContainer.peek().addChild(variableReference);
    }

    // STYLERULE
    /**
     * Creates a new Stylerule,
     * adds it as a child to the item at the top of the stack
     * and pushes it to the stack
     * @param ctx
     */
    @Override
    public void enterStylerule(ICSSParser.StyleruleContext ctx) {
        Stylerule stylerule = new Stylerule();
        currentContainer.peek().addChild(stylerule);
        currentContainer.push(stylerule);
    }

    /**
     * Pops the item at the top of the stack (Stylerule)
     * @param ctx
     */
    @Override
    public void exitStylerule(ICSSParser.StyleruleContext ctx) {
        currentContainer.pop();
    }

    // DECLARATION
    /**
     * Creates a new Declaration,
     * adds it as a child to the item at the top of the stack
     * and pushes it to the stack
     * @param ctx
     */
    @Override
    public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
        Declaration declaration = new Declaration();
        currentContainer.peek().addChild(declaration);
        currentContainer.push(declaration);
    }

    /**
     * Pops the item at the top of the stack (Declaration)
     * @param ctx
     */
    @Override
    public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
        currentContainer.pop();
    }

    // PROPERTYNAME
    /**
     * Creates a new PropertyName,
     * adds it as a child to the item at the top of the stack
     * and pushes it to the stack
     * @param ctx
     */
    @Override
    public void enterPropertyName(ICSSParser.PropertyNameContext ctx) {
        PropertyName propertyName = new PropertyName(ctx.getText());
        currentContainer.peek().addChild(propertyName);
        currentContainer.push(propertyName);
    }

    /**
     * Pops the item at the top of the stack (PropertyName)
     * @param ctx
     */
    @Override
    public void exitPropertyName(ICSSParser.PropertyNameContext ctx) {
        currentContainer.pop();
    }

    // OPERATIONS
    /**
     * Creates a new MultiplyOperation,
     * adds it as a child to the item at the top of the stack
     * and pushes it to the stack
     * @param ctx
     */
    @Override
    public void enterMultiplyOperation(ICSSParser.MultiplyOperationContext ctx) {
        Operation operation = new MultiplyOperation();
        currentContainer.peek().addChild(operation);
        currentContainer.push(operation);
    }

    /**
     * Pops the item at the top of the stack (MultiplyOperation)
     * @param ctx
     */
    @Override
    public void exitMultiplyOperation(ICSSParser.MultiplyOperationContext ctx) {
        currentContainer.pop();
    }

    /**
     * Creates a new AddOperation,
     * adds it as a child to the item at the top of the stack
     * and pushes it to the stack
     * @param ctx
     */
    @Override
    public void enterAddOperation(ICSSParser.AddOperationContext ctx) {
        Operation operation = new AddOperation();
        currentContainer.peek().addChild(operation);
        currentContainer.push(operation);
    }

    /**
     * Pops the item at the top of the stack (AddOperation)
     * @param ctx
     */
    @Override
    public void exitAddOperation(ICSSParser.AddOperationContext ctx) {
        currentContainer.pop();
    }

    /**
     * Creates a new SubtractOperation,
     * adds it as a child to the item at the top of the stack
     * and pushes it to the stack
     * @param ctx
     */
    @Override
    public void enterSubtractOperation(ICSSParser.SubtractOperationContext ctx) {
        Operation operation = new SubtractOperation();
        currentContainer.peek().addChild(operation);
        currentContainer.push(operation);
    }

    /**
     * Pops the item at the top of the stack (SubtractOperation)
     * @param ctx
     */
    @Override
    public void exitSubtractOperation(ICSSParser.SubtractOperationContext ctx) {
        currentContainer.pop();
    }

    // IF CLAUSE
    /**
     * Creates a new IfClause,
     * adds it as a child to the item at the top of the stack
     * and pushes it to the stack
     * @param ctx
     */
    @Override
    public void enterIfClause(ICSSParser.IfClauseContext ctx) {
        IfClause ifClause = new IfClause();
        currentContainer.peek().addChild(ifClause);
        currentContainer.push(ifClause);
    }

    /**
     * Pops the item at the top of the stack (IfClause)
     * @param ctx
     */
    @Override
    public void exitIfClause(ICSSParser.IfClauseContext ctx) {
        currentContainer.pop();
    }

    // SELECTORS
    /**
     * Creates a new ClassSelector
     * and adds it as a child to the item at the top of the stack.
     * Since it is the lowest node in the tree, it can be added to the tree instantly.
     * @param ctx
     */
    @Override
    public void exitClassSelector(ICSSParser.ClassSelectorContext ctx) {
        Selector selector = new ClassSelector(ctx.getText());
        currentContainer.peek().addChild(selector);
    }

    /**
     * Creates a new IdSelector
     * and adds it as a child to the item at the top of the stack.
     * Since it is the lowest node in the tree, it can be added to the tree instantly.
     * @param ctx
     */
    @Override
    public void exitIdSelector(ICSSParser.IdSelectorContext ctx) {
        Selector selector = new IdSelector(ctx.getText());
        currentContainer.peek().addChild(selector);
    }

    /**
     * Creates a new TagSelector
     * and adds it as a child to the item at the top of the stack.
     * Since it is the lowest node in the tree, it can be added to the tree instantly.
     * @param ctx
     */
    @Override
    public void exitTagSelector(ICSSParser.TagSelectorContext ctx) {
        Selector selector = new TagSelector(ctx.getText());
        currentContainer.peek().addChild(selector);
    }

    // LITERALS
    /**
     * Creates a new BoolLiteral
     * and adds it as a child to the item at the top of the stack.
     * Since it is the lowest node in the tree, it can be added to the tree instantly.
     * @param ctx
     */
    @Override
    public void exitBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
        Literal literal = new BoolLiteral(ctx.getText());
        currentContainer.peek().addChild(literal);
    }

    /**
     * Creates a new ColorLiteral
     * and adds it as a child to the item at the top of the stack.
     * Since it is the lowest node in the tree, it can be added to the tree instantly.
     * @param ctx
     */
    @Override
    public void exitColorLiteral(ICSSParser.ColorLiteralContext ctx) {
        Literal literal = new ColorLiteral(ctx.getText());
        currentContainer.peek().addChild(literal);
    }

    /**
     * Creates a new PercentageLiteral
     * and adds it as a child to the item at the top of the stack.
     * Since it is the lowest node in the tree, it can be added to the tree instantly.
     * @param ctx
     */
    @Override
    public void exitPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
        Literal literal = new PercentageLiteral(ctx.getText());
        currentContainer.peek().addChild(literal);
    }

    /**
     * Creates a new PixelLiteral
     * and adds it as a child to the item at the top of the stack.
     * Since it is the lowest node in the tree, it can be added to the tree instantly.
     * @param ctx
     */
    @Override
    public void exitPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
        Literal literal = new PixelLiteral(ctx.getText());
        currentContainer.peek().addChild(literal);
    }

    /**
     * Creates a new ScalarLiteral
     * and adds it as a child to the item at the top of the stack.
     * Since it is the lowest node in the tree, it can be added to the tree instantly.
     * @param ctx
     */
    @Override
    public void exitScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
        Literal literal = new ScalarLiteral(ctx.getText());
        currentContainer.peek().addChild(literal);
    }
}