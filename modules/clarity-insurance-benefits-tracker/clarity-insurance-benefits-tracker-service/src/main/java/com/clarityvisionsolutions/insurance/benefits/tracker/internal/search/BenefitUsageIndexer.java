package com.clarityvisionsolutions.insurance.benefits.tracker.internal.search;

import com.clarityvisionsolutions.insurance.benefits.tracker.model.BenefitUsage;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.BenefitUsageLocalService;
import com.liferay.knowledge.base.util.KnowledgeBaseUtil;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.*;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

@Component(service = Indexer.class)
public class BenefitUsageIndexer extends BaseIndexer<BenefitUsage> {

    public static final String CLASS_NAME = BenefitUsage.class.getName();

    public BenefitUsageIndexer() {
        super();

        setDefaultSelectedFieldNames(
                Field.COMPANY_ID, Field.CONTENT, Field.CREATE_DATE,
                Field.DESCRIPTION, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
                Field.MODIFIED_DATE, Field.TITLE, Field.UID, Field.USER_NAME);

        setFilterSearch(true);
        setPermissionAware(true);
    }

    @Override
    public boolean hasPermission(
            PermissionChecker permissionChecker, String entryClassName,
            long entryClassPK, String actionId)
            throws PortalException {

        return _benefitUsageModelResourcePermission.contains(
                permissionChecker, entryClassPK, ActionKeys.VIEW);
    }

    @Override
    public void postProcessSearchQuery(
            BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
            SearchContext searchContext)
            throws Exception {

        if (searchContext.isIncludeAttachments() ||
                searchContext.isIncludeDiscussions()) {

            addSearchTerm(searchQuery, searchContext, Field.CONTENT, true);
            addSearchTerm(searchQuery, searchContext, Field.DESCRIPTION, true);
            addSearchTerm(searchQuery, searchContext, Field.TITLE, true);
            addSearchTerm(searchQuery, searchContext, Field.USER_NAME, true);

            return;
        }

        BooleanQuery keywordsBooleanQuery = new BooleanQueryImpl();

        addSearchTerm(keywordsBooleanQuery, searchContext, Field.CONTENT, true);
        addSearchTerm(
                keywordsBooleanQuery, searchContext, Field.DESCRIPTION, true);
        addSearchTerm(keywordsBooleanQuery, searchContext, Field.TITLE, true);
        addSearchTerm(
                keywordsBooleanQuery, searchContext, Field.USER_NAME, true);

        if (!keywordsBooleanQuery.hasClauses()) {
            return;
        }

        try {
            BooleanQuery modelBooleanQuery = new BooleanQueryImpl();

            modelBooleanQuery.add(
                    new TermQueryImpl("entryClassName", CLASS_NAME),
                    BooleanClauseOccur.MUST);
            modelBooleanQuery.add(
                    keywordsBooleanQuery, BooleanClauseOccur.MUST);

            searchQuery.add(modelBooleanQuery, BooleanClauseOccur.SHOULD);
        }
        catch (ParseException parseException) {
            throw new SystemException(parseException);
        }
    }

    @Override
    public Hits search(SearchContext searchContext) throws SearchException {
        Hits hits = super.search(searchContext);

        String[] queryTerms = ArrayUtil.append(
                GetterUtil.getStringValues(hits.getQueryTerms()),
                KnowledgeBaseUtil.splitKeywords(searchContext.getKeywords()));

        hits.setQueryTerms(queryTerms);

        return hits;
    }

    @Override
    protected void doDelete(BenefitUsage benefitUsage) throws Exception {
        deleteDocument(benefitUsage.getCompanyId(), benefitUsage.getBenefitUsageId());
    }

    @Override
    protected Document doGetDocument(BenefitUsage benefitUsage) throws Exception {
        Document document = getBaseModelDocument(CLASS_NAME, benefitUsage);

        _serviceTrackerList.forEach(modelDocumentContributor -> modelDocumentContributor.contribute(document, benefitUsage));

        return document;
    }

    @Override
    protected Summary doGetSummary(Document document, Locale locale, String snippet, PortletRequest portletRequest, PortletResponse portletResponse) throws Exception {
        String prefix = Field.SNIPPET + StringPool.UNDERLINE;

        String title = document.get(prefix + Field.TITLE, Field.TITLE);

        String content = snippet;

        if (Validator.isNull(snippet)) {
            content = document.get(
                    prefix + Field.DESCRIPTION, Field.DESCRIPTION);

            if (Validator.isNull(content)) {
                content = document.get(prefix + Field.CONTENT, Field.CONTENT);
            }
        }

        Summary summary = new Summary(title, content);

        summary.setMaxContentLength(200);

        return summary;
    }

    @Override
    protected void doReindex(String className, long classPK) throws Exception {
        BenefitUsage benefitUsage = benefitUsageLocalService.fetchBenefitUsage(classPK);

        if (benefitUsage != null) {
            _reindexBenefitUsages(benefitUsage);
        }
    }

    @Override
    protected void doReindex(String[] ids) throws Exception {
        long companyId = GetterUtil.getLong(ids[0]);

        _log.info("Reindexing benefit usages for company " + companyId + "...");

        _reindexBenefitUsages(companyId);
    }

    @Override
    protected void doReindex(BenefitUsage benefitUsage) throws Exception {
        indexWriterHelper.updateDocument(
                benefitUsage.getCompanyId(), getDocument(benefitUsage));
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }

    @Activate
    protected void activate(BundleContext bundleContext) {
        _serviceTrackerList = ServiceTrackerListFactory.open(
                bundleContext,
                (Class<ModelDocumentContributor<BenefitUsage>>)
                        (Class<?>)ModelDocumentContributor.class,
                "(indexer.class.name=com.clarityvisionsolutions.insurance.benefits.tracker.model.BenefitUsage)");
    }

    @Deactivate
    protected void deactivate() {
        _serviceTrackerList.close();
    }

    private void _reindexBenefitUsages(long companyId) throws Exception {
        IndexableActionableDynamicQuery indexableActionableDynamicQuery =
                benefitUsageLocalService.getIndexableActionableDynamicQuery();

        indexableActionableDynamicQuery.setAddCriteriaMethod(
                dynamicQuery -> {
                    Property property = PropertyFactoryUtil.forName("status");

                    dynamicQuery.add(
                            property.eq(WorkflowConstants.STATUS_APPROVED));
                });
        indexableActionableDynamicQuery.setCompanyId(companyId);
        indexableActionableDynamicQuery.setPerformActionMethod(
                (BenefitUsage benefitUsage) -> {
                    try {
                        indexableActionableDynamicQuery.addDocuments(
                                getDocument(benefitUsage));
                    }
                    catch (PortalException portalException) {
                        if (_log.isWarnEnabled()) {
                            _log.warn(
                                    "Unable to index benefit usage " +
                                            benefitUsage.getBenefitUsageId(),
                                    portalException);
                        }
                    }
                });

        indexableActionableDynamicQuery.performActions();
    }

    private void _reindexBenefitUsages(BenefitUsage benefitUsage) throws Exception {
        Collection<Document> documents = new ArrayList<>();

            documents.add(getDocument(benefitUsage));

        indexWriterHelper.updateDocuments(
                benefitUsage.getCompanyId(), documents, isCommitImmediately());
    }

    private static final Log _log = LogFactoryUtil.getLog(BenefitUsageIndexer.class);

    @Reference
    protected IndexWriterHelper indexWriterHelper;

    @Reference
    protected BenefitUsageLocalService benefitUsageLocalService;

    @Reference(target = "(model.class.name=com.clarityvisionsolutions.insurance.benefits.tracker.model.BenefitUsage)")
    private ModelResourcePermission<BenefitUsage> _benefitUsageModelResourcePermission;

    private ServiceTrackerList<ModelDocumentContributor<BenefitUsage>> _serviceTrackerList;
}
