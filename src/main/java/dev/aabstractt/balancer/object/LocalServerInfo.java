package dev.aabstractt.balancer.object;

import dev.aabstractt.balancer.HubBalancer;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.annotation.Nullable;

@AllArgsConstructor @Data
public class LocalServerInfo {

    private final @NonNull String serverName;
    private int maxSlots;

    private boolean online = false;

    public @Nullable ServerInfo toWaterdogServer() {
        return ProxyServer.getInstance().getServerInfo(this.serverName);
    }

    public int getCurrentPlayers() {
        ServerInfo serverInfo = this.toWaterdogServer();

        return (serverInfo != null ? serverInfo.getPlayers().size() : 0) + HubBalancer.getServerPlayersCount(this.serverName);
    }
}