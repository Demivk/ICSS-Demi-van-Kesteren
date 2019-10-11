grammar ICSS;

//--- LEXER: ---
// IF support:
IF: 'if';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;

//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';

//--- PARSER: --- // TODO camelCase

// Stylesheet
stylesheet: variableassignment+ stylerule+ EOF;

// Variables
variableassignment: variablereference ASSIGNMENT_OPERATOR expression SEMICOLON;
variablereference: CAPITAL_IDENT;

// Stylerule
stylerule: selector OPEN_BRACE scope CLOSE_BRACE;
scope: body+;
body: declaration | ifclause;

// Declaration
declaration: propertyname COLON expression SEMICOLON;
propertyname: LOWER_IDENT;

// Expression
expression: expression MUL expression | expression PLUS expression | expression MIN expression | variablereference | literal;

// If clause
ifclause: IF BOX_BRACKET_OPEN expression BOX_BRACKET_CLOSE OPEN_BRACE scope CLOSE_BRACE;

// Selectors
selector: tagSelector | classSelector | idSelector;
tagSelector: LOWER_IDENT;
classSelector: CLASS_IDENT;
idSelector: ID_IDENT;

// Literals
literal: boolLiteral | pixelLiteral | percentageLiteral | colorLiteral | scalarLiteral;
boolLiteral: TRUE | FALSE;
colorLiteral: COLOR;
percentageLiteral: PERCENTAGE;
pixelLiteral: PIXELSIZE;
scalarLiteral: SCALAR;