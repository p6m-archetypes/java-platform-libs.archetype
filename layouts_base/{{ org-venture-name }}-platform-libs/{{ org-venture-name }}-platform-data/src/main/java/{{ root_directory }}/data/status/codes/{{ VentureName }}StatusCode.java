package {{ root_package }}.data.status.codes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;

/**
 * The idea behind this class is to give composable status codes
 * i.e. transaction::check::mitek::nsf
 * Gives improved readability over just enums when it traverses our services
 */

@Immutable
@JsonSerialize(as = Immutable{{ VentureName }}StatusCode.class)
@JsonDeserialize(as = Immutable{{ VentureName }}StatusCode.class)
public abstract class {{ VentureName }}StatusCode {
  public abstract List<String> tags();

  @Override
  @Derived
  public String toString() {
    return String.join("::", tags());
  }

  public static {{ VentureName }}StatusCode fromString(String joinedTags) {
    return fromStrings(joinedTags.split("::"));
  }

  public static {{ VentureName }}StatusCode fromStrings(String... tags) {
    var code = Immutable{{ VentureName }}StatusCode.builder();
    code.addTags(tags);
    return code.build();
  }
}
