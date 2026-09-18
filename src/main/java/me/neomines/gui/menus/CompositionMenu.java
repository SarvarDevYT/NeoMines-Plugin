package me.neomines.gui.menus;

import me.neomines.NeoMines;
import me.neomines.gui.PaginatedMenu;
import me.neomines.gui.PlayerMenuUtility;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.mine.components.NeoMineBlock;
import me.neomines.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class CompositionMenu extends PaginatedMenu {

    private final CuboidNeoMine mine;
    private final NeoMines plugin;

    public CompositionMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        playerMenuUtility.setMenu(this);
        this.mine = playerMenuUtility.getMine();
        this.plugin = NeoMines.getInstance();
    }

    @Override
    public String getMenuName() {
        return plugin.getLangString("GUI.Composition-Menu.Title").replaceAll("%chance%", String.valueOf(mine.getCompositionChance()));
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null) {
            return;
        }

        int slot = event.getRawSlot();
        Player player = (Player) event.getWhoClicked();

        switch (slot) {
            case 49:
                new MineMenu(playerMenuUtility).open();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                return;
            case 48:
                if (page == 0) {
                    player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.GUI.First-Page"));
                } else {
                    page = page - 1;
                    super.open();
                }
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                return;
            case 50:
                if (!((index + 1) >= mine.getBlocks().size())) {
                    page = page + 1;
                    super.open();
                } else {
                    player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.GUI.Last-Page"));
                }
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                return;
        }

        if (event.getClickedInventory() == player.getOpenInventory().getTopInventory()) {
            if (!(slot >= 10 && slot <= 34) || (slot + 1) % 9 == 0 || slot % 9 == 0) return;

            int row = slot / 9;
            int itemIndex = getMaxItemsPerPage() * page + (slot - (8 + 2 * row));
            if (itemIndex >= mine.getBlocks().size()) return;

            if (event.isRightClick()) {
                mine.removeBlock(itemIndex);
                mine.save();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                updateMenus();
            } else {
                playerMenuUtility.setBlock(mine.getBlocks().get(itemIndex));
                new CompositionBlockMenu(playerMenuUtility).open();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
            }
            return;
        }

        Material mat = event.getCurrentItem().getType();
        switch (mat) {
            case WATER_BUCKET:
                mat = Material.WATER;
                break;
            case LAVA_BUCKET:
                mat = Material.LAVA;
                break;
        }

        if (!mat.isBlock()) {
            player.sendMessage(plugin.getLangString("Error-Messages.Mine.Material-Not-Solid"));
            return;
        }

        BlockData blockData = mat.createBlockData();
        try {
            mine.addBlock(new NeoMineBlock(blockData, 0));
            mine.save();
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
            updateMenus();
        } catch (IllegalArgumentException ex) {
            player.sendMessage(NeoMines.PREFIX + ex.getMessage());
        }
    }

    @Override
    public void setMenuItems() {
        addMenuBorder();

        ArrayList<NeoMineBlock> blocks = new ArrayList<>(mine.getBlocks());
        if (!blocks.isEmpty()) {
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= blocks.size()) break;
                if (blocks.get(index) != null) {
                    NeoMineBlock neoBlock = blocks.get(index);
                    BlockData blockData = neoBlock.getBlockData();
                    Material material = blockData.getMaterial();
                    if (!material.isItem()) {
                        material = Material.WRITTEN_BOOK;
                    }

                    List<String> lore = new ArrayList<>();
                    String blockDataStr = blockData.getAsString(true);
                    String subStr = blockDataStr.length() > 10 + blockData.getMaterial().name().length()
                            ? blockDataStr.substring(10 + blockData.getMaterial().name().length())
                            : blockDataStr;

                    for (String s : plugin.getLangStringList("GUI.Composition-Menu.Items.Composition-Block.Lore")) {
                        lore.add(s.replaceAll("%blockdata%", subStr).replaceAll("%chance%", String.valueOf(neoBlock.getChance())));
                    }

                    inventory.addItem(ItemStackBuilder.buildItem(material, material == Material.WRITTEN_BOOK ? ChatColor.WHITE + blockData.getMaterial().name() : "", lore));
                }
            }
        }

        inventory.setItem(53, ItemStackBuilder.buildItem(Material.OAK_SIGN,
                plugin.getLangString("GUI.Composition-Menu.Items.Info.Name"),
                plugin.getLangStringList("GUI.Composition-Menu.Items.Info.Lore")));
    }
}
