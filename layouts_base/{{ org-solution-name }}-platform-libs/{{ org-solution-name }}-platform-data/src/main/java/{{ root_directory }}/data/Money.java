package {{ root_package }}.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import org.immutables.value.Value.Immutable;

@JsonSerialize(as = ImmutableMoney.class)
@JsonDeserialize(as = ImmutableMoney.class)
@Immutable
public interface Money {

  BigDecimal amount();

  Currency currency();
}
