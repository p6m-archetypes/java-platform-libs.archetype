package {{ root_package }}.persistence.jpa;


import java.io.Serializable;

/**
 * A top-level parent interface for all JPA entities having a single identifier as a primary key.
 */
public interface Identifiable<T extends Serializable> {

    /**
     * Get the identifier for this entity.
     *
     * @see "https://vladmihalcea.com/2016/06/06/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/"
     */
    T getId();
}
