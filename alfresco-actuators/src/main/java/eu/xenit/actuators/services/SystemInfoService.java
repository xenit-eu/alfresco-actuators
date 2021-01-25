package eu.xenit.actuators.services;

import eu.xenit.actuators.system.model.gen.CpuInfo;
import eu.xenit.actuators.system.model.gen.JavaInfo;
import eu.xenit.actuators.system.model.gen.OperatingSystemInfo;
import eu.xenit.actuators.system.model.gen.SystemInfo;
import org.springframework.stereotype.Service;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


@Service
public class SystemInfoService {

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
}
