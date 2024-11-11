package net.azisaba.userworldedit.commands;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import net.azisaba.userworldedit.util.BlockVectorUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LineCommand extends PlayerTabExecutor {
    private static final Set<String> MATERIAL_NAMES = Stream.of(Material.values())
            .filter(Material::isBlock)
            .map(Enum::name)
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
    private final Plugin plugin;
    final Economy economy;
    final int destroyCost;
    private final int maxBlocks;

    public LineCommand(@NotNull Plugin plugin, @NotNull Economy economy, int destroyCost, int maxBlocks) {
        this.plugin = Objects.requireNonNull(plugin);
        this.economy = Objects.requireNonNull(economy);
        this.destroyCost = destroyCost;
        this.maxBlocks = maxBlocks;
    }

    public void onCommand(@NotNull Player player, String[] args) {
        Region region = getRegion(player);
        if (region == null) return;
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "/line <materials...> [radius: number] [filled: boolean]");
            return;
        }
        String[] materialsString = args[0].split(",");
        List<Material> materials = Arrays.stream(materialsString).map(s -> Material.valueOf(s.toUpperCase())).collect(Collectors.toList());
        List<BlockVector3> vectors;
        if (region instanceof CuboidRegion) {
            CuboidRegion cuboidRegion = (CuboidRegion) region;
            vectors = ImmutableList.of(cuboidRegion.getPos1(), cuboidRegion.getPos2());
        } else {
            ConvexPolyhedralRegion convexRegion = (ConvexPolyhedralRegion) region;
            vectors = ImmutableList.copyOf(convexRegion.getVertices());
        }
        int radius = 0;
        if (args.length > 1) {
            try {
                radius = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "半径(太さ)は整数で指定してください。");
                return;
            }
        }
        boolean filled = args.length > 2 && args[2].equalsIgnoreCase("true");
        Set<BlockVector3> vset = BlockVectorUtil.drawLine(vectors, radius, filled);
        // check world
        if (!BukkitAdapter.adapt(player.getWorld()).equals(region.getWorld())) {
            player.sendMessage(ChatColor.RED + "選択範囲が異なるワールドになっています。");
            return;
        }
        if (vset.size() > maxBlocks) {
            player.sendMessage(ChatColor.RED + "範囲が広すぎます。一度に" + maxBlocks + "ブロックまで置き換え可能です。");
        } else {
            new SetCommandRunnable(economy, destroyCost, vset.iterator(), materials, player).runTaskTimer(this.plugin, 0L, 1L);
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
