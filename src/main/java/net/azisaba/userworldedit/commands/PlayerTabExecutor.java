package net.azisaba.userworldedit.commands;

import java.util.Collections;
import java.util.List;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import net.azisaba.userworldedit.util.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerTabExecutor implements TabExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "このコマンドはコンソールから実行できません。");
        } else {
            this.onCommand((Player)sender, args);
        }
        return true;
    }

    public abstract void onCommand(@NotNull Player var1, String[] var2);

    protected @Nullable Region getRegion(@NotNull Player player) {
        try {
            return WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player)).getSelection();
        } catch (IncompleteRegionException e) {
            MessageUtil.invalidSelection(player);
            return null;
        }
    }

    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return !(sender instanceof Player) ? Collections.emptyList() : this.onTabComplete((Player)sender, args);
    }

    public List<String> onTabComplete(@NotNull Player player, String[] args) {
        return Collections.emptyList();
    }
}
