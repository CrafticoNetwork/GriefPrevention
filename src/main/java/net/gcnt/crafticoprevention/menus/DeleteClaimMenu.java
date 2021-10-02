package net.gcnt.crafticoprevention.menus;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.Messages;
import me.ryanhamshire.GriefPrevention.PlayerData;
import me.ryanhamshire.GriefPrevention.TextMode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static net.gcnt.crafticoprevention.menus.ClaimAdminMenu.createItem;

public class DeleteClaimMenu implements Listener
{

    private static final ItemStack[] FILL_ITEMS;
    public static final String DELETE_CLAIM_MENU_TITLE = "Delete Claim";

    static {
        ItemStack blue = createItem(Material.BLUE_STAINED_GLASS_PANE, 1, "§r");
        ItemStack white = createItem(Material.WHITE_STAINED_GLASS_PANE, 1, "§r");

        FILL_ITEMS = new ItemStack[]{blue,blue,blue,blue,blue,blue,blue,blue,blue,
                blue, white, null, white, white, white, null, white, blue,
                blue,blue,blue,blue,blue,blue,blue,blue,blue};

    }

    public void openMenu(Player p, Claim claim) {
        ArrayList<String> builders = new ArrayList<>();
        ArrayList<String> accessors = new ArrayList<>();
        ArrayList<String> containers = new ArrayList<>();
        ArrayList<String> managers = new ArrayList<>();
        claim.getPermissions(builders, containers, accessors, managers);

        Inventory menu = Bukkit.createInventory(null, 27, DELETE_CLAIM_MENU_TITLE);
        menu.setContents(FILL_ITEMS);

        ItemStack delete = createItem(Material.LIME_WOOL, 1, "§a§lCONFIRM",
                "",
                "§4Irreversibly delete §7this claim.",
                "§7This will give all currently",
                "§7non-trusted players access to your",
                "§7land and allows them to claim it.",
                "",
                "§c§oThis action §ncannot§r§c§o be undone!",
                "§6Click §eto delete this claim.");
        menu.setItem(11, delete);

        ItemStack cancel = createItem(Material.RED_WOOL, 1, "§c§lCANCEL",
                "",
                "§cCancel §7the claim removal and",
                "§7head back to the main menu.",
                "",
                "§6Click §eto cancel the removal.");
        menu.setItem(15, cancel);

        p.openInventory(menu);
        ClaimAdminMenu.claimManaging.put(p.getUniqueId(), claim);

    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (e.getClickedInventory() == null) return;

        if (!e.getView().getTitle().equals(DELETE_CLAIM_MENU_TITLE)) return;
        e.setCancelled(true);
        if (!e.getClickedInventory().equals(e.getView().getTopInventory())) return;
        Player player = (Player) e.getWhoClicked();
        if (!ClaimAdminMenu.claimManaging.containsKey(player.getUniqueId())) return;

        Claim claim = ClaimAdminMenu.claimManaging.get(player.getUniqueId());

        if (e.getSlot() == 11) {
            // player confirmed the removal.
            player.closeInventory();
            player.sendTitle("§c§lCLAIM DELETED", "§7You successfully deleted this claim!", 10, 50, 10);

            PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());

            claim.removeSurfaceFluids(null);
            GriefPrevention.instance.dataStore.deleteClaim(claim, true, false);

            //if in a creative mode world, restore the claim area
            if (GriefPrevention.instance.creativeRulesApply(claim.getLesserBoundaryCorner()))
            {
                GriefPrevention.AddLogEntry(player.getName() + " abandoned a claim @ " + GriefPrevention.getfriendlyLocationString(claim.getLesserBoundaryCorner()));
                GriefPrevention.sendMessage(player, TextMode.Warn, Messages.UnclaimCleanupWarning);
                GriefPrevention.instance.restoreClaim(claim, 20L * 60 * 2);
            }

            //adjust claim blocks when abandoning a top level claim
            if (GriefPrevention.instance.config_claims_abandonReturnRatio != 1.0D && claim.parent == null && claim.ownerID.equals(playerData.playerID))
            {
                playerData.setAccruedClaimBlocks(playerData.getAccruedClaimBlocks() - (int) Math.ceil((claim.getArea() * (1 - GriefPrevention.instance.config_claims_abandonReturnRatio))));
            }
        }
        else if (e.getSlot() == 15) {
            // player clicked the delete button.
            GriefPrevention.instance.getClaimAdminMenu().openMenu(player, claim);
        }

    }

}
