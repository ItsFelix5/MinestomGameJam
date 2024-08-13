package reimplementation;

import gamejam.Utils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerDeathEvent;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.inventory.TransactionType;

import java.time.Duration;
import java.util.Arrays;

public class Drops {
    public static void implement() {
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();

        eventHandler.addListener(PickupItemEvent.class, event -> {
            Player player = (Player) event.getLivingEntity();
            if(player.getGameMode() == GameMode.SPECTATOR || !player.getInventory().addItemStack(event.getItemStack()))
                event.setCancelled(true);
        });

        eventHandler.addListener(ItemDropEvent.class, event -> {
            event.setCancelled(true);
        });

        eventHandler.addListener(PlayerDeathEvent.class, event -> {
            Arrays.stream(event.getPlayer().getInventory().getItemStacks()).filter(i -> i.material().name().contains("concrete")).forEach(i -> {
                event.getPlayer().getInventory().processItemStack(i, TransactionType.TAKE, TransactionOption.ALL);
                Pos pos = event.getEntity().getPosition();
                ItemEntity itemEntity = new ItemEntity(i);
                itemEntity.setInstance(event.getPlayer().getInstance(), Vec.fromPoint(pos).add(0, event.getPlayer().getEyeHeight() - .3f, 0));
                itemEntity.setPickupDelay(Duration.ofSeconds(2));
                itemEntity.setVelocity(new Vec(Utils.random.nextFloat(), .5f, Utils.random.nextFloat()));
            });
        });
    }
}
