package com.arbergashi.charts.rendererpanels.statistical;

import com.arbergashi.charts.model.DefaultChartModel;
import com.arbergashi.charts.render.statistical.BoxPlotRenderer;
import com.arbergashi.charts.ui.ArberChartPanel;
import com.arbergashi.charts.api.ArberChartBuilder;
import com.arbergashi.charts.rendererpanels.DemoPanelUtils;
import java.util.Random;

public class BoxPlotChartPanelProvider {
    public static ArberChartPanel create() {
        // Response Time Distribution Across Microservices
        // Professional statistical analysis for DevOps/SRE teams
        DefaultChartModel model = new DefaultChartModel("Response Time (ms)");
        Random rand = new Random(DemoPanelUtils.DEMO_SEED + 100);
        
        String[] services = {"Auth Service", "User API", "Order API", "Payment Gateway", "Search", "Notifications"};

        for (int i = 0; i < services.length; i++) {
            // Generate realistic response time distributions
            double baseLatency = 18 + i * 12 + rand.nextDouble() * 8;
            double median = baseLatency + rand.nextDouble() * 6;
            double iqr = 7 + rand.nextDouble() * 10;
            double q1 = median - iqr / 2;
            double q3 = median + iqr / 2;
            double min = Math.max(5, q1 - 1.5 * iqr + rand.nextDouble() * 5);
            double max = q3 + 1.5 * iqr + rand.nextDouble() * 18;

            // addPoint(x, median, min, max, iqr, label)
            model.addPoint(i, median, min, max, iqr,
                String.format("%s (p50: %.1fms)", services[i], median));
        }
        
        return ArberChartBuilder.create()
                .withTitle("Microservice Response Time Distribution")
                .addLayer(model, new BoxPlotRenderer())
                .withTooltips(true)
                .withLegend(true)
                .withAnimations(true)
                .build();
    }
}
