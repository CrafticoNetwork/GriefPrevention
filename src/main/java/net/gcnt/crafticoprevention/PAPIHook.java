package net.gcnt.crafticoprevention;

import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.claims.Claim;
import me.ryanhamshire.GriefPrevention.data.DataStore;
import me.ryanhamshire.GriefPrevention.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PAPIHook extends PlaceholderExpansion implements Configurable
{
    private GriefPrevention plugin;

    public boolean canRegister()
    {
        return (Bukkit.getPluginManager().getPlugin(getRequiredPlugin()) != null);
    }

    public boolean register()
    {
        if (!canRegister())
            return false;
        this.plugin = (GriefPrevention) Bukkit.getPluginManager().getPlugin(getRequiredPlugin());
        if (this.plugin == null)
            return false;
        return super.register();
    }

    public String getAuthor()
    {
        return "pixar02";
    }

    public String getIdentifier()
    {
        return "griefprevention";
    }

    public String getRequiredPlugin()
    {
        return "GriefPrevention";
    }

    public String getVersion()
    {
        return "1.5.2";
    }

    public Map<String, Object> getDefaults()
    {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("formatting.thousands", "k");
        defaults.put("formatting.millions", "M");
        defaults.put("formatting.billions", "B");
        defaults.put("formatting.trillions", "T");
        defaults.put("formatting.quadrillions", "Q");
        defaults.put("color.enemy", "&4");
        defaults.put("color.trusted", "&a");
        defaults.put("color.neutral", "&7");
        defaults.put("translate.unclaimed", "Unclaimed");
        return defaults;
    }

    public String onRequest(OfflinePlayer p, String identifier)
    {
        if (!p.isOnline() || p == null)
            return "";
        Player player = p.getPlayer();
        DataStore DataS = this.plugin.dataStore;
        PlayerData pd = DataS.getPlayerData(player.getUniqueId());
        if (identifier.equals("claims"))
            return String.valueOf(pd.getClaims().size());
        if (identifier.equals("claims_formatted"))
            return fixMoney(pd.getClaims().size());
        if (identifier.equals("bonusclaims"))
            return String.valueOf(pd.getBonusClaimBlocks());
        if (identifier.equals("bonusclaims_formatted"))
            return fixMoney(pd.getBonusClaimBlocks());
        if (identifier.equals("accruedclaims"))
            return String.valueOf(pd.getAccruedClaimBlocks());
        if (identifier.equals("accruedclaims_formatted"))
            return fixMoney(pd.getAccruedClaimBlocks());
        if (identifier.equals("accruedclaims_limit"))
            return String.valueOf(pd.getAccruedClaimBlocksLimit());
        if (identifier.equals("remainingclaims"))
            return String.valueOf(pd.getRemainingClaimBlocks());
        if (identifier.equals("remainingclaims_formatted"))
            return fixMoney(pd.getRemainingClaimBlocks());
        if (identifier.equals("currentclaim_ownername"))
        {
            Claim claim = DataS.getClaimAt(player.getLocation(), true, null);
            if (claim == null)
                return getString("translate.unclaimed", "Unclaimed!");
            return String.valueOf(claim.getOwnerName());
        }
        if (identifier.equals("currentclaim_ownername_color"))
        {
            Claim claim = DataS.getClaimAt(player.getLocation(), true, null);
            if (claim == null)
                return ChatColor.translateAlternateColorCodes('&',
                        getString("color.neutral", "") +
                                getString("translate.unclaimed", "Unclaimed!"));
            if (claim.allowAccess(player) == null)
                return ChatColor.translateAlternateColorCodes('&',
                        getString("color.trusted", "") + String.valueOf(claim.getOwnerName()));
            return ChatColor.translateAlternateColorCodes('&',
                    getString("color.enemy", "") + String.valueOf(claim.getOwnerName()));
        }
        return null;
    }

    private String fixMoney(double d)
    {
        if (d < 1000.0D)
            return format(d);
        if (d < 1000000.0D)
            return format(d / 1000.0D) + getString("formatting.thousands", "k");
        if (d < 1.0E9D)
            return format(d / 1000000.0D) + getString("formatting.millions", "m");
        if (d < 1.0E12D)
            return format(d / 1.0E9D) + getString("formatting.billions", "b");
        if (d < 1.0E15D)
            return format(d / 1.0E12D) + getString("formatting.trillions", "t");
        if (d < 1.0E18D)
            return format(d / 1.0E15D) + getString("formatting.quadrillions", "q");
        return toLong(d);
    }

    private String toLong(double amt)
    {
        long send = (long) amt;
        return String.valueOf(send);
    }

    private String format(double d)
    {
        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(0);
        return format.format(d);
    }
}
