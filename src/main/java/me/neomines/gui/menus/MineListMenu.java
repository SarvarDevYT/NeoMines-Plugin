package me.neomines.gui.menus;

import me.neomines.NeoMines;
import me.neomines.gui.PaginatedMenu;
import me.neomines.gui.PlayerMenuUtility;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.mine.components.NeoMineBlock;
import me.neomines.schedulers.MineManager;
import me.neomines.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MineListMenu extends PaginatedMenu {

    public MineListMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        playerMenuUtility.setMenu(this);
    }

    public MineListMenu(PlayerMenuUtility playerMenuUtility, int page) {
        super(playerMenuUtility);
        playerMenuUtility.setMenu(this);
        this.page = page;
    }

    @Override
    public String getMenuName() {
        return NeoMines.getInstance().getLangString("GUI.Mine-List-Menu.Title");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType().equals(Material.GRAY_STAINED_GLASS_PANE) || Objects.equals(event.getClickedInventory(), event.getWhoClicked().getInventory())) {
            return;
        }

        if (itemStack.getItemMeta() == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        List<CuboidNeoMine> cuboidNeoMines = MineManager.getInstance().getMines();

        if (event.getCurrentItem().getType() != Material.AIR && !event.getCurrentItem().getType().equals(Material.GRAY_STAINED_GLASS_PANE)) {
            switch (event.getRawSlot()) {
                case 48:
                    if (page == 0) {
                        player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.GUI.First-Page"));
                    } else {
                        page = page - 1;
                        playerMenuUtility.setMineListMenuPage(playerMenuUtility.getMineListMenuPage() - 1);
                        super.open();
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                    }
                    break;
                case 49:
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                    break;
                case 50:
                    if (!((index + 1) >= cuboidNeoMines.size())) {
                        page = page + 1;
                        playerMenuUtility.setMineListMenuPage(playerMenuUtility.getMineListMenuPage() + 1);
                        super.open();
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                    } else {
                        player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.GUI.Last-Page"));
                    }
                    break;
                default:
                    String displayName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
                    CuboidNeoMine targetMine = MineManager.getInstance().getMine(displayName);
                    if (targetMine != null) {
                        new MineMenu(playerMenuUtility, targetMine).open();
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                    } else {
                        player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.GUI.Mine-Doesnt-Exist"));
                    }
            }
        }
    }

    @Override
    public void setMenuItems() {
        addMenuBorder();

        List<CuboidNeoMine> cuboidNeoMines = MineManager.getInstance().getMines();
        if (cuboidNeoMines != null && !cuboidNeoMines.isEmpty()) {
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= cuboidNeoMines.size()) break;
                if (cuboidNeoMines.get(index) != null) {
                    CuboidNeoMine cuboidNeoMine = cuboidNeoMines.get(index);

                    Material material = Material.STICK;
                    if (!cuboidNeoMine.getBlocks().isEmpty()) {
                        NeoMineBlock maxBlock = cuboidNeoMine.getBlocks().stream().max(Comparator.comparingDouble(NeoMineBlock::getChance)).orElse(null);
                        if (maxBlock != null) {
                            material = maxBlock.getBlockData().getMaterial();
                        }
                    }

                    if (!material.isSolid()) {
                        material = Material.WRITTEN_BOOK;
                    }

                    ArrayList<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add(ChatColor.AQUA + "Composition:");
                    int miniIndex = 1;
                    for (NeoMineBlock block : cuboidNeoMine.getBlocks()) {
                        lore.add(ChatColor.RED + "  " + miniIndex + ". " + ChatColor.GOLD + block.getBlockData().getMaterial() + ": " + ChatColor.RED + block.getChance() + "%");
                        miniIndex++;
                    }
                    lore.add(ChatColor.AQUA + "Delay: " + ChatColor.RED + cuboidNeoMine.getResetDelay());
                    lore.add(ChatColor.AQUA + "Reset percentage: " + ChatColor.RED + cuboidNeoMine.getResetPercentage() + "%");
                    lore.add(ChatColor.AQUA + "Replace mode: " + ChatColor.RED + cuboidNeoMine.isReplaceMode());
                    lore.add(ChatColor.AQUA + "Warns: " + ChatColor.RED + cuboidNeoMine.isWarn());
                    lore.add(ChatColor.AQUA + "  Warns hotbar: " + ChatColor.RED + cuboidNeoMine.isWarnHotbar());
                    lore.add(ChatColor.AQUA + "  Warns globally: " + ChatColor.RED + cuboidNeoMine.isWarnGlobal());
                    String warnSeconds = cuboidNeoMine.getWarnSeconds().toString();
                    lore.add(ChatColor.AQUA + "  Warn seconds: " + ChatColor.RED + warnSeconds.substring(1, warnSeconds.length() - 1));
                    lore.add(ChatColor.AQUA + "  Warn distance: " + ChatColor.RED + cuboidNeoMine.getWarnDistance());
                    lore.add(ChatColor.AQUA + "Is stopped: " + ChatColor.RED + cuboidNeoMine.isStopped());

                    ItemStack mineItem = ItemStackBuilder.buildItem(material,
                            !cuboidNeoMine.isStopped() && cuboidNeoMine.isRunnable()
                                    ? ChatColor.GREEN + cuboidNeoMine.getName()
                                    : ChatColor.RED + cuboidNeoMine.getName(),
                            lore);

                    inventory.addItem(mineItem);
                }
            }
        }
    }
}
