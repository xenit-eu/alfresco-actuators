package eu.xenit.actuators.services;

import eu.xenit.actuators.Health;
import eu.xenit.actuators.HealthDetailsError;
import eu.xenit.actuators.HealthIndicator;
import eu.xenit.actuators.HealthStatus;
import eu.xenit.actuators.model.gen.StatusInfo;
import java.util.Properties;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.admin.RepoAdminService;
import org.alfresco.service.cmr.audit.AuditService;
import org.alfresco.service.cmr.thumbnail.ThumbnailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class StatusInfoService implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(StatusInfoService.class);

    @Autowired
    private RepoAdminService repoAdminService;
    @Autowired
    private AuditService auditService;
    @Autowired
    private ThumbnailService thumbnailService;
    @Autowired
    private RetryingTransactionHelper retryingTransactionHelper;
    @Autowired
    @Qualifier("global-properties")
    private Properties globalProperties;

    // needs authentication
    protected StatusInfo retrieveStatusInfo() {
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

    @Override
    public Health isHealthy() {
        Health health = new Health();
        try {
            StatusInfo statusInfo = retrieveStatusInfo();
            health.setDetails(statusInfo);
            health.setStatus(HealthStatus.UP);
        } catch (Exception exception) {
            logger.error("Problem retrieving Status info", exception);
            health.setStatus(HealthStatus.DOWN);
            health.setDetails(new HealthDetailsError(exception.getMessage()));
        }
        return health;
    }
}
