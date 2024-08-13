package gamejam.buffs;

import net.minestom.server.instance.block.Block;

import java.util.HashMap;
import java.util.Map;

public class Buffs {
    public static final Map<Block, Buff> buffs = new HashMap<>();

    public static final Buff DASH = new DashBuff();
    public static final Buff DOUBLE_JUMP = new DoubleJumpBuff();
    public static final Buff SLAM = new SlamBuff();
    public static final Buff ADRENALINE = new AdrenalineBuff();
    public static final Buff FLAMETHROWER = new FlamethrowerBuff();
    public static final Buff GRAPPLE = new GrappleBuff();
    public static final Buff BEHEADING = new BeheadingBuff();
    public static final Buff VAMPIRISM = new VampirismBuff();

    static {
        buffs.put(Block.BLUE_CONCRETE_POWDER, DASH);
        buffs.put(Block.GREEN_CONCRETE_POWDER, DOUBLE_JUMP);
        buffs.put(Block.RED_CONCRETE_POWDER, SLAM);
        buffs.put(Block.YELLOW_CONCRETE_POWDER, ADRENALINE);
        buffs.put(Block.ORANGE_CONCRETE_POWDER, FLAMETHROWER);
        buffs.put(Block.BROWN_CONCRETE_POWDER, GRAPPLE);
        buffs.put(Block.BLACK_CONCRETE_POWDER, BEHEADING);
        buffs.put(Block.PINK_CONCRETE_POWDER, VAMPIRISM);
    }
}
