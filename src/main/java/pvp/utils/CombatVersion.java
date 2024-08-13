package pvp.utils;

import pvp.feature.CombatFeature;

public final class CombatVersion implements CombatFeature {
    public static CombatVersion MODERN = new CombatVersion();

    public boolean modern() {
        return true;
    }

    public boolean legacy() {
        return false;
    }
}
