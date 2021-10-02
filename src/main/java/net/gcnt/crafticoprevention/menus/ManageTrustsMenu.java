package net.gcnt.crafticoprevention.menus;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.events.TrustChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static net.gcnt.crafticoprevention.menus.ClaimAdminMenu.createItem;

public class ManageTrustsMenu implements Listener
{

    private static final ItemStack[] FILL_ITEMS;
    public static final HashMap<ClaimPermission, String> TITLES;
    public static final HashMap<UUID, ClaimPermission> playerPerm = new HashMap<>();
    public static final HashMap<UUID, ArrayList<String>> playerTrusts = new HashMap<>();
    public static final List<UUID> playerChats = new ArrayList<>();
    public static final String TRUST_ADD_MESSAGE = "§eType the name of the player you want to trust in the chat. Type '§7CANCEL§e' to cancel.";

    static
    {
        ItemStack bar = createItem(Material.BARRIER, 1, "§r");
        ItemStack blue = createItem(Material.WHITE_STAINED_GLASS_PANE, 1, "§r");

        FILL_ITEMS = new ItemStack[]{bar, bar, bar, bar, bar, bar, bar, bar, bar,
                bar, bar, bar, bar, bar, bar, bar, bar, bar,
                blue, blue, blue, blue, null, blue, blue, blue, null};

        TITLES = new HashMap<>();
        TITLES.put(ClaimPermission.Build, "Manage Trusted Builders");
        TITLES.put(ClaimPermission.Inventory, "Manage Container Accessors");
        TITLES.put(ClaimPermission.Manage, "Manage Administrators");
        TITLES.put(ClaimPermission.Access, "Manage Accessors");
    }

    public void openMenu(Player p, Claim claim, ClaimPermission category)
    {
        ArrayList<String> builders = new ArrayList<>();
        ArrayList<String> accessors = new ArrayList<>();
        ArrayList<String> containers = new ArrayList<>();
        ArrayList<String> managers = new ArrayList<>();
        claim.getPermissions(builders, containers, accessors, managers);

        String title = TITLES.get(category);
        ArrayList<String> players;
        switch (category)
        {
            case Build -> players = builders;
            case Access -> players = accessors;
            case Inventory -> players = containers;
            case Manage -> players = managers;
            default -> players = new ArrayList<>();
        }

        Inventory menu = Bukkit.createInventory(null, 27, title);
        menu.setContents(FILL_ITEMS);

        for (int i = 0; i < players.size() && i < 18; i++)
        {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(players.get(i)));

            ItemStack item = createItem(Material.PLAYER_HEAD, 1, "§e" + player.getName(),
                    "", "§6Shift Click §eto remove this player.");
            SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
            skullMeta.setOwningPlayer(player);
            item.setItemMeta(skullMeta);

            menu.setItem(i, item);
        }

        ItemStack delete = createItem(Material.BOOK, 1, "§bGo Back",
                "",
                "§6Click §eto go back to the main menu.");
        menu.setItem(22, delete);

        ItemStack add = createItem(Material.LIME_WOOL, 1, "§aAdd a new player",
                "",
                "§6Click §eto trust a new player.");
        menu.setItem(26, add);

        p.openInventory(menu);
        ClaimAdminMenu.claimManaging.put(p.getUniqueId(), claim);
        playerChats.remove(p.getUniqueId());
        playerTrusts.put(p.getUniqueId(), players);
        playerPerm.put(p.getUniqueId(), category);

    }

    @EventHandler
    public void onClose(InventoryCloseEvent e)
    {
        if (!playerChats.contains(e.getPlayer().getUniqueId()))
        {
            playerTrusts.remove(e.getPlayer().getUniqueId());
            playerPerm.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e)
    {
        if (e.getCurrentItem() == null) return;
        if (e.getClickedInventory() == null) return;
        if (!playerPerm.containsKey(e.getWhoClicked().getUniqueId())) return;


        if (!e.getView().getTitle().equals(TITLES.get(playerPerm.get(e.getWhoClicked().getUniqueId())))) return;
        e.setCancelled(true);
        if (!e.getClickedInventory().equals(e.getView().getTopInventory())) return;
        Player player = (Player) e.getWhoClicked();
        if (!ClaimAdminMenu.claimManaging.containsKey(player.getUniqueId())) return;

        Claim claim = ClaimAdminMenu.claimManaging.get(player.getUniqueId());
        ClaimPermission perm = playerPerm.get(player.getUniqueId());

        if (e.getSlot() < 18 && e.getCurrentItem().getType() != Material.BARRIER && e.getClick().isShiftClick())
        {
            int slot = e.getSlot();
            List<String> uids = playerTrusts.get(player.getUniqueId());
            if (slot < uids.size())
            {
                String toRemove = uids.get(slot);

                TrustChangedEvent event = new TrustChangedEvent(player, claim, null, false, toRemove);
                Bukkit.getPluginManager().callEvent(event);

                if (!event.isCancelled())
                {
                    claim.dropPermission(toRemove);
                    if (perm == ClaimPermission.Manage)
                    {
                        claim.managers.remove(toRemove);
                    }
                    player.sendMessage("§aSuccessfully untrusted " + Bukkit.getOfflinePlayer(UUID.fromString(toRemove)).getName() + " from your claim.");
                    GriefPrevention.instance.dataStore.saveClaim(claim);
                }
            }
            openMenu(player, claim, perm);
        }
        else if (e.getSlot() == 22)
        {
            GriefPrevention.instance.getClaimAdminMenu().openMenu(player, claim);
        }
        else if (e.getSlot() == 26)
        {
            playerChats.add(player.getUniqueId());
            player.sendMessage(TRUST_ADD_MESSAGE);
            player.closeInventory();
        }

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e)
    {
        playerChats.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e)
    {
        if (!playerChats.contains(e.getPlayer().getUniqueId()))
        {
            e.getPlayer().sendMessage("player chats not containging");
            return;
        }
        if (!playerPerm.containsKey(e.getPlayer().getUniqueId()))
        {
            e.getPlayer().sendMessage("player perm is null");
            return;
        }
        if (!ClaimAdminMenu.claimManaging.containsKey(e.getPlayer().getUniqueId()))
        {
            e.getPlayer().sendMessage("player claim is null");
            return;
        }

        e.setCancelled(true);

        Bukkit.getScheduler().runTask(GriefPrevention.instance, () -> {

            Claim claim = ClaimAdminMenu.claimManaging.get(e.getPlayer().getUniqueId());
            ClaimPermission selectedPerm = playerPerm.get(e.getPlayer().getUniqueId());
            if (!e.getMessage().equalsIgnoreCase("cancel"))
            {

                OfflinePlayer op = GriefPrevention.instance.resolvePlayerByName(e.getMessage());
                if (op == null)
                {
                    e.getPlayer().sendMessage("§cWe couldn't find a player with that name. Please try again, or type '§7CANCEL§c' to cancel.");
                    return;
                }
                else
                {
                    ClaimPermission perm = claim.getPermission(op.getUniqueId().toString());
                    String id = op.getUniqueId().toString();
                    if (perm == null || perm != selectedPerm)
                    {
                        TrustChangedEvent event = new TrustChangedEvent(e.getPlayer(), claim, selectedPerm, true, id);
                        Bukkit.getPluginManager().callEvent(event);

                        if (!event.isCancelled())
                        {
                            claim.setPermission(id, selectedPerm);
                            e.getPlayer().sendMessage("§aSuccessfully added " + op.getName() + " to the trusted players for the category " + selectedPerm.name() + ".");
                        }
                        GriefPrevention.instance.dataStore.saveClaim(claim);
                    }
                    else
                    {
                        e.getPlayer().sendMessage("§cThat player is already trusted in this category. Please try again, or type '§7CANCEL§c' to cancel.");
                        return;
                    }
                }
            }
            else {
                e.getPlayer().sendMessage("§cTrusting a new player has been cancelled.");
            }

            playerChats.remove(e.getPlayer().getUniqueId());
            openMenu(e.getPlayer(), claim, playerPerm.get(e.getPlayer().getUniqueId()));

        });
    }

}
