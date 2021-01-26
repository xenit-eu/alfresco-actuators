package eu.xenit.actuators.services;

import eu.xenit.actuators.model.gen.HealthInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HealthInfoService {

    @Autowired
    private AlfrescoInfoService alfrescoInfoService;
    @Autowired
    private SystemInfoService systemInfoService;

    public HealthInfo getHealthInfo() {
        return new HealthInfo()
                .system(systemInfoService.getSystemInfo())
                .alfresco(alfrescoInfoService.getAlfrescoInfo());
    }
}
