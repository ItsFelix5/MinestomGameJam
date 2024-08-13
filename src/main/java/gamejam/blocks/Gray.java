package gamejam.blocks;

import gamejam.Utils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.Direction;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Gray implements BlockHandler {
    public static final Block GRAY = Block.LIGHT_GRAY_CONCRETE_POWDER.withHandler(new Gray());

    @Override
    public void onPlace(@NotNull BlockHandler.Placement placement) {
        Instance instance = placement.getInstance();

        ArrayList<Direction> list = new ArrayList<>(List.of(Direction.values()));

        Point pos = placement.getBlockPosition();
        boolean x = pos.x() > 0;
        boolean z = pos.z() > 200;
        instance.scheduler().scheduleTask(() -> {
            Point point;
            Block block;
            int size = list.size();
            do {
                if (size < 1) return TaskSchedule.stop();
                final int i = Utils.random.nextInt(size--);
                point = pos.add(list.get(i).vec());
                list.remove(i);
            } while ((block = instance.getBlock(point, Block.Getter.Condition.TYPE)).isAir() || block.compare(GRAY) || point.x() > 0 != x || point.z() > 200 != z || !instance.getWorldBorder().inBounds(point));

            instance.setBlock(point, GRAY);
            return TaskSchedule.millis(Utils.random.nextInt(3000, 10000));
        }, TaskSchedule.seconds(2));
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("gamejam", "gray");
    }
}
