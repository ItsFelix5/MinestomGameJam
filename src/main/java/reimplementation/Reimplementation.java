package reimplementation;

import blocks.MinestomBlocks;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.instance.InstanceRegisterEvent;
import pvp.enchantment.CombatEnchantments;
import pvp.feature.CombatFeatures;
import pvp.feature.FeatureType;
import pvp.feature.config.CombatFeatureRegistry;
import pvp.player.CombatPlayer;
import pvp.player.CombatPlayerImpl;
import pvp.potion.effect.CombatPotionEffects;
import pvp.potion.item.CombatPotionTypes;

public class Reimplementation {
    public static void reimplementAll() {
        CombatEnchantments.registerAll();
        CombatPotionEffects.registerAll();
        CombatPotionTypes.registerAll();
        CombatFeatureRegistry.init();
        MinecraftServer.getConnectionManager().setPlayerProvider(CombatPlayerImpl::new);
        MinecraftServer.getGlobalEventHandler().addChild(CombatFeatures.FEATURES.createNode());
        CombatPlayer.init(MinecraftServer.getGlobalEventHandler());
        MinecraftServer.getGlobalEventHandler().addListener(InstanceRegisterEvent.class,
                event -> event.getInstance().setExplosionSupplier(CombatFeatures.FEATURES.get(FeatureType.EXPLOSION).getExplosionSupplier()));

        MinestomBlocks.init();

        Drops.implement();
        Doors.implement();
        //GravityBlock.implement();
        Chest.implement();
    }
}
