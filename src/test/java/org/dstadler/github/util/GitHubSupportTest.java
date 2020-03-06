package org.dstadler.github.util;

import org.dstadler.github.upgrade.ProjectStatuses;
import org.dstadler.github.upgrade.UpgradeStatus;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class GitHubSupportTest {
    @Before
    public void setUp() {
        File credFile = new File(System.getenv("HOME") + "/.github");
        if(!credFile.exists()) {
            //noinspection ConstantConditions
            Assume.assumeFalse("Credentials need to exist at " + credFile.getAbsolutePath() + " for this test", true);
        }
    }

    @Test
    public void testFilter() throws IOException {
        Map<String, String> projects = new HashMap<>();
        projects.put("project/project", "1.0");     // non-existing
        projects.put("muthu1809/RB", "3.16");       // zero stars/watchers
        projects.put("centic9/jgit-cookbook", "1.0");   // has stars/watchers
        //projects.put("centic9/invalid project &/()", "1.0");   // invalid project name

        ProjectStatuses projectStatuses = new ProjectStatuses();
        Map<String, String> projectsOfInterest = GitHubSupport.filterForProjectsOfInterest(projects, projectStatuses, 10);

        assertEquals("Original map not changed, but had: " + projects,
                3, projects.size());

        assertEquals("Two projects should be filtered out, but had: " + projects,
                1, projectsOfInterest.size());
        assertEquals("1.0", projectsOfInterest.get("centic9/jgit-cookbook"));

        assertEquals(UpgradeStatus.NotAccessible, projectStatuses.get("project/project").getStatus());
        assertEquals(UpgradeStatus.NoStarsOrWatchers, projectStatuses.get("muthu1809/RB").getStatus());
        assertNull(projectStatuses.get("centic9/jgit-cookbook"));
        assertEquals(2, projectStatuses.size());
    }

    @Test//(expected = HttpException.class)
    public void testInvalidProject() throws IOException {
        Map<String, String> projects = new HashMap<>();
        projects.put("centic9/invalid project &/()", "1.0");   // invalid project name

        ProjectStatuses projectStatuses = new ProjectStatuses();
        Map<String, String> map = GitHubSupport.filterForProjectsOfInterest(projects, projectStatuses, 10);
        assertTrue(map.isEmpty());

        /*GitHub github = BaseSearch.connect();
        GHRepository repository = github.getRepository(projects.keySet().iterator().next());
        assertTrue(0 < repository.getStargazersCount());
        assertTrue(0 < repository.getWatchers());

        fail("Should catch Exception, but had: " + map);*/
    }
}
