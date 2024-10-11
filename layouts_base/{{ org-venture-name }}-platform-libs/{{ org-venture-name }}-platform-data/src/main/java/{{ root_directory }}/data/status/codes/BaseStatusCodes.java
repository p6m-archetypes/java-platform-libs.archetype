package {{ root_package }}.data.status.codes;

/**
 * These builders are used so that services that import the status code pattern can start off with
 * consistent builders.
 */
public class BaseStatusCodes {
  public static final Immutable{{ SolutionName }}StatusCode.Builder TRANSACTION_BASE = Immutable{{ SolutionName }}StatusCode.
      builder()
      .addTags("transaction");
  public static final Immutable{{ SolutionName }}StatusCode.Builder ACCOUNT_BASE = Immutable{{ SolutionName }}StatusCode.
      builder()
      .addTags("account");
}
