package pvp.feature.fall;

import gamejam.blocks.Gray;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityTickEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.trait.EntityInstanceEvent;
import net.minestom.server.gamedata.tags.TagManager;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.TaskSchedule;
import pvp.events.PlayerFallDamageEvent;
import pvp.feature.CombatFeature;
import pvp.feature.FeatureType;
import pvp.feature.RegistrableFeature;
import pvp.feature.config.DefinedFeature;
import pvp.feature.config.FeatureConfiguration;
import pvp.feature.state.PlayerStateFeature;

/**
 * Vanilla implementation of {@link FallFeature}
 */
public class VanillaFallFeature implements FallFeature, CombatFeature, RegistrableFeature {
    public static final Tag<Double> FALL_DISTANCE = Tag.Transient("fallDistance");
    public static final DefinedFeature<VanillaFallFeature> DEFINED = new DefinedFeature<>(
            FeatureType.FALL, VanillaFallFeature::new,
            VanillaFallFeature::initPlayer,
            FeatureType.PLAYER_STATE
    );
    public static final Tag<Boolean> EXTRA_FALL_PARTICLES = Tag.Transient("extraFallParticles");

    private final FeatureConfiguration configuration;

    private PlayerStateFeature playerStateFeature;

    public VanillaFallFeature(FeatureConfiguration configuration) {
        this.configuration = configuration;
    }

    public static void initPlayer(Player player, boolean firstInit) {
        player.setTag(FALL_DISTANCE, 0.0);
    }

    @Override
    public void initDependencies() {
        this.playerStateFeature = configuration.get(FeatureType.PLAYER_STATE);
    }

    @Override
    public void init(EventNode<EntityInstanceEvent> node) {
        // For living non-player entities, handle fall damage every tick
        node.addListener(EntityTickEvent.class, event -> {
            if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
            if (livingEntity instanceof Player) return;

            Pos previousPosition = livingEntity.getPreviousPosition();
            handleFallDamage(livingEntity, previousPosition, livingEntity.getPosition(), livingEntity.isOnGround());
        });

        // For players, handle fall damage on move event
        node.addListener(PlayerMoveEvent.class, event -> {
            Player player = event.getPlayer();
            if (playerStateFeature.isClimbing(player)) player.setTag(FALL_DISTANCE, 0.0);
            Point pos = player.getPosition();
            if (pos.y() < 0) {
                if (event.getInstance().isInVoid(player.getPosition())) {
                    player.damage(DamageType.OUT_OF_WORLD, 10000);
                    return;
                }
                if (event.getInstance().getBlock(pos.sub(0, .2f, 0)).compare(Block.SLIME_BLOCK)) {
                    Runnable yeet = ()-> player.setVelocity(player.getVelocity().add(0, 1000, 0));
                    yeet.run();
                    player.scheduler().scheduleTask(yeet, TaskSchedule.tick(30), TaskSchedule.stop());
                    player.scheduler().scheduleTask(yeet, TaskSchedule.tick(50), TaskSchedule.stop());
                    return;
                }
            }

            handleFallDamage(
                    player, player.getPosition(),
                    event.getNewPosition(), event.isOnGround()
            );
        });
    }

    public void handleFallDamage(LivingEntity entity, Pos currPos, Pos newPos, boolean onGround) {
        double dy = newPos.y() - currPos.y();
        double fallDistance = getFallDistance(entity);

        if ((entity instanceof Player player && player.isFlying())
                || entity.hasEffect(PotionEffect.LEVITATION)
                || entity.hasEffect(PotionEffect.SLOW_FALLING) || dy > 0) {
            entity.setTag(FALL_DISTANCE, 0.0);
            return;
        }

        if (entity.isFlyingWithElytra() && entity.getVelocity().y() > -0.5) {
            entity.setTag(FALL_DISTANCE, 1.0);
            return;
        }

        if (!onGround) {
            if (dy < 0) entity.setTag(FALL_DISTANCE, fallDistance - dy);
            return;
        }

        Point landingPos = getLandingPos(entity, newPos);
        Block block = entity.getInstance().getBlock(landingPos);

        if (entity.hasTag(EXTRA_FALL_PARTICLES) && entity.getTag(EXTRA_FALL_PARTICLES) && fallDistance > 0.0) {
            Vec position = Vec.fromPoint(landingPos).apply(Vec.Operator.FLOOR).add(0.5, 1, 0.5);
            int particleCount = (int) Math.max(0, Math.min(200, 50 * fallDistance));

            entity.sendPacketToViewersAndSelf(new ParticlePacket(
                    Particle.BLOCK.withBlock(block),
                    position.x(), position.y(), position.z(),
                    0.3f, 0.3f, 0.3f,
                    0.15f, particleCount
            ));

            entity.removeTag(EXTRA_FALL_PARTICLES);
        }

        entity.setTag(FALL_DISTANCE, 0.0);
        int damage = entity instanceof Player player && !player.getGameMode().canTakeDamage() ? 0 : getFallDamage(entity, fallDistance);
        if ((block.compare(Block.SLIME_BLOCK) && !entity.isSneaking()) || block.compare(Block.WATER) || entity.getInstance().getBlock(landingPos.add(0, 1, 0)).compare(Block.WATER))
            damage = 0;
        else if (block.compare(Gray.GRAY)) damage = 1;

        if (damage > 1 && entity instanceof Player player) {
            PlayerFallDamageEvent event = new PlayerFallDamageEvent(player, damage, fallDistance, block);
            EventDispatcher.call(event);
            if (event.isCancelled()) return;
            damage = event.getDamage();
        }

        double safeFallDistance = entity.getAttributeValue(Attribute.GENERIC_SAFE_FALL_DISTANCE);
        if (fallDistance > safeFallDistance) {
            if (!block.isAir()) {
                double damageDistance = Math.ceil(fallDistance - safeFallDistance);
                double particleMultiplier = Math.min(0.2 + damageDistance / 15.0, 2.5);
                int particleCount = (int) (150 * particleMultiplier);

                entity.sendPacketToViewersAndSelf(new ParticlePacket(
                        Particle.BLOCK.withBlock(block),
                        false,
                        newPos.x(), newPos.y(), newPos.z(),
                        0, 0, 0,
                        0.15f, particleCount
                ));
            }
        }

        if (damage > 0) {
            playFallSound(entity, damage);
            entity.damage(DamageType.FALL, damage);
        }
    }

    public void playFallSound(LivingEntity entity, int damage) {
        boolean bigFall = damage > 4;

        entity.getViewersAsAudience().playSound(Sound.sound(
                bigFall ?
                        SoundEvent.ENTITY_PLAYER_BIG_FALL :
                        SoundEvent.ENTITY_PLAYER_SMALL_FALL,
                entity instanceof Player ? Sound.Source.PLAYER : Sound.Source.HOSTILE,
                1.0f, 1.0f
        ), entity);
    }

    @Override
    public int getFallDamage(LivingEntity entity, double fallDistance) {
        double safeFallDistance = entity.getAttributeValue(Attribute.GENERIC_SAFE_FALL_DISTANCE);
        return (int) Math.ceil((fallDistance - safeFallDistance) * entity.getAttributeValue(Attribute.GENERIC_FALL_DAMAGE_MULTIPLIER));
    }

    @Override
    public double getFallDistance(LivingEntity entity) {
        return entity.hasTag(FALL_DISTANCE) ? entity.getTag(FALL_DISTANCE) : 0.0;
    }

    @Override
    public void resetFallDistance(LivingEntity entity) {
        entity.setTag(FALL_DISTANCE, 0.0);
    }

    @Override
    public void setExtraFallParticles(LivingEntity entity, boolean extraFallParticles) {
        if (extraFallParticles) entity.setTag(EXTRA_FALL_PARTICLES, true);
        else entity.removeTag(EXTRA_FALL_PARTICLES);
    }

    protected Point getLandingPos(LivingEntity livingEntity, Pos position) {
        Point offset = position.add(0, -0.2, 0);
        Instance instance = livingEntity.getInstance();

        if (instance == null) return offset;
        if (!instance.getBlock(offset).isAir()) return offset;

        Point offsetDown = offset.add(0, -1, 0);
        Block block = instance.getBlock(offsetDown);

        TagManager tagManager = MinecraftServer.getTagManager();
        var fences = tagManager.getTag(net.minestom.server.gamedata.tags.Tag.BasicType.BLOCKS, "minecraft:fences");
        var walls = tagManager.getTag(net.minestom.server.gamedata.tags.Tag.BasicType.BLOCKS, "minecraft:walls");
        var fenceGates = tagManager.getTag(net.minestom.server.gamedata.tags.Tag.BasicType.BLOCKS, "minecraft:fence_gates");

        assert fences != null;
        assert walls != null;
        assert fenceGates != null;

        if (fences.contains(block.namespace())
                || walls.contains(block.namespace())
                || fenceGates.contains(block.namespace())) {
            return offsetDown;
        }

        return offset;
    }
}
