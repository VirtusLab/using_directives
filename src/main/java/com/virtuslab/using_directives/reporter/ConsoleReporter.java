package com.virtuslab.using_directives.reporter;

import com.virtuslab.using_directives.custom.utils.Position;

public class ConsoleReporter implements Reporter {

  private int errorCount = 0;

  private String msgWithPos(Position pos, String msg) {
    return String.format("%d:%d:\n%s", pos.getLine(), pos.getLine(), msg);
  }

  private String errorMessage(String msg) {
    errorCount++;
    return String.format("ERROR: %s", msg);
  }

  private String warningMessage(String msg) {
    return String.format("WARNING: %s", msg);
  }

  @Override
  public void error(String msg) {
    System.out.println(errorMessage(msg));
  }

  @Override
  public void warning(String msg) {
    System.out.println(warningMessage(msg));
  }

  @Override
  public void error(Position position, String msg) {
    System.out.println(msgWithPos(position, errorMessage(msg)));
  }

  @Override
  public void warning(Position position, String msg) {
    System.out.println(msgWithPos(position, warningMessage(msg)));
  }

  @Override
  public boolean hasErrors() {
    return errorCount != 0;
  }

  @Override
  public void reset() {
    errorCount = 0;
  }
}
