package {{ root_package }}.persistence.jpa;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

/**
 * Extending from this class will add a 'created' timestamp field that automatically sets the creation date on initial
 * insert of the entity, and a 'modified timestamp field automatically sets the last modified date on insert and
 * update.
 */
@MappedSuperclass
public abstract class AbstractCreatedModified extends AbstractCreated implements Modified {

    @Column(nullable = false, name = "modified")
    private Instant lastModifiedDate;

    @Override
    public Instant getModifiedAt() {
        return lastModifiedDate;
    }

    @Override
    public void setModifiedAt(final Instant instant) {
        this.lastModifiedDate = instant;
    }
}
