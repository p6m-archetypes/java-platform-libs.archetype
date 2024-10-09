package {{ root_package }}.persistence.jpa;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
public class AbstractLookupEntity extends AbstractEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "ordinal")
    private short ordinal;

    @Column(name = "display_name")
    private String displayName;

    public AbstractLookupEntity() {
    }

    public String getName() {
        return name;
    }

    public short getOrdinal() {
        return ordinal;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractLookupEntity that = (AbstractLookupEntity) o;
        return ordinal == that.ordinal && name.equals(that.name) && displayName.equals(that.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ordinal, displayName);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "id='" + getId() + '\'' +
                ", name='" + name + '\'' +
                ", ordinal=" + ordinal +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}