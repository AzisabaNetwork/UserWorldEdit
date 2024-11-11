package net.azisaba.userworldedit.commands;

import com.sk89q.worldedit.math.BlockVector3;
import net.azisaba.userworldedit.util.EventUtil;
import net.azisaba.userworldedit.util.ItemUtil;
import net.azisaba.userworldedit.util.MessageUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.List;

public class SetCommandRunnable extends BukkitRunnable {
    private final Economy economy;
    private final int destroyCost;
    private final Iterator<BlockVector3> iterator;
    private final List<Material> materials;
    private final Player player;
    private final World world;
    private long affected;

    public SetCommandRunnable(Economy economy, int destroyCost, Iterator<BlockVector3> iterator, List<Material> materials, Player player) {
        this.economy = economy;
        this.destroyCost = destroyCost;
        this.iterator = iterator;
        this.materials = materials;
        this.player = player;
        this.world = player.getWorld();
    }

    public void run() {
        if (this.iterator.hasNext()) {
            BlockVector3 pos = this.iterator.next();
            Block block = world.getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
            Material material = this.materials.size() == 1
                    ? this.materials.get(0)
                    : this.materials.get((int)((double)this.materials.size() * Math.random()));
            if (!material.isAir()) {
                if (!ItemUtil.playerHasMaterial(this.player, material)) {
                    MessageUtil.missingMaterial(this.player, material);
                    this.stop();
                    return;
                }

                if (block.getType() != material) {
                    if (!EventUtil.callEvent(new BlockBreakEvent(block, this.player))) {
                        this.stop();
                        return;
                    }

                    ItemUtil.removeItem(this.player, material);
                    block.breakNaturally();
                }
            }

            if (material.isAir()) {
                if (!economy.has(this.player, destroyCost)) {
                    MessageUtil.notEnoughMoney(this.player, destroyCost);
                    this.stop();
                    return;
                }

                if (!block.getType().isAir()) {
                    if (!EventUtil.callEvent(new BlockBreakEvent(block, this.player))) {
                        this.stop();
                        return;
                    }

                    economy.withdrawPlayer(this.player, destroyCost);
                    block.breakNaturally();
                }
            }

            BlockState originalState = block.getState();
            block.setType(material);
            BlockPlaceEvent event = new BlockPlaceEvent(block, block.getState(), block, new ItemStack(material), this.player, true, EquipmentSlot.HAND);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.canBuild() || event.isCancelled()) {
                block.setType(originalState.getType());
                block.setBlockData(originalState.getBlockData());
                this.stop();
                return;
            }

            block.setType(material);
            this.affected++;
        } else {
            this.stop();
        }
    }

    private void stop() {
        MessageUtil.affectedBlocks(this.player, this.affected);
        this.cancel();
    }
}
