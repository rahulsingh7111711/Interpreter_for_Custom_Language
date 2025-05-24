package com.interpreter.jail;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
public class Jail {
    static boolean errorOccured = false;
    static boolean runtimeError = false;

    private static final Interpreter interpreter = new Interpreter();
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jail [script]");
            System.exit(64);     
        }
        else if (args.length == 1) {
            runFile(args[0]);    
        }
        else {
            runPrompt();
        }
    }

    //TO run a file present at path
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        // Indicate an error in the exit code.
        if (errorOccured) System.exit(65);
        if(runtimeError) System.exit(70);
    }

    //To reun the code from command Line
    private static void runPrompt() throws IOException {
        System.out.println("!!!!!Welcome TO jail");
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) 
                break;
            run(line);
            errorOccured = false;
        }
    }
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        // System.out.println(tokens);
        // System.out.println(new AstPrinter().print(expression));
        // Stop if there was a syntax error.
        if (errorOccured) return;
        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);
        if (errorOccured) return;
        interpreter.interpret(statements);
    }
    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
            "\n[line " + error.token.line + "]");
        runtimeError = true;
    }
    static void error(int line, String message) {
        report(line, "", message);
    }
    private static void report(int line, String where,String message) {
        System.err.println(
        "[line " + line + "] Error" + where + ": " + message);
        errorOccured = true;
    }
    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } 
        else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

}

