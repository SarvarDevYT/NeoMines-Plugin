package me.neomines.utils;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import me.clip.placeholderapi.PlaceholderAPI;
import me.neomines.NeoMines;
import me.neomines.gui.Menu;
import me.neomines.gui.PlayerMenuUtility;
import me.neomines.gui.menus.CompositionBlockMenu;
import me.neomines.gui.menus.CompositionMenu;
import me.neomines.gui.menus.ChangeBlockLootTableMenu;
import me.neomines.gui.menus.LootItemListMenu;
import me.neomines.gui.menus.LootItemMenu;
import me.neomines.mine.AbstractNeoMine;
import me.neomines.schedulers.MineManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    @SuppressWarnings("deprecation")
    public static String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    @SuppressWarnings("deprecation")
    public static String setPlaceholders(String input, AbstractNeoMine mine) {
        if (input == null) return "";

        Map<String, String> replacements = new HashMap<>();
        String prefixClean = NeoMines.PREFIX != null ? NeoMines.PREFIX.trim() : "";
        replacements.put("nm", prefixClean);
        replacements.put("neomines", prefixClean);
        replacements.put("cm", prefixClean);
        replacements.put("mine", mine.getName());
        replacements.put("seconds", String.valueOf(mine.getCountdown()));
        replacements.put("formattedseconds", secondsToTimeFormat(mine.getCountdown()));
        replacements.put("formattedtime", mine.getFormattedTimeString());
        replacements.put("time", mine.getCountdown() / 60 == 1 ?
                NeoMines.getInstance().getLangString("Time.Second") :
                NeoMines.getInstance().getLangString("Time.Seconds"));
        replacements.put("resetpercentage", String.valueOf(mine.getResetPercentage()));
        replacements.put("remainingblocksper", String.valueOf(mine.getRemainingBlocksPer()));

        String translatedText = input;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            translatedText = translatedText.replace("%" + entry.getKey() + "%", entry.getValue());
        }

        if (NeoMines.getInstance().placeholderAPI) {
            try {
                translatedText = PlaceholderAPI.setPlaceholders(null, translatedText);
            } catch (Throwable ignored) {}
        }

        return ChatColor.translateAlternateColorCodes('&', translatedText);
    }

    public static String secondsToTimeFormat(int seconds) {
        if (seconds >= 60) {
            if (seconds >= 3600) {
                return String.format("%d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
            }
            return String.format("%d:%02d", seconds / 60, seconds % 60);
        }
        return String.valueOf(seconds);
    }

    @SuppressWarnings("deprecation")
    public static void sendActionBar(Player player, String message) {
        try {
            player.sendActionBar(LegacyComponentSerializer.legacyAmpersand().deserialize(message.replace('§', '&')));
            return;
        } catch (Throwable ignored) {}

        try {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        } catch (Throwable ignored) {}
    }

    @SuppressWarnings("deprecation")
    public static Enchantment getEfficiencyEnchantment() {
        try {
            Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft("efficiency"));
            if (ench != null) return ench;
        } catch (Throwable ignored) {}

        try {
            return Enchantment.EFFICIENCY;
        } catch (Throwable ignored) {}

        return null;
    }

    @SuppressWarnings("deprecation")
    public static Enchantment getFortuneEnchantment() {
        try {
            Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft("fortune"));
            if (ench != null) return ench;
        } catch (Throwable ignored) {}

        try {
            return Enchantment.FORTUNE;
        } catch (Throwable ignored) {}

        return null;
    }

    @Nullable
    public static String regionToStr(@Nonnull Region region) {
        com.sk89q.worldedit.world.World world = region.getWorld();
        if (world == null) return null;
        return world.getName() + ";"
                + region.getMinimumPoint().x() + ";"
                + region.getMinimumPoint().y() + ";"
                + region.getMinimumPoint().z() + ";"
                + region.getMaximumPoint().x() + ";"
                + region.getMaximumPoint().y() + ";"
                + region.getMaximumPoint().z() + ";";
    }

    @Nonnull
    public static String[] regionToArray(@Nonnull Region region) {
        String[] strings = new String[7];
        com.sk89q.worldedit.world.World world = region.getWorld();
        strings[0] = world != null ? world.getName() : "";
        BlockVector3 minP = region.getMinimumPoint();
        BlockVector3 maxP = region.getMaximumPoint();
        strings[1] = String.valueOf(minP.x());
        strings[2] = String.valueOf(minP.y());
        strings[3] = String.valueOf(minP.z());
        strings[4] = String.valueOf(maxP.x());
        strings[5] = String.valueOf(maxP.y());
        strings[6] = String.valueOf(maxP.z());
        return strings;
    }

    @Nullable
    public static Region strToRegion(@Nonnull String str) {
        String[] args = str.split(";");
        if (args.length < 7) return null;
        if (Bukkit.getWorld(args[0]) == null) return null;

        return new CuboidRegion(BukkitAdapter.adapt(Bukkit.getWorld(args[0])),
                BlockVector3.at(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])),
                BlockVector3.at(Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6])));
    }

    @Nullable
    public static Region getWorldEditSelectionOfPlayer(Player player) {
        try {
            RegionSelector selector = WorldEdit.getInstance().getSessionManager()
                    .get(BukkitAdapter.adapt(player))
                    .getRegionSelector(BukkitAdapter.adapt(player.getWorld()));
            if (selector != null && selector.isDefined()) {
                return selector.getRegion();
            }
        } catch (IncompleteRegionException ex) {
            player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Incomplete-Region"));
            return null;
        } catch (Throwable ignored) {}

        player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Incomplete-Region"));
        return null;
    }

    public static void updateMenus() {
        for (Map.Entry<Player, PlayerMenuUtility> entry : NeoMines.getPlayerMenuUtilityMap().entrySet()) {
            Player player = entry.getKey();
            PlayerMenuUtility playerMenuUtility = entry.getValue();

            if (playerMenuUtility.getMine() != null && !MineManager.getInstance().getMines().contains(playerMenuUtility.getMine())) {
                player.closeInventory();
                NeoMines.removePlayerMenuUtility(player);
                player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.GUI.Mine-Deleted"));
                continue;
            }

            if (playerMenuUtility.getMenu() instanceof CompositionBlockMenu
                    || playerMenuUtility.getMenu() instanceof ChangeBlockLootTableMenu
                    || playerMenuUtility.getMenu() instanceof LootItemListMenu
                    || playerMenuUtility.getMenu() instanceof LootItemMenu) {

                if (playerMenuUtility.getMine() != null && !playerMenuUtility.getMine().containsBlock(playerMenuUtility.getBlock())) {
                    playerMenuUtility.setBlock(null);
                    new CompositionMenu(playerMenuUtility);
                    player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.GUI.Block-Deleted"));
                }
            }

            if (playerMenuUtility.getMenu() instanceof LootItemMenu) {
                if (playerMenuUtility.getBlock() != null && !playerMenuUtility.getBlock().getLootTable().contains(playerMenuUtility.getItem())) {
                    playerMenuUtility.setItem(null);
                    new LootItemListMenu(playerMenuUtility);
                    player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.GUI.Loot-Table-Change"));
                }
            }

            if (player.getOpenInventory().getTopInventory() != null) {
                InventoryHolder inventoryHolder = player.getOpenInventory().getTopInventory().getHolder();
                if (inventoryHolder instanceof Menu && playerMenuUtility.getMenu() != null) {
                    playerMenuUtility.getMenu().open();
                }
            }
        }
    }
}
