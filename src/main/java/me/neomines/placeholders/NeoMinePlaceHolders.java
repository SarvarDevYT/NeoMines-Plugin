package me.neomines.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.neomines.NeoMines;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.schedulers.MineManager;
import me.neomines.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class NeoMinePlaceHolders extends PlaceholderExpansion {

    private final NeoMines plugin;

    public NeoMinePlaceHolders(NeoMines plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "neomines";
    }

    @Override
    public @NotNull String getAuthor() {
        return "NeoMinesTeam";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        String[] args = identifier.split("_");
        if (args.length <= 1) {
            if (args[0].equalsIgnoreCase("prefix") || args[0].equalsIgnoreCase("p")) {
                return NeoMines.PREFIX != null ? NeoMines.PREFIX : "";
            }
            return null;
        }

        // Support both %neomines_countdown_<mine>% and %neomines_<mine>_countdown%
        String mineName;
        String property;

        if (MineManager.getInstance() != null && MineManager.getInstance().getMine(args[1]) != null) {
            property = args[0].toLowerCase();
            mineName = args[1];
        } else if (MineManager.getInstance() != null && MineManager.getInstance().getMine(args[0]) != null) {
            mineName = args[0];
            property = args[1].toLowerCase();
        } else {
            mineName = args[1];
            property = args[0].toLowerCase();
        }

        if (MineManager.getInstance() == null) {
            return "";
        }

        CuboidNeoMine neoMine = MineManager.getInstance().getMine(mineName);
        if (neoMine == null) {
            return plugin.getLangString("Error-Messages.Mine.Not-Exist");
        }

        if (!neoMine.isRunnable()) {
            return "inactive";
        }

        switch (property) {
            case "countdown":
            case "time":
                return Utils.secondsToTimeFormat(neoMine.getCountdown());
            case "remainingseconds":
                return String.valueOf(neoMine.getCountdown());
            case "totalblocks":
                return String.valueOf(neoMine.getTotalBlocks());
            case "remainingblocks":
                return String.valueOf(neoMine.getBlockCount());
            case "minedblocks":
                return String.valueOf(neoMine.getMinedBlocks());
            case "remainingblockspercentage":
            case "remainingblocksper":
                return String.valueOf(neoMine.getRemainingBlocksPer());
            case "resetpercentage":
                return String.valueOf(neoMine.getResetPercentage());
            case "timestring":
                return neoMine.getFormattedTimeString();
            default:
                return null;
        }
    }
}
