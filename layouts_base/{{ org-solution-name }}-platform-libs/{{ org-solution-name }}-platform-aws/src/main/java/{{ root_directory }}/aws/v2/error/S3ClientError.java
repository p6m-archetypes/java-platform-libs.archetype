package {{ root_package }}.aws.v2.error;

public class S3ClientError extends RuntimeException {
  private final S3ErrorType errorType;

  public S3ClientError(S3ErrorType errorType, Exception jsonException) {
    super(jsonException);
    this.errorType = errorType;
  }

  public S3ErrorType getErrorType() {
    return errorType;
  }
}
