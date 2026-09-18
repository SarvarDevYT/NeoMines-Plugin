package me.neomines.gui.menus;

import me.neomines.NeoMines;
import me.neomines.gui.Menu;
import me.neomines.gui.PlayerMenuUtility;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class FlagsMenu extends Menu {

    private final CuboidNeoMine mine;
    private final NeoMines plugin;

    public FlagsMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        playerMenuUtility.setMenu(this);
        this.mine = playerMenuUtility.getMine();
        this.plugin = NeoMines.getInstance();
    }

    @Override
    public String getMenuName() {
        return plugin.getLangString("GUI.Flags-Menu.Title");
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        event.setCancelled(true);

        switch (event.getRawSlot()) {
            case 19:
                mine.setWarn(!mine.isWarn());
                break;
            case 10:
                mine.setWarnGlobal(!mine.isWarnGlobal());
                break;
            case 28:
                mine.setWarnHotbar(!mine.isWarnHotbar());
                break;
            case 21:
                mine.setTeleportPlayers(!mine.isTeleportPlayers());
                break;
            case 30:
                mine.setTeleportPlayersToResetLocation(!mine.isTeleportPlayersToResetLocation());
                break;
            case 23:
                mine.setReplaceMode(!mine.isReplaceMode());
                break;
            case 25:
                new WarnDistanceMenu(playerMenuUtility).open();
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                return;
            case 36:
                new MineMenu(playerMenuUtility).open();
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                return;
        }

        mine.save();
        updateMenus();
    }

    @Override
    public void setMenuItems() {
        // Enable warn
        inventory.setItem(19, ItemStackBuilder.buildItem(
                mine.isWarn() ? Material.LIME_DYE : Material.GRAY_DYE,
                mine.isWarn() ? plugin.getLangString("GUI.Flags-Menu.Items.Warn.Active.Name") : plugin.getLangString("GUI.Flags-Menu.Items.Warn.Inactive.Name"),
                mine.isWarn() ? plugin.getLangStringList("GUI.Flags-Menu.Items.Warn.Active.Lore") : plugin.getLangStringList("GUI.Flags-Menu.Items.Warn.Inactive.Lore")));

        // Warn globally
        inventory.setItem(10, ItemStackBuilder.buildItem(
                mine.isWarnGlobal() ? Material.LIME_DYE : Material.GRAY_DYE,
                mine.isWarnGlobal() ? plugin.getLangString("GUI.Flags-Menu.Items.Warn-Global.Active.Name") : plugin.getLangString("GUI.Flags-Menu.Items.Warn-Global.Inactive.Name"),
                mine.isWarnGlobal() ? plugin.getLangStringList("GUI.Flags-Menu.Items.Warn-Global.Active.Lore") : plugin.getLangStringList("GUI.Flags-Menu.Items.Warn-Global.Inactive.Lore")));

        // Action bar
        inventory.setItem(28, ItemStackBuilder.buildItem(
                mine.isWarnHotbar() ? Material.LIME_DYE : Material.GRAY_DYE,
                mine.isWarnHotbar() ? plugin.getLangString("GUI.Flags-Menu.Items.Warn-Hotbar.Active.Name") : plugin.getLangString("GUI.Flags-Menu.Items.Warn-Hotbar.Inactive.Name"),
                mine.isWarnHotbar() ? plugin.getLangStringList("GUI.Flags-Menu.Items.Warn-Hotbar.Active.Lore") : plugin.getLangStringList("GUI.Flags-Menu.Items.Warn-Hotbar.Inactive.Lore")));

        // Teleport players
        inventory.setItem(21, ItemStackBuilder.buildItem(
                mine.isTeleportPlayers() ? Material.LIME_DYE : Material.GRAY_DYE,
                mine.isTeleportPlayers() ? plugin.getLangString("GUI.Flags-Menu.Items.Teleport-Players.Active.Name") : plugin.getLangString("GUI.Flags-Menu.Items.Teleport-Players.Inactive.Name"),
                mine.isTeleportPlayers() ? plugin.getLangStringList("GUI.Flags-Menu.Items.Teleport-Players.Active.Lore") : plugin.getLangStringList("GUI.Flags-Menu.Items.Teleport-Players.Inactive.Lore")));

        // Teleport players to reset location
        inventory.setItem(30, ItemStackBuilder.buildItem(
                mine.isTeleportPlayersToResetLocation() ? Material.LIME_DYE : Material.GRAY_DYE,
                mine.isTeleportPlayersToResetLocation() ? plugin.getLangString("GUI.Flags-Menu.Items.Teleport-Players-To-Reset-Location.Active.Name") : plugin.getLangString("GUI.Flags-Menu.Items.Teleport-Players-To-Reset-Location.Inactive.Name"),
                mine.isTeleportPlayersToResetLocation() ? plugin.getLangStringList("GUI.Flags-Menu.Items.Teleport-Players-To-Reset-Location.Active.Lore") : plugin.getLangStringList("GUI.Flags-Menu.Items.Teleport-Players-To-Reset-Location.Inactive.Lore")));

        // Replace mode
        inventory.setItem(23, ItemStackBuilder.buildItem(
                mine.isReplaceMode() ? Material.LIME_DYE : Material.GRAY_DYE,
                mine.isReplaceMode() ? plugin.getLangString("GUI.Flags-Menu.Items.Replace-Mode.Active.Name") : plugin.getLangString("GUI.Flags-Menu.Items.Replace-Mode.Inactive.Name"),
                mine.isReplaceMode() ? plugin.getLangStringList("GUI.Flags-Menu.Items.Replace-Mode.Active.Lore") : plugin.getLangStringList("GUI.Flags-Menu.Items.Replace-Mode.Inactive.Lore")));

        // Warn distance
        List<String> warnDistanceLore = plugin.getLangStringList("GUI.Flags-Menu.Items.Warn-Distance.Lore");
        warnDistanceLore.replaceAll(s -> s.replaceAll("%distance%", String.valueOf(mine.getWarnDistance())));
        inventory.setItem(25, ItemStackBuilder.buildItem(Material.STICK, plugin.getLangString("GUI.Flags-Menu.Items.Warn-Distance.Name"), warnDistanceLore));

        // Back arrow
        inventory.setItem(36, ItemStackBuilder.buildItem(
                Material.ARROW,
                plugin.getLangString("GUI.Universal.Back-To-Mine-Menu.Name"),
                plugin.getLangStringList("GUI.Universal.Back-To-Mine-Menu.Lore")));
    }
}
