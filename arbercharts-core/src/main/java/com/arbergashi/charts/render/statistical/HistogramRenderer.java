package com.arbergashi.charts.render.statistical;

import com.arbergashi.charts.api.PlotContext;
import com.arbergashi.charts.model.ChartModel;
import com.arbergashi.charts.render.BaseRenderer;
import com.arbergashi.charts.util.ChartAssets;
import com.arbergashi.charts.util.ColorUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Professional, zero-allocation histogram renderer.
 *
 * @author Arber Gashi
 * @version 1.0.0
 * @since 2025-06-01
 */
public final class HistogramRenderer extends BaseRenderer {

    public HistogramRenderer() {
        super("histogram");
    }

    @Override
    protected void drawData(Graphics2D g2, ChartModel model, PlotContext context) {
        final int n = model.getPointCount();
        if (n == 0) return;

        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            double x = model.getX(i);
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
        }
        if (minX >= maxX) return;

        int bins = ChartAssets.getInt("chart.histogram.bins", 20);
        if (bins <= 0) bins = 20;

        double[] counts = com.arbergashi.charts.tools.RendererAllocationCache.getDoubleArray(this, "hist.counts", bins);
        double binWidth = (maxX - minX) / bins;

        for (int i = 0; i < n; i++) {
            double x = model.getX(i);
            int bin = (int) ((x - minX) / binWidth);
            if (bin >= bins) bin = bins - 1;
            double w = model.getValue(i, 2) > 0 ? model.getValue(i, 2) : 1.0; // weight
            counts[bin] += w;
        }

        double maxCount = 0;
        for (int i = 0; i < bins; i++) {
            double c = counts[i];
            if (c > maxCount) maxCount = c;
        }
        if (maxCount <= 0) return;

        Rectangle2D bounds = context.plotBounds();
        double plotX = bounds.getX();
        double plotY = bounds.getY();
        double plotW = bounds.getWidth();
        double plotH = bounds.getHeight();

        double barPixelWidth = plotW / bins;

        for (int i = 0; i < bins; i++) {
            double c = counts[i];
            if (c <= 0) continue;

            // Each bin gets a distinct color from the theme palette
            Color binColor = seriesOrBase(model, context, i);
            Color fill = ColorUtils.withAlpha(binColor, 0.67f);

            double heightRatio = c / maxCount;
            double h = plotH * heightRatio;
            double x = plotX + i * barPixelWidth;
            double y = plotY + (plotH - h);

            g2.setColor(fill);
            g2.fill(getRect(x, y, Math.max(1.0, barPixelWidth * 0.9), h));
        }

        if (ChartAssets.getBoolean("chart.histogram.drawOutline", false)) {
            g2.setStroke(getSeriesStroke());
            for (int i = 0; i < bins; i++) {
                double c = counts[i];
                if (c <= 0) continue;

                Color binColor = seriesOrBase(model, context, i);
                g2.setColor(binColor.darker());

                double heightRatio = c / maxCount;
                double h = plotH * heightRatio;
                double x = plotX + i * barPixelWidth;
                double y = plotY + (plotH - h);
                g2.draw(getRect(x, y, Math.max(1.0, barPixelWidth * 0.9), h));
            }
        }
    }
}
