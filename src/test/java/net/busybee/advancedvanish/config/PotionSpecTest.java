package net.busybee.advancedvanish.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PotionSpecTest {

    @Test
    void parsesUpperCaseNameAndLevel() {
        PotionSpec spec = PotionSpec.parse("INVISIBILITY:2");
        assertEquals("minecraft:invisibility", spec.key().toString());
        assertEquals(1, spec.amplifier());
    }

    @Test
    void mapsLegacyBukkitNames() {
        assertEquals("minecraft:jump_boost", PotionSpec.parse("JUMP:1").key().toString());
        assertEquals("minecraft:strength", PotionSpec.parse("INCREASE_DAMAGE:1").key().toString());
        assertEquals("minecraft:slowness", PotionSpec.parse("SLOW:3").key().toString());
    }

    @Test
    void acceptsNamespacedKeys() {
        PotionSpec spec = PotionSpec.parse("minecraft:night_vision:1");
        assertEquals("minecraft:night_vision", spec.key().toString());
        assertEquals(0, spec.amplifier());
    }

    @Test
    void rejectsMalformedEntries() {
        assertNull(PotionSpec.parse("INVISIBILITY"));
        assertNull(PotionSpec.parse("INVISIBILITY:"));
        assertNull(PotionSpec.parse(":2"));
        assertNull(PotionSpec.parse("INVISIBILITY:two"));
        assertNull(PotionSpec.parse("INVISIBILITY:0"));
        assertNull(PotionSpec.parse(null));
    }
}
