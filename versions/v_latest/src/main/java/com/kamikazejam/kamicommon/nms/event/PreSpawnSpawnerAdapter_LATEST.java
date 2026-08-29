package com.kamikazejam.kamicommon.nms.event;

import com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent;
import com.kamikazejam.kamicommon.nms.abstraction.event.PreSpawnSpawnerEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

// 1.13+ added the PreSpawnerSpawnEvent to Paper
/**
 * The 26.x twin of {@code PreSpawnSpawnerAdapter_1_13_R1} in {@code versions/v1_13_R1}, and the only reason it exists is to be
 * compiled.
 * <p>
 * Nothing dispatches here. The ladder sends every server, 26.x included, to the v1_13_R1 copy, because
 * under this project's convention a class lives in the module named for the FIRST version it works
 * on. That is correct for dispatch and bad for early warning: it means the implementation is only
 * ever compiled against an old dev bundle, so an API this code uses could be removed in 26.x and the
 * build would not notice until a server did.
 * </p><p>
 * This copy closes that. It compiles against {@code highestPaperDep}, so bumping that version
 * compile-checks this capability against bleeding-edge Paper. If it stops compiling, that IS the
 * finding: fix it here and in the twin, and add a ladder branch if the two must now differ.
 * </p>
 */
public class PreSpawnSpawnerAdapter_LATEST implements Listener {
    @EventHandler
    public void onSpawn(PreSpawnerSpawnEvent e) {
        PreSpawnSpawnerEvent preEvent = new PreSpawnSpawnerEvent(e.getSpawnerLocation().getBlock(), e.getType(), e.getSpawnLocation());
        preEvent.setCancelled(e.isCancelled());
        Bukkit.getServer().getPluginManager().callEvent(preEvent);
        e.setCancelled(preEvent.isCancelled());
    }
}
