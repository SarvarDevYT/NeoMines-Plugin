package me.neomines.commands;

import me.neomines.NeoMines;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class NeoMinesHelpCommand implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!sender.hasPermission("neomines.help")) {
            sender.sendMessage("");
            sender.sendMessage("      §6§lN§be§ao§6§lM§bi§an§6e§bs");
            sender.sendMessage("§7Versiya: §a" + NeoMines.getInstance().getPluginVersion());
            sender.sendMessage("");
            return true;
        }

        sender.sendMessage(NeoMines.PREFIX + "Buyruqlar ro'yxati:");
        sender.sendMessage("");
        sender.sendMessage(
                "§b/nm §agui §6(mine) §7Boshqaruv menyusini ochadi.\n" +
                "§b/nm §alist          §7Barcha shaxtalar ro'yxatini ko'rsatadi.\n" +
                "§b/nm §ainfo §6<mine> §7Shaxta haqida to'liq ma'lumotni ko'rsatadi.\n" +
                "§b/nm §acreate §6<mine> §7Yangi shaxta yaratadi.\n" +
                "§b/nm §adelete §6<mine> §7Shaxtani o'chiradi.\n" +
                "§b/nm §aset §6<mine> [blok] (%) §7Shaxtaga blok va uning foizini o'rnatadi.\n" +
                "§b/nm §aunset §6<mine> (blok) §7Shaxtadan blokni olib tashlaydi.\n" +
                "§b/nm §asetdelay §6<mine> [vaqt] §7Reset vaqtini soniyalarda o'rnatadi.\n" +
                "§b/nm §aresetmode §6<mine> [rejim] §7Reset rejimini o'rnatadi (TIME/PERCENTAGE/TIME_PERCENTAGE).\n" +
                "§b/nm §aresetpercentage §6<mine> [%] §7Reset foizini o'rnatadi.\n" +
                "§b/nm §areset §6<mine> §7Shaxtani qo'lda zudlik bilan yangilaydi.\n" +
                "§b/nm §aflag §6<mine> <flag> §7Shaxta flagini sozlaydi.\n" +
                "§b/nm §astart §6<mine> §7Shaxtani ishga tushiradi.\n" +
                "§b/nm §astop §6<mine> §7Shaxtani to'xtatadi.\n" +
                "§b/nm §astarttasks            §7Barcha shaxtalar taymerlarini boshlaydi.\n" +
                "§b/nm §astoptasks             §7Barcha shaxtalar taymerlarini to'xtatadi.\n" +
                "§b/nm §atp §6<mine> §7Shaxtaga teleportatsiya qiladi.\n" +
                "§b/nm §asettp §6<mine> §7Shaxtaga teleport nuqtasini o'rnatadi.\n" +
                "§b/nm §asetresettp §6<mine> §7Reset paytida o'yinchilar otiladigan nuqtani belgilaydi.\n" +
                "§b/nm §aredefine §6<mine> §7Shaxta hududini yangi tanlovga o'tkazadi.\n" +
                "§b/nm §async                  §7Barcha shaxtalarni bir vaqtda sinxron yangilaydi.\n" +
                "§b/nm §areload [tur]         §7Shaxtalar, config, xabarlar yoki parametrlarni qayta yuklaydi.");

        return true;
    }
}
