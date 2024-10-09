package {{ root_package }}.persistence.jpa;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public abstract class AbstractIdentifiableCreatedModifiedVersioned<T extends Serializable> extends AbstractCreatedModifiedVersioned implements Identifiable<T> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private T id;

    @Override
    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }
}
