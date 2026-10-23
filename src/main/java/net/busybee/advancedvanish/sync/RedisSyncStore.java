package net.busybee.advancedvanish.sync;

import net.busybee.advancedvanish.config.Settings;
import redis.clients.jedis.RedisClient;

import java.util.UUID;

/** Stores each state as {@code advancedvanish-<uuid> = true|false}, the same keys earlier versions used. */
final class RedisSyncStore implements SyncStore {

    private final Settings settings;
    private RedisClient client;

    RedisSyncStore(Settings settings) {
        this.settings = settings;
    }

    @Override
    public String name() {
        return "Redis";
    }

    @Override
    public void connect() {
        String auth = settings.redisAuth == null || settings.redisAuth.isEmpty() ? null : settings.redisAuth;
        client = RedisClient.create(settings.redisHost, settings.redisPort, null, auth);
        client.ping();
    }

    @Override
    public void close() {
        if (client != null) client.close();
        client = null;
    }

    @Override
    public Boolean get(UUID uuid) {
        String value = client.get(key(uuid));
        return value == null ? null : Boolean.parseBoolean(value);
    }

    @Override
    public void set(UUID uuid, boolean vanished) {
        client.set(key(uuid), Boolean.toString(vanished));
    }

    private static String key(UUID uuid) {
        return "advancedvanish-" + uuid;
    }
}
