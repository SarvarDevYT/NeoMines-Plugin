package me.neomines.mine.components;

import me.neomines.NeoMines;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

@SerializableAs("NeoMineLootItem")
public class NeoMineLootItem implements ConfigurationSerializable {

    private final Random random = new Random();
    private ItemStack item;
    private double chance;
    private boolean fortune;

    public NeoMineLootItem(ItemStack item) {
        this.item = item;
        this.chance = 100d;
        this.fortune = false;
    }

    public NeoMineLootItem(ItemStack item, double chance) {
        this(item, chance, false);
    }

    public NeoMineLootItem(ItemStack item, double chance, boolean fortune) {
        this.item = item;
        if (chance < 0 || chance > 100) {
            throw new IllegalArgumentException(NeoMines.getInstance().getLangString("Error-Messages.Mine.Invalid-Chance"));
        }
        this.chance = Math.round(chance * 1000) / 1000d;
        this.fortune = fortune;
    }

    @SuppressWarnings("unchecked")
    public static NeoMineLootItem deserialize(Map<String, Object> serializedLootItem) {
        ItemStack item = null;
        if (serializedLootItem.containsKey("item")) {
            item = ItemStack.deserialize((Map<String, Object>) serializedLootItem.get("item"));
        }

        double chance = 100d;
        if (serializedLootItem.containsKey("chance")) {
            chance = ((Number) serializedLootItem.get("chance")).doubleValue();
        }

        boolean fortune = false;
        if (serializedLootItem.containsKey("fortune")) {
            fortune = (boolean) serializedLootItem.get("fortune");
        }

        return new NeoMineLootItem(item, chance, fortune);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serializedLootItem = new LinkedHashMap<>();
        serializedLootItem.put("item", item.serialize());
        serializedLootItem.put("chance", chance);
        serializedLootItem.put("fortune", fortune);
        return serializedLootItem;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getDropCount(int fortuneLevel) {
        ItemStack fortuneItem = item.clone();
        if (fortuneLevel > 0) {
            int multiplier = random.nextInt(fortuneLevel + 2) - 1;
            if (multiplier < 0) multiplier = 0;
            return fortuneItem.getAmount() * (multiplier + 1);
        }
        return fortuneItem.getAmount();
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        if (chance < 0 || chance > 100) {
            throw new IllegalArgumentException(NeoMines.getInstance().getLangString("Error-Messages.Mine.Invalid-Chance"));
        }
        this.chance = Math.round(chance * 1000) / 1000d;
    }

    public boolean isFortune() {
        return fortune;
    }

    public void setFortune(boolean fortune) {
        this.fortune = fortune;
    }
}
