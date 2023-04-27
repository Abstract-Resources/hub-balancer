package dev.aabstractt.balancer.factory;

import dev.aabstractt.balancer.datasource.RedisDataSource;
import dev.aabstractt.balancer.object.LocalServerInfo;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerFactory {

    @Getter private final static @NonNull ServerFactory instance = new ServerFactory();

    private final @NonNull Map<@NonNull String, @NonNull LocalServerInfo> servers = new ConcurrentHashMap<>();

    public void init() {
        List<String> priorities = ProxyServer.getInstance().getConfiguration().getPriorities();

        for (String serverName : priorities) {
            this.servers.put(serverName, new LocalServerInfo(
                    serverName,
                    0,
                    false
            ));
        }

        ProxyServer.getInstance().getScheduler().scheduleRepeating(() -> {
            Set<String> serversOnline = RedisDataSource.query(jedis -> {
                return jedis.smembers("servers-online");
            });

            for (LocalServerInfo localServerInfo : this.servers.values()) {
                localServerInfo.setOnline(serversOnline.contains(localServerInfo.getServerName()));

                String maxSlots = RedisDataSource.query(jedis -> {
                    return jedis.hget("servers:" + localServerInfo.getServerName(), "max_slots");
                });
                localServerInfo.setMaxSlots(maxSlots == null ? 0 : Integer.parseInt(maxSlots));
            }
        }, 10, true);
    }

    public @Nullable LocalServerInfo getBetterServer(@Nullable String except) {
        LocalServerInfo currentServer = null;

        for (LocalServerInfo serverInfo : this.servers.values()) {
            if (!serverInfo.isOnline()) continue;
            if (serverInfo.getServerName().equals(except)) continue;

            if (serverInfo.getCurrentPlayers() >= serverInfo.getMaxSlots()) continue;

            if (currentServer == null) {
                currentServer = serverInfo;

                continue;
            }

            ServerInfo currentWaterdogServer = currentServer.toWaterdogServer();
            if (currentWaterdogServer == null) continue;
            if (serverInfo.getCurrentPlayers() >= currentWaterdogServer.getPlayers().size()) continue;

            currentServer = serverInfo;
        }

        return currentServer != null && !currentServer.isOnline() ? null : currentServer;
    }
}