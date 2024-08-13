package pvp.feature.projectile;

import net.kyori.adventure.sound.Sound;
import net.minestom.server.MinecraftServer;
import net.minestom.server.ServerFlag;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityShootEvent;
import net.minestom.server.event.entity.projectile.ProjectileCollideWithBlockEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.trait.EntityInstanceEvent;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.MathUtils;
import pvp.entity.projectile.FishingBobber;
import pvp.events.FishingBobberRetrieveEvent;
import pvp.feature.FeatureType;
import pvp.feature.RegistrableFeature;
import pvp.feature.config.DefinedFeature;
import pvp.feature.config.FeatureConfiguration;
import pvp.feature.item.ItemDamageFeature;
import pvp.utils.CombatVersion;
import pvp.utils.ViewUtil;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Vanilla implementation of {@link FishingRodFeature}
 */
public class VanillaFishingRodFeature implements FishingRodFeature, RegistrableFeature {
    public static final DefinedFeature<VanillaFishingRodFeature> DEFINED = new DefinedFeature<>(
            FeatureType.FISHING_ROD, VanillaFishingRodFeature::new,
            FeatureType.ITEM_DAMAGE, FeatureType.VERSION
    );

    public static final Tag<FishingBobber> FISHING_BOBBER = Tag.Transient("fishingBobber");

    private final FeatureConfiguration configuration;

    private ItemDamageFeature itemDamageFeature;
    private CombatVersion version;

    public VanillaFishingRodFeature(FeatureConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initDependencies() {
        this.itemDamageFeature = configuration.get(FeatureType.ITEM_DAMAGE);
        this.version = configuration.get(FeatureType.VERSION);
    }

    @Override
    public void init(EventNode<EntityInstanceEvent> node) {
        node.addListener(PlayerUseItemEvent.class, event -> {
            if (event.getItemStack().material() != Material.FISHING_ROD) return;

            ThreadLocalRandom random = ThreadLocalRandom.current();
            Player player = event.getPlayer();

            if (player.hasTag(FISHING_BOBBER)) {
                FishingBobber bobber = player.getTag(FISHING_BOBBER);

                FishingBobberRetrieveEvent retrieveEvent = new FishingBobberRetrieveEvent(player, bobber);
                EventDispatcher.callCancellable(retrieveEvent, () -> {
                    int durability = bobber.retrieve();
                    if (player.getGameMode() != GameMode.CREATIVE)
                        itemDamageFeature.damageEquipment(player, event.getHand() == Player.Hand.MAIN ?
                                EquipmentSlot.MAIN_HAND : EquipmentSlot.OFF_HAND, durability);

                    ViewUtil.viewersAndSelf(player).playSound(Sound.sound(
                            SoundEvent.ENTITY_FISHING_BOBBER_RETRIEVE, Sound.Source.NEUTRAL,
                            1.0f, 0.4f / (random.nextFloat() * 0.4f + 0.8f)
                    ), player);
                });
            } else {
                ViewUtil.viewersAndSelf(player).playSound(Sound.sound(
                        SoundEvent.ENTITY_FISHING_BOBBER_THROW, Sound.Source.NEUTRAL,
                        0.5f, 0.4f / (random.nextFloat() * 0.4f + 0.8f)
                ), player);

                FishingBobber bobber = new FishingBobber(player);
                player.setTag(FISHING_BOBBER, bobber);

                EntityShootEvent shootEvent = new EntityShootEvent(player, bobber,
                        player.getPosition(), 0, 1.0);
                EventDispatcher.call(shootEvent);
                if (shootEvent.isCancelled()) {
                    bobber.remove();
                    return;
                }
                double spread = shootEvent.getSpread() * (version.legacy() ? 0.0075 : 0.0045);

                Pos playerPos = player.getPosition();
                float playerPitch = playerPos.pitch();
                float playerYaw = playerPos.yaw();

                float zDir = (float) Math.cos(Math.toRadians(-playerYaw) - Math.PI);
                float xDir = (float) Math.sin(Math.toRadians(-playerYaw) - Math.PI);
                double x = playerPos.x() - (double) xDir * 0.3D;
                double y = playerPos.y() + player.getEyeHeight();
                double z = playerPos.z() - (double) zDir * 0.3D;
                bobber.setInstance(Objects.requireNonNull(player.getInstance()), new Pos(x, y, z));

                Vec velocity = new Vec(
                        -xDir,
                        MathUtils.clamp(-(
                                (float) Math.sin(Math.toRadians(-playerPitch)) /
                                        (float) -Math.cos(Math.toRadians(-playerPitch))
                        ), -5.0F, 5.0F),
                        -zDir
                );
                double length = velocity.length();
                velocity = velocity.mul(
                        0.6D / length + 0.5D + random.nextGaussian() * spread,
                        0.6D / length + 0.5D + random.nextGaussian() * spread,
                        0.6D / length + 0.5D + random.nextGaussian() * spread
                );

                //TODO fix velocity code
                bobber.setVelocity(velocity.mul(ServerFlag.SERVER_TICKS_PER_SECOND * 0.75));
            }
        });

        node.addListener(ProjectileCollideWithBlockEvent.class, event-> {
            if(event.getEntity().getEntityType() != EntityType.FISHING_BOBBER) return;
            ((FishingBobber) event.getEntity()).setInBlock();
        });
    }
}
