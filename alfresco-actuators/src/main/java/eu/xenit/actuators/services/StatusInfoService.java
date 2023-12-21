package eu.xenit.actuators.services;

import eu.xenit.actuators.HealthIndicator;
import eu.xenit.actuators.model.gen.StatusInfo;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.admin.RepoAdminService;
import org.alfresco.service.cmr.audit.AuditService;
import org.alfresco.service.cmr.thumbnail.ThumbnailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatusInfoService extends HealthIndicator {


    @Autowired
    private RepoAdminService repoAdminService;
    @Autowired
    private AuditService auditService;
    @Autowired
    private ThumbnailService thumbnailService;
    @Autowired
    private RetryingTransactionHelper retryingTransactionHelper;


    // needs authentication
    @Override
    protected StatusInfo getHealthDetails() {
        final StatusInfo statusInfo = new StatusInfo();
        retryingTransactionHelper.doInTransaction(() -> {
            AuthenticationUtil.runAs(() -> {
                statusInfo
                        .readOnly(repoAdminService.getUsage().isReadOnly())
                        .auditEnabled(auditService.isAuditEnabled())
                        .thumbnailGeneration(thumbnailService.getThumbnailsEnabled());
                return null;
            }, AuthenticationUtil.getAdminUserName());
            return null;
        }, true);
        return statusInfo;
    }

}
