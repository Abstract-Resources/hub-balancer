package dev.aabstractt.balancer.datasource;

import lombok.Getter;
import lombok.NonNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public final class RedisDataSource {

    @Getter private final static @NonNull RedisDataSource instance = new RedisDataSource();

    private @Nullable JedisPool jedisPool = null;
    private @Nullable String password = null;

    public void init(String address, String password, String channel) {
        if (address == null) {
            return;
        }

        String[] addressSplit = address.split(":");
        String host = addressSplit[0];
        int port = addressSplit.length > 1 ? Integer.parseInt(addressSplit[1]) : Protocol.DEFAULT_PORT;

        this.password = password != null && password.length() > 0 ? password : null;

        this.jedisPool = new JedisPool(new JedisPoolConfig(), host, port, 30_000, this.password, 0, null);
    }

    public static <T> T query(Function<Jedis, T> action) {
        JedisPool jedisPool = instance.jedisPool;

        if (jedisPool == null || jedisPool.isClosed()) {
            throw new RuntimeException("Jedis was disconnected");
        }

        try (Jedis jedis = jedisPool.getResource()) {
            if (instance.password != null && !instance.password.isEmpty()) {
                jedis.auth(instance.password);
            }

            return action.apply(jedis);
        }
    }

    public static void query(Consumer<Jedis> action) {
        JedisPool jedisPool = instance.jedisPool;

        if (jedisPool == null || jedisPool.isClosed()) {
            throw new RuntimeException("Jedis was disconnected");
        }

        try (Jedis jedis = jedisPool.getResource()) {
            if (instance.password != null && !instance.password.isEmpty()) {
                jedis.auth(instance.password);
            }

            action.accept(jedis);
        }
    }

    public void close() {
        if (this.jedisPool != null) {
            this.jedisPool.destroy();
        }
    }
}