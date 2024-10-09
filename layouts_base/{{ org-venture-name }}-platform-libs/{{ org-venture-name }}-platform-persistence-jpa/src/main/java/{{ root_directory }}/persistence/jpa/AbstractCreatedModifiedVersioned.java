package {{ root_package }}.persistence.jpa;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * Extending from this class will add a 'created' timestamp field that automatically sets the creation date on initial
 * insert of the entity, a 'modified' timestamp field automatically sets the last modified date on insert and
 * update, and a 'version' field for optimistic locking that increments on every update.
 */
@MappedSuperclass
public abstract class AbstractCreatedModifiedVersioned extends AbstractCreatedModified implements Versioned {

    @Version
    @Column(name = "version")
    protected Short version;

    @Override
    public Short getVersion() {
        return version;
    }

    @Override
    public void setVersion(final Short version) {
        this.version = version;
    }
}
