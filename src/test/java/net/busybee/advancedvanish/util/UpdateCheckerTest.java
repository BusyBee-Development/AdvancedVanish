package net.busybee.advancedvanish.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateCheckerTest {

    @Test
    void comparesDottedVersions() {
        assertTrue(UpdateChecker.isNewer("26.10.1", "26.10.0"));
        assertTrue(UpdateChecker.isNewer("26.11.0", "26.10.9"));
        assertTrue(UpdateChecker.isNewer("27.1", "26.12.3"));
        assertFalse(UpdateChecker.isNewer("26.10.0", "26.10.0"));
        assertFalse(UpdateChecker.isNewer("26.9.5", "26.10.0"));
        // Old tags used 2026.x; they must not look newer than the YY.M.patch scheme
        assertFalse(UpdateChecker.isNewer("2026.5.0", "26.10.0"));
        assertTrue(UpdateChecker.isNewer("26.10.0", "2026.5.0"));
    }
}
