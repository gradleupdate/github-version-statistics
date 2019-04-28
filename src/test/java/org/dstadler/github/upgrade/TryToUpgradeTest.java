package org.dstadler.github.upgrade;

import org.dstadler.github.ProcessResults;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TryToUpgradeTest {
    @Ignore("Just used for local testing")
    @Test
    public void testLocalBuild() throws IOException {
        TryToUpgrade.buildViaGradle("nd-team/goddess-java", new File("/tmp/TestGitRepository6087905793861044471"));
    }

    @Test
    public void testReadLines() throws IOException {
        File[] files = ProcessResults.getStatsFiles();
        Map<String, String> projects = new HashMap<>();

        TryToUpgrade.readLines(
                // do not read all files to make test execute faster
                Arrays.copyOf(files, 50),
                projects);

        assertFalse(projects.isEmpty());
        assertEquals("3.14", projects.get("DmitriiSolovev/Excel"));
    }
}
