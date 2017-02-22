package de.db.searchify.processor;

import com.google.common.collect.ImmutableMap;
import de.db.searchify.api.Processor;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Kurz (thomas.kurz@redlink.co)
 * @since 20.02.17.
 */
@Service
public class HtmlToMetadataProcessor implements Processor {

    @Value("${searchify.processor.html.description:description}")
    private String DESCRIPTION;

    @Value("${searchify.processor.html.knowledges:knowledges}")
    private String KNOWLEDGE_AREAS;

    @Value("${searchify.processor.html.experts:experts}")
    private String EXPERTS;

    @Value("${searchify.processor.html.applications:applications}")
    private String APPLICATIONS;

    @Value("${searchify.processor.html.technologies:technologies}")
    private String TECHNOLOGIES;

    @Value("${searchify.processor.html.qualifications:qualifications}")
    private String QUALIFICATIONS;

    @Value("${searchify.graph.vertex.body:dbsearch_content_t}")
    private String BODY;

    @Value("${searchify.graph.vertex.abstract:dbsearch_abstract_t}")
    private String ABSTRACT;

    @Value("${searchify.graph.vertex.link:dbsearch_link_s}")
    private String LINK;

    @Value("${searchify.graph.vertex.title:dbsearch_title_s}")
    private String TITLE;

    @Value("${searchify.graph.vertex.id:id}")
    private String ID;

    @Value("${searchify.graph.vertex.type:dbsearch_doctype_s}")
    private String TYPE;

    @Value("${searchify.processor.html.base_url:https://expertfinder.db.redlink.io/confluence}")
    private String BASE_URL;

    @Autowired
    Graph graph;

    private ExpertiseScrappingParser parser = new ExpertiseScrappingParser();

    private Map<String,String> selectors;
    private Map<String,String> relations;

    @PostConstruct
    public void postConstruct() {
        selectors = ImmutableMap.of(
                KNOWLEDGE_AREAS,"ul.expertfinder.knowledges li",
                TECHNOLOGIES, "ul.expertfinder.technologies li",
                QUALIFICATIONS, "ul.expertfinder.qualifications li",
                APPLICATIONS, "ul.expertfinder.applications li"
        );

        relations = ImmutableMap.of(
                KNOWLEDGE_AREAS,"knowledge",
                TECHNOLOGIES, "technology",
                QUALIFICATIONS, "qualification",
                APPLICATIONS, "application"
        );
    }

    public void run() {
        graph.vertices().forEachRemaining(
                vertex -> {
                    if(vertex.property(BODY).isPresent()) {
                        parser.parseBody(vertex);
                    }
                }
        );
    }

    /**
     * Expertise confluence scrapping parser
     *
     * @author sergio.fernandez@redlink.co
     */
    private class ExpertiseScrappingParser {

        public static final String CONFLUENCE_CREATELINK_CLASS = "createlink";

        /**
         * Parse confluence body view
         *
         * @return map with all parsed (TODO: define proper api)
         */
        public void parseBody(Vertex vertex) {

            String body = (String) vertex.property(BODY).value();

            final Document doc = Jsoup.parse(body,BASE_URL);

            //description scrapping
            final Elements descriptionElement = doc.select("div.expertfinder.description");
            final Collection<String> description = new ArrayList<>();
            description.add(descriptionElement.text());
            vertex.property(ABSTRACT, description);

            for(Map.Entry<String,String> selector : selectors.entrySet()) {
                final Elements projectElements = doc.select(selector.getValue());
                for (Element element : projectElements) {
                    String link = getLink(element.select("a").first());
                    if(link != null) {
                        Vertex linkNode = getLinkNode(link,selector.getKey());
                        vertex.addEdge(relations.get(selector.getKey()), linkNode);
                    }
                }
            }

            //experts scrapping
            final Elements expertsElements = doc.select("ul.expertfinder.experts li");
            for (Element element : expertsElements) {
                final Element user = element.select("a").first();
                if (user != null && user.hasAttr("data-username")) {
                    final String username = user.attr("data-username");
                    final String href = user.attr("abs:href");
                    final String name = user.text();
                    Vertex u_vertex = getUserNode(username,href,name);
                    vertex.addEdge("expert", u_vertex);
                }
            }
        }

        private synchronized Vertex getUserNode(String username, String href, String name) {
            if(graph.traversal().V().has("id", href).hasNext()) {
                return graph.traversal().V().has("id",href).next();
            }
            else return graph.addVertex(
                    T.label, name,
                    "id", href,
                    "dbsearch_link_s", href,
                    "dbsearch_title_s", name,
                    "dbsearch_abstract_t", username,
                    TYPE, "Person"
            );
        }

        private synchronized Vertex getLinkNode(String link, String type) {
            if(graph.traversal().V().has("id",link).hasNext()) {
                return graph.traversal().V().has("id",link).next();
            }
            else return graph.addVertex(
                    "id", link,
                    T.label, link,
                    "dbsearch_title_s", link,
                    "dbsearch_link_s", link,
                    TYPE, type);
        }

        /**
         * Adds only actually existing links (see DBEF-28)
         *
         * @param element    element to check
         */
        private String getLink(Element element) {
            if (element != null && !element.hasClass(CONFLUENCE_CREATELINK_CLASS)) {
                return element.attr("abs:href");
            } return null;
        }

    }

}
