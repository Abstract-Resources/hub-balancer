package dev.aabstractt.balancer.handler;

import dev.aabstractt.balancer.HubBalancer;
import dev.aabstractt.balancer.factory.ServerFactory;
import dev.aabstractt.balancer.object.LocalServerInfo;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import dev.waterdog.waterdogpe.utils.types.IReconnectHandler;

public final class ReconnectHandler implements IReconnectHandler {

    @Override
    public ServerInfo getFallbackServer(ProxiedPlayer proxiedPlayer, ServerInfo serverInfo, String s) {
        proxiedPlayer.sendMessage(s);

        if (HubBalancer.isLocked(proxiedPlayer)) return null;

        HubBalancer.lock(proxiedPlayer);

        LocalServerInfo betterServerInfo = ServerFactory.getInstance().getBetterServer(serverInfo.getServerName());
        if (betterServerInfo == null || !betterServerInfo.isOnline()) return null;

        HubBalancer.updateLock(proxiedPlayer, betterServerInfo.getServerName());

        return betterServerInfo.toWaterdogServer();
    }
}