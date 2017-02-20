AJS.toInit(function () {
    var $pageMetadata = AJS.$('#content.page.view .page-metadata:first');

    if ($pageMetadata.length > 0) {
        var selectedAjsParams = {
            pageId: AJS.params.pageId,
            pageTitle: AJS.params.pageTitle,
            parentPageId: AJS.params.parentPageId,
            spaceKey: AJS.params.spaceKey,
            spaceName: AJS.params.spaceName
        };
        var template = io.redlink.db.expertfinder.confluence.templates.listSelectedAjsParams(selectedAjsParams);
        $pageMetadata.after(template);
    }
});