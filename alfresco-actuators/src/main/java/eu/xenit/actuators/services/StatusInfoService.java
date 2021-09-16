package eu.xenit.actuators.services;

import static eu.xenit.actuators.Health.KEY_ERROR;
import static eu.xenit.actuators.Health.KEY_OUTPUT;

import eu.xenit.actuators.Health;
import eu.xenit.actuators.HealthIndicator;
import eu.xenit.actuators.HealthStatus;
import eu.xenit.actuators.model.gen.StatusInfo;
import java.util.Collections;
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

    public static final String GLOBAL_HEALTH_ON_READONLY = "eu.xenit.actuators.repository.healthyOnReadOnly";

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
    private StatusInfo retrieveStatusInfo() {
        final StatusInfo statusInfo = new StatusInfo();
        retryingTransactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
            @Override
            public Object execute() throws Throwable {
                AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
                    @Override
                    public Object doWork() throws Exception {
                        statusInfo
                                .readOnly(repoAdminService.getUsage().isReadOnly())
                                .auditEnabled(auditService.isAuditEnabled())
                                .thumbnailGeneration(thumbnailService.getThumbnailsEnabled());
                        return null;
                    }
                }, AuthenticationUtil.getAdminUserName());
                return null;
            }
        }, true);
        return statusInfo;
    }

    @Override
    public Health isHealthy() {
        Health health = new Health();
        try {
            StatusInfo statusInfo = retrieveStatusInfo();
            health.setDetails(Collections.singletonMap(KEY_OUTPUT, statusInfo.toString()));
            boolean healthyOnReadonly = Boolean.parseBoolean(
                    (String) globalProperties.getOrDefault(GLOBAL_HEALTH_ON_READONLY, "true"));
            if (!healthyOnReadonly && statusInfo.getReadOnly()) {
                health.setStatus(HealthStatus.DOWN);
                health.setDetails(Collections.singletonMap(KEY_ERROR, "Alfresco is in readonly. Handling as down."));
            } else {
                health.setStatus(HealthStatus.UP);
            }
        } catch (Exception exception) {
            health.setStatus(HealthStatus.DOWN);
            health.setDetails(Collections.singletonMap(KEY_ERROR, exception.getMessage()));
        }
        return health;
    }
}
