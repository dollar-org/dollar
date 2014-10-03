package com.cazcade.dollar.monitor;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 */
public class DefaultMonitor implements Monitor {
    private static final Logger log = Logger.getLogger("$");
    private static ThreadLocal<MetricRegistry> metrics = new ThreadLocal<MetricRegistry>() {
        @Override
        protected MetricRegistry initialValue() {
            MetricRegistry metricRegistry = new MetricRegistry();
            ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .build();
            reporter.start(30, TimeUnit.SECONDS);
            return metricRegistry;
        }
    };


    @Override
    public <R> R run(String simpleLabel, String namespacedLabel, String info, Supplier<R> code) {
        Timer timer = metrics.get().timer(namespacedLabel);
        log.debug("BEFORE : " + simpleLabel + " : " + info);
        Timer.Context time = timer.time();
        R r = code.get();
        time.stop();
        log.debug("AFTER : " + simpleLabel + " : " + info);
        return r;
    }

    @Override
    public void run(String simpleLabel, String namespacedLabel, String info, Runnable code) {
        Timer timer = metrics.get().timer(namespacedLabel);
        log.debug("BEFORE : " + simpleLabel + " : " + info);
        Timer.Context time = timer.time();
        code.run();
        time.stop();
        log.debug("AFTER : " + simpleLabel + " : " + info);
    }

}
