package ut.io.redlink.db.expertfinder.confluence.actions;

import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.labels.*;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.opensymphony.xwork.Action;
import io.redlink.db.expertfinder.confluence.actions.AddMeAsExpertAction;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Testing {@link io.redlink.db.expertfinder.confluence.actions.AddMeAsExpertAction}
 *
 * @author sergio.fernandez@redlink.co
 */
@RunWith(MockitoJUnitRunner.class)
public class AddMeAsExpertActionTest {

    @Mock
    private LabelManager labelManager;

    private AddMeAsExpertAction action;

    @Before
    public void before() {
        final ConfluenceUser user = new ConfluenceUserImpl("test", "test user", "test@redlink.io");
        AuthenticatedUserThreadLocal.set(user);
        action = new AddMeAsExpertAction();
    }

    @After
    public void after() {
        action = null;
        AuthenticatedUserThreadLocal.reset();
    }

    @Test
    @Ignore("TODO")
    public void testAddExpert() throws IOException {
        final AbstractPage page = new TestPage();
        final BodyContent content = new BodyContent();
        final InputStream is = this.getClass().getResourceAsStream("/page_body.xml");
        content.setBody(IOUtils.toString(is, Charset.defaultCharset()));
        page.setBodyContent(content);
        labelManager.addLabel(page, new Label("Qualifikation"));

        action.setPage(page);

        Assert.assertEquals(Action.SUCCESS, action.execute());
        //TODO: check content addition
    }

    @Test
    public void testAddExpertOnRegularPage() throws IOException {
        final AbstractPage page = new TestPage();
        action.setPage(page);

        Assert.assertEquals(Action.NONE, action.execute());
    }

    private class TestPage extends AbstractPage {

        @Override
        public String getType() {
            return "page";
        }

        @Override
        public String getLinkWikiMarkup() {
            return "";
        }

    }

}
