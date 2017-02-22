package it.io.redlink.db.expertfinder.confluence;

import org.junit.Test;
import org.junit.runner.RunWith;
import com.atlassian.plugins.osgi.test.AtlassianPluginsTestRunner;
import io.redlink.db.expertfinder.confluence.components.ExpertFinderPluginComponent;
import com.atlassian.sal.api.ApplicationProperties;

import static org.junit.Assert.assertEquals;

@RunWith(AtlassianPluginsTestRunner.class)
public class ExpertFinderComponentWiredTest {

    private final ApplicationProperties applicationProperties;
    private final ExpertFinderPluginComponent myPluginComponent;

    public ExpertFinderComponentWiredTest(ApplicationProperties applicationProperties, ExpertFinderPluginComponent myPluginComponent) {
        this.applicationProperties = applicationProperties;
        this.myPluginComponent = myPluginComponent;
    }

    @Test
    public void testMyName() {
        assertEquals("names do not match!", "ExpertFinder " + applicationProperties.getDisplayName() + " Plugin", myPluginComponent.getName());
    }

}