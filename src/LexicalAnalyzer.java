import java.io.FileReader;
import java.io.IOException;

public class LexicalAnalyzer {

    //문자 유형
    final static int LETTER = 11;
    final static int DIGIT = 12;
    final static int UNKNOWN = 99;

    final static int EOF = -1;

    //토큰 유형
    final static int INT_LIT = 0;
    final static int ADD_OP = 1;
    final static int SUB_OP = 2;
    final static int LEFT_PAREN = 3;
    final static int RIGHT_PAREN = 4;
    final static int SEMI_COLON = 5;
    final static int MUL_OP = 6;
    final static int DIV_OP = 7;
    final static int IDENT = 9;
    final static int ASSIGN_OP = 10;

    int next_token;
    int charClass;
    char nextChar;
    String token_string = "";
    FileReader fileReader;

    public LexicalAnalyzer(String fileName) throws IOException {
        fileReader = new FileReader(fileName);
        getChar();
    }

    public int lookup(char ch) {
        switch (ch) {
            case '+':
                next_token = ADD_OP;
                addChar();
                break;
            case '-':
                next_token = SUB_OP;
                addChar();
                break;
            case '(':
                next_token = LEFT_PAREN;
                addChar();
                break;
            case ')':
                next_token = RIGHT_PAREN;
                addChar();
            case ';':
                next_token = SEMI_COLON;
                addChar();
                break;
            case '*':
                next_token = MUL_OP;
                addChar();
                break;
            case '/':
                next_token = DIV_OP;
                addChar();
                break;
            case ':':
                addChar();
                getChar();
                addChar();
                next_token = ASSIGN_OP;
                break;
            default:
                ;
        }
        return next_token;
    }

    private void addChar() {
        token_string += nextChar;
    }

    private void getChar() {

        try {

            if ((nextChar = (char) fileReader.read()) != EOF) {
                if (Character.isAlphabetic(nextChar)) charClass = LETTER;
                else if (Character.isDigit(nextChar)) charClass = DIGIT;
                else charClass = UNKNOWN;
            } else charClass = EOF;
        } catch (IOException e) {

        }

    }

    private void getNonBlank() {
        while (nextChar <= 32) {
            getChar();
        }
    }

    public void lexical() {
        token_string = "";
        getNonBlank();
        switch (charClass) {

            case LETTER:
                addChar();
                getChar();
                while (charClass == LETTER || charClass == DIGIT) {
                    addChar();
                    getChar();
                }
                next_token = IDENT;
                break;

            case DIGIT:
                addChar();
                getChar();
                while (charClass == DIGIT) {
                    addChar();
                    getChar();
                }
                next_token = INT_LIT;
                break;

            case UNKNOWN:
                lookup(nextChar);
                getChar();
                break;

        }
    }

}
