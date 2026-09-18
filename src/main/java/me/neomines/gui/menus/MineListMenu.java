package me.neomines.gui.menus;

import me.neomines.NeoMines;
import me.neomines.gui.PaginatedMenu;
import me.neomines.gui.PlayerMenuUtility;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.mine.components.NeoMineBlock;
import me.neomines.schedulers.MineManager;
import me.neomines.utils.ItemStackBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
    @SuppressWarnings("null")
    public void handleMenu(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType().equals(Material.GRAY_STAINED_GLASS_PANE) || Objects.equals(event.getClickedInventory(), event.getWhoClicked().getInventory())) {
            return;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
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
                    String displayName = "";
                    Component comp = itemMeta.displayName();
                    if (comp != null) {
                        displayName = PlainTextComponentSerializer.plainText().serialize(comp).trim();
                    }
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
    @SuppressWarnings("null")
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
                        NeoMineBlock maxBlock = cuboidNeoMine.getBlocks().stream()
                                .filter(Objects::nonNull)
                                .max(Comparator.comparingDouble(b -> b.getChance()))
                                .orElse(null);
                        if (maxBlock != null) {
                            material = maxBlock.getBlockData().getMaterial();
                        }
                    }

                    if (!material.isSolid()) {
                        material = Material.WRITTEN_BOOK;
                    }

                    ArrayList<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add("§bTarkibi:");
                    int miniIndex = 1;
                    for (NeoMineBlock block : cuboidNeoMine.getBlocks()) {
                        lore.add("§c  " + miniIndex + ". §6" + block.getBlockData().getMaterial() + ": §c" + block.getChance() + "%");
                        miniIndex++;
                    }
                    lore.add("§bKutish vaqti: §c" + cuboidNeoMine.getResetDelay());
                    lore.add("§bReset foizi: §c" + cuboidNeoMine.getResetPercentage() + "%");
                    lore.add("§bReplace rejimi: §c" + cuboidNeoMine.isReplaceMode());
                    lore.add("§bOgohlantirish: §c" + cuboidNeoMine.isWarn());
                    lore.add("§b  Hotbarda: §c" + cuboidNeoMine.isWarnHotbar());
                    lore.add("§b  Global: §c" + cuboidNeoMine.isWarnGlobal());
                    String warnSeconds = cuboidNeoMine.getWarnSeconds().toString();
                    lore.add("§b  Ogohlantirish soniyalari: §c" + warnSeconds.substring(1, warnSeconds.length() - 1));
                    lore.add("§b  Ogohlantirish masofasi: §c" + cuboidNeoMine.getWarnDistance());
                    lore.add("§bTo'xtatilgan: §c" + cuboidNeoMine.isStopped());

                    ItemStack mineItem = ItemStackBuilder.buildItem(material,
                            !cuboidNeoMine.isStopped() && cuboidNeoMine.isRunnable()
                                    ? "§a" + cuboidNeoMine.getName()
                                    : "§c" + cuboidNeoMine.getName(),
                            lore);

                    inventory.addItem(mineItem);
                }
            }
        }
    }
}
