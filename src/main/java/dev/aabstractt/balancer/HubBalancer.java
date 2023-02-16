package dev.aabstractt.balancer;

import com.google.common.collect.Maps;
import dev.aabstractt.balancer.command.HubCommand;
import dev.aabstractt.balancer.datasource.RedisDataSource;
import dev.aabstractt.balancer.factory.ServerFactory;
import dev.aabstractt.balancer.handler.JoinHandler;
import dev.aabstractt.balancer.handler.ReconnectHandler;
import dev.aabstractt.balancer.listener.PostTransferCompleteListener;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.event.defaults.PostTransferCompleteEvent;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import dev.waterdog.waterdogpe.plugin.Plugin;
import dev.waterdog.waterdogpe.utils.config.Configuration;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class HubBalancer extends Plugin {

    @Getter private static HubBalancer instance = null;

    private final static @NonNull Set<@NonNull String> locked = new HashSet<>();
    private final static @NonNull Map<@NonNull String, @NonNull String> serverLocked = Maps.newConcurrentMap();

    @Override
    public void onEnable() {
        instance = this;

        Configuration configuration = this.getConfig();

        RedisDataSource.getInstance().init(
                configuration.getString("redis.address"),
                configuration.getString("redis.password"),
                configuration.getString("redis.channel")
        );
        ServerFactory.getInstance().init();

        this.getProxy().setReconnectHandler(new ReconnectHandler());
        this.getProxy().setJoinHandler(new JoinHandler());

        this.getProxy().getEventManager().subscribe(PostTransferCompleteEvent.class, PostTransferCompleteListener::onPostTransferCompleteEvent);

        this.getProxy().getCommandMap().registerCommand(new HubCommand("hub"));
    }

    public static void lock(@NonNull ProxiedPlayer player) {
        if (isLocked(player)) return;

        locked.add(player.getXuid());

        ProxyServer.getInstance().getScheduler().scheduleDelayed(() -> HubBalancer.unlock(player), 20);
    }

    public static void updateLock(@NonNull ProxiedPlayer player, @NonNull String serverName) {
        if (!serverLocked.containsKey(player.getXuid())) return;

        serverLocked.put(player.getXuid(), serverName);
    }

    public static void unlock(@NonNull ProxiedPlayer player) {
        locked.remove(player.getXuid());
        serverLocked.remove(player.getXuid());
    }

    public static boolean isLocked(@NonNull ProxiedPlayer player) {
        return locked.contains(player.getXuid());
    }

    public static int getServerPlayersCount(@NonNull String serverName) {
        int count = 0;

        for (String targetServerName : serverLocked.values()) {
            if (!targetServerName.equals(serverName)) continue;

            count++;
        }

        return count;
    }
}