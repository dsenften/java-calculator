// Definieren der Grammatik für unseren Rechner

grammar Calculator;

program
    : statement+                                # printStatement
    ;

statement
    : expression NEWLINE                        # printExpression
    | IDENTIFIER '=' expression                 # assignExpression
    | NEWLINE                                   # blank
    ;
    
expression
    : expression op=( MUL | DIV ) expression    # MulDiv
    | expression op=( ADD | SUB ) expression    # AddSub
    | ('+' | '-')? NUMBER                       # Number
    ;


// Definieren der einzelnen Tokens

MUL        : '*' ;
DIV        : '/' ;
ADD        : '+' ;
SUB        : '-' ;
NUMBER     : [0-9]+('.')?[0-9]* ;
IDENTIFIER : [a-zA-Z]+ ;

NEWLINE    : '\r'? '\n' ;
WS         : [ \t]+ -> skip ;

LINE_COMMENT  : '//' ~[\r\n]* -> skip ;
BLOCK_COMMENT : '/*' .*? '*/' -> skip ;