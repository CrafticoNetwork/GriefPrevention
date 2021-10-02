package net.gcnt.crafticoprevention.commands;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.gcnt.crafticoprevention.menus.ClaimAdminMenu;
import org.bukkit.Sound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] args)
    {

        if (!(sender instanceof Player))
        {
            sender.sendMessage("§cYou must be a player to perform this.");
            return true;
        }

        Player player = (Player) sender;

        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), true, true, null);

        if (claim == null)
        {
            player.sendTitle("§c§lNOT CLAIMED", "§7This land is not claimed", 10, 50, 10);
            player.sendMessage("§cYou can't manage this land because it has not been §7claimed§c by anyone.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            return true;
        }

        if (!claim.getOwnerID().equals(player.getUniqueId()))
        {
            player.sendTitle("§c§lLAND NOT YOURS!", "§7This piece of land does not belong to you", 10, 50, 10);
            player.sendMessage("§7You can't manage this land because it does not belong to you.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            return true;
        }

        new ClaimAdminMenu().openMenu(player, claim);

        return true;

    }
}
