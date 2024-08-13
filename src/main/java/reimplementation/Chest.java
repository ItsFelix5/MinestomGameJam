package reimplementation;

import gamejam.Utils;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class Chest implements BlockHandler {
    public static final Tag<List<ItemStack>> ITEMS = Tag.ItemStack("items").list();
    private static final Tag<@Nullable Inventory> INVENTORY = Tag.Transient("inventory");

    public static void implement() {
        MinecraftServer.getBlockManager().registerHandler("minecraft:chest", Chest::new);
    }

    @Override
    public void onDestroy(@NotNull Destroy destroy) {
        destroy.getInstance().scheduler().scheduleNextTick(() -> {
            Point pos = destroy.getBlockPosition();
            Block block = destroy.getBlock();
            if (destroy.getInstance().getBlock(pos).compare(block)) return;

            Inventory inv = block.getTag(INVENTORY);
            if (inv != null) inv.getViewers().forEach(inv::removeViewer);

            if (!block.hasTag(ITEMS)) return;
            for (ItemStack item : block.getTag(ITEMS)) {
                if (item.isAir()) continue;

                ItemEntity entity = new ItemEntity(item);
                entity.setInstance(destroy.getInstance());
                entity.teleport(new Pos(pos.x(), pos.y() + .5f, pos.z()));
                entity.setVelocity(new Vec(Utils.random.nextDouble() * 0.2 - 0.1, 0.2, Utils.random.nextDouble() * 0.2 - 0.1));
            }
        });
    }

    @Override
    public boolean onInteract(@NotNull Interaction interaction) {
        Player player = interaction.getPlayer();
        if (player.isSneaking()) return true;
        Block block = interaction.getBlock();
        Instance instance = interaction.getInstance();
        Point pos = interaction.getBlockPosition();

        Inventory inv = block.getTag(INVENTORY);
        if (inv == null) {
            inv = new BlockInventory(InventoryType.CHEST_3_ROW, Component.text("Chest"), instance, pos);
            if (block.hasTag(ITEMS)) inv.addItemStacks(block.getTag(ITEMS), TransactionOption.ALL);
            instance.setBlock(pos, block.withTag(INVENTORY, inv));
        }
        player.openInventory(inv);
        return false;
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("minecraft:chest");
    }

    private static class BlockInventory extends Inventory {
        private final Instance instance;
        private final Point position;

        public BlockInventory(@NotNull InventoryType inventoryType, @NotNull Component title, Instance instance, Point position) {
            super(inventoryType, title);
            this.instance = instance;
            this.position = position;
        }

        @Override
        public boolean removeViewer(@NotNull Player player) {
            if (getViewers().size() < 2) instance.setBlock(position, instance.getBlock(position).withTag(INVENTORY, null).withTag(ITEMS,
                    Arrays.stream(getItemStacks()).toList()));// this puts everything at the start but idk how to fix
            return super.removeViewer(player);
        }
    }
}