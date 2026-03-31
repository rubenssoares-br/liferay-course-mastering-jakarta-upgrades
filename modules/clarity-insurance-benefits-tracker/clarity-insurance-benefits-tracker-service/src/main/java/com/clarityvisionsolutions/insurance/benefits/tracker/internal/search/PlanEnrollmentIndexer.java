package com.clarityvisionsolutions.insurance.benefits.tracker.internal.search;

import com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.PlanEnrollmentLocalService;
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
import java.util.Map;

@Component(service = Indexer.class)
public class PlanEnrollmentIndexer extends BaseIndexer<PlanEnrollment> {

    public static final String CLASS_NAME = PlanEnrollment.class.getName();

    public PlanEnrollmentIndexer() {
        super();

        setDefaultSelectedFieldNames(
                Field.COMPANY_ID, Field.CONTENT, Field.CREATE_DATE, Field.SCOPE_GROUP_ID,
                Field.GROUP_ID,
                Field.DESCRIPTION, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
                Field.MODIFIED_DATE, Field.TITLE, Field.UID);

        setFilterSearch(true);
        setPermissionAware(true);
    }

    @Override
    public boolean hasPermission(
            PermissionChecker permissionChecker, String entryClassName,
            long entryClassPK, String actionId)
            throws PortalException {

        return _planEnrollmentModelResourcePermission.contains(
                permissionChecker, entryClassPK, ActionKeys.VIEW);
    }

    @Override
    public void postProcessSearchQuery(
            BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
            SearchContext searchContext)
            throws Exception {

        BooleanQuery keywordsBooleanQuery = new BooleanQueryImpl();

        addSearchTerm(keywordsBooleanQuery, searchContext, Field.CONTENT, true);
        addSearchTerm(
                keywordsBooleanQuery, searchContext, Field.DESCRIPTION, true);
        addSearchTerm(keywordsBooleanQuery, searchContext, Field.TITLE, true);

        if (!keywordsBooleanQuery.hasClauses()) {
            _log.info("No clauses in the keyword query.");

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
            _log.error("Error adding query objects: " + parseException.getMessage(), parseException);

            throw new SystemException(parseException);
        }
    }

    @Override
    public Hits search(SearchContext searchContext) throws SearchException {
        _log.info("Need to search for plan enrollments...");
        _log.info("Search context: ");
        _log.info("  keywords:" + searchContext.getKeywords());
        _log.info("  company id:" + searchContext.getCompanyId());
        for (long groupId : searchContext.getGroupIds()) {
            _log.info("  group id:" + groupId);
        }
        _log.info("  user id:" + searchContext.getUserId());

        try {
            Hits hits = super.search(searchContext);

            String[] queryTerms = ArrayUtil.append(
                    GetterUtil.getStringValues(hits.getQueryTerms()),
                    KnowledgeBaseUtil.splitKeywords(searchContext.getKeywords()));

            hits.setQueryTerms(queryTerms);

            return hits;
        } catch (SearchException e) {
            _log.error("Error completing the search: " + e.getMessage(), e);

            throw e;
        }
    }

    @Override
    protected void doDelete(PlanEnrollment planEnrollment) throws Exception {
        deleteDocument(planEnrollment.getCompanyId(), planEnrollment.getPlanEnrollmentId());
    }

    @Override
    protected Document doGetDocument(PlanEnrollment planEnrollment) throws Exception {
        Document document = getBaseModelDocument(CLASS_NAME, planEnrollment);

        _serviceTrackerList.forEach(modelDocumentContributor -> modelDocumentContributor.contribute(document, planEnrollment));

        _log.info("Fields collected for enrollment doc:");
        Map<String, Field> fields = document.getFields();
        if (fields == null) {
            _log.info("No fields in the document");
        } else {
            for (String key : fields.keySet()) {
                Field field = fields.get(key);

                _log.info("  Key [" + key + "]: " + field.getValue());
            }
        }

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
        PlanEnrollment planEnrollment = planEnrollmentLocalService.fetchPlanEnrollment(classPK);

        if (planEnrollment != null) {
            _reindexPlanEnrollments(planEnrollment);
        }
    }

    @Override
    protected void doReindex(String[] ids) throws Exception {
        long companyId = GetterUtil.getLong(ids[0]);

        _log.info("Reindexing plan enrollments for company " + companyId + "...");

        _reindexPlanEnrollments(companyId);
    }

    @Override
    protected void doReindex(PlanEnrollment planEnrollment) throws Exception {
        indexWriterHelper.updateDocument(
                planEnrollment.getCompanyId(), getDocument(planEnrollment));
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }

    @Activate
    protected void activate(BundleContext bundleContext) {
        _serviceTrackerList = ServiceTrackerListFactory.open(
                bundleContext,
                (Class<ModelDocumentContributor<PlanEnrollment>>)
                        (Class<?>)ModelDocumentContributor.class,
                "(indexer.class.name=com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment)");
    }

    @Deactivate
    protected void deactivate() {
        _serviceTrackerList.close();
    }

    private void _reindexPlanEnrollments(long companyId) throws Exception {
        IndexableActionableDynamicQuery indexableActionableDynamicQuery =
                planEnrollmentLocalService.getIndexableActionableDynamicQuery();

        indexableActionableDynamicQuery.setAddCriteriaMethod(
                dynamicQuery -> {
                    Property property = PropertyFactoryUtil.forName("status");

                    dynamicQuery.add(
                            property.eq(WorkflowConstants.STATUS_APPROVED));
                });
        indexableActionableDynamicQuery.setCompanyId(companyId);
        indexableActionableDynamicQuery.setPerformActionMethod(
                (PlanEnrollment planEnrollment) -> {
                    try {
                        indexableActionableDynamicQuery.addDocuments(
                                getDocument(planEnrollment));
                    }
                    catch (PortalException portalException) {
                        if (_log.isWarnEnabled()) {
                            _log.warn(
                                    "Unable to index plan enrollment " +
                                            planEnrollment.getPlanEnrollmentId(),
                                    portalException);
                        }
                    }
                });

        indexableActionableDynamicQuery.performActions();
    }

    private void _reindexPlanEnrollments(PlanEnrollment planEnrollment) throws Exception {
        Collection<Document> documents = new ArrayList<>();

            documents.add(getDocument(planEnrollment));

        indexWriterHelper.updateDocuments(
                planEnrollment.getCompanyId(), documents, isCommitImmediately());
    }

    private static final Log _log = LogFactoryUtil.getLog(PlanEnrollmentIndexer.class);

    @Reference
    protected IndexWriterHelper indexWriterHelper;

    @Reference
    protected PlanEnrollmentLocalService planEnrollmentLocalService;

    @Reference(target = "(model.class.name=com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment)")
    private ModelResourcePermission<PlanEnrollment> _planEnrollmentModelResourcePermission;

    private ServiceTrackerList<ModelDocumentContributor<PlanEnrollment>> _serviceTrackerList;
}
