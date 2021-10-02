package me.ryanhamshire.GriefPrevention.tasks;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public record PendingItemProtection(Location location, UUID owner, long expirationTimestamp, ItemStack itemStack)
{
}
