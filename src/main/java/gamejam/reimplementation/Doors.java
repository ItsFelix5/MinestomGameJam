package gamejam.reimplementation;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class Doors extends BlockPlacementRule {
    protected Doors(@NotNull Block block) {
        super(block);
    }

    public static void implement() {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockInteractEvent.class, event -> {
            Instance instance = event.getInstance();
            Block block = event.getBlock();
            if (event.getPlayer().isSneaking() || !block.name().contains("door") && !block.name().contains("gate")) return;
            String open = Objects.equals(block.getProperty("open"), "true") ? "false" : "true";
            instance.setBlock(event.getBlockPosition(), block.withProperty("open", open));
            if (block.name().contains("_door")) {
                Point other = event.getBlockPosition().add(0, block.getProperty("half").equals("lower") ? 1 : -1, 0);
                instance.setBlock(other, instance.getBlock(other).withProperty("open", open));
            }
            event.setBlockingItemUse(true);
        });
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockBreakEvent.class, event -> {
            if (event.getBlock().name().contains("_door")) event.getInstance().setBlock(event.getBlockPosition().add(0, event.getBlock().getProperty(
                            "half").equals("lower") ? 1 : -1, 0),
                    Block.AIR);
        });
        List.of(
                Block.OAK_DOOR,
                Block.SPRUCE_DOOR,
                Block.BIRCH_DOOR,
                Block.JUNGLE_DOOR,
                Block.ACACIA_DOOR,
                Block.DARK_OAK_DOOR,
                Block.CRIMSON_DOOR,
                Block.WARPED_DOOR,
                Block.IRON_DOOR,
                Block.BAMBOO_DOOR,
                Block.MANGROVE_DOOR,
                Block.CHERRY_DOOR,
                Block.COPPER_DOOR,
                Block.EXPOSED_COPPER_DOOR,
                Block.WEATHERED_COPPER_DOOR,
                Block.OXIDIZED_COPPER_DOOR,
                Block.WAXED_COPPER_DOOR,
                Block.WAXED_EXPOSED_COPPER_DOOR,
                Block.WAXED_WEATHERED_COPPER_DOOR,
                Block.WAXED_OXIDIZED_COPPER_DOOR
        ).forEach(door -> MinecraftServer.getBlockManager().registerBlockPlacementRule(new Doors(door)));
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState placementState) {
        Point pos = placementState.placePosition().add(0, 1, 0);
        if (!placementState.instance().getBlock(pos).isAir()) return null;
        ((Instance) placementState.instance()).setBlock(pos, block.withProperty("half", "upper"));
        return placementState.block();
    }
}
