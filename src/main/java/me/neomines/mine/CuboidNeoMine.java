package me.neomines.mine;

import com.google.common.base.Enums;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import me.neomines.NeoMines;
import me.neomines.mine.components.NeoMineBlock;
import me.neomines.mine.components.NeoMineLootItem;
import me.neomines.mine.components.NeoMineResetMode;
import me.neomines.utils.Utils;
import me.neomines.utils.configuration.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@SerializableAs("CuboidNeoMine")
public class CuboidNeoMine extends AbstractNeoMine implements ConfigurationSerializable {

    private final Random random = new Random();
    private File file;
    private YamlConfiguration fileConfig;

    public CuboidNeoMine(String name, Region region) {
        super(name, region);
    }

    @SuppressWarnings("unchecked")
    public static CuboidNeoMine deserialize(Map<String, Object> serializedNeoMine) {
        Logger logger = NeoMines.getInstance().getLogger();
        String name = (String) serializedNeoMine.get("name");

        Location minimumPoint = null;
        Location maximumPoint = null;
        String world = "";
        Region region = null;

        if (serializedNeoMine.containsKey("region")) {
            Map<String, Object> regionLocations = (Map<String, Object>) serializedNeoMine.get("region");
            world = (String) regionLocations.get("world");
            World bukkitWorld = null;
            boolean loadable = true;
            try {
                bukkitWorld = Bukkit.getWorld(world);
            } catch (Throwable throwable) {
                logger.severe("World " + regionLocations.get("world") + " not found");
            }
            if (bukkitWorld == null) {
                logger.severe("Could not find world " + world + ".");
                loadable = false;
            }
            try {
                minimumPoint = (Location) regionLocations.get("p1");
                maximumPoint = (Location) regionLocations.get("p2");
            } catch (Throwable throwable) {
                logger.severe("Could not load locations for mine " + name);
                loadable = false;
            }

            if (minimumPoint == null || maximumPoint == null || minimumPoint.getWorld() == null || !Objects.equals(minimumPoint.getWorld(), maximumPoint.getWorld())) {
                logger.severe("Could not load locations for mine " + name + ", does the world exist?");
                loadable = false;
            }

            if (loadable && minimumPoint != null && maximumPoint != null && minimumPoint.getWorld() != null) {
                region = new CuboidRegion(BukkitAdapter.adapt(minimumPoint.getWorld()),
                        BlockVector3.at(minimumPoint.getX(), minimumPoint.getY(), minimumPoint.getZ()),
                        BlockVector3.at(maximumPoint.getX(), maximumPoint.getY(), maximumPoint.getZ()));
            }
        }

        ArrayList<NeoMineBlock> blocks = new ArrayList<>();
        if (serializedNeoMine.containsKey("composition")) {
            ArrayList<Map<String, Object>> serializedBlocks = (ArrayList<Map<String, Object>>) serializedNeoMine.get("composition");
            for (Map<String, Object> serializedBlock : serializedBlocks) {
                blocks.add(NeoMineBlock.deserialize(serializedBlock));
            }
        }

        NeoMineResetMode resetMode = NeoMineResetMode.TIME;
        if (serializedNeoMine.containsKey("resetMode")) {
            String modeStr = Objects.toString(serializedNeoMine.get("resetMode"), "TIME");
            resetMode = Enums.getIfPresent(NeoMineResetMode.class, modeStr).or(NeoMineResetMode.TIME);
        }

        int resetDelay = 0;
        if (serializedNeoMine.containsKey("resetDelay")) {
            resetDelay = ((Number) serializedNeoMine.get("resetDelay")).intValue();
        }

        int countdown = resetDelay;
        if (serializedNeoMine.containsKey("countdown")) {
            countdown = ((Number) serializedNeoMine.get("countdown")).intValue();
        }

        double resetPercentage = 0;
        if (serializedNeoMine.containsKey("resetPercentage")) {
            resetPercentage = ((Number) serializedNeoMine.get("resetPercentage")).doubleValue();
        }

        boolean replaceMode = false;
        if (serializedNeoMine.containsKey("replaceMode")) {
            replaceMode = (boolean) serializedNeoMine.get("replaceMode");
        }

        boolean teleportPlayers = false;
        if (serializedNeoMine.containsKey("teleportPlayers")) {
            teleportPlayers = (boolean) serializedNeoMine.get("teleportPlayers");
        }

        boolean teleportPlayersToResetLocation = false;
        if (serializedNeoMine.containsKey("teleportPlayersToResetLocation")) {
            teleportPlayersToResetLocation = (boolean) serializedNeoMine.get("teleportPlayersToResetLocation");
        }

        boolean warnHotbar = false;
        String warnHotbarMessage = "&a%seconds%";
        boolean warn = false;
        boolean warnGlobal = false;
        String warnMessage = "default";
        String resetMessage = "default";
        List<Integer> warnSeconds = Arrays.asList(1, 2, 3, 5, 20, 60);
        int warnDistance = 5;

        if (serializedNeoMine.containsKey("warn")) {
            Map<String, Object> serializedWarn = (Map<String, Object>) serializedNeoMine.get("warn");
            if (serializedWarn.containsKey("warnHotbar")) {
                warnHotbar = (boolean) serializedWarn.get("warnHotbar");
            }
            if (serializedWarn.containsKey("warnHotbarMessage")) {
                warnHotbarMessage = (String) serializedWarn.get("warnHotbarMessage");
            }
            if (serializedWarn.containsKey("enableWarn")) {
                warn = (boolean) serializedWarn.get("enableWarn");
            }
            if (serializedWarn.containsKey("warnGlobal")) {
                warnGlobal = (boolean) serializedWarn.get("warnGlobal");
            }
            if (serializedWarn.containsKey("warnMessage")) {
                warnMessage = (String) serializedWarn.get("warnMessage");
            }
            if (serializedWarn.containsKey("resetMessage")) {
                resetMessage = (String) serializedWarn.get("resetMessage");
            }
            if (serializedWarn.containsKey("warnSeconds")) {
                warnSeconds = (List<Integer>) serializedWarn.get("warnSeconds");
            }
            if (serializedWarn.containsKey("warnDistance")) {
                warnDistance = ((Number) serializedWarn.get("warnDistance")).intValue();
            }
        }

        int minEfficiencyLvl = 0;
        if (serializedNeoMine.containsKey("minEfficiencyLvl")) {
            minEfficiencyLvl = ((Number) serializedNeoMine.get("minEfficiencyLvl")).intValue();
        }

        Location teleportLocation = null;
        if (serializedNeoMine.containsKey("teleportLocation")) {
            teleportLocation = (Location) serializedNeoMine.get("teleportLocation");
        }

        Location teleportResetLocation = null;
        if (serializedNeoMine.containsKey("teleportResetLocation")) {
            teleportResetLocation = (Location) serializedNeoMine.get("teleportResetLocation");
        }

        boolean isStopped = false;
        if (serializedNeoMine.containsKey("isStopped")) {
            isStopped = (boolean) serializedNeoMine.get("isStopped");
        }

        CuboidNeoMine mine = new CuboidNeoMine(name, region);
        mine.setWorld(world);
        mine.setBlocks(blocks);
        mine.setResetMode(resetMode);
        mine.setResetDelay(resetDelay);
        mine.setResetPercentage(resetPercentage);
        mine.setReplaceMode(replaceMode);
        mine.setWarnHotbar(warnHotbar);
        mine.setWarnHotbarMessage(warnHotbarMessage);
        mine.setWarn(warn);
        mine.setWarnGlobal(warnGlobal);
        mine.setWarnMessage(warnMessage);
        mine.setResetMessage(resetMessage);
        mine.setWarnSeconds(warnSeconds);
        mine.setWarnDistance(warnDistance);
        mine.setTeleportPlayers(teleportPlayers);
        mine.setStopped(isStopped);
        mine.setTeleportLocation(teleportLocation);
        mine.setMinEfficiencyLvl(minEfficiencyLvl);
        mine.setTeleportPlayersToResetLocation(teleportPlayersToResetLocation);
        mine.setTeleportResetLocation(teleportResetLocation);
        mine.setCountdown(countdown);
        mine.blocksToRandomPattern();

        if (region != null && mine.getRandomPattern() != null && resetMode != NeoMineResetMode.TIME) {
            mine.forceReset();
        }

        return mine;
    }

    @Override
    @Nonnull
    public Map<String, Object> serialize() {
        Map<String, Object> mapSerializer = new LinkedHashMap<>();
        mapSerializer.put("name", name);

        if (region != null) {
            com.sk89q.worldedit.world.World regionWorld = region.getWorld();
            if (regionWorld != null) {
                Map<String, Object> mappedRegion = new LinkedHashMap<>();
                mappedRegion.put("type", "CUBOID");
                mappedRegion.put("world", regionWorld.getName());
                World bukkitWorld = BukkitAdapter.adapt(regionWorld);
                mappedRegion.put("p1", BukkitAdapter.adapt(bukkitWorld, region.getMinimumPoint()));
                mappedRegion.put("p2", BukkitAdapter.adapt(bukkitWorld, region.getMaximumPoint()));
                mapSerializer.put("region", mappedRegion);
            }
        }

        ArrayList<Map<String, Object>> tempSerializeBlocks = new ArrayList<>();
        for (NeoMineBlock block : blocks) {
            tempSerializeBlocks.add(block.serialize());
        }
        mapSerializer.put("composition", tempSerializeBlocks);

        mapSerializer.put("resetMode", resetMode.name());
        mapSerializer.put("resetDelay", resetDelay);
        mapSerializer.put("countdown", countdown);
        mapSerializer.put("resetPercentage", resetPercentage);
        mapSerializer.put("replaceMode", replaceMode);
        mapSerializer.put("teleportPlayers", teleportPlayers);
        mapSerializer.put("teleportPlayersToResetLocation", teleportPlayersToResetLocation);
        mapSerializer.put("isStopped", isStopped);
        mapSerializer.put("warn", warn);

        Map<String, Object> mappedWarn = new LinkedHashMap<>();
        mappedWarn.put("warnHotbar", warnHotbar);
        mappedWarn.put("warnHotbarMessage", warnHotbarMessage);
        mappedWarn.put("enableWarn", warn);
        mappedWarn.put("warnGlobal", warnGlobal);
        mappedWarn.put("warnMessage", warnMessage);
        mappedWarn.put("resetMessage", resetMessage);
        mappedWarn.put("warnSeconds", warnSeconds);
        mappedWarn.put("warnDistance", warnDistance);
        mapSerializer.put("warn", mappedWarn);

        mapSerializer.put("minEfficiencyLvl", minEfficiencyLvl);
        mapSerializer.put("teleportLocation", teleportLocation);
        mapSerializer.put("teleportResetLocation", teleportResetLocation);

        return mapSerializer;
    }

    @Override
    public void handleBlockBreak(BlockBreakEvent event) {
        if (getMinEfficiencyLvl() > 0 && !event.getPlayer().hasPermission("neomines.break")) {
            Player player = event.getPlayer();
            int efficiencyLvl = 0;
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            Enchantment effEnch = Utils.getEfficiencyEnchantment();
            if (effEnch != null && itemInHand.containsEnchantment(effEnch)) {
                efficiencyLvl = itemInHand.getEnchantmentLevel(effEnch);
            }

            if (efficiencyLvl < getMinEfficiencyLvl()) {
                event.setCancelled(true);
                player.sendMessage(NeoMines.PREFIX + Utils.color(
                        NeoMines.getInstance().getDefaultString("Tool-Too-Weak")
                                .replaceAll("%level%", String.valueOf(minEfficiencyLvl))));
                return;
            }
        }

        blockCount--;

        if (warnHotbar && (resetMode == NeoMineResetMode.PERCENTAGE || resetMode == NeoMineResetMode.TIME_PERCENTAGE)) {
            broadcastHotbar();
        }

        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;

        for (NeoMineBlock block : blocks) {
            if (block.getLootTable().isEmpty() || !block.getBlockData().equals(event.getBlock().getBlockData())) {
                continue;
            }

            if (!block.isAddLootTable()) {
                event.setDropItems(false);
            }

            for (NeoMineLootItem lootItem : block.getLootTable()) {
                ItemStack item = lootItem.getItem().clone();
                double chance = lootItem.getChance();
                double r1 = random.nextDouble() * 100;

                if (chance < r1) {
                    continue;
                }

                Location location = event.getBlock().getLocation();
                if (lootItem.isFortune()) {
                    ItemStack usedTool = event.getPlayer().getInventory().getItemInMainHand();
                    Enchantment fortuneEnch = Utils.getFortuneEnchantment();
                    if (fortuneEnch != null && usedTool.containsEnchantment(fortuneEnch)) {
                        item.setAmount(lootItem.getDropCount(usedTool.getEnchantmentLevel(fortuneEnch)));
                    }
                }
                if (location.getWorld() != null) {
                    location.getWorld().dropItemNaturally(location, item);
                }
            }
        }
    }

    public void save() {
        if (file == null) {
            file = new File(NeoMines.getInstance().getDataFolder() + "/mines", name + ".yml");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (fileConfig == null) {
            fileConfig = new YamlConfiguration();
        }
        fileConfig.set("Mine", this);
        try {
            fileConfig.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(FileConfig fileConfig) {
        fileConfig.set("Mine", this);
        fileConfig.saveConfig();
    }

    @Override
    public Location getTeleportLocation() {
        if (teleportLocation == null) {
            if (region != null && region.getWorld() != null) {
                return new Location(BukkitAdapter.adapt(region.getWorld()),
                        region.getCenter().x() + 0.5,
                        region.getMaximumPoint().y() + 1,
                        region.getCenter().z() + 0.5);
            }
        }
        return teleportLocation;
    }

    @Override
    public CuboidNeoMine clone() {
        return (CuboidNeoMine) super.clone();
    }

    @Override
    public long getTotalBlocks() {
        return region != null ? region.getVolume() : 0L;
    }

    @Override
    public double getRemainingBlocksPer() {
        long total = getTotalBlocks();
        if (total == 0) return 0d;
        return Math.round(((double) getBlockCount() / (double) total) * 10000d) / 100d;
    }

    public void resetFiles() {
        file = null;
        fileConfig = null;
    }
}
