package com.virtuslab.using_directives.custom.utils;

import java.util.ArrayList;
import java.util.List;

public class CommentSource extends Source {
    private final List<Integer> lineOffsetSums;
    public final List<Integer> lineOffsets;

    public CommentSource(char[] content, List<Integer> lineOffsets) {
        super(content);
        List<Integer> lineOffsetsSums = new ArrayList<>();
        lineOffsetsSums.add(lineOffsets.get(0));
        for(int i = 1; i < lineOffsets.size(); i++) {
            lineOffsetsSums.add(lineOffsetsSums.get(i - 1) + lineOffsets.get(i));
        }
        this.lineOffsetSums = lineOffsetsSums;
        this.lineOffsets = lineOffsets;
    }

    @Override
    public Position getPositionFromOffset(int offset) {
        int lineNumber = -1;
        int columnNumber = -1;
        for(int i = lineStarts.size() - 1; i >= 0; i--) {
            if(lineStarts.get(i) <= offset) {
                lineNumber = i;
                if(i < lineOffsets.size()) {
                    columnNumber = offset - lineStarts.get(i) + lineOffsets.get(i);
                } else {
                    columnNumber = offset - lineStarts.get(i);
                }
                break;
            }
        }
        return new Position(lineNumber, columnNumber, offset);
    }

    @Override
    public int translateOffset(int offset) {
        int newOffset = offset;
        for(int i = lineStarts.size() - 1; i >= 0; i--) {
            if(lineStarts.get(i) < offset) {
                newOffset += lineOffsetSums.get(i);
                break;
            }
        }
        return newOffset;
    }
}
