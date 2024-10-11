package {{ root_package }}.persistence.cache;

import {{ root_package }}.persistence.jpa.AbstractLookupEntity;

import java.util.List;
import java.util.UUID;

public interface LookupCache<T extends AbstractLookupEntity> {

    String getName();

    void refresh();

    T getValue(UUID key);

    T valueOf(String name);

    boolean contains(String name);

    List<T> values();
}
