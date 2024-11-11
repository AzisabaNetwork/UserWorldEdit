package net.azisaba.userworldedit.util;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public class EventUtil {
    /**
     * Call event
     * @param event Event
     * @return true if event is not cancelled, if cancellable. Otherwise, return true.
     */
    public static boolean callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
        if (event instanceof org.bukkit.event.Cancellable) {
            return !((org.bukkit.event.Cancellable) event).isCancelled();
        }
        return true;
    }
}
