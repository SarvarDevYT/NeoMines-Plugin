package me.neomines.gui.menus;

import me.neomines.NeoMines;
import me.neomines.gui.Menu;
import me.neomines.gui.PlayerMenuUtility;
import me.neomines.mine.components.NeoMineBlock;
import me.neomines.mine.components.NeoMineLootItem;
import me.neomines.utils.ItemStackBuilder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class LootItemListMenu extends Menu {

    private final NeoMineBlock block;

    public LootItemListMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        playerMenuUtility.setMenu(this);
        this.block = playerMenuUtility.getBlock();
    }

    @Override
    public String getMenuName() {
        return "§bTushish ehtimollarini sozlash";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        int rawSlot = event.getRawSlot();
        if (rawSlot >= 46 || Objects.equals(event.getClickedInventory(), event.getWhoClicked().getInventory()) || event.getCurrentItem() == null) {
            return;
        }

        if (rawSlot == 45) {
            new CompositionBlockMenu(playerMenuUtility).open();
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
            return;
        }

        if (rawSlot < block.getLootTable().size()) {
            NeoMineLootItem item = block.getLootTable().get(rawSlot);
            playerMenuUtility.setItem(item);
            new LootItemMenu(playerMenuUtility).open();
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
        }
    }

    @Override
    public void setMenuItems() {
        for (int i = 0; i < block.getLootTable().size(); i++) {
            if (i >= 45) break;
            NeoMineLootItem lootItem = block.getLootTable().get(i);
            ItemStack itemStack = lootItem.getItem().clone();
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                List<String> itemLore = NeoMines.getInstance().getLangStringList("GUI.Loot-Table-List-Menu.Items.Drop-Item.Lore");
                itemLore.replaceAll(s -> s.replaceAll("%chance%", String.valueOf(lootItem.getChance())));
                itemMeta.lore(itemLore.stream().map(LegacyComponentSerializer.legacySection()::deserialize).toList());
                itemStack.setItemMeta(itemMeta);
            }
            inventory.setItem(i, itemStack);
        }

        for (int i = 45; i < getSlots(); i++) {
            inventory.setItem(i, ItemStackBuilder.buildItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }

        inventory.setItem(45, ItemStackBuilder.buildItem(Material.ARROW,
                NeoMines.getInstance().getLangString("GUI.Universal.Back-To-Block-Menu.Name"),
                NeoMines.getInstance().getLangStringList("GUI.Universal.Back-To-Block-Menu.Lore")));
    }
}
