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
@JsonSerialize(as = Immutable{{ SolutionName }}StatusCode.class)
@JsonDeserialize(as = Immutable{{ SolutionName }}StatusCode.class)
public abstract class {{ SolutionName }}StatusCode {
  public abstract List<String> tags();

  @Override
  @Derived
  public String toString() {
    return String.join("::", tags());
  }

  public static {{ SolutionName }}StatusCode fromString(String joinedTags) {
    return fromStrings(joinedTags.split("::"));
  }

  public static {{ SolutionName }}StatusCode fromStrings(String... tags) {
    var code = Immutable{{ SolutionName }}StatusCode.builder();
    code.addTags(tags);
    return code.build();
  }
}
