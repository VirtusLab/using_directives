package dotty.using_directives.custom;

import dotty.using_directives.custom.utils.Source;
import dotty.using_directives.custom.utils.ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import java.util.concurrent.TimeUnit;

public class Parser {

    Source source;

    public Parser(Source source) {
        this.source = source;
        this.in = new Scanner(source.getContent(), 0);
    }

    Scanner in;

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
        if (in.td.token == Tokens.AT) {
            in.nextToken();
            if (in.td.token == Tokens.IDENTIFIER && in.td.name.equals("using")) {
                int offset = in.td.offset;
                in.nextToken();
                // in.observeIndented();
                return new UsingDef(settings(), source.getPositionFromOffset(offset));
            }
        }
        return null;
    }

    SettingDefs settings() {
        ArrayList<SettingDef> settings = new ArrayList<>();
        int offset = in.td.offset;
        if(in.td.token == Tokens.INDENT) {
            in.nextToken();
            while(in.td.token != Tokens.OUTDENT) {
                if(in.td.token == Tokens.NEWLINE) {
                    in.nextToken();
                }
                settings.add(setting());
            }
            in.nextToken();
            if(in.td.token == Tokens.NEWLINE) {
                in.nextToken();
            }
        } else {
            settings.add(setting());
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
        if(in.td.token == Tokens.COLONEOL) {
            in.nextToken();
            return settings();
        } else {
            return value();
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