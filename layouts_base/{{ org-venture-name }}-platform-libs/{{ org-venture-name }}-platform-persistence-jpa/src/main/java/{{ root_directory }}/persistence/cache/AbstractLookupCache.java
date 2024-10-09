package {{ root_package }}.persistence.cache;

import {{ root_package }}.persistence.jpa.AbstractLookupEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractLookupCache<T extends AbstractLookupEntity> implements LookupCache<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractLookupCache.class);

    protected final JpaRepository<T, UUID> repository;
    private Map<UUID, T> idMap;
    private Map<String, T> nameMap;
    private List<T> valuesList;


    protected AbstractLookupCache(JpaRepository<T, UUID> repository) {
        this.repository = repository;
        refresh();
    }

    @Override
    public void refresh() {
        logger.info("event=refresh Initializing cache name={} started", getName());
        Collection<T> entities = repository.findAll();

        idMap = entities.stream()
                .collect(Collectors.toUnmodifiableMap(
                        T::getId,
                        e -> e));

        nameMap = entities.stream()
                .collect(Collectors.toUnmodifiableMap(
                        T::getName,
                        e -> e));

        valuesList = entities.stream()
                .sorted(Comparator.comparing(T::getOrdinal))
                .collect(Collectors.toUnmodifiableList());

        logger.info("event=refresh Initializing cache name={} finished. Cache values: valuesList={}, idMap={}, nameMap={}",
                getName(), valuesList, idMap, nameMap);
    }

    @Override
    public T getValue(UUID key) {
        return idMap.get(key);
    }

    @Override
    public T valueOf(String name) {
        return nameMap.get(name);
    }

    @Override
    public boolean contains(String name) {
        return nameMap.containsKey(name);
    }

    @Override
    public List<T> values() {
        return valuesList;
    }
}
