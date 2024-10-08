package dev.aabstractt.balancer.handler;

import dev.aabstractt.balancer.HubBalancer;
import dev.aabstractt.balancer.factory.ServerFactory;
import dev.aabstractt.balancer.object.LocalServerInfo;
import dev.waterdog.waterdogpe.network.connection.handler.IJoinHandler;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

public final class JoinHandler implements IJoinHandler {

    @Override
    public ServerInfo determineServer(ProxiedPlayer proxiedPlayer) {
        HubBalancer.lock(proxiedPlayer);

        LocalServerInfo betterServerInfo = ServerFactory.getInstance().getBetterServer(null);
        if (betterServerInfo == null || !betterServerInfo.isOnline()) return null;

        return betterServerInfo.toWaterdogServer();
    }
}