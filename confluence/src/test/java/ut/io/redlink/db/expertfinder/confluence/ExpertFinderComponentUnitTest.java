package ut.io.redlink.db.expertfinder.confluence;

import org.junit.Test;
import io.redlink.db.expertfinder.confluence.api.ExpertFinderPluginComponent;
import io.redlink.db.expertfinder.confluence.impl.ExpertFinderPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class ExpertFinderComponentUnitTest {

    @Test
    public void testMyName() {
        ExpertFinderPluginComponent component = new ExpertFinderPluginComponentImpl(null, null);
        assertEquals("names do not match!", "ExpertFinder Plugin", component.getName());
    }

}