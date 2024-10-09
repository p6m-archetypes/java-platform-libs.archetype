package {{ root_package }}.data.status.codes;

/**
 * These builders are used so that services that import the status code pattern can start off with
 * consistent builders.
 */
public class BaseStatusCodes {
  public static final Immutable{{ VentureName }}StatusCode.Builder TRANSACTION_BASE = Immutable{{ VentureName }}StatusCode.
      builder()
      .addTags("transaction");
  public static final Immutable{{ VentureName }}StatusCode.Builder ACCOUNT_BASE = Immutable{{ VentureName }}StatusCode.
      builder()
      .addTags("account");
}
