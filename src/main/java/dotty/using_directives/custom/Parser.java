package dotty.using_directives.custom;

import dotty.using_directives.custom.utils.Source;
import dotty.using_directives.custom.utils.ast.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static dotty.using_directives.custom.Tokens.*;
import static dotty.using_directives.custom.utils.TokenUtils.literalTokens;
import static dotty.using_directives.custom.utils.TokenUtils.tokenFromInt;

public class Parser {

    Source source;

    public Parser(Source source) {
        this.source = source;
        this.in = new Scanner(source.getContent(), 0);
    }

    Scanner in;

    /* Combinators */

    public boolean isStatSep() {
        return in.td.isNewLine() || in.td.token == SEMI;
    }

    public void accept(Tokens token) {
        if(in.td.token == token) {
            in.nextToken();
        }
        else {
            // error(Expected token but found td.token)
        }
    }

    public <T> T enclosed(Tokens token, Supplier<T> callback) {
        accept(token);
        try {
            return callback.get();
        } finally {
            accept(tokenFromInt(token.ordinal() + 1));
        }
    }

    public <T> T inBracesOrIndented(Supplier<T> callback) {
        switch(in.td.token) {
            case INDENT:
                return enclosed(INDENT, callback);
            case LBRACE:
                return enclosed(LBRACE, callback);
            default:
                return callback.get();
                // report error
        }
    }

    public void possibleTemplateStart() {
        in.observeColonEOL();
        if(in.td.token == COLONEOL) {
            if(in.lookahead().token == END) {
                in.td.token = NEWLINE;
            }
            else {
                in.nextToken();
                if(in.td.token != INDENT && in.td.token != LBRACE) {
                    // error(i"indented definitions expected, ${in} found")
                }
            }
        } else {
            newLineOptWhenFollowedBy(LBRACE);
        }
    }

    public void newLineOptWhenFollowedBy(Tokens token) {
        if(in.td.token == NEWLINE && in.next.token == token) {
            in.nextToken();
        }
    }

    /* */

    Integer nameStart() {
        if (in.td.token == Tokens.BACKQUOTED_IDENT) {
            return in.td.offset + 1;
        }
        else {
            return in.td.offset;
        }
    }

    public UsingTree parse() {
        UsingTree t = usingDirectives();
        //TODO return rest of the code
        return t;
    }

    UsingTree atSpan(Integer start, Integer startName, UsingTree ut) {
        //TODO IMPLEMENT Spans
        return ut;
    }

    UsingTree usingDirectives() {
        ArrayList<UsingDef> usingTrees = new ArrayList<>();
        UsingDef ud = usingDirective();
        int offset = in.td.offset;
        while(ud != null) {
            usingTrees.add(ud);
            in.nextToken();
            ud = usingDirective();
        }
        return new UsingDefs(usingTrees, source.getPositionFromOffset(offset));
    }

    UsingDef usingDirective() {
        if (in.td.token == Tokens.ATUSING) {
                int offset = in.td.offset;
                in.nextToken();
                // in.observeIndented();
                return new UsingDef(settings(), source.getPositionFromOffset(offset));
        }
        return null;
    }

    private List<SettingDef> parseSettings() {
        if(isStatSep()) {
            in.nextToken();
            if(in.td.token == IDENTIFIER) {
                List<SettingDef> settings = new ArrayList<>();
                settings.add(setting());
                settings.addAll(parseSettings());
                return settings;
            } else {
                return parseSettings();
            }
        } else if(in.td.token == IDENTIFIER) {
            List<SettingDef> settings = new ArrayList<>();
            settings.add(setting());
            settings.addAll(parseSettings());
            return settings;
        } else {
            return new ArrayList<>();
        }
    }

    SettingDefs settings() {
        possibleTemplateStart();
        ArrayList<SettingDef> settings = new ArrayList<>();
        int offset = in.td.offset;
        if(in.td.token == IDENTIFIER) {
            settings.add(setting());
        } else {
            settings.addAll(inBracesOrIndented(this::parseSettings));
        }
        return new SettingDefs(settings, source.getPositionFromOffset(offset));
    }

    SettingDef setting() {
        int offset = in.td.offset;
        String key = key();
        SettingDefOrUsingValue value = valueOrSetting();
        return new SettingDef(key, value, source.getPositionFromOffset(offset));
    }

    String key() {
        if(in.td.token == Tokens.IDENTIFIER) {
            String key = in.td.name;
            in.nextToken();
            if(in.td.token == Tokens.DOT) {
                in.nextToken();
                return key + "." + key();
            } else {
                return key;
            }
        }
        return null;
    }

    SettingDefOrUsingValue valueOrSetting() {
        if(literalTokens.contains(in.td.token) || (in.td.token == IDENTIFIER && in.td.name.equals("-"))) {
            return value();
        }
        else {
            return settings();
        }
    }

    UsingValue value() {
        UsingPrimitive p = primitive();
        int offset = in.td.offset;
        in.nextToken();
        if(in.td.token == Tokens.COMMA) {
            in.nextToken();
            UsingValue rest = value();
            if(rest instanceof UsingPrimitive) {
                ArrayList<UsingPrimitive> res = new ArrayList<>();
                res.add(p);
                res.add((UsingPrimitive)rest);
                return new UsingValues(res, source.getPositionFromOffset(offset));
            } else {
                ((UsingValues)rest).values.add(0, p);
                return rest;
            }
        } else {
            return p;
        }
    }
    
    private final List<Tokens> numericTokens = Arrays.asList(
        Tokens.INTLIT,
        Tokens.DECILIT,
        Tokens.EXPOLIT,
        Tokens.LONGLIT,
        Tokens.FLOATLIT,
        Tokens.DOUBLELIT
    );

    UsingPrimitive primitive() {
        if(in.td.token == Tokens.STRINGLIT) {
            return new StringLiteral(in.td.strVal, source.getPositionFromOffset(in.td.offset));
        } else if (in.td.token == Tokens.IDENTIFIER && in.td.name.equals("-")) {
            in.nextToken();
            if (numericTokens.contains(in.td.token)) {
                return new NumericLiteral("-" + in.td.strVal, source.getPositionFromOffset(in.td.offset));
            }
        } else if (numericTokens.contains(in.td.token)) {
            //TODO check negative
            return new NumericLiteral(in.td.strVal, source.getPositionFromOffset(in.td.offset));
        } else if (in.td.token == Tokens.TRUE) {
            return new BooleanLiteral(true, source.getPositionFromOffset(in.td.offset));
        } else if (in.td.token == Tokens.FALSE) {
            return new BooleanLiteral(false, source.getPositionFromOffset(in.td.offset));
        }
        return null;
    }
}