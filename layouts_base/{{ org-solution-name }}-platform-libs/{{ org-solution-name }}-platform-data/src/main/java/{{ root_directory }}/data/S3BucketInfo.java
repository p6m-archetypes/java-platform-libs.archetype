package {{ root_package }}.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@JsonSerialize(as = ImmutableS3BucketInfo.class)
@JsonDeserialize(as = ImmutableS3BucketInfo.class)
@Immutable
public abstract class S3BucketInfo {

  @Parameter
  public abstract String bucketName();

  @Parameter
  public abstract String fileName();

  @Derived
  @Override
  public String toString() {
    return bucketName() + "::" + fileName();
  }

}
