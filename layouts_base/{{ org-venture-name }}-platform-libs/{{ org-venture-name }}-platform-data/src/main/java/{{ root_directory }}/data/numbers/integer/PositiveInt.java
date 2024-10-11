package {{ root_package }}.data.numbers.integer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Optional;

/**
 * Represents a natural number: [1, inf)
 */
@JsonSerialize(as = PositiveInt.class)
@JsonDeserialize(as = PositiveInt.class)
public class PositiveInt extends {{ SolutionName }}Int {

  private PositiveInt(int i) {
    super(i);
  }

  public static Optional<PositiveInt> from(int i) {
    if (i <= 0) {
      return Optional.empty();
    } else {
      return Optional.of(new PositiveInt(i));
    }
  }
}
