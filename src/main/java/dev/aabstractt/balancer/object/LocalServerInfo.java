package dev.aabstractt.balancer.object;

import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.annotation.Nullable;

@AllArgsConstructor @Data
public class LocalServerInfo {

    private final @NonNull String serverName;
    private int maxSlots;

    private boolean online;

    public @Nullable ServerInfo toWaterdogServer() {
        return ProxyServer.getInstance().getServerInfo(this.serverName);
    }

    public int getCurrentPlayers() {
        ServerInfo serverInfo = this.toWaterdogServer();
        if (serverInfo == null) return 0;

        int currentPlayers = serverInfo.getPlayers().size();

        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers().values()) {
            if (!proxiedPlayer.isConnected()) continue;

            ServerInfo targetServerInfo = proxiedPlayer.getConnectingServer();
            if (targetServerInfo == null) continue;

            currentPlayers++;
        }

        return currentPlayers;
    }
}