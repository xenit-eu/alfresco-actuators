package eu.xenit.actuators.services;

import eu.xenit.actuators.Health;
import eu.xenit.actuators.HealthIndicator;
import eu.xenit.actuators.model.gen.CpuInfo;
import eu.xenit.actuators.model.gen.JavaInfo;
import eu.xenit.actuators.model.gen.OperatingSystemInfo;
import eu.xenit.actuators.model.gen.SystemInfo;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class SystemInfoService implements HealthIndicator {
    private static final Logger logger = LoggerFactory.getLogger(HealthIndicator.class);

    SystemInfo getSystemInfo() {
        return new SystemInfo()
                .os(osInfo())
                .java(javaInfo())
                .cpu(cpuInfo());

    }

    private static OperatingSystemInfo osInfo() {
        OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();

        return new OperatingSystemInfo()
                .name(osMXBean.getName())
                .version(osMXBean.getVersion())
                .arch(osMXBean.getArch());

    }

    private static JavaInfo javaInfo() {

        List<GarbageCollectorMXBean> gcMxBeans = ManagementFactory.getGarbageCollectorMXBeans();

        List<String> gcs = new ArrayList<>(gcMxBeans.size());
        for (GarbageCollectorMXBean bean : gcMxBeans)
            gcs.add(bean.getName());


        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

        Map<String, String> javaProperties = new TreeMap<>();
        for (Map.Entry<String,String> entry : runtimeMXBean.getSystemProperties().entrySet()) {
            if (entry.getKey().startsWith("java")){
                String key = entry.getKey().substring(5);
                if (!javaProperties.containsKey(key))
                    javaProperties.put(key,entry.getValue());
            }
        }


        return new JavaInfo()
                .inputArguments(runtimeMXBean.getInputArguments())
                .garbageCollectors(gcs)
                .systemProperties(javaProperties);
    }

    private static CpuInfo cpuInfo() {
        OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
        return new CpuInfo()
                .processors(osMXBean.getAvailableProcessors());
    }

    @Override
    public Health isHealthy() {
        Health health = new Health();
        try {
            SystemInfo systemInfo = getSystemInfo();
            health.setStatus("UP");
            health.setDetails(Collections.singletonMap("output",systemInfo.toString()));
        } catch (Exception e) {
            health.setStatus("DOWN");
            health.setDetails(Collections.singletonMap("error",e.getMessage()));
        }
        return health;
    }
}
