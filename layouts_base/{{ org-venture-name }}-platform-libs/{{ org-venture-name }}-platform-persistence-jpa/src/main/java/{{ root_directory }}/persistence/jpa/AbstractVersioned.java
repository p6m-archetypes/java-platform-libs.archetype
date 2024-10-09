package {{ root_package }}.persistence.jpa;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * Extending from this abstract MappedSuperclass will enable automatic optimistic locking.
 */
@MappedSuperclass
public class AbstractVersioned implements Versioned {

    @Version
    @Column(name = "version")
    private Short version;

    @Override
    public Short getVersion() {
        return version;
    }

    @Override
    public void setVersion(final Short version) {
        this.version = version;
    }
}
