package {{ root_package }}.tracing;

import datadog.trace.api.GlobalTracer;
import datadog.trace.api.interceptor.MutableSpan;
import datadog.trace.api.interceptor.TraceInterceptor;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TracingInitializer {

    private static final Set<String> excludedResourcePrefixes;

    static {
        excludedResourcePrefixes = new HashSet<>();
        excludedResourcePrefixes.add("GET /health");
        excludedResourcePrefixes.add("GET /prometheus");
    }

    public static void initialize() {
        GlobalTracer.get().addTraceInterceptor(new TraceInterceptor() {
            @Override
            public Collection<? extends MutableSpan> onTraceComplete(Collection<? extends MutableSpan> trace) {
                for (MutableSpan span : trace) {
                    for (String excludedResource : excludedResourcePrefixes) {
                        if (StringUtils.startsWith(span.getResourceName(), excludedResource)) {
                            return Collections.emptyList();
                        }
                    }
                }
                return trace;
            }
            @Override
            public int priority() {
                return 200;  // Some unique number
            }
        });
    }
}
