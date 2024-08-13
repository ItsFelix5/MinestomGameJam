package gamejam.blocks;

import gamejam.Main;
import gamejam.Utils;
import gamejam.buffs.Buff;
import gamejam.buffs.Buffs;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.instance.block.predicate.BlockPredicate;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.BlockPredicates;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class BuffBlock implements BlockHandler {

    @Override
    public boolean onInteract(@NotNull BlockHandler.Interaction interaction) {
        if (Arrays.stream(interaction.getPlayer().getInventory().getItemStacks()).anyMatch(i -> i.material().name().contains("concrete")))
            return true;
        interaction.getInstance().setBlock(interaction.getBlockPosition(), Block.AIR);
        Block block = interaction.getBlock();
        Buff buff = Buffs.buffs.get(block.defaultState());
        interaction.getPlayer().getInventory().addItemStack(ItemStack.of(Objects.requireNonNull(Material.fromNamespaceId(block.namespace()))).withCustomName(buff.getName()).with(ItemComponent.CAN_PLACE_ON,
                new BlockPredicates(new BlockPredicate(Block.NETHERITE_BLOCK))));
        Main.games.get(interaction.getInstance()).teams.get(Utils.getTerritory(interaction.getBlockPosition())).removeBuff(buff);
        return false;
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("gamejam:buff");
    }
}
