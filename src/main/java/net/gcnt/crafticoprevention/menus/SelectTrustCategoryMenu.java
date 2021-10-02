package net.gcnt.crafticoprevention.menus;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SelectTrustCategoryMenu implements Listener
{

    private static final ItemStack[] FILL_ITEMS;
    public static final String MANAGE_CLAIM_MENU_TITLE = "Select Trusted Category";
    public static final HashMap<UUID, Claim> claimManaging = new HashMap<>();

    static
    {
        ItemStack blue = createItem(Material.BLUE_STAINED_GLASS_PANE, 1, "§r");
        ItemStack white = createItem(Material.WHITE_STAINED_GLASS_PANE, 1, "§r");

        FILL_ITEMS = new ItemStack[]{blue, blue, blue, blue, blue, blue, blue, blue, blue,
                blue, null, white, null, white, null, white, null, blue,
                blue, blue, blue, blue, null, blue, blue, blue, blue};

    }

    public void openMenu(Player p, Claim claim)
    {

        ArrayList<String> builders = new ArrayList<>();
        ArrayList<String> accessors = new ArrayList<>();
        ArrayList<String> containers = new ArrayList<>();
        ArrayList<String> managers = new ArrayList<>();
        claim.getPermissions(builders, containers, accessors, managers);

        Inventory menu = Bukkit.createInventory(null, 27, MANAGE_CLAIM_MENU_TITLE);
        menu.setContents(FILL_ITEMS);

        ItemStack buildItem = createItem(Material.GRASS_BLOCK, 1, "§eManage Builders");
        ItemMeta buildMeta = buildItem.getItemMeta();
        List<String> buildLore = new ArrayList<>();
        if (!builders.isEmpty())
        {
            for (String uid : builders)
            {
                buildLore.add("§8- §7" + Bukkit.getOfflinePlayer(UUID.fromString(uid)).getName());
            }
        }
        else
        {
            buildLore.add("§cThis category currently does not");
            buildLore.add("§chave any §7trusted players");
        }
        buildLore.add("");
        buildLore.add("§6Click §eto manage trusted players.");
        buildMeta.setLore(buildLore);
        buildItem.setItemMeta(buildMeta);

        menu.setItem(10, buildItem);

        ItemStack invItem = createItem(Material.CHEST, 1, "§eManage Container Accessors");
        ItemMeta invMeta = invItem.getItemMeta();
        List<String> invLore = new ArrayList<>();
        if (!containers.isEmpty())
        {
            for (String uid : containers)
            {
                invLore.add("§8- §7" + Bukkit.getOfflinePlayer(UUID.fromString(uid)).getName());
            }
        }
        else
        {
            invLore.add("§cThis category currently does not");
            invLore.add("§chave any §7trusted players");
        }
        invLore.add("");
        invLore.add("§6Click §eto manage trusted players.");
        invMeta.setLore(invLore);
        invItem.setItemMeta(invMeta);

        menu.setItem(12, invItem);

        ItemStack managerItem = createItem(Material.TNT, 1, "§eManage Administrators");
        ItemMeta managerMeta = managerItem.getItemMeta();
        List<String> managerLore = new ArrayList<>();
        if (!managers.isEmpty())
        {
            for (String uid : managers)
            {
                managerLore.add("§8- §7" + Bukkit.getOfflinePlayer(UUID.fromString(uid)).getName());
            }
        }
        else
        {
            managerLore.add("§cThis category currently does not");
            managerLore.add("§chave any §7trusted players");
        }
        managerLore.add("");
        managerLore.add("§6Click §eto manage trusted players.");
        managerMeta.setLore(managerLore);
        managerItem.setItemMeta(managerMeta);

        menu.setItem(14, managerItem);

        ItemStack accessItem = createItem(Material.SHIELD, 1, "§eManage Accessors");
        ItemMeta accessMeta = accessItem.getItemMeta();
        List<String> accessLore = new ArrayList<>();
        if (!accessors.isEmpty())
        {
            for (String uid : accessors)
            {
                accessLore.add("§8- §7" + Bukkit.getOfflinePlayer(UUID.fromString(uid)).getName());
            }
        }
        else
        {
            accessLore.add("§cThis category currently does not");
            accessLore.add("§chave any §7trusted players");
        }
        accessLore.add("");
        accessLore.add("§6Click §eto manage trusted players.");
        accessMeta.setLore(accessLore);
        accessItem.setItemMeta(accessMeta);

        menu.setItem(16, accessItem);

        ItemStack delete = createItem(Material.BOOK, 1, "§bGo Back",
                "",
                "§6Click §eto go back to the main menu.");
        menu.setItem(22, delete);

        p.openInventory(menu);
        claimManaging.put(p.getUniqueId(), claim);

    }

    @EventHandler
    public void onClick(InventoryClickEvent e)
    {
        if (e.getCurrentItem() == null) return;
        if (e.getClickedInventory() == null) return;

        if (!e.getView().getTitle().equals(MANAGE_CLAIM_MENU_TITLE)) return;
        e.setCancelled(true);
        if (!e.getClickedInventory().equals(e.getView().getTopInventory()))
        {
            return;
        }
        if (!SelectTrustCategoryMenu.claimManaging.containsKey(e.getWhoClicked().getUniqueId()))
        {
            return;
        }

        if (e.getSlot() == 10)
        {
            GriefPrevention.instance.getManageTrustsMenu().openMenu((Player) e.getWhoClicked(),claimManaging.get(e.getWhoClicked().getUniqueId()), ClaimPermission.Build);
            // player clicked the manager thing.
        }
        else if (e.getSlot() == 12) {
            GriefPrevention.instance.getManageTrustsMenu().openMenu((Player) e.getWhoClicked(),claimManaging.get(e.getWhoClicked().getUniqueId()), ClaimPermission.Inventory);
        }
        else if (e.getSlot() == 14) {
            GriefPrevention.instance.getManageTrustsMenu().openMenu((Player) e.getWhoClicked(),claimManaging.get(e.getWhoClicked().getUniqueId()), ClaimPermission.Manage);
        }
        else if (e.getSlot() == 16) {
            GriefPrevention.instance.getManageTrustsMenu().openMenu((Player) e.getWhoClicked(),claimManaging.get(e.getWhoClicked().getUniqueId()), ClaimPermission.Access);
        }
        else if (e.getSlot() == 22)
        {
            GriefPrevention.instance.getClaimAdminMenu().openMenu((Player) e.getWhoClicked(), claimManaging.get(e.getWhoClicked().getUniqueId()));
        }

    }

    public static ItemStack createItem(Material material, int amount, String displayName, String... lore)
    {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

        List<String> loreList = new ArrayList<>();
        for (String s : lore)
        {
            loreList.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.setLore(loreList);
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);
        return item;
    }

}
