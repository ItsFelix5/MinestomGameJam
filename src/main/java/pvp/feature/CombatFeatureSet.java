package pvp.feature;

import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.EntityInstanceEvent;
import pvp.feature.config.FeatureConfiguration;

/**
 * A container for multiple {@link CombatFeature}s. Use {@link CombatFeatureSet#createNode()} to get an event node.
 */
public class CombatFeatureSet extends FeatureConfiguration implements RegistrableFeature {
    @Override
    public void init(EventNode<EntityInstanceEvent> node) {
        for (CombatFeature feature : listFeatures()) {
            if (!(feature instanceof RegistrableFeature registrable)) continue;
            node.addChild(registrable.createNode());
        }
    }

    @Override
    public void initDependencies() {
        for (CombatFeature feature : listFeatures()) {
            feature.initDependencies();
        }
    }
}
