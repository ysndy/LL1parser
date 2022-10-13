import java.io.IOException;
import java.util.HashMap;

public class Main {

    public static HashMap<String, Integer> symbolTable;
    public static LexicalAnalyzer lexicalAnalyzer;
    private static int count_id, count_const, count_op;
    private static String syntax;
    public static Exception error;
    public static Exception warning;


    public static void main(String[] args) {
        String fileName = "src//eval4.txt";
        //fileName = args[0];
        try {
            lexicalAnalyzer = new LexicalAnalyzer(fileName);
            symbolTable = new HashMap<>();
            program();
        } catch (IOException e) {
            error = e;
            print_state();
        }

    }

    //토큰 종류별 갯수 출력
    public static void print_tokenCount(int id, int constant, int op) {
        System.out.println("ID: " + id + "; CONST: " + constant + "; OP: " + op + ";");
    }

    //파싱 상태 출력
    public static void print_state() {
        String str;
        if (error == null) {
            if (warning == null) str = "(OK)";
            else str = "(Warning) \"" + warning.getMessage() + "\"";
        } else str = "(Error) \"" + error.getMessage() + "\"";
        System.out.println(str);
    }

    //결과 출력
    public static void print_results() {
        System.out.print("Result ==> ");
        symbolTable.forEach((key, value) -> {
            System.out.print(key + ": " + (value == null ? "Unknown" : value) + "; ");
        });
    }

    public static void program() {
        lexicalAnalyzer.lexical();
        statements();
        print_results();
    }

    public static void statements() {
        statement();
        if (lexicalAnalyzer.next_token == LexicalAnalyzer.SEMI_COLON) {
            semi_colon();
            statements();
        }
    }

    public static void statement() {
        count_id = count_const = count_op = 0;
        syntax = "";
        error = warning = null;
        String ident = ident();
        assignment_op();
        Integer value = expression();
        symbolTable.put(ident, value);
        System.out.println(syntax);
        print_tokenCount(count_id, count_const, count_op);
        print_state();
    }


    public static Integer expression() {
        return term_tail(term());
    }


    public static Integer term_tail(Integer operand) {
        Integer result = operand;
        if (lexicalAnalyzer.next_token == LexicalAnalyzer.ADD_OP) {
            add_operator();
            Integer term_tail = term_tail(term());
            try {
                result = operand + term_tail;
            } catch (NullPointerException e) {
                result = null;
            }
        } else if (lexicalAnalyzer.next_token == LexicalAnalyzer.SUB_OP) {
            add_operator();
            result = operand - term_tail(term());

        } else epsilon();
        return result;
    }

    public static Integer term() {
        return factor_tail(factor());
    }


    public static Integer factor_tail(Integer operand) {
        Integer result = operand;
        if (lexicalAnalyzer.next_token == LexicalAnalyzer.MUL_OP) {
            mul_operator();
            Integer factor_tail = factor_tail(factor());
            try {
                result = operand * factor_tail;
            } catch (NullPointerException e) {
                result = null;
            }
        } else if (lexicalAnalyzer.next_token == LexicalAnalyzer.DIV_OP) {
            mul_operator();
            result = operand / factor_tail(factor());
        } else epsilon();

        return result;
    }

    public static Integer factor() {
        Integer result;
        String ident;
        if (lexicalAnalyzer.next_token == LexicalAnalyzer.LEFT_PAREN) {
            left_paren();
            result = expression();
            right_paren();
        } else if (lexicalAnalyzer.next_token == LexicalAnalyzer.IDENT) {
            ident = ident();
            if (!symbolTable.containsKey(ident)) {
                error = new Exception("정의되지 않은 변수(" + ident + ")가 참조됨");
                symbolTable.put(ident, null);
            }
            result = symbolTable.get(ident);
        } else result = constant();
        return result;
    }

    public static Integer constant() {
        count_const++;
        int result;
        try {
            result = Integer.parseInt(lexicalAnalyzer.token_string);
        } catch (NumberFormatException e) {
            warning = new Exception("중복 연산자(" + lexicalAnalyzer.token_string + ") 제거");
            lexicalAnalyzer.lexical();
            result = Integer.parseInt(lexicalAnalyzer.token_string);
        }
        syntax+=lexicalAnalyzer.token_string+" ";
        lexicalAnalyzer.lexical();
        return result;

    }

    public static String ident() {
        String result = lexicalAnalyzer.token_string;
        count_id++;
        syntax+=lexicalAnalyzer.token_string+" ";
        lexicalAnalyzer.lexical();
        return result;
    }

    public static void assignment_op() {
        syntax+=lexicalAnalyzer.token_string+" ";
        lexicalAnalyzer.lexical();
    }

    public static void semi_colon() {
        syntax+=lexicalAnalyzer.token_string+" ";
        lexicalAnalyzer.lexical();
    }

    public static void add_operator() {
        count_op++;
        syntax+=lexicalAnalyzer.token_string+" ";
        lexicalAnalyzer.lexical();
    }

    public static void mul_operator() {
        count_op++;
        syntax+=lexicalAnalyzer.token_string+" ";
        lexicalAnalyzer.lexical();
    }

    public static void left_paren() {
        syntax+=lexicalAnalyzer.token_string+" ";
        lexicalAnalyzer.lexical();
    }

    public static void right_paren() {
        syntax+=lexicalAnalyzer.token_string+" ";
        lexicalAnalyzer.lexical();

    }

    public static void epsilon() {
    }


}
