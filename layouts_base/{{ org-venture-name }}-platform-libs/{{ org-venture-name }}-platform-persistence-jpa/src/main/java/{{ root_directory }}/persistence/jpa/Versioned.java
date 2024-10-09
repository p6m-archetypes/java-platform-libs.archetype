package {{ root_package }}.persistence.jpa;

import java.io.Serializable;

/**
 * A common interface for all entities requiring optimistic locking.  To get optimistic locking, you'll need to extend from
 * an abstract MappedSuperclass with a field annotated with {@link javax.persistence.Version}, such as {@link AbstractVersioned}
 * or {@link AbstractCreatedModifiedVersioned}, etc.  Most classes should implement this interface.
 *
 * <p>
 * Liquibase:
 * <pre>
 * &lt;createTable tableName="example"&gt;
 *    &lt;column name="version" type="SMALLINT"&gt;
 *        &lt;constraints nullable="false"/&gt;
 *    &lt;/column&gt;
 * &lt;/createTable&gt;
 * </pre>
 */
public interface Versioned extends Serializable {

    /**
     * Gets the version number of the entity.
     *
     * @return entity version
     */
    Short getVersion();

    /**
     * Sets the version number for this entity.  This value is typically set and auto-incremented by Hibernate.  Setting
     * this value by hand may be necessary when editing an entity in a client from where it's important to know if the
     * entity has been modified in another process.  In this case, the user may be notified that changes were made and
     * can be presented with a "before" and "after" to help them merge in the changes.
     * <p/>
     * Note: the database column should be a <b>smallint</b>.
     *
     * @param version entity version
     */
    void setVersion(Short version);
}
