package dotty.using_directives.custom.utils;

public class Chars {
    public static final char SU = (char) 0x001A;
    public static final char FF = (char) 0x000C;
    public static final char CR = (char) 0x000D;
    public static final char LF = (char) 0x000A;

    public static int digitToInt(char ch, int base) {
        try {
            return Integer.parseInt(String.valueOf(ch), base);
        } catch(NumberFormatException e) {
            return -1;
        }
    }

    public static boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == CR;
    }

    public static boolean isIdentifierStart(char c) {
        return c == '_' || c == '$' || Character.isUnicodeIdentifierStart(c);
    }

    public static boolean isIdentifierPart(char c) {
        return c == '$' || Character.isUnicodeIdentifierPart(c);
    }

    public static boolean isSpecial(char c) {
        int chtp = Character.getType(c);
        return chtp == Character.MATH_SYMBOL || chtp == Character.OTHER_SYMBOL;
    }
}
