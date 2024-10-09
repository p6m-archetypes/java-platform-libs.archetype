package {{ root_package }}.data.numbers.integer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Optional;

/**
 * Represents a whole number: [0, inf)
 */
@JsonSerialize(as = NonNegativeInt.class)
@JsonDeserialize(as = NonNegativeInt.class)
public class NonNegativeInt extends {{ VentureName }}Int {

  private NonNegativeInt(int i) {
    super(i);
  }

  public static Optional<NonNegativeInt> from(int i) {
    if (i < 0) {
      return Optional.empty();
    } else {
      return Optional.of(new NonNegativeInt(i));
    }
  }
}
