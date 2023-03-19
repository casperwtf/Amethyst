package wtf.casper.amethyst.core.quartz;

import lombok.Getter;

import java.util.Properties;

public class QuartzProperties {
    @Getter
    private final static Properties properties = new Properties();

    static {
        properties.setProperty("org.quartz.scheduler.instanceName", "AmethystScheduler");
        properties.setProperty("org.quartz.scheduler.instanceId", "AUTO");
        properties.setProperty("org.quartz.scheduler.skipUpdateCheck", "true");
        properties.setProperty("org.quartz.scheduler.makeSchedulerThreadDaemon", "true");
        properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        properties.setProperty("org.quartz.threadPool.threadCount", "3");
        properties.setProperty("org.quartz.threadPool.threadPriority", "5");
        properties.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
        properties.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
        properties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
    }
}
