package gamejam;

import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class Kit {
    public static void equip(PlayerInventory inv) {
        inv.setHelmet(ItemStack.of(Material.CHAINMAIL_HELMET));
        inv.setChestplate(ItemStack.of(Material.CHAINMAIL_CHESTPLATE));
        inv.setLeggings(ItemStack.of(Material.CHAINMAIL_LEGGINGS));
        inv.setBoots(ItemStack.of(Material.CHAINMAIL_BOOTS));
        inv.setItemInOffHand(ItemStack.of(Material.SHIELD));

        inv.setItemStack(0, ItemStack.of(Material.IRON_SWORD));
        inv.setItemStack(1, ItemStack.of(Material.IRON_AXE));
        inv.setItemStack(2, ItemStack.of(Material.BOW));
        inv.setItemStack(9, ItemStack.of(Material.ARROW, 64));
        inv.setItemStack(8, ItemStack.of(Material.COOKED_BEEF, 64));
    }
}
