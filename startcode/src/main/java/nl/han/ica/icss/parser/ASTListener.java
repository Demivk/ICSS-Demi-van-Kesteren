package nl.han.ica.icss.parser;


import java.util.Stack;
import nl.han.ica.icss.ast.*;
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


    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
        Stylesheet stylesheet = new Stylesheet();
        ast.root = stylesheet;
        currentContainer.push(stylesheet);
    }


    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
        currentContainer.pop();
    }


    @Override
    public void enterStylerule(ICSSParser.StyleruleContext ctx) {
        Stylerule stylerule = new Stylerule();
        currentContainer.peek().addChild(stylerule);
        currentContainer.push(stylerule);
    }


    @Override
    public void exitStylerule(ICSSParser.StyleruleContext ctx) {
        currentContainer.pop();
    }


    @Override
    public void enterTagSelector(ICSSParser.TagSelectorContext ctx) {
        TagSelector tagSelector = new TagSelector(ctx.getText());
        currentContainer.peek().addChild(tagSelector);
        currentContainer.push(tagSelector);
    }


    @Override
    public void exitTagSelector(ICSSParser.TagSelectorContext ctx) {
        currentContainer.pop();
    }


    @Override
    public void enterClassSelector(ICSSParser.ClassSelectorContext ctx) {
        ClassSelector classSelector = new ClassSelector(ctx.getText());
        currentContainer.peek().addChild(classSelector);
        currentContainer.push(classSelector);
    }


    @Override
    public void exitClassSelector(ICSSParser.ClassSelectorContext ctx) {
        currentContainer.pop();
    }


    @Override
    public void enterIdSelector(ICSSParser.IdSelectorContext ctx) {
        IdSelector idSelector = new IdSelector(ctx.getText());
        currentContainer.peek().addChild(idSelector);
        currentContainer.push(idSelector);
    }


    @Override
    public void exitIdSelector(ICSSParser.IdSelectorContext ctx) {
        currentContainer.pop();
    }
}