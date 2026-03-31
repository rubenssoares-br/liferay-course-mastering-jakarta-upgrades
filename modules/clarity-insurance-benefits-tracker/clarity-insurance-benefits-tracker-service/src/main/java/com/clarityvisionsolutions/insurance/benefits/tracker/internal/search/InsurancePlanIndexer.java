package com.clarityvisionsolutions.insurance.benefits.tracker.internal.search;

import com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.InsurancePlanLocalService;
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
public class InsurancePlanIndexer extends BaseIndexer<InsurancePlan> {

    public static final String CLASS_NAME = InsurancePlan.class.getName();

    public InsurancePlanIndexer() {
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

        return _insurancePlanModelResourcePermission.contains(
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
    protected void doDelete(InsurancePlan insurancePlan) throws Exception {
        deleteDocument(insurancePlan.getCompanyId(), insurancePlan.getInsurancePlanId());
    }

    @Override
    protected Document doGetDocument(InsurancePlan insurancePlan) throws Exception {
        Document document = getBaseModelDocument(CLASS_NAME, insurancePlan);

        _serviceTrackerList.forEach(modelDocumentContributor -> modelDocumentContributor.contribute(document, insurancePlan));

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
        InsurancePlan insurancePlan = insurancePlanLocalService.fetchInsurancePlan(classPK);

        if (insurancePlan != null) {
            _reindexInsurancePlans(insurancePlan);
        }
    }

    @Override
    protected void doReindex(String[] ids) throws Exception {
        long companyId = GetterUtil.getLong(ids[0]);

        _log.info("Reindexing insurance plans for company " + companyId + "...");

        _reindexInsurancePlans(companyId);
    }

    @Override
    protected void doReindex(InsurancePlan insurancePlan) throws Exception {
        indexWriterHelper.updateDocument(
                insurancePlan.getCompanyId(), getDocument(insurancePlan));
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }

    @Activate
    protected void activate(BundleContext bundleContext) {
        _serviceTrackerList = ServiceTrackerListFactory.open(
                bundleContext,
                (Class<ModelDocumentContributor<InsurancePlan>>)
                        (Class<?>)ModelDocumentContributor.class,
                "(indexer.class.name=com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan)");
    }

    @Deactivate
    protected void deactivate() {
        _serviceTrackerList.close();
    }

    private void _reindexInsurancePlans(long companyId) throws Exception {
        IndexableActionableDynamicQuery indexableActionableDynamicQuery =
                insurancePlanLocalService.getIndexableActionableDynamicQuery();

        indexableActionableDynamicQuery.setAddCriteriaMethod(
                dynamicQuery -> {
                    Property property = PropertyFactoryUtil.forName("status");

                    dynamicQuery.add(
                            property.eq(WorkflowConstants.STATUS_APPROVED));
                });
        indexableActionableDynamicQuery.setCompanyId(companyId);
        indexableActionableDynamicQuery.setPerformActionMethod(
                (InsurancePlan insurancePlan) -> {
                    try {
                        indexableActionableDynamicQuery.addDocuments(
                                getDocument(insurancePlan));
                    }
                    catch (PortalException portalException) {
                        if (_log.isWarnEnabled()) {
                            _log.warn(
                                    "Unable to index insurance plan " +
                                            insurancePlan.getInsurancePlanId(),
                                    portalException);
                        }
                    }
                });

        indexableActionableDynamicQuery.performActions();
    }

    private void _reindexInsurancePlans(InsurancePlan insurancePlan) throws Exception {
        Collection<Document> documents = new ArrayList<>();

            documents.add(getDocument(insurancePlan));

        indexWriterHelper.updateDocuments(
                insurancePlan.getCompanyId(), documents, isCommitImmediately());
    }

    private static final Log _log = LogFactoryUtil.getLog(InsurancePlanIndexer.class);

    @Reference
    protected IndexWriterHelper indexWriterHelper;

    @Reference
    protected InsurancePlanLocalService insurancePlanLocalService;

    @Reference(target = "(model.class.name=com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan)")
    private ModelResourcePermission<InsurancePlan> _insurancePlanModelResourcePermission;

    private ServiceTrackerList<ModelDocumentContributor<InsurancePlan>> _serviceTrackerList;
}
