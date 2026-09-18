package me.neomines.gui.menus;

import me.neomines.NeoMines;
import me.neomines.gui.Menu;
import me.neomines.gui.PlayerMenuUtility;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.mine.components.NeoMineBlock;
import me.neomines.mine.components.NeoMineLootItem;
import me.neomines.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ChangeBlockLootTableMenu extends Menu {

    private final CuboidNeoMine mine;
    private final NeoMineBlock block;

    public ChangeBlockLootTableMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        playerMenuUtility.setMenu(this);
        this.mine = playerMenuUtility.getMine();
        this.block = playerMenuUtility.getBlock();
    }

    @Override
    public String getMenuName() {
        return NeoMines.getInstance().getLangString("GUI.Change-Loot-Table-Menu.Title");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getRawSlot() >= 45 && event.getRawSlot() <= 53) {
            event.setCancelled(true);
            if (event.getRawSlot() == 45) {
                new CompositionBlockMenu(playerMenuUtility).open();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                return;
            }

            if (event.getRawSlot() == 53) {
                block.getLootTable().clear();
                for (int i = 0; i < 45; i++) {
                    ItemStack itemStack = event.getInventory().getItem(i);
                    if (itemStack == null || itemStack.getType().isAir()) {
                        continue;
                    }
                    block.getLootTable().add(new NeoMineLootItem(itemStack));
                }

                mine.save();
                player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("GUI.Change-Loot-Table-Menu.Items.Save-Table.Message"));
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                updateMenus();
            }
        }
    }

    @Override
    public void setMenuItems() {
        for (int i = 0; i < block.getLootTable().size(); i++) {
            if (i >= 45) break;
            inventory.setItem(i, block.getLootTable().get(i).getItem());
        }

        for (int i = 45; i < getSlots(); i++) {
            inventory.setItem(i, ItemStackBuilder.buildItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }

        inventory.setItem(45, ItemStackBuilder.buildItem(Material.ARROW,
                NeoMines.getInstance().getLangString("GUI.Universal.Back-To-Block-Menu.Name"),
                NeoMines.getInstance().getLangStringList("GUI.Universal.Back-To-Block-Menu.Lore")));

        inventory.setItem(53, ItemStackBuilder.buildItem(Material.LIME_STAINED_GLASS_PANE,
                NeoMines.getInstance().getLangString("GUI.Change-Loot-Table-Menu.Items.Save-Table.Name")));
    }
}
