package io.redlink.db.expertfinder.confluence.impl;

import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import io.redlink.db.expertfinder.confluence.api.ExpertFinderPluginComponent;

import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService ({ExpertFinderPluginComponent.class})
@Named ("expertfinderPluginComponent")
public class ExpertFinderPluginComponentImpl implements ExpertFinderPluginComponent {

    @ComponentImport
    private final ApplicationProperties applicationProperties;

    @ComponentImport
    private final I18nResolver i18nResolver;

    @Inject
    public ExpertFinderPluginComponentImpl(final ApplicationProperties applicationProperties, I18nResolver i18nResolver) {
        this.applicationProperties = applicationProperties;
        this.i18nResolver = i18nResolver;
    }

    @Override
    public String getName() {
        if(null != applicationProperties) {
            return String.format("ExpertFinder %s Plugin", applicationProperties.getDisplayName());
        } else {
            return "ExpertFinder Plugin";
        }
    }

}