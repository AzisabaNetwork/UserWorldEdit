package net.azisaba.userworldedit.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.storageBox.utils.StorageBox;

public class ItemUtil {
    public static boolean playerHasMaterial(@NotNull Player player, @NotNull Material material) {
        ItemStack vanillaStack = new ItemStack(material);
        for (ItemStack stack : player.getInventory()) {
            if (stack != null) {
                if (stack.isSimilar(vanillaStack)) {
                    return true;
                }

                StorageBox box = StorageBox.getStorageBox(stack);
                if (box != null && box.isComponentItemStackSimilar(vanillaStack) && box.getAmount() > 0L) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean removeItem(@NotNull Player player, @NotNull Material material) {
        ItemStack vanillaStack = new ItemStack(material);

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack != null) {
                if (stack.isSimilar(vanillaStack)) {
                    stack.setAmount(stack.getAmount() - 1);
                    player.getInventory().setItem(i, stack);
                    return true;
                }

                StorageBox box = StorageBox.getStorageBox(stack);
                if (box != null && box.isComponentItemStackSimilar(vanillaStack) && box.getAmount() > 0L) {
                    box.setAmount(box.getAmount() - 1L);
                    player.getInventory().setItem(i, box.getItemStack());
                    return true;
                }
            }
        }
        return false;
    }
}
