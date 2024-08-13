package reimplementation;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.other.FallingBlockMeta;
import net.minestom.server.event.entity.EntityTickEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GravityBlock extends BlockPlacementRule {
    protected GravityBlock(@NotNull Block block) {
        super(block);
    }

    public static void implement() {
        MinecraftServer.getBlockManager().registerBlockPlacementRule(new GravityBlock(Block.SAND));
        MinecraftServer.getBlockManager().registerBlockPlacementRule(new GravityBlock(Block.RED_SAND));
        MinecraftServer.getBlockManager().registerBlockPlacementRule(new GravityBlock(Block.GRAVEL));
        // No concrete powder because lazy

        MinecraftServer.getGlobalEventHandler().addListener(EntityTickEvent.class, event -> {
            Entity entity = event.getEntity();
            if (entity.getEntityType() != EntityType.FALLING_BLOCK) return;
            if (!entity.isOnGround()) return;
            event.getInstance().setBlock(entity.getPosition(), ((FallingBlockMeta) entity.getEntityMeta()).getBlock());
            entity.remove();
        });
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        if (!state.instance().getBlock(state.placePosition().sub(0, 1, 0)).isAir()) return state.block();
        Entity fallingBlock = new Entity(EntityType.FALLING_BLOCK);
        fallingBlock.editEntityMeta(FallingBlockMeta.class, meta -> meta.setBlock(state.block()));
        fallingBlock.setInstance((Instance) state.instance(), state.placePosition().add(.5, 0, .5));
        return Block.AIR;
    }

    @Override
    public @NotNull Block blockUpdate(@NotNull BlockPlacementRule.UpdateState state) {
        if (state.fromFace() != BlockFace.BOTTOM || !state.instance().getBlock(state.blockPosition().sub(0, 1, 0)).isAir())
            return state.currentBlock();
        Entity fallingBlock = new Entity(EntityType.FALLING_BLOCK);
        fallingBlock.editEntityMeta(FallingBlockMeta.class, meta -> meta.setBlock(state.currentBlock()));
        fallingBlock.setInstance((Instance) state.instance(), state.blockPosition().add(.5, 0, .5));
        return Block.AIR;
    }
}
