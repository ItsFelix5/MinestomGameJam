package gamejam;

import net.minestom.server.coordinate.Point;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.Random;
import java.util.function.Function;

public class Utils {
    public static Random random = new Random();

    public static Teams getTerritory(Point pos) {
        if (pos.x() > 0) {
            if (pos.z() > 200) return Teams.A;
            else return Teams.B;
        } else {
            if (pos.z() > 200) return Teams.C;
            else return Teams.D;
        }
    }

    public static void findItemsOf(PlayerInventory inv, Material material, Function<ItemStack, ItemStack> cb) {
        for (int slot = 0; slot < inv.getInnerSize(); slot++) {
            ItemStack stack = inv.getItemStack(slot);
            if(stack.material() != material) continue;
            inv.setItemStack(slot, cb.apply(stack));
        }
    }
}
