package com.virtuslab.using_directives.custom;

import static com.virtuslab.using_directives.custom.utils.Chars.*;

import com.virtuslab.using_directives.custom.utils.CommentSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommentExtractor {
  private CustomCharArrayReader reader;

  private List<Integer> lineOffsets = new ArrayList<>();
  private List<char[]> lines = new ArrayList<>();

  public CommentExtractor(char[] source) {
    this.reader = new CustomCharArrayReader(source, (a, b) -> {});
    reader.nextChar();
    nextComment();
  }

  public CommentSource getCommentSource() {
    char[] commentContent =
        lines.stream().map(String::new).collect(Collectors.joining()).toCharArray();
    if (lineOffsets.isEmpty()) lineOffsets.add(0);
    return new CommentSource(commentContent, lineOffsets);
  }

  private void nextComment() {
    boolean flag = true;
    while (flag) {
      char ch = reader.ch;
      if (ch == ' ' || ch == '\t' || ch == CR || ch == LF || ch == FF) {
        reader.nextChar();
      } else if (ch == '/') {
        if (!skipComment()) {
          flag = false;
        }
      } else {
        flag = false;
      }
    }
  }

  private boolean skipComment() {
    Runnable skipLine =
        () -> {
          reader.nextChar();
          while (reader.ch != CR && reader.ch != LF && reader.ch != SU) {
            reader.nextChar();
          }
        };
    Runnable skipComment =
        () -> {
          int nested = 0;
          boolean flag = true;
          while (flag) {
            if (reader.ch == '/') {
              reader.nextChar();
              if (reader.ch == '*') {
                nested += 1;
                reader.nextChar();
              }
            } else if (reader.ch == '*') {
              reader.nextChar();
              if (reader.ch == '/') {
                if (nested > 0) {
                  nested -= 1;
                  reader.nextChar();
                } else flag = false;
              }
            } else if (reader.ch == LF || reader.ch == FF) {
              lineOffsets.add(0);
              reader.nextChar();
            } else {
              reader.nextChar();
            }
          }
        };
    reader.nextChar();
    if (reader.ch == '/') {
      int commentStart = reader.charOffset;
      lineOffsets.add(reader.charOffset - reader.lineStartOffset);
      skipLine.run();

      int commentEnd = reader.charOffset;
      lines.add(Arrays.copyOfRange(reader.buf, commentStart, commentEnd));
      return true;
    } else if (reader.ch == '*') {
      int commentStart = reader.charOffset;
      lineOffsets.add(reader.charOffset - reader.lineStartOffset);
      reader.nextChar();
      skipComment.run();
      // We need to take two steps back to finish before `*/` fragment
      int commentEnd = reader.charOffset - 2;
      lines.add(Arrays.copyOfRange(reader.buf, commentStart, commentEnd));
      return true;
    } else {
      return false;
    }
  }
}
