package exception;

public class SkierServerException extends Exception {
  public SkierServerException(String errorMessage, Throwable err) {
    super(errorMessage, err);
  }
}