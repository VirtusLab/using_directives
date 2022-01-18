package com.virtuslab.using_directives.reporter;

import com.virtuslab.using_directives.custom.utils.Position;
import java.util.ArrayList;
import java.util.Optional;

public class PersistentReporter implements Reporter {
  public static class Diagnostic {
    private Optional<Position> position;
    private String message;
    private boolean isError;

    public Diagnostic(String message, Boolean isError, Position position) {
      this.position = Optional.of(position);
      this.isError = isError;
      this.message = message;
    }

    public Diagnostic(String message, Boolean isError) {
      this.position = Optional.empty();
      this.isError = isError;
      this.message = message;
    }

    public Optional<Position> getPosition() {
      return position;
    }

    public String getMessage() {
      return message;
    }
  }

  private ArrayList<Diagnostic> diagnostics = new ArrayList<>();

  @Override
  public void error(String msg) {
    diagnostics.add(new Diagnostic(msg, true));
  }

  @Override
  public void warning(String msg) {
    diagnostics.add(new Diagnostic(msg, false));
  }

  @Override
  public void error(Position position, String msg) {
    diagnostics.add(new Diagnostic(msg, true, position));
  }

  @Override
  public void warning(Position position, String msg) {
    diagnostics.add(new Diagnostic(msg, false, position));
  }

  @Override
  public boolean hasErrors() {
    return diagnostics.stream().filter(d -> d.isError).count() > 0;
  }

  @Override
  public void reset() {
    this.diagnostics = new ArrayList<>();
  }

  public ArrayList<Diagnostic> getDiagnostics() {
    return diagnostics;
  }
}
