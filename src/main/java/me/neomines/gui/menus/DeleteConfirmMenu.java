package me.neomines.gui.menus;

import me.neomines.NeoMines;
import me.neomines.gui.Menu;
import me.neomines.gui.PlayerMenuUtility;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.schedulers.MineManager;
import me.neomines.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;

public class DeleteConfirmMenu extends Menu {

    private final CuboidNeoMine mine;

    public DeleteConfirmMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        this.mine = playerMenuUtility.getMine();
    }

    @Override
    public String getMenuName() {
        return mine.getName() + " shaxtasini o'chirish?";
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        switch (event.getRawSlot()) {
            case 0:
                File file = new File(NeoMines.getInstance().getDataFolder() + "/mines", mine.getName() + ".yml");
                if (file.exists()) {
                    file.delete();
                }
                MineManager.getInstance().getMines().remove(mine);
                player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Delete").replaceAll("%mine%", mine.getName()));
                new MineListMenu(NeoMines.getPlayerMenuUtility(player)).open();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                updateMenus();
                break;
            case 8:
                new MineMenu(playerMenuUtility, playerMenuUtility.getMine()).open();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 1F);
                break;
        }
    }

    @Override
    public void setMenuItems() {
        inventory.setItem(0, ItemStackBuilder.buildItem(Material.GREEN_STAINED_GLASS_PANE, "§a§lTASDIQLASH", "", "§cShaxtani butunlay o'chirib tashlaydi"));
        inventory.setItem(8, ItemStackBuilder.buildItem(Material.RED_STAINED_GLASS_PANE, "§4§lBEKOR QILISH", "", "§aJarayonni bekor qiladi"));
        setFillerGlass();
    }
}
