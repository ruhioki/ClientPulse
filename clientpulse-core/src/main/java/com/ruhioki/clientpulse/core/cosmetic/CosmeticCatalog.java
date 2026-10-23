package com.ruhioki.clientpulse.core.cosmetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CosmeticCatalog {
    private static final List<CosmeticDefinition> ITEMS;

    static {
        List<CosmeticDefinition> items = new ArrayList<CosmeticDefinition>();
        items.add(new CosmeticDefinition("pulse-cape", "Pulse Cape", CosmeticCategory.CAPES));
        items.add(new CosmeticDefinition("aurora-wings", "Aurora Wings", CosmeticCategory.WINGS));
        items.add(new CosmeticDefinition("orbit-halo", "Orbit Halo", CosmeticCategory.HATS));
        items.add(new CosmeticDefinition("starlight-trail", "Starlight Trail", CosmeticCategory.TRAILS));
        items.add(new CosmeticDefinition("pulse-badge", "Pulse Badge", CosmeticCategory.BADGES));
        ITEMS = Collections.unmodifiableList(items);
    }

    private CosmeticCatalog() {
    }

    public static List<CosmeticDefinition> all() {
        return ITEMS;
    }

    public static CosmeticDefinition find(String id) {
        if (id == null) {
            return null;
        }
        for (CosmeticDefinition item : ITEMS) {
            if (item.id().equals(id)) {
                return item;
            }
        }
        return null;
    }

    public static List<CosmeticDefinition> inCategory(CosmeticCategory category) {
        List<CosmeticDefinition> result = new ArrayList<CosmeticDefinition>();
        for (CosmeticDefinition item : ITEMS) {
            if (item.category() == category) {
                result.add(item);
            }
        }
        return Collections.unmodifiableList(result);
    }
}
