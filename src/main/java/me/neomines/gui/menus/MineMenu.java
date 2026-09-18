package me.neomines.gui.menus;

import me.neomines.NeoMines;
import me.neomines.gui.Menu;
import me.neomines.gui.PlayerMenuUtility;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class MineMenu extends Menu {

    private final CuboidNeoMine mine;
    private final NeoMines plugin;

    public MineMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        playerMenuUtility.setMenu(this);
        this.mine = playerMenuUtility.getMine();
        this.plugin = NeoMines.getInstance();
    }

    public MineMenu(PlayerMenuUtility playerMenuUtility, CuboidNeoMine mine) {
        super(playerMenuUtility);
        playerMenuUtility.setMenu(this);
        this.mine = mine;
        playerMenuUtility.setMine(mine);
        this.plugin = NeoMines.getInstance();
    }

    @Override
    public String getMenuName() {
        return plugin.getLangString("GUI.Mine-Menu.Title").replaceAll("%mine%", mine.getName());
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        switch (event.getRawSlot()) {
            case 10:
                new CompositionMenu(playerMenuUtility).open();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                break;
            case 12:
                if (!event.isRightClick()) {
                    switch (mine.getResetMode()) {
                        case TIME:
                        case TIME_PERCENTAGE:
                            new ResetDelayMenu(playerMenuUtility).open();
                            break;
                        case PERCENTAGE:
                            new ResetPercentageMenu(playerMenuUtility).open();
                            break;
                    }
                } else {
                    mine.setResetMode(mine.getResetMode().next());
                    mine.save();
                    updateMenus();
                }
                break;
            case 14:
                new FlagsMenu(playerMenuUtility).open();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                break;
            case 16:
                new PickaxeEfficiencyMenu(playerMenuUtility).open();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                break;
            case 29:
                player.performCommand("nm reset " + mine.getName());
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                break;
            case 31:
                player.closeInventory();
                if (mine.getTeleportLocation() == null) {
                    player.sendMessage(NeoMines.PREFIX + plugin.getLangString("Error-Messages.Mine.Teleport-Error").replaceAll("%mine%", mine.getName()));
                    return;
                }
                player.teleport(mine.getTeleportLocation());
                player.playSound(player.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 0.3F, 1F);
                break;
            case 33:
                new DeleteConfirmMenu(playerMenuUtility).open();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                break;
            case 45:
                if (mine.getRegion() == null) {
                    player.sendMessage(NeoMines.PREFIX + plugin.getLangString("Error-Messages.Mine.Invalid-Region"));
                    break;
                }
                mine.setStopped(!mine.isStopped());
                mine.save();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                updateMenus();
                break;
            case 49:
                new MineListMenu(playerMenuUtility, playerMenuUtility.getMineListMenuPage()).open();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                break;
        }
    }

    @Override
    public void setMenuItems() {
        // Configuring the composition
        List<String> compLore = plugin.getLangStringList("GUI.Mine-Menu.Items.Configure-Composition.Lore");
        compLore.replaceAll(s -> s.replaceAll("%chance%", String.valueOf(mine.getCompositionChance())));
        inventory.setItem(10, ItemStackBuilder.buildItem(Material.STONE, plugin.getLangString("GUI.Mine-Menu.Items.Configure-Composition.Name"), compLore));

        Material item = Material.BARRIER;
        String itemname = "";
        List<String> lore = new ArrayList<>();

        switch (mine.getResetMode()) {
            case TIME:
                item = Material.CLOCK;
                itemname = plugin.getLangString("GUI.Mine-Menu.Items.Configure-Delay.Name");
                lore = plugin.getLangStringList("GUI.Mine-Menu.Items.Configure-Delay.Lore");
                break;
            case PERCENTAGE:
                item = Material.STONE_PICKAXE;
                itemname = plugin.getLangString("GUI.Mine-Menu.Items.Configure-Percentage.Name");
                lore = plugin.getLangStringList("GUI.Mine-Menu.Items.Configure-Percentage.Lore");
                break;
            case TIME_PERCENTAGE:
                item = Material.COMPARATOR;
                itemname = plugin.getLangString("GUI.Mine-Menu.Items.Configure-TimePercentage.Name");
                lore = plugin.getLangStringList("GUI.Mine-Menu.Items.Configure-TimePercentage.Lore");
                break;
        }

        lore.replaceAll(s -> s.replaceAll("%delay%", String.valueOf(mine.getResetDelay())).replaceAll("%percentage%", String.valueOf(mine.getResetPercentage())));
        inventory.setItem(12, ItemStackBuilder.buildItem(item, itemname, lore));

        // Flag menu
        List<String> flagLore = plugin.getLangStringList("GUI.Mine-Menu.Items.Flag-Menu.Lore");
        flagLore.replaceAll(s -> s.replaceAll("%warn%", String.valueOf(mine.isWarn()))
                .replaceAll("%global%", String.valueOf(mine.isWarnGlobal()))
                .replaceAll("%actionbar%", String.valueOf(mine.isWarnHotbar()))
                .replaceAll("%teleports%", String.valueOf(mine.isTeleportPlayers()))
                .replaceAll("%resettp%", String.valueOf(mine.isTeleportPlayersToResetLocation()))
                .replaceAll("%replacemode%", String.valueOf(mine.isReplaceMode()))
                .replaceAll("%warndistance%", String.valueOf(mine.getWarnDistance())));
        inventory.setItem(14, ItemStackBuilder.buildItem(Material.TORCH, plugin.getLangString("GUI.Mine-Menu.Items.Flag-Menu.Name"), flagLore));

        // Efficiency lvl
        List<String> efficiencyLore = plugin.getLangStringList("GUI.Mine-Menu.Items.Efficiency-Lvl.Lore");
        efficiencyLore.replaceAll(s -> s.replaceAll("%level%", String.valueOf(mine.getMinEfficiencyLvl())));
        inventory.setItem(16, ItemStackBuilder.buildItem(Material.DIAMOND_PICKAXE, plugin.getLangString("GUI.Mine-Menu.Items.Efficiency-Lvl.Name"), efficiencyLore));

        // Reset mine
        inventory.setItem(29, ItemStackBuilder.buildItem(Material.REDSTONE, plugin.getLangString("GUI.Mine-Menu.Items.Reset-Mine.Name")));
        // Teleport to mine
        inventory.setItem(31, ItemStackBuilder.buildItem(Material.ENDER_PEARL, plugin.getLangString("GUI.Mine-Menu.Items.Teleport.Name"), plugin.getLangStringList("GUI.Mine-Menu.Items.Teleport.Lore")));
        // Delete mine
        inventory.setItem(33, ItemStackBuilder.buildItem(Material.REDSTONE_BLOCK, plugin.getLangString("GUI.Mine-Menu.Items.Delete.Name")));

        // Start / Stop
        inventory.setItem(45, ItemStackBuilder.buildItem(!mine.isStopped() ? Material.REDSTONE_TORCH : Material.LEVER,
                !mine.isStopped() ? plugin.getLangString("GUI.Mine-Menu.Items.Start-Stop.Active.Name") : ChatColor.GRAY + plugin.getLangString("GUI.Mine-Menu.Items.Start-Stop.Inactive.Name"),
                plugin.getLangStringList("GUI.Mine-Menu.Items.Start-Stop.Lore")));

        // Go back
        inventory.setItem(49, ItemStackBuilder.buildItem(Material.BARRIER, plugin.getLangString("GUI.Mine-Menu.Items.Back.Name"), plugin.getLangStringList("GUI.Mine-Menu.Items.Back.Lore")));

        // Running status
        if (mine.checkRunnable() && !mine.isStopped()) {
            inventory.setItem(53, ItemStackBuilder.buildItem(Material.LIME_DYE, plugin.getLangString("GUI.Mine-Menu.Items.Running.Active.Name"), plugin.getLangStringList("GUI.Mine-Menu.Items.Running.Active.Lore")));
        } else {
            ArrayList<String> reasons = new ArrayList<>();
            if (mine.getRegion() == null) {
                reasons.add(plugin.getLangString("GUI.Mine-Menu.Items.Running.Inactive.Reasons.Invalid-Region"));
            }
            if (mine.isStopped()) {
                reasons.add(plugin.getLangString("GUI.Mine-Menu.Items.Running.Inactive.Reasons.Stopped"));
            }
            if (mine.getRandomPattern() == null) {
                reasons.add(plugin.getLangString("GUI.Mine-Menu.Items.Running.Inactive.Reasons.No-Composition"));
            }
            if (!(mine.getResetDelay() > 0 && mine.getResetDelay() <= 100000)) {
                reasons.add(plugin.getLangString("GUI.Mine-Menu.Items.Running.Inactive.Reasons.Invalid-Delay").replaceAll("%delay%", String.valueOf(mine.getResetDelay())));
            }
            inventory.setItem(53, ItemStackBuilder.buildItem(Material.GRAY_DYE, plugin.getLangString("GUI.Mine-Menu.Items.Running.Inactive.Name"), reasons));
        }
    }
}
