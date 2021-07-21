package dotty.using_directives.custom;


import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static dotty.using_directives.custom.utils.Chars.*;

public class CustomCharArrayReader {

    public char[] buf;
    public int startFrom = 0;
    public boolean decodeUni = false;
    public BiConsumer<String, Integer> error;
    public char ch;
    public int charOffset = startFrom;
    public int lastCharOffset = startFrom;
    public int lineStartOffset = startFrom;
    private int lastUnicodeOffset = -1;
    public boolean isUnicodeEscape() {
        return charOffset == lastCharOffset;
    }
    public void nextChar() {
        int idx = charOffset;
        lastCharOffset = idx;
        charOffset = idx + 1;
        if(idx >= buf.length) ch = SU;
        else {
            char c = buf[idx];
            ch = c;
            if(c == '\\') potentialUnicode();
            else if (c < ' ') {
                skipCR();
                potentialLineEnd();
            }
        }
    }

    public char getc() {
        nextChar();
        return ch;
    }

    public void nextRawChar() {
        int idx = charOffset;
        lastCharOffset = idx;
        charOffset = idx + 1;
        if(idx >= buf.length) ch = SU;
        else {
            char c = buf[idx];
            ch = c;
            if(c == '\\') potentialUnicode();
        }
    }

    private void potentialUnicode() {
        Supplier<Boolean> evenSlashPrefix = () -> {
          int p = charOffset - 2;
          while(p >= 0 && buf[p] == '\\') p -= 1;
            return (charOffset - p) % 2 == 0;
        };

        Supplier<Integer> udigit = () -> {
            if(charOffset >= buf.length) {
                error.accept("incomplete unicode escape", charOffset - 1);
                return (int) SU;
            } else {
                return digitToInt(buf[charOffset], 16);
            }
        };

        if (charOffset < buf.length && buf[charOffset] == 'u' && decodeUni && evenSlashPrefix.get()) {
            charOffset += 1;
            while(charOffset + 1 < buf.length && buf[charOffset + 1] == 'u') {
                charOffset += 1;
            }
            int code = udigit.get() << 12 | udigit.get() << 8 | udigit.get() << 4 | udigit.get();
            lastUnicodeOffset = charOffset;
            ch = (char) code;
        }
    }

    private void skipCR() {
        if (ch == CR) {
            if (charOffset < buf.length && buf[charOffset] == LF) {
                charOffset += 1;
                ch = LF;
            }
        }
    }

    private void potentialLineEnd() {
        if (ch == LF || ch == FF) {
            lineStartOffset = charOffset;
        }
    }

    public boolean isAtEnd() {
        return charOffset >= buf.length;
    }

    public CustomCharArrayReader getLookaheadCharArrayReader() {
        CustomCharArrayReader lcar = new CustomCharArrayReader(buf, error);
        lcar.charOffset = charOffset;
        lcar.ch = ch;
        lcar.decodeUni = decodeUni;
        return lcar;
    }

    public char lookaheadChar() {
        return getLookaheadCharArrayReader().getc();
    }


    public CustomCharArrayReader(char[] buf, BiConsumer<String, Integer> error) {
        this.buf = buf;
        this.error = error;
    }
}
