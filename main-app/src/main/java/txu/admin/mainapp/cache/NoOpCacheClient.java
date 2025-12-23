package txu.admin.mainapp.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

//@ConditionalOnMissingBean(CacheClient.class)
public class NoOpCacheClient {

//    @Override
//    public <T> T get(
//            String key,
//            Class<T> type,
//            Duration ttl,
//            Supplier<T> loader
//    ) {
//        return loader.get();
//    }
//
//    @Override
//    public void evict(String key) {
//        // no-op
//    }
}
