package com.interpreter.jail;

import static com.interpreter.jail.TokenType.*;

import java.util.ArrayList;
import java.util.List; 
class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    Scanner(String source) {
        this.source = source;
    }
    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }
    private boolean isAtEnd() {
        return current >= source.length();
    }
    private char advance() {
        current++;
        return source.charAt(current - 1);
    }
        private void addToken(TokenType type) {
        addToken(type, null);
    }
        private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break; 
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) advance();
                }   
                    else 
                    addToken(SLASH);
                    break;
            case ' ':
            case '\r':
            case '\t':
            // Ignore whitespace.
                break;
            case '\n':line++;
                break;
            case '"':string();
                break;
            default:
                if (isDigit(c)) {
                    number();
                } 
                else if (isAlpha(c)) {
                    identifier();
                } else {
                    Jail.error(line, "Unexpected character.");
                }
        }
    }
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }
        if (isAtEnd()) {
            Jail.error(line, "Unterminated string.");
            return;
        }
        // The closing ".
        advance();
        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
        Character.isDigit('t');
    }
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    private void number() {
        while (isDigit(peek())) advance();
        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
        // Consume the "."
            advance();
            while (isDigit(peek())) advance();
        }
        addToken(NUMBER,
        Double.parseDouble(source.substring(start, current)));
    }
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    } 
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = Keywords.keywords.getOrDefault(text,IDENTIFIER);
        addToken(type);
    }
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||(c>= 'A' && c <= 'Z') ||c == '_';
    }
    private boolean isAlphaNumeric(char c){
        return isAlpha(c) || isDigit(c);
    }
    
}
