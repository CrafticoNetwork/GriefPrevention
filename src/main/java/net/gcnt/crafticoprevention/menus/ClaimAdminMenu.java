package net.gcnt.crafticoprevention.menus;

import me.ryanhamshire.GriefPrevention.claims.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.data.PlayerData;
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

public class ClaimAdminMenu implements Listener
{

    private static final ItemStack[] FILL_ITEMS;
    public static final String MANAGE_CLAIM_MENU_TITLE = "Manage Claim";
    public static final HashMap<UUID, Claim> claimManaging = new HashMap<>();

    static {
        ItemStack blue = createItem(Material.BLUE_STAINED_GLASS_PANE, 1, "§r");
        ItemStack white = createItem(Material.WHITE_STAINED_GLASS_PANE, 1, "§r");

        FILL_ITEMS = new ItemStack[]{blue,blue,blue,blue,blue,blue,blue,blue,blue,
                blue, white, null, white, null, white, null, white, blue,
                blue,blue,blue,blue,blue,blue,blue,blue,blue};

    }

    public void openMenu(Player p, Claim claim) {

        ArrayList<String> builders = new ArrayList<>();
        ArrayList<String> accessors = new ArrayList<>();
        ArrayList<String> containers = new ArrayList<>();
        ArrayList<String> managers = new ArrayList<>();
        claim.getPermissions(builders, containers, accessors, managers);

        Inventory menu = Bukkit.createInventory(null, 27, MANAGE_CLAIM_MENU_TITLE);
        menu.setContents(FILL_ITEMS);

        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(p.getUniqueId());

        final int totalBlocks = playerData.getAccruedClaimBlocks() + playerData.getBonusClaimBlocks() + GriefPrevention.instance.dataStore.getGroupBonusBlocks(p.getUniqueId());

        ItemStack item = createItem(Material.PLAYER_HEAD,1,"§eClaim information §8(#" + claim.getID() + ")",
                "", "§6World: §7" + claim.getLesserBoundaryCorner().getWorld().getName(),
                "§6Boundaries: §7x" + claim.getLesserBoundaryCorner().getBlockX() + ", z" + claim.getLesserBoundaryCorner().getBlockZ() + " &bto &7x" + claim.getGreaterBoundaryCorner().getBlockX() + ", z" + claim.getGreaterBoundaryCorner().getBlockZ(),
                "§6Spent blocks: §7" + claim.getArea(),
                "§6Blocks left: §7" + playerData.getRemainingClaimBlocks() + "§8/§7" + totalBlocks);
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()));
        item.setItemMeta(skullMeta);

        menu.setItem(11, item);

        final int trustedTotal = builders.size() + accessors.size() + containers.size() + managers.size();

        ItemStack trusted = createItem(Material.COMPARATOR, 1, "§eTrusted players §8(" + trustedTotal + " total)");
        ItemMeta tmeta = trusted.getItemMeta();
        List<String> lore = new ArrayList<>();

        lore.add(" ");

        if (trustedTotal == 0) {
            lore.add("§cThis claim currently does not");
            lore.add("§chave any §7trusted players");
        }
        else {
            if (!managers.isEmpty()) {
                lore.add("§6Managers:");
                int added = 0;
                for (String uid : managers) {
                    if (added < 1) {
                        lore.add("§8- §7" + Bukkit.getOfflinePlayer(UUID.fromString(uid)).getName());
                    }
                    else {
                        String add = managers.size() > 2 ? (" §9(" + (managers.size()-2) + " more)") : "";
                        lore.add("§8- §7" + Bukkit.getOfflinePlayer(UUID.fromString(uid)).getName() + add);
                        break;
                    }
                    added++;
                }
            }
            if (!builders.isEmpty()) {
                lore.add("§6Builders:");
                int added = 0;
                for (String uid : builders) {
                    if (added < 1) {
                        lore.add("§8- §7" + Bukkit.getOfflinePlayer(UUID.fromString(uid)).getName());
                    }
                    else {
                        String add = builders.size() > 2 ? (" §9(" + (builders.size()-2) + " more)") : "";
                        lore.add("§8- §7" + Bukkit.getOfflinePlayer(UUID.fromString(uid)).getName() + add);
                        break;
                    }
                    added++;
                }
            }
            if (!containers.isEmpty()) {
                lore.add("§6Container accessors:");
                int added = 0;
                for (String uid : containers) {
                    if (added < 1) {
                        lore.add("§8- §7" + Bukkit.getOfflinePlayer(UUID.fromString(uid)).getName());
                    }
                    else {
                        String add = containers.size() > 2 ? (" §9(" + (containers.size()-2) + " more)") : "";
                        lore.add("§8- §7" + Bukkit.getOfflinePlayer(UUID.fromString(uid)).getName() + add);
                        break;
                    }
                    added++;
                }
            }
            if (!accessors.isEmpty()) {
                lore.add("§6Accessors:");
                int added = 0;
                for (String uid : accessors) {
                    if (added < 1) {
                        lore.add("§8- §7" + Bukkit.getOfflinePlayer(UUID.fromString(uid)).getName());
                    }
                    else {
                        String add = accessors.size() > 2 ? (" §9(" + (accessors.size()-2) + " more)") : "";
                        lore.add("§8- §7" + Bukkit.getOfflinePlayer(UUID.fromString(uid)).getName() + add);
                        break;
                    }
                    added++;
                }
            }
        }

        lore.add("");
        lore.add("§6Click §eto manage trusted players.");

        tmeta.setLore(lore);
        trusted.setItemMeta(tmeta);
        menu.setItem(13, trusted);

        ItemStack delete = createItem(Material.BARRIER, 1, "§c§lDELETE CLAIM",
                "",
                "§4Irreversibly delete §7this claim.",
                "§7This will give all currently",
                "§7non-trusted players access to your",
                "§7land and allows them to claim it.",
                "",
                "§c§oThis action §ncannot§r§c§o be undone!",
                "§6Click §eto delete this claim.");
        menu.setItem(15, delete);

        p.openInventory(menu);
        claimManaging.put(p.getUniqueId(), claim);

    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!ManageTrustsMenu.playerChats.contains(e.getPlayer().getUniqueId()))
        claimManaging.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (e.getClickedInventory() == null) return;

        if (!e.getView().getTitle().equals(MANAGE_CLAIM_MENU_TITLE)) return;
        e.setCancelled(true);
        if (!e.getClickedInventory().equals(e.getView().getTopInventory())) {
            return;
        }
        if (!ClaimAdminMenu.claimManaging.containsKey(e.getWhoClicked().getUniqueId())) {
            return;
        }
        Claim claim = claimManaging.get(e.getWhoClicked().getUniqueId());

        if (e.getSlot() == 13) {
            // player clicked the manager thing.
            GriefPrevention.instance.getSelectTrustCategoryMenu().openMenu((Player) e.getWhoClicked(), claim );
        }
        else if (e.getSlot() == 15) {
            // player clicked the delete button.
            GriefPrevention.instance.getDeleteClaimMenu().openMenu((Player) e.getWhoClicked(), claim);
        }

    }

    public static ItemStack createItem(Material material, int amount, String displayName, String... lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

        List<String> loreList = new ArrayList<>();
        for (String s : lore) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.setLore(loreList);
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);
        return item;
    }

}
