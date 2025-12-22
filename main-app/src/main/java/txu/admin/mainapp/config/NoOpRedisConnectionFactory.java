package txu.admin.mainapp.config;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConnection;

public class NoOpRedisConnectionFactory implements RedisConnectionFactory {

    private RedisConnectionFailureException ex() {
        return new RedisConnectionFailureException("Redis disabled / unavailable");
    }

    @Override
    public RedisConnection getConnection() {
        throw ex();
    }

    @Override
    public RedisClusterConnection getClusterConnection() {
        throw ex();
    }

    @Override
    public RedisSentinelConnection getSentinelConnection() {
        throw ex();
    }

    @Override
    public boolean getConvertPipelineAndTxResults() {
        return false;
    }

    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        return null;
    }
}