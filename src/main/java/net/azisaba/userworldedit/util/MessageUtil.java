package net.azisaba.userworldedit.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MessageUtil {
    public static void invalidSelection(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "選択範囲が無効です。");
    }

    public static void missingMaterial(@NotNull CommandSender sender, @NotNull Material material) {
        sender.sendMessage(ChatColor.RED + material.name() + "が足りません。");
    }

    public static void notEnoughMoney(@NotNull CommandSender sender, double money) {
        sender.sendMessage(ChatColor.RED + "お金が足りません。" + money + "円必要です。");
    }

    public static void affectedBlocks(@NotNull CommandSender sender, long blocks) {
        sender.sendMessage("" + ChatColor.YELLOW + blocks + "ブロックを置き換えました。");
    }
}
