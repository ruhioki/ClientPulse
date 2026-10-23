package com.ruhioki.clientpulse.v189;

import com.ruhioki.clientpulse.core.cosmetic.CosmeticCategory;
import com.ruhioki.clientpulse.core.cosmetic.CosmeticProfile;
import java.util.Properties;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CosmeticProfileTest {
    @Test
    public void profileRoundTripsSelectionsFavoritesAndControls() {
        CosmeticProfile profile = new CosmeticProfile();
        profile.setEnabled(true);
        profile.setAnimationsEnabled(false);
        profile.setColor(0xB078FF);
        profile.setScale(1.25);
        profile.setAnimationDuration(1200);
        profile.setCapeVerticalOffset(-0.15);
        profile.setWingVerticalOffset(0.2);
        profile.select(CosmeticCategory.CAPES, "pulse-cape");
        profile.select(CosmeticCategory.WINGS, "aurora-wings");
        profile.setFavorite("orbit-halo", true);

        Properties serialized = new Properties();
        profile.save(serialized);
        CosmeticProfile restored = new CosmeticProfile();

        assertTrue(restored.restore(serialized).isEmpty());
        assertTrue(restored.enabled());
        assertFalse(restored.animationsEnabled());
        assertEquals(0xB078FF, restored.color());
        assertEquals(1.25, restored.scale(), 0.0001);
        assertEquals(1200, restored.animationDuration());
        assertEquals(-0.15, restored.capeVerticalOffset(), 0.0001);
        assertEquals(0.2, restored.wingVerticalOffset(), 0.0001);
        assertEquals("pulse-cape", restored.selected(CosmeticCategory.CAPES));
        assertEquals("aurora-wings", restored.selected(CosmeticCategory.WINGS));
        assertTrue(restored.isFavorite("orbit-halo"));
    }

    @Test
    public void restoreIgnoresInvalidValuesWithoutDiscardingValidSettings() {
        Properties serialized = new Properties();
        serialized.setProperty("cosmetics.enabled", "true");
        serialized.setProperty("cosmetics.scale", "10.0");
        serialized.setProperty("cosmetics.selected.capes", "not-a-cosmetic");
        serialized.setProperty("cosmetics.favorites", "missing-item");
        CosmeticProfile restored = new CosmeticProfile();

        assertEquals(3, restored.restore(serialized).size());
        assertTrue(restored.enabled());
        assertEquals(1.0, restored.scale(), 0.0001);
        assertEquals(null, restored.selected(CosmeticCategory.CAPES));
        assertFalse(restored.isFavorite("missing-item"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectionRejectsItemsFromAnotherCategory() {
        new CosmeticProfile().select(CosmeticCategory.CAPES, "aurora-wings");
    }

    @Test(expected = IllegalArgumentException.class)
    public void scaleRejectsNonFiniteValues() {
        new CosmeticProfile().setScale(Double.NaN);
    }

    @Test
    public void resetReturnsEverySettingToDefaults() {
        CosmeticProfile profile = new CosmeticProfile();
        profile.setEnabled(true);
        profile.setAnimationsEnabled(false);
        profile.setColor(0xB078FF);
        profile.setScale(1.25);
        profile.setAnimationDuration(1200);
        profile.setCapeVerticalOffset(0.25);
        profile.setWingVerticalOffset(-0.25);
        profile.select(CosmeticCategory.CAPES, "pulse-cape");
        profile.setFavorite("pulse-cape", true);

        profile.reset();

        assertFalse(profile.enabled());
        assertTrue(profile.animationsEnabled());
        assertEquals(0x65B8FF, profile.color());
        assertEquals(1.0, profile.scale(), 0.0001);
        assertEquals(900, profile.animationDuration());
        assertEquals(0.0, profile.capeVerticalOffset(), 0.0001);
        assertEquals(0.0, profile.wingVerticalOffset(), 0.0001);
        assertEquals(null, profile.selected(CosmeticCategory.CAPES));
        assertFalse(profile.isFavorite("pulse-cape"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void verticalOffsetRejectsOutOfRangeValue() {
        new CosmeticProfile().setCapeVerticalOffset(0.6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void placementOffsetsRejectNonFiniteValues() {
        new CosmeticProfile().setWingVerticalOffset(Double.POSITIVE_INFINITY);
    }
}
