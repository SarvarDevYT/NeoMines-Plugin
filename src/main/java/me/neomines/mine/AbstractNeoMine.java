package me.neomines.mine;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.neomines.NeoMines;
import me.neomines.mine.components.NeoMineBlock;
import me.neomines.mine.components.NeoMineResetMode;
import me.neomines.utils.Utils;
import me.neomines.utils.configuration.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.BoundingBox;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractNeoMine implements Cloneable {

    protected String name;
    protected String world;
    protected Region region;
    protected List<NeoMineBlock> blocks = new ArrayList<>();
    protected RandomPattern randomPattern;
    protected int resetDelay;
    protected double resetPercentage;
    protected NeoMineResetMode resetMode = NeoMineResetMode.TIME;
    protected boolean replaceMode;
    protected boolean warnHotbar;
    protected String warnHotbarMessage = "default";
    protected boolean warn;
    protected boolean warnGlobal;
    protected String warnMessage = "default";
    protected String resetMessage = "default";
    protected List<Integer> warnSeconds = NeoMines.getInstance().getFileManager().getDefaultIntegers("Default-Warn-Seconds");
    protected int warnDistance = 5;
    protected boolean teleportPlayers;
    protected boolean isStopped;
    protected Location teleportLocation;
    protected int minEfficiencyLvl;
    protected boolean teleportPlayersToResetLocation;
    protected Location teleportResetLocation;
    protected long blockCount;
    protected boolean runnable;
    protected boolean firstCycle = true;
    protected int countdown;

    private final Random random = new Random();
    protected int countdownForAutoReset = random.nextInt(200) + 500;

    public AbstractNeoMine(String name, Region region) {
        this.name = name;
        if (region != null) {
            this.region = region.clone();
            com.sk89q.worldedit.world.World regionWorld = region.getWorld();
            this.world = regionWorld != null ? regionWorld.getName() : null;
        }
    }

    public void run() {
        if (isStopped || !checkRunnable()) {
            return;
        }

        switch (resetMode) {
            case TIME:
                if (resetDelay <= 0) return;
                if (!firstCycle) {
                    --countdown;
                } else {
                    firstCycle = false;
                    countdown = resetDelay;
                }

                if (warn && warnSeconds != null && warnSeconds.contains(countdown)) {
                    broadcastWarnMessage();
                }

                if (countdown <= 0) {
                    reset();
                    firstCycle = true;
                }
                break;

            case PERCENTAGE:
                --countdownForAutoReset;
                if (countdownForAutoReset <= 0) {
                    forceReset();
                    countdownForAutoReset = random.nextInt(200) + 500;
                }
                if (getRemainingBlocksPer() <= resetPercentage) {
                    reset();
                }
                break;

            case TIME_PERCENTAGE:
                if (resetDelay <= 0) return;
                if (!firstCycle) {
                    --countdown;
                } else {
                    firstCycle = false;
                    countdown = resetDelay;
                }

                if (warn && warnSeconds != null && warnSeconds.contains(countdown)) {
                    broadcastWarnMessage();
                }

                if (countdown <= 0) {
                    reset();
                    firstCycle = true;
                    break;
                }

                if (getRemainingBlocksPer() <= resetPercentage) {
                    reset();
                    firstCycle = true;
                    break;
                }
                break;
        }

        if (warnHotbar) {
            broadcastHotbar();
        }
    }

    public void reset() {
        if (region == null || region.getWorld() == null) return;

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(region.getWorld())) {
            if (blockCount != getTotalBlocks()) {
                blockCount = getTotalBlocks();
                if (!replaceMode) {
                    editSession.setBlocks(region, randomPattern);
                } else {
                    com.sk89q.worldedit.world.block.BlockType airType = BlockTypes.AIR;
                    if (airType != null) {
                        editSession.replaceBlocks(region, Collections.singleton(airType.getDefaultState().toBaseBlock()), randomPattern);
                    }
                }
            }

            if (warn) {
                broadcastResetMessage();
            }
            if (teleportPlayers) {
                teleportPlayers();
            }
        } catch (MaxChangedBlocksException exception) {
            throw new IllegalArgumentException(name + " tried to set too many blocks!");
        } catch (NoSuchMethodError exception) {
            throw new NoSuchMethodError("Could not reset " + name + " because your version of WorldEdit is incompatible!");
        } catch (Exception e) {
            NeoMines.getInstance().getLogger().warning("Error resetting mine " + name + ": " + e.getMessage());
        }
    }

    public void forceReset() {
        blockCount = 0;
        reset();
    }

    public boolean checkRunnable() {
        if (region == null) {
            FileConfig fileConfig = new FileConfig(NeoMines.getInstance().getDataFolder() + "/mines", name + ".yml");
            if (Bukkit.getWorld(world) != null) {
                loadRegion(fileConfig);
            } else {
                setStopped(true);
                NeoMines.getInstance().getLogger().warning("World " + world + " not found. Stopped " + name);
                return false;
            }
        }
        setRunnable(region != null && randomPattern != null);
        return runnable;
    }

    public boolean isRunnable() {
        return runnable;
    }

    public void setRunnable(boolean runnable) {
        this.runnable = runnable;
    }

    public void loadRegion(FileConfig fileConfig) {
        Location minimumPoint = (Location) fileConfig.get("Mine.region.p1");
        Location maximumPoint = (Location) fileConfig.get("Mine.region.p2");

        if (minimumPoint == null || maximumPoint == null || minimumPoint.getWorld() == null || maximumPoint.getWorld() == null || !(Objects.equals(minimumPoint.getWorld(), maximumPoint.getWorld()))) {
            NeoMines.getInstance().getLogger().severe("Could not load region of " + name);
            setStopped(true);
            return;
        }

        region = new CuboidRegion(BukkitAdapter.adapt(minimumPoint.getWorld()),
                BlockVector3.at(minimumPoint.getX(), minimumPoint.getY(), minimumPoint.getZ()),
                BlockVector3.at(maximumPoint.getX(), maximumPoint.getY(), maximumPoint.getZ()));
        world = minimumPoint.getWorld().getName();
    }

    public void addBlock(NeoMineBlock neoMineBlock) {
        double chanceSum = 0;
        for (NeoMineBlock block : blocks) {
            if (block.getBlockData().equals(neoMineBlock.getBlockData())) {
                continue;
            }
            chanceSum += block.getChance();
        }

        chanceSum += neoMineBlock.getChance();
        if (chanceSum > 100) {
            throw new IllegalArgumentException(NeoMines.getInstance().getLangString("Error-Messages.Mine.Chance-Over-100"));
        }

        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).getBlockData().equals(neoMineBlock.getBlockData())) {
                blocks.set(i, neoMineBlock);
                blocksToRandomPattern();
                return;
            }
        }

        blocks.add(neoMineBlock);
        blocksToRandomPattern();
    }

    public void clearComposition() {
        blocks.clear();
        blocksToRandomPattern();
    }

    public void removeBlock(org.bukkit.block.data.BlockData blockData) {
        if (!containsBlockData(blockData)) {
            throw new IllegalArgumentException(NeoMines.getInstance().getLangString("Error-Messages.Mine.Block-Not-In-Composition"));
        }
        blocks.remove(getBlock(blockData));
        blocksToRandomPattern();
    }

    public void removeBlock(int index) {
        if (index >= 0 && index < blocks.size()) {
            blocks.remove(index);
            blocksToRandomPattern();
        }
    }

    public double getCompositionChance() {
        double chance = 0d;
        for (NeoMineBlock block : blocks) {
            chance += block.getChance();
        }
        return Math.round(chance * 100) / 100d;
    }

    public double getBlockChance(Material material) {
        double chance = 0;
        for (NeoMineBlock block : blocks) {
            if (block.getBlockData().getMaterial().equals(material)) {
                chance = block.getChance();
            }
        }
        return chance;
    }

    public void setBlockChance(NeoMineBlock block, double chance) {
        double chanceSum = 0;
        for (NeoMineBlock neoMineBlock : blocks) {
            if (neoMineBlock.equals(block)) {
                chanceSum += chance;
                continue;
            }
            chanceSum += neoMineBlock.getChance();
        }

        if (chanceSum > 100) {
            throw new IllegalArgumentException(NeoMines.getInstance().getLangString("Error-Messages.Mine.Invalid-Chance"));
        }

        block.setChance(chance);
        blocksToRandomPattern();
    }

    public void setBlockChance(org.bukkit.block.data.BlockData blockData, double chance) {
        setBlockChance(getBlock(blockData), chance);
    }

    public NeoMineBlock getBlock(org.bukkit.block.data.BlockData blockData) {
        for (NeoMineBlock block : blocks) {
            if (block.getBlockData().equals(blockData)) {
                return block;
            }
        }
        return null;
    }

    public boolean containsBlock(NeoMineBlock block) {
        return blocks.contains(block);
    }

    public boolean containsBlockData(org.bukkit.block.data.BlockData blockData) {
        for (NeoMineBlock block : blocks) {
            if (block.getBlockData().equals(blockData)) return true;
        }
        return false;
    }

    public boolean containsBlockMaterial(Material material) {
        for (NeoMineBlock block : blocks) {
            if (block.getBlockData().getMaterial().equals(material)) return true;
        }
        return false;
    }

    public void blocksToRandomPattern() {
        if (blocks.isEmpty() || blocks.stream().allMatch(neoMineBlock -> neoMineBlock.getChance() == 0)) {
            randomPattern = null;
            return;
        }

        randomPattern = new RandomPattern();
        blocks.stream().filter(neoMineBlock -> neoMineBlock.getChance() > 0)
                .forEach(neoMineBlock -> randomPattern.add(BukkitAdapter.adapt(neoMineBlock.getBlockData()).toBaseBlock(), neoMineBlock.getChance()));
    }

    public void broadcastHotbar() {
        String finalMessage = Utils.setPlaceholders(getWarnHotbarMessage(resetMode), this);
        getPlayersInDistance().forEach(player -> Utils.sendActionBar(player, finalMessage));
    }

    public void broadcastWarnMessage() {
        String finalWarnMessage = Utils.setPlaceholders(getWarnMessage(), this);
        getPlayersToWarn().forEach(player -> {
            for (String line : finalWarnMessage.split("\n")) {
                player.sendMessage(line);
            }
        });
    }

    public void broadcastResetMessage() {
        String finalResetMessage = Utils.setPlaceholders(getResetMessage(), this);
        getPlayersToWarn().forEach(player -> {
            for (String line : finalResetMessage.split("\n")) {
                player.sendMessage(line);
            }
        });
    }

    public void teleportPlayers() {
        if (!teleportPlayersToResetLocation) {
            getPlayersInRegion().forEach(player -> player.teleport(new Location(player.getWorld(),
                    player.getLocation().getX(), region.getMaximumPoint().y() + 1,
                    player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch())));
        } else {
            if (teleportResetLocation == null) {
                return;
            }
            getPlayersInRegion().forEach(player -> player.teleport(teleportResetLocation));
        }
    }

    public Collection<? extends Player> getPlayersInRegion() {
        if (region == null) return Collections.emptyList();
        com.sk89q.worldedit.world.World regionWorld = region.getWorld();
        if (regionWorld == null) return Collections.emptyList();
        String regionWorldName = regionWorld.getName();

        return Bukkit.getOnlinePlayers().stream().filter(player ->
                Objects.equals(regionWorldName, player.getWorld().getName()) &&
                region.contains(BlockVector3.at(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()))
        ).collect(Collectors.toList());
    }

    public Collection<? extends Player> getPlayersInDistance() {
        if (region == null) return Collections.emptyList();
        com.sk89q.worldedit.world.World regionWorld = region.getWorld();
        if (regionWorld == null) return Collections.emptyList();
        String regionWorldName = regionWorld.getName();

        return Bukkit.getOnlinePlayers().stream().filter(player ->
                Objects.equals(regionWorldName, player.getWorld().getName()) &&
                player.getBoundingBox().overlaps(
                        new BoundingBox(
                                region.getMinimumPoint().x(),
                                region.getMinimumPoint().y(),
                                region.getMinimumPoint().z(),
                                region.getMaximumPoint().x() + 1,
                                region.getMaximumPoint().y() + 1,
                                region.getMaximumPoint().z() + 1
                        ).expand(warnDistance)
                )
        ).collect(Collectors.toList());
    }

    public Collection<? extends Player> getPlayersToWarn() {
        return !warnGlobal ? getPlayersInDistance() : Bukkit.getOnlinePlayers();
    }

    public String getFormattedTimeString() {
        NeoMines plugin = NeoMines.getInstance();
        switch (countdown) {
            case 3600:
                return plugin.getLangString("Time.Hour");
            case 60:
                return plugin.getLangString("Time.Minute");
            case 1:
                return plugin.getLangString("Time.Second");
            default:
                if (countdown >= 3600) {
                    return plugin.getLangString("Time.Hours");
                } else if (countdown >= 60) {
                    return plugin.getLangString("Time.Minutes");
                } else {
                    return plugin.getLangString("Time.Seconds");
                }
        }
    }

    public abstract void handleBlockBreak(BlockBreakEvent event);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
        if (region != null) {
            com.sk89q.worldedit.world.World regionWorld = region.getWorld();
            this.world = regionWorld != null ? regionWorld.getName() : null;
        } else {
            this.world = null;
        }
    }

    public List<NeoMineBlock> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<NeoMineBlock> blocks) {
        this.blocks = blocks;
    }

    public RandomPattern getRandomPattern() {
        return randomPattern;
    }

    public void setRandomPattern(RandomPattern randomPattern) {
        this.randomPattern = randomPattern;
    }

    public NeoMineResetMode getResetMode() {
        return resetMode;
    }

    public void setResetMode(NeoMineResetMode resetMode) {
        this.resetMode = resetMode;
    }

    public int getResetDelay() {
        return resetDelay;
    }

    public void setResetDelay(int resetDelay) {
        this.resetDelay = resetDelay;
    }

    public double getResetPercentage() {
        return resetPercentage;
    }

    public void setResetPercentage(double resetPercentage) {
        if (resetPercentage < 0) resetPercentage = 0;
        if (resetPercentage > 100) resetPercentage = 100;
        this.resetPercentage = Math.round(resetPercentage * 100) / 100d;
    }

    public boolean isReplaceMode() {
        return replaceMode;
    }

    public void setReplaceMode(boolean replaceMode) {
        this.replaceMode = replaceMode;
    }

    public boolean isWarn() {
        return warn;
    }

    public void setWarn(boolean warn) {
        this.warn = warn;
    }

    public boolean isWarnHotbar() {
        return warnHotbar;
    }

    public void setWarnHotbar(boolean warnHotbar) {
        this.warnHotbar = warnHotbar;
    }

    public String getWarnHotbarMessage(NeoMineResetMode resetMode) {
        if (warnHotbarMessage != null && !warnHotbarMessage.equalsIgnoreCase("default")) {
            return warnHotbarMessage;
        }
        return NeoMines.getInstance().getDefaultString("Default-Hotbar-Message-" + resetMode.name());
    }

    public void setWarnHotbarMessage(String warnHotbarMessage) {
        this.warnHotbarMessage = warnHotbarMessage;
    }

    public boolean isWarnGlobal() {
        return warnGlobal;
    }

    public void setWarnGlobal(boolean warnGlobal) {
        this.warnGlobal = warnGlobal;
    }

    public String getWarnMessage() {
        return (warnMessage != null && !warnMessage.equalsIgnoreCase("default"))
                ? warnMessage : NeoMines.getInstance().getDefaultString("Default-Warn-Message");
    }

    public void setWarnMessage(String warnMessage) {
        this.warnMessage = warnMessage;
    }

    public String getResetMessage() {
        return (resetMessage != null && !resetMessage.equalsIgnoreCase("default"))
                ? resetMessage : NeoMines.getInstance().getDefaultString("Default-Reset-Message");
    }

    public void setResetMessage(String resetMessage) {
        this.resetMessage = resetMessage;
    }

    public List<Integer> getWarnSeconds() {
        return warnSeconds;
    }

    public void setWarnSeconds(List<Integer> warnSeconds) {
        this.warnSeconds = warnSeconds;
    }

    public int getWarnDistance() {
        return warnDistance;
    }

    public void setWarnDistance(int warnDistance) {
        this.warnDistance = warnDistance;
    }

    public boolean isTeleportPlayers() {
        return teleportPlayers;
    }

    public void setTeleportPlayers(boolean teleportPlayers) {
        this.teleportPlayers = teleportPlayers;
    }

    public boolean isTeleportPlayersToResetLocation() {
        return teleportPlayersToResetLocation;
    }

    public void setTeleportPlayersToResetLocation(boolean teleportPlayersToResetLocation) {
        this.teleportPlayersToResetLocation = teleportPlayersToResetLocation;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void setStopped(boolean stopped) {
        isStopped = stopped;
    }

    public abstract Location getTeleportLocation();

    public void setTeleportLocation(Location teleportLocation) {
        this.teleportLocation = teleportLocation;
    }

    public Location getTeleportResetLocation() {
        return teleportResetLocation;
    }

    public void setTeleportResetLocation(Location teleportResetLocation) {
        this.teleportResetLocation = teleportResetLocation;
    }

    public int getMinEfficiencyLvl() {
        return minEfficiencyLvl;
    }

    public void setMinEfficiencyLvl(int minEfficiencyLvl) {
        this.minEfficiencyLvl = minEfficiencyLvl;
    }

    public boolean isFirstCycle() {
        return firstCycle;
    }

    public void setFirstCycle(boolean firstCycle) {
        this.firstCycle = firstCycle;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    @Override
    public AbstractNeoMine clone() {
        try {
            return (AbstractNeoMine) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public abstract long getTotalBlocks();

    public long getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(long blockCount) {
        this.blockCount = blockCount;
    }

    public long getMinedBlocks() {
        return getTotalBlocks() - getBlockCount();
    }

    public abstract double getRemainingBlocksPer();
}
