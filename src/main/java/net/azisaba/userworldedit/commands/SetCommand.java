package net.azisaba.userworldedit.commands;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class SetCommand extends PlayerTabExecutor {
    private static final Set<String> MATERIAL_NAMES = Stream.of(Material.values())
            .filter(Material::isBlock)
            .map(Enum::name)
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
    private final Plugin plugin;
    final Economy economy;
    final int destroyCost;
    private final int maxBlocks;

    public SetCommand(@NotNull Plugin plugin, @NotNull Economy economy, int destroyCost, int maxBlocks) {
        this.plugin = Objects.requireNonNull(plugin);
        this.economy = Objects.requireNonNull(economy);
        this.destroyCost = destroyCost;
        this.maxBlocks = maxBlocks;
    }

    public void onCommand(@NotNull Player player, String[] args) {
        Region region = getRegion(player);
        if (region == null) return;
        // check world
        if (!BukkitAdapter.adapt(player.getWorld()).equals(region.getWorld())) {
            player.sendMessage(ChatColor.RED + "選択範囲が異なるワールドになっています。");
            return;
        }
        if (region.getVolume() > maxBlocks) {
            player.sendMessage(ChatColor.RED + "範囲が広すぎます。一度に" + maxBlocks + "ブロックまで選択可能です。");
        } else {
            String[] materialsString = args[0].split(",");
            List<Material> materials = Arrays.stream(materialsString).map(s -> Material.valueOf(s.toUpperCase())).collect(Collectors.toList());
            Iterator<BlockVector3> iterator = region.iterator();
            new SetCommandRunnable(economy, destroyCost, iterator, materials, player).runTaskTimer(this.plugin, 0L, 1L);
        }
    }

    public List<String> onTabComplete(@NotNull Player player, String[] args) {
        if (args.length == 1) {
            String[] split = args[0].split(",");
            String last = split[split.length - 1];
            String argWithoutLast = args[0].substring(0, args[0].length() - last.length());
            return MATERIAL_NAMES.stream().filter(s -> s.toLowerCase().startsWith(last)).map(s -> argWithoutLast + s).collect(Collectors.toList());
        } else {
            return super.onTabComplete(player, args);
        }
    }
}
