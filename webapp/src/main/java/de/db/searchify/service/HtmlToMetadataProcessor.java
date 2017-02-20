package de.db.searchify.service;

import de.db.searchify.api.Processor;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    private String BODY = "body";

    @Autowired
    Graph graph;

    private ExpertiseScrappingParser parser = new ExpertiseScrappingParser();

    public void run() {
        graph.vertices().forEachRemaining(
                vertex -> {
                    if(vertex.property(BODY).isPresent()) {
                        handleVertex(vertex);
                    }
                }
        );
    }

    private void handleVertex(Vertex vertex) {
        Map<String,Collection<String>> results = parser.parseBody((String)vertex.property(BODY).value());

        //TODO adapt parser and write results
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
        public Map<String, Collection<String>> parseBody(String body) {
            final Map<String, Collection<String>> parsed = new HashMap();

            final Document doc = Jsoup.parse(body);

            //description scrapping
            final Elements descriptionElement = doc.select("div.expertfinder.description");
            final Collection<String> description = new ArrayList<>();
            description.add(descriptionElement.text());
            parsed.put(DESCRIPTION, description);

            //TODO: narrower items?

            //projects scrapping
            final Elements projectElements = doc.select("ul.expertfinder.knowledges li");
            final Collection<String> projects = new ArrayList<>();
            for (Element element : projectElements) {
                final Element project = element.select("a").first();
                addExistingLink(projects, project);
            }
            parsed.put(KNOWLEDGE_AREAS, projects);

            //applications scrapping
            final Elements applicationsElements = doc.select("ul.expertfinder.applications li");
            final Collection<String> applications = new ArrayList<>();
            for (Element element : applicationsElements) {
                final Element app = element.select("a").first();
                addExistingLink(applications, app);
            }
            parsed.put(APPLICATIONS, applications);

            //technologies scrapping
            final Elements technologiesElements = doc.select("ul.expertfinder.technologies li");
            final Collection<String> technologies = new ArrayList<>();
            for (Element element : technologiesElements) {
                final Element tech = element.select("a").first();
                addExistingLink(technologies, tech);
            }
            parsed.put(TECHNOLOGIES, technologies);

            //qualifications scrapping
            final Elements qualificationsElements = doc.select("ul.expertfinder.qualifications li");
            final Collection<String> qualifications = new ArrayList<>();
            for (Element element : qualificationsElements) {
                final Element qualification = element.select("a").first();
                addExistingLink(qualifications, qualification);
            }
            parsed.put(QUALIFICATIONS, qualifications);

            //experts scrapping
            final Elements expertsElements = doc.select("ul.expertfinder.experts li");
            final Collection<String> experts = new ArrayList<>();
            for (Element element : expertsElements) {
                final Element user = element.select("a").first();
                if (user != null && user.hasAttr("data-username")) {
                    final String username = user.attr("data-username");
                    experts.add(username);
                }
            }
            parsed.put(EXPERTS, experts);

            return parsed;
        }

        /**
         * Adds only actually existing links (see DBEF-28)
         *
         * @param collection collection to add the new element
         * @param element    element to check
         */
        private void addExistingLink(Collection<String> collection, Element element) {
            if (element != null && !element.hasClass(CONFLUENCE_CREATELINK_CLASS)) {
                collection.add(element.attr("abs:href"));
            }
        }
    }

}
