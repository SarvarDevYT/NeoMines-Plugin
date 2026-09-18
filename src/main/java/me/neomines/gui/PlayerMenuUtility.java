package me.neomines.gui;

import me.neomines.mine.CuboidNeoMine;
import me.neomines.mine.components.NeoMineBlock;
import me.neomines.mine.components.NeoMineLootItem;
import org.bukkit.entity.Player;

public class PlayerMenuUtility {

    private Player owner;
    private CuboidNeoMine cuboidNeoMine;
    private NeoMineBlock block;
    private NeoMineLootItem item;
    private Menu menu;
    private int mineListMenuPage;

    public PlayerMenuUtility(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public CuboidNeoMine getMine() {
        return cuboidNeoMine;
    }

    public void setMine(CuboidNeoMine cuboidNeoMine) {
        this.cuboidNeoMine = cuboidNeoMine;
    }

    public NeoMineBlock getBlock() {
        return block;
    }

    public void setBlock(NeoMineBlock block) {
        this.block = block;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public int getMineListMenuPage() {
        return mineListMenuPage;
    }

    public void setMineListMenuPage(int mineListMenuPage) {
        this.mineListMenuPage = mineListMenuPage;
    }

    public NeoMineLootItem getItem() {
        return item;
    }

    public void setItem(NeoMineLootItem item) {
        this.item = item;
    }
}
