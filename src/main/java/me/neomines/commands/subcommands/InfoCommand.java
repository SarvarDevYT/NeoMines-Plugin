package me.neomines.commands.subcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.mine.components.NeoMineResetMode;
import me.neomines.schedulers.MineManager;
import me.neomines.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class InfoCommand implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (!sender.hasPermission("neomines.info")) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.No-Permission"));
            return true;
        }

        if (args.length == 2) {
            if (!MineManager.getInstance().getMineListNames().contains(args[1])) {
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Not-Exist"));
                return true;
            }

            CuboidNeoMine cuboidNeoMine = MineManager.getInstance().getMine(args[1]);

            // Header of the info
            sender.sendMessage(NeoMines.PREFIX + "§c" + args[1] + " §7shaxtasi ma'lumotlari:\n" +
                    "--------------------------------");

            // Displays the region
            TextComponent component = new TextComponent("Hududni yuklab bo'lmadi");
            if (cuboidNeoMine.getRegion() != null) {
                String[] strings = Utils.regionToArray(cuboidNeoMine.getRegion());
                String regionString = "§bHudud:" + "\n" +
                        "  §6Dunyo: §c" + strings[0] + "\n" +
                        "    §6p1:" + "\n" +
                        "      §7x1: §c" + strings[1] + "\n" +
                        "      §7y1: §c" + strings[2] + "\n" +
                        "      §7z1: §c" + strings[3] + "\n" +
                        "    §6p2:" + "\n" +
                        "      §7x2: §c" + strings[4] + "\n" +
                        "      §7y2: §c" + strings[5] + "\n" +
                        "      §7z2: §c" + strings[6];

                component = new TextComponent(regionString);
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§aTeleport bo'lish uchun bosing!")));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/neomines tp " + cuboidNeoMine.getName()));
            }
            sender.spigot().sendMessage(component);

            sender.sendMessage("§7--------------------------------");
            sender.sendMessage("§bBloklar tarkibi:");
            cuboidNeoMine.getBlocks().forEach(block -> sender.sendMessage("  " + ChatColor.GOLD + block.getBlockData().getAsString(true) + ChatColor.AQUA + " , " + ChatColor.RED + block.getChance() + "%"));

            sender.sendMessage("§7--------------------------------");
            sender.sendMessage(
                    "§bReset rejimi: §6" + cuboidNeoMine.getResetMode().name() +
                            "\n§bReset vaqti: §c" + cuboidNeoMine.getResetDelay() + " §7soniya." +
                            "\n§bReset foizi: §c" + cuboidNeoMine.getResetPercentage() + "%" +
                            "\n§bHavo almashtirish (Replace mode): §c" + cuboidNeoMine.isReplaceMode() +
                            "\n§bO'yinchilarni teleport qilish: §c" + cuboidNeoMine.isTeleportPlayers() +
                            "\n§bIshlay oladi: §c" + cuboidNeoMine.checkRunnable() +
                            "\n§bTo'xtatilgan: §c" + cuboidNeoMine.isStopped() +
                            "\n§bYangilanishgacha: §c" + (cuboidNeoMine.isRunnable() && !cuboidNeoMine.isStopped() ? cuboidNeoMine.getCountdown() + " §7soniya" : "Shaxta nofaol"));

            sender.sendMessage("§7--------------------------------");
            sender.sendMessage("§6Ogohlantirish sozlamalari:");

            sender.sendMessage("§6Ogohlantirish yoqilgan: §c" + cuboidNeoMine.isWarn()
                    + "\n§6Global ogohlantirish: §c" + cuboidNeoMine.isWarnGlobal() + "\n" +
                    "§6Actionbarda ogohlantirish: §c" + cuboidNeoMine.isWarnHotbar());

            sender.sendMessage("§7--------------------------------");
            sender.sendMessage("§6Xabarlar:");

            String prefixChop = NeoMines.PREFIX != null ? NeoMines.PREFIX.trim() : "";
            String warnMessage = org.bukkit.ChatColor.translateAlternateColorCodes('&', cuboidNeoMine.getWarnMessage()
                    .replaceAll("%nm%", prefixChop)
                    .replaceAll("%cm%", prefixChop)
                    .replaceAll("%mine%", cuboidNeoMine.getName()));
            Arrays.stream(warnMessage.split("/n")).forEach(sender::sendMessage);

            String resetMessage = org.bukkit.ChatColor.translateAlternateColorCodes('&', cuboidNeoMine.getResetMessage()
                    .replaceAll("%nm%", prefixChop)
                    .replaceAll("%cm%", prefixChop)
                    .replaceAll("%mine%", cuboidNeoMine.getName()));
            Arrays.stream(resetMessage.split("/n")).forEach(sender::sendMessage);

            String hotbarMessageTime = org.bukkit.ChatColor.translateAlternateColorCodes('&', cuboidNeoMine.getWarnHotbarMessage(NeoMineResetMode.TIME).replaceAll("%mine%", cuboidNeoMine.getName()));
            sender.sendMessage(hotbarMessageTime);

            String hotbarMessagePer = org.bukkit.ChatColor.translateAlternateColorCodes('&', cuboidNeoMine.getWarnHotbarMessage(NeoMineResetMode.PERCENTAGE).replaceAll("%mine%", cuboidNeoMine.getName()));
            sender.sendMessage(hotbarMessagePer);

            sender.sendMessage("");

            String warnSeconds = cuboidNeoMine.getWarnSeconds().toString();
            sender.sendMessage("§6Qolgan soniyalarda xabar: §e" + warnSeconds.substring(1, warnSeconds.length() - 1)
                    + "\n§6Ogohlantirish masofasi: §e" + cuboidNeoMine.getWarnDistance() + " blok");

        } else {
            sender.sendMessage(NeoMines.PREFIX + "/neomines info <mine>");
        }
        return true;
    }
}
