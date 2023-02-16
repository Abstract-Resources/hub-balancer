package dev.aabstractt.balancer.listener;

import dev.waterdog.waterdogpe.event.defaults.PostTransferCompleteEvent;
import dev.waterdog.waterdogpe.logger.Color;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

public final class PostTransferCompleteListener {

    public static void onPostTransferCompleteEvent(PostTransferCompleteEvent ev) {
        ProxiedPlayer player = ev.getPlayer();

        player.
        player.sendMessage(Color.AQUA + "Finding an optimal server for you...");
        player.sendMessage(Color.DARK_GRAY + Color.BOLD.toString() + "> " + Color.AQUA + "You are now connected on " + Color.DARK_AQUA + Color.BOLD + ev.getClient().getServerInfo().getServerName());
    }
}