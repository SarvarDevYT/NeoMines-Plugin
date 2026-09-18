package me.neomines.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("deprecation")
public class ItemStackBuilder {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacySection();

    public static ItemStack buildItem(Material material, final String name, final String... lore) {
        return buildItem(material, 1, name, lore != null ? Arrays.asList(lore) : null);
    }

    public static ItemStack buildItem(Material material, final String name, final List<String> lore) {
        return buildItem(material, 1, name, lore);
    }

    public static ItemStack buildItem(Material material, int amount, final String name, final String... lore) {
        return buildItem(material, amount, name, lore != null ? Arrays.asList(lore) : null);
    }

    public static ItemStack buildItem(Material material, int amount, final String name, final List<String> lore) {
        final ItemStack item = new ItemStack(material, amount);
        final ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            if (name != null) {
                meta.displayName(SERIALIZER.deserialize(name));
            }
            if (lore != null) {
                List<Component> componentLore = new ArrayList<>();
                for (String line : lore) {
                    if (line != null) {
                        componentLore.add(SERIALIZER.deserialize(line));
                    }
                }
                meta.lore(componentLore);
            }
            item.setItemMeta(meta);
        }

        return item;
    }
}
