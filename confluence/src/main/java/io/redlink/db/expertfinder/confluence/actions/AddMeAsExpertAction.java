package io.redlink.db.expertfinder.confluence.actions;

import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.actions.PageAware;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.opensymphony.xwork.Action;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Action to add current use as expert for the current page
 *
 * @author sergio.fernandez@redlink.co
 */
public class AddMeAsExpertAction extends ConfluenceActionSupport implements PageAware {

    private static final Logger log = LoggerFactory.getLogger(AddMeAsExpertAction.class);

    public static final String EXPERTS_LIST_CSS_SELECTOR = "ul.expertfinder.experts";

    public static final List<String> labels = Arrays.asList("projekt", "fachliche domäne", "applikation", "technologie", "qualifikation");

    private static final String EXPERT_ITEM__TEMPLATE = "<li>" +
            "<a class=\"confluence-userlink user-mention\" " +
            "data-username=\"%s\" " +
            "href=\"/confluence/display/~%s\" " +
            "data-linked-resource-id=\"%s\" " +
            "data-linked-resource-version=\"1\" " +
            "data-linked-resource-type=\"userinfo\">%s</a>" +
            "</li>";

    private static final String EXPERT_ITEM_PLACEHOLDER = "<li>" +
            "<ac:placeholder ac:type=\"mention\">" +
            "@mention experts" +
            "</ac:placeholder>" +
            "</li>";

    private AbstractPage page;

    @Override
    public AbstractPage getPage() {
        return page;
    }

    @Override
    public void setPage(AbstractPage page) {
        this.page = page;
    }

    @Override
    public boolean isPageRequired() {
        return true;
    }

    @Override
    public boolean isLatestVersionRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }

    @Override
    public String execute() {
        final boolean isExpertFinderPage = page.getLabels()
                .stream()
                .map(label -> label.getName())
                .anyMatch(label -> labels.contains(label));
        if (isExpertFinderPage) {
            log.info("Executing AddMeAsExpertAction over page {}...", page.getId());
            try {
                // 1. parse current content
                final Document document = Jsoup.parse(page.getBodyAsString(), "", Parser.xmlParser());
                document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
                document.outputSettings().escapeMode(Entities.EscapeMode.extended);

                // 2. add expert to the dom
                final ConfluenceUser user = AuthenticatedUserThreadLocal.get();
                final Element list = document.select(EXPERTS_LIST_CSS_SELECTOR).first();
                if (list != null && !list.html().contains(String.format("~%s", user.getName()))) {  //FIXME: better duplicate check
                    final String link = String.format(EXPERT_ITEM__TEMPLATE, user.getName(), user.getName(),
                            user.getKey().getStringValue(), user.getFullName());
                    final Element lastItem = list.select("li").last();
                    if (lastItem != null && lastItem.toString().contains("ac:placeholder")) { //FIXME: better placeholder check
                        lastItem.before(link);
                    } else {
                        list.append(link);
                        list.append(EXPERT_ITEM_PLACEHOLDER);
                    }

                    // 3. save new content
                    final BodyContent content = new BodyContent();
                    content.setBody(document.toString());
                    page.setBodyContent(content);

                    log.info("Added {} as expert on page {}", user.getName(), page.getId());
                    return Action.SUCCESS;
                } else {
                    log.warn("Not necessary to add experts to page {}", page.getId());
                    return NONE;
                }
            } catch (RuntimeException e) {
                log.error("Error intercepting content for page {}: {}", page.getId(), e.getMessage());
                return Action.ERROR;
            }
        } else {
            log.debug("Ignored non-expertfinder page");
            return Action.NONE;
        }
    }

}
