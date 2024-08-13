package gamejam.buffs;

import gamejam.Utils;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.ServerFlag;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerHandAnimationEvent;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;
import pvp.entity.projectile.AbstractArrow;
import pvp.entity.projectile.Arrow;
import pvp.feature.CombatFeatures;
import pvp.feature.FeatureType;
import pvp.feature.effect.EffectFeature;
import pvp.feature.enchantment.EnchantmentFeature;
import pvp.utils.ViewUtil;

public class FlamethrowerBuff extends Buff {
    private static final EffectFeature effectFeature = CombatFeatures.FEATURES.get(FeatureType.EFFECT);
    private static final EnchantmentFeature enchantmentFeature = CombatFeatures.FEATURES.get(FeatureType.ENCHANTMENT);

    FlamethrowerBuff() {
        addListener(PlayerHandAnimationEvent.class, event -> {
            Player player = event.getPlayer();
            ItemStack stack = player.getItemInMainHand();
            if (stack.material() != Material.BOW || (stack.has(ItemComponent.DAMAGE) && stack.get(ItemComponent.DAMAGE) > 100)) return;
            player.setItemInMainHand(stack.with(ItemComponent.DAMAGE, 1000));
            for (float yaw = -50; yaw < 50; yaw += 20) {
                Arrow arrow = new Arrow(player, effectFeature, enchantmentFeature);
                arrow.setFireTicksLeft(100 * ServerFlag.SERVER_TICKS_PER_SECOND);
                arrow.setSound(SoundEvent.ITEM_CROSSBOW_HIT);
                arrow.setPickupMode(AbstractArrow.PickupMode.CREATIVE_ONLY);

                Pos position = player.getPosition().add(0, player.getEyeHeight() - 0.1, 0);
                arrow.setInstance(player.getInstance(), position);
                arrow.shootFrom(position.withYaw(position.yaw() + yaw), 2, 50);

                ViewUtil.viewersAndSelf(player).playSound(Sound.sound(
                        SoundEvent.ITEM_CROSSBOW_SHOOT, Sound.Source.PLAYER,
                        1.0f, 1.0f
                ), player);
            }
            player.scheduler().scheduleTask(()-> Utils.findItemsOf(player.getInventory(), Material.BOW, i->i.without(ItemComponent.DAMAGE)), TaskSchedule.seconds(3), TaskSchedule.stop());
            event.setCancelled(true);
        });
    }

    @Override
    public String description() {
        return "Left click your bow to shoot flame arrows!";
    }

    @Override
    public TextColor color() {
        return TextColor.color(234, 110, 26);
    }

    @Override
    protected String name() {
        return "Flamethrower";
    }
}
