package com.hubspot.singularity.executor.cleanup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hubspot.mesos.JavaUtils;
import com.hubspot.mesos.client.SingularityMesosClientModule;
import com.hubspot.singularity.executor.SingularityExecutorCleanupStatistics;
import com.hubspot.singularity.executor.cleanup.config.SingularityExecutorCleanupConfiguration;
import com.hubspot.singularity.executor.cleanup.config.SingularityExecutorCleanupConfigurationLoader;
import com.hubspot.singularity.executor.config.SingularityExecutorConfigurationLoader;
import com.hubspot.singularity.executor.config.SingularityExecutorModule;
import com.hubspot.singularity.runner.base.config.SingularityRunnerBaseModule;
import com.hubspot.singularity.runner.base.shared.JsonObjectFileHelper;
import com.hubspot.singularity.s3.base.config.SingularityS3ConfigurationLoader;


public class SingularityExecutorCleanupRunner {

  private static final Logger LOG = LoggerFactory.getLogger(SingularityExecutorCleanupRunner.class);

  public static void main(String... args) {
    final long start = System.currentTimeMillis();

    try {
      final Injector injector = Guice.createInjector(new SingularityRunnerBaseModule(new SingularityS3ConfigurationLoader(), new SingularityExecutorConfigurationLoader(), new SingularityExecutorCleanupConfigurationLoader()), new SingularityExecutorModule(), new SingularityMesosClientModule());
      final SingularityExecutorCleanupRunner runner = injector.getInstance(SingularityExecutorCleanupRunner.class);

      LOG.info("Starting cleanup");

      final SingularityExecutorCleanupStatistics statistics = runner.cleanup();

      LOG.info("Finished with {} after {}", statistics, JavaUtils.duration(start));

      System.exit(0);
    } catch (Throwable t) {
      LOG.error("Finished after {} with error", JavaUtils.duration(start), t);
      System.exit(1);
    }
  }

  private final SingularityExecutorCleanup cleanup;
  private final JsonObjectFileHelper fileHelper;
  private final SingularityExecutorCleanupConfiguration cleanupConfiguration;

  @Inject
  public SingularityExecutorCleanupRunner(SingularityExecutorCleanup cleanup, JsonObjectFileHelper fileHelper, SingularityExecutorCleanupConfiguration cleanupConfiguration) {
    this.cleanup = cleanup;
    this.fileHelper = fileHelper;
    this.cleanupConfiguration = cleanupConfiguration;
  }

  public SingularityExecutorCleanupStatistics cleanup() {
    SingularityExecutorCleanupStatistics cleanupStatistics = cleanup.clean();

    fileHelper.writeObject(cleanupStatistics, cleanupConfiguration.getExecutorCleanupResultsDirectory().resolve(String.format("%s%s", System.currentTimeMillis(), cleanupConfiguration.getExecutorCleanupResultsSuffix())), LOG);

    return cleanupStatistics;
  }

}
