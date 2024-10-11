package {{ root_package }}.persistence.jpa;

import java.time.Instant;

/**
 * Entities implementing this interface expose a created date field.
 * <p>
 * Liquibase:
 * <pre>
 * &lt;createTable tableName="example"&gt;
 *    &lt;column name="created" type="TIMESTAMP"&gt;
 *        &lt;constraints nullable="false"/&gt;
 *    &lt;/column&gt;
 * &lt;/createTable&gt;
 * </pre>
 */
public interface Created {

    /**
     * Gets the {@link Instant} that the entity was created.
     *
     * @return created datetime
     */
    Instant getCreatedAt();

    /**
     * Sets the {@link Instant} that the entity was created. This property should typically be set for you by a
     * listener such as {@link DateFieldListener}, and should not be set by hand.  Only gets set once in an
     * entity's lifecycle.
     *
     * @param instant created datetime
     */
    void setCreatedAt(Instant instant);
}
