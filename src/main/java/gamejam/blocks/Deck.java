package gamejam.blocks;

import gamejam.Main;
import gamejam.Utils;
import gamejam.buffs.Buff;
import gamejam.buffs.Buffs;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Deck extends BlockPlacementRule {
    public Deck() {
        super(Block.NETHERITE_BLOCK);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState placementState) {
        return placementState.block();
    }

    @Override
    public @NotNull Block blockUpdate(@NotNull BlockPlacementRule.UpdateState updateState) {
        Instance instance = (Instance) updateState.instance();
        Point pos = updateState.blockPosition().add(updateState.fromFace().toDirection().vec());
        Block block = instance.getBlock(pos);
        if (block.handler() != null) return updateState.currentBlock();
        Buff buff = Buffs.buffs.get(block.defaultState());
        if (buff == null) return updateState.currentBlock();

        instance.setBlock(pos, block.withHandler(new BuffBlock()));
        Main.games.get(instance).teams.get(Utils.getTerritory(updateState.blockPosition())).addBuff(buff);
        return updateState.currentBlock();
    }
}
