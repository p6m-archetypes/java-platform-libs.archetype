package {{ root_package }}.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import static java.lang.String.format;

public interface MergingJpaRepository<T extends AbstractEntity> extends JpaRepository<T, UUID> {

    /**
     * This is a helper method that enables proper merge of the entity. The main use case is following.
     * A service:
     * - receives DTO object in update method
     * - converts it into an entity
     * - uses this method to merge with the data that is in DB already
     * Without this method JPA, even though it recognizes that the object is not new, creates a new object anyways.
     * Here is a details description of the issue https://stackoverflow.com/questions/5105043/jpa-hibernate-merge-performs-insert-instead-of-update
     * @param entity entity to merge
     */
    default void merge(T entity){
        T entityFromDb = findById(entity.getId()).orElseThrow(() ->
                new IllegalArgumentException(format("Unable find an entity of type %s by id", entity.getClass().getSimpleName())));
        entity.setVersion(entityFromDb.getVersion());
        entity.setCreatedAt(entityFromDb.getCreatedAt());
        this.save(entity);
    }

}
