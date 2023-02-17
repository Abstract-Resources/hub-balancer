package dev.aabstractt.balancer.command;

import dev.aabstractt.balancer.HubBalancer;
import dev.aabstractt.balancer.factory.ServerFactory;
import dev.aabstractt.balancer.object.LocalServerInfo;
import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.logger.Color;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import dev.waterdog.waterdogpe.utils.types.TranslationContainer;

public final class HubCommand extends Command {

    public HubCommand(String name) {
        super(name);
    }

    @Override
    public boolean onExecute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(Color.RED + "Run this command in-game");

            return true;
        }

        if (HubBalancer.isLocked((ProxiedPlayer) sender)) {
            sender.sendMessage(Color.RED + "Please wait a second!");

            return true;
        }

        HubBalancer.lock((ProxiedPlayer) sender);

        LocalServerInfo betterServerInfo = ServerFactory.getInstance().getBetterServer(((ProxiedPlayer) sender).getServerInfo().getServerName());
        if (betterServerInfo == null || !betterServerInfo.isOnline()) {
            sender.sendMessage(new TranslationContainer("waterdog.downstream.transfer.failed", "Hub", ""));

            return true;
        }

        ((ProxiedPlayer) sender).connect(betterServerInfo.toWaterdogServer());

        return true;
    }
}