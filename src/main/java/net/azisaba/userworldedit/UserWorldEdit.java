package net.azisaba.userworldedit;

import java.util.NoSuchElementException;
import java.util.Objects;

import net.azisaba.userworldedit.commands.LineCommand;
import net.azisaba.userworldedit.commands.SetCommand;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class UserWorldEdit extends JavaPlugin {
    private Economy economy;

    @NotNull
    public static UserWorldEdit getInstance() {
        return getPlugin(UserWorldEdit.class);
    }

    public void onEnable() {
        this.saveDefaultConfig();
        RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (provider == null) {
            throw new NoSuchElementException("Economy not found");
        }
        this.economy = provider.getProvider();
        Objects.requireNonNull(this.getCommand("set"))
                .setExecutor(new SetCommand(this, economy, getConfig().getInt("cost.destroy"), getConfig().getInt("max-blocks", 400)));
        Objects.requireNonNull(this.getCommand("line"))
                .setExecutor(new LineCommand(this, economy, getConfig().getInt("cost.destroy"), getConfig().getInt("max-blocks", 400)));
    }

    @NotNull
    public Economy getEconomy() {
        return Objects.requireNonNull(this.economy);
    }
}
