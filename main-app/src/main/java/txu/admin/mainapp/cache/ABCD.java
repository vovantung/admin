package txu.admin.mainapp.cache;

import java.time.Duration;
import java.util.function.Supplier;

public interface ABCD {
    <T> T get(
            String key,
            Class<T> type,
            Duration ttl,
            Supplier<T> loader
    );

    void evict(String key);
}
