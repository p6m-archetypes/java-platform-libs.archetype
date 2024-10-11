package {{ root_package }}.persistence.jpa;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.Instant;

/**
 * This listener class sets a 'created' field, if it exists, on initial insert, and sets the 'modified' field, if it exists,
 * on initial insert and subsequent updates.  Generally, this is handled automatically by having an Entity extend from
 * {@link AbstractCreated} or {@link AbstractCreatedModified}, etc, and should be rare to have to use this listener
 * directly.
 */
public class DateFieldListener {

    @PrePersist
    public void setPrePersistAuditProperties(final Object entity) {
        final Instant now = Instant.now();
        if (entity instanceof Created) {
            ((Created) entity).setCreatedAt(now);
        }
        if (entity instanceof Modified) {
            ((Modified) entity).setModifiedAt(now);
        }
    }

    /**
     * Sets the lastModifiedDate during an update.
     *
     * @param entity entity being updated
     */
    @PreUpdate
    public void setPreUpdateAuditProperties(final Object entity) {
        if (entity instanceof Modified) {
            ((Modified) entity).setModifiedAt(Instant.now());
        }
    }
}
