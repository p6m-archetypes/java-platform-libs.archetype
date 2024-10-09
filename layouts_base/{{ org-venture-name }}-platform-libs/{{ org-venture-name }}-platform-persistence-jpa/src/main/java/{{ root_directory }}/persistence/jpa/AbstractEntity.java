package {{ root_package }}.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.*;
import java.util.UUID;

import static java.lang.String.format;

/**
 * A top-level Abstract MappedSuperclass Entity with a UUID 'id' primary key, managed 'created' and 'modified' fields,
 * and a managed 'version' field for optimistic locking. Most persistent Entities should extend from this class.
 *
 * <p>
 * Liquibase:
 * <pre>
 * &lt;createTable tableName="example"&gt;
 *    &lt;column name="id" type="uuid"&gt;
 *        &lt;constraints nullable="false" primaryKey="true"/&gt;
 *    &lt;/column&gt;
 *    &lt;column name="created" type="TIMESTAMP"&gt;
 *        &lt;constraints nullable="false"/&gt;
 *    &lt;/column&gt;
 *    &lt;column name="modified" type="TIMESTAMP"&gt;
 *        &lt;constraints nullable="false"/&gt;
 *    &lt;/column&gt;
 *    &lt;column name="version" type="SMALLINT"&gt;
 *        &lt;constraints nullable="false"/&gt;
 *    &lt;/column&gt;
 * &lt;/createTable&gt;
 * </pre>
 */
@MappedSuperclass
public class AbstractEntity extends AbstractIdentifiableCreatedModifiedVersioned<UUID> {

}
