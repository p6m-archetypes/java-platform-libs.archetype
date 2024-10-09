package {{ root_package }}.persistence.jpa;

import java.time.Instant;

/**
 * Entities implementing this interface expose a last modified date field.
 * <p>
 * Liquibase:
 * <pre>
 * &lt;createTable tableName="example"&gt;
 *    &lt;column name="modified" type="TIMESTAMP"&gt;
 *        &lt;constraints nullable="false"/&gt;
 *    &lt;/column&gt;
 * &lt;/createTable&gt;
 * </pre>
 */
public interface Modified {

    /**
     * Gets the {@link Instant} that the entity was last modified.
     *
     * @return last modified datetime
     */
    Instant getModifiedAt();

    /**
     * Sets the {@link Instant} that the entity was last modified.  This property should typically be set for you by a
     * listener such as {@link DateFieldListener}, and should not be set by hand.  Gets set each time an entity
     * is modified.
     *
     * @param instant last modified datetime
     */
    void setModifiedAt(Instant instant);
}
