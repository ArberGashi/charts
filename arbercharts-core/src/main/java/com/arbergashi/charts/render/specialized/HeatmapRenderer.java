package com.arbergashi.charts.render.specialized;

import com.arbergashi.charts.api.ChartTheme;
import com.arbergashi.charts.api.PlotContext;
import com.arbergashi.charts.internal.RendererDescriptor;
import com.arbergashi.charts.render.RendererRegistry;
import com.arbergashi.charts.model.ChartModel;
import com.arbergashi.charts.render.BaseRenderer;
import com.arbergashi.charts.render.ChartRenderer;
import com.arbergashi.charts.util.ChartAssets;
import com.arbergashi.charts.util.ColorUtils;
import com.arbergashi.charts.util.MathUtils;

import java.awt.*;

/**
 * Renderer for 2D heatmaps.
 *
 * <p>Unified renderer for density and grid visualizations.
 * Supports two modes:
 * 1. <b>Grid mode:</b> Direct rendering of a double[][] matrix (via {@link #setGridData}).
 * 2. <b>Density mode:</b> Automatic binning of {@link ChartModel} points into a grid.</p>
 *
 * <p>Optimized for performance via direct pixel operations and a color LUT.</p>
 *
 * @author Arber Gashi
 * @version 1.0.0
 * @since 2025-06-01
 */
public class HeatmapRenderer extends BaseRenderer implements ChartRenderer {
    static {
        RendererRegistry.register(
                "heatmap",
                new RendererDescriptor("heatmap", "renderer.heatmap", "/icons/heatmap.svg"),
                HeatmapRenderer::new
        );
    }

    // Mapping Buffer
    private final double[] p0 = new double[2];
    private final double[] p1 = new double[2];
    // Zero-Allocation: Color Lookup Table (LUT)
    private final Color[] colorLut = new Color[256];
    private ChartTheme lastTheme;
    private boolean lastMultiColor;
    private double[][] gridData;
    private double minVal = 0;
    private double maxVal = 1;
    private double gridMinX, gridMaxX, gridMinY, gridMaxY;

    public HeatmapRenderer() {
        super("heatmap");
    }

    @Override
    public String getName() {
        return "Heatmap";
    }

    @Override
    public String getTooltipText(int index, ChartModel model) {
        // Simple tooltip: show value at index if available
        if (model == null || index < 0 || index >= model.getPointCount()) return null;
        double x = model.getXData()[index];
        double y = model.getYData()[index];
        return String.format("(%.2f, %.2f)", x, y);
    }

    @Override
    public java.util.Optional<Integer> getPointAt(java.awt.geom.Point2D pixel, ChartModel model, PlotContext context) {
        // Not implemented: return empty
        return java.util.Optional.empty();
    }

    /**
     * Sets the grid data.
     *
     * @param data [rows][cols] array (row = y, col = x).
     * @param minX Start X.
     * @param maxX End X.
     * @param minY Start Y.
     * @param maxY End Y.
     */
    public void setGridData(double[][] data, double minX, double maxX, double minY, double maxY) {
        this.gridData = data;
        this.gridMinX = minX;
        this.gridMaxX = maxX;
        this.gridMinY = minY;
        this.gridMaxY = maxY;

        // Auto range for colors.
        minVal = Double.MAX_VALUE;
        maxVal = -Double.MAX_VALUE;
        for (double[] row : data) {
            for (double v : row) {
                if (v < minVal) minVal = v;
                if (v > maxVal) maxVal = v;
            }
        }
    }

    @Override
    protected void drawData(Graphics2D g2, ChartModel model, PlotContext context) {
        ensureLut(context);
        // Mode decision: explicit grid or automatic binning?
        if (gridData != null && gridData.length > 0) {
            drawGrid(g2, gridData, gridMinX, gridMaxX, gridMinY, gridMaxY, context);
        } else {
            drawDensityFromModel(g2, model, context);
        }
    }

    private void drawDensityFromModel(Graphics2D g2, ChartModel model, PlotContext context) {
        int count = model.getPointCount();
        if (count == 0) return;

        int cols = ChartAssets.getInt("chart.heatmap.cols", 64);
        int rows = ChartAssets.getInt("chart.heatmap.rows", 64);

        // Temporary grid for binning (stack/heap, depends on resolution).
        // For zero-allocation, this could be cached when cols/rows do not change.
        double[][] bins = new double[rows][cols];

        double minX = context.minX();
        double maxX = context.maxX();
        double minY = context.minY();
        double maxY = context.maxY();

        double rangeX = maxX - minX;
        double rangeY = maxY - minY;
        if (rangeX <= 0 || rangeY <= 0) return;

        double[] xData = model.getXData();
        double[] yData = model.getYData();

        double maxBin = 0;

        for (int i = 0; i < count; i++) {
            double x = xData[i];
            double y = yData[i];

            if (x < minX || x >= maxX || y < minY || y >= maxY) continue;

            int c = (int) ((x - minX) / rangeX * cols);
            int r = (int) ((y - minY) / rangeY * rows);

            // Clamp indices
            if (c >= cols) c = cols - 1;
            if (r >= rows) r = rows - 1;

            bins[r][c]++;
            if (bins[r][c] > maxBin) maxBin = bins[r][c];
        }

        if (maxBin > 0) {
            // Temporarily override min/max for this render pass.
            double oldMin = this.minVal;
            double oldMax = this.maxVal;
            this.minVal = 0;
            this.maxVal = maxBin;

            drawGrid(g2, bins, minX, maxX, minY, maxY, context);

            this.minVal = oldMin;
            this.maxVal = oldMax;
        }
    }

    private void drawGrid(Graphics2D g2, double[][] data, double minX, double maxX, double minY, double maxY, PlotContext context) {
        int rows = data.length;
        int cols = data[0].length;

        double stepX = (maxX - minX) / cols;
        double stepY = (maxY - minY) / rows;

        // Visibility check (culling).
        if (maxX < context.minX() || minX > context.maxX() ||
                maxY < context.minY() || minY > context.maxY()) {
            return;
        }

        // Iterate over the grid.
        for (int r = 0; r < rows; r++) {
            double y = minY + r * stepY;
            // Y-Culling
            if (y + stepY < context.minY() || y > context.maxY()) continue;

            for (int c = 0; c < cols; c++) {
                double x = minX + c * stepX;
                // X-Culling
                if (x + stepX < context.minX() || x > context.maxX()) continue;

                double val = data[r][c];
                if (val <= minVal) continue; // Skip empty/low cells

                // Compute color (simple heatmap gradient: blue -> green -> red).
                float norm = (float) MathUtils.clamp((val - minVal) / (maxVal - minVal), 0, 1);
                int colorIdx = (int) (norm * 255);
                g2.setColor(colorLut[colorIdx]);

                // Pixel-Koordinaten berechnen
                context.mapToPixel(x, y, p0);
                context.mapToPixel(x + stepX, y + stepY, p1);

                int px = (int) p0[0];
                int py = (int) p1[1]; // p1 ist "oben" im Swing-Koord (kleineres Y), p0 ist "unten"
                int pw = (int) Math.ceil(p1[0] - p0[0]);
                int ph = (int) Math.ceil(p0[1] - p1[1]);

                g2.fillRect(px, py, pw, ph);
            }
        }
    }

    private void ensureLut(PlotContext context) {
        ChartTheme theme = resolveTheme(context);
        boolean multi = isMultiColor();
        if (theme == lastTheme && lastMultiColor == multi) return;
        lastTheme = theme;
        lastMultiColor = multi;
        rebuildLut(theme, multi);
    }

    private void rebuildLut(ChartTheme theme, boolean multiColor) {
        Color c0;
        Color c1;
        Color c2;
        Color c3;
        if (multiColor) {
            c0 = theme.getSeriesColor(0);
            c1 = theme.getSeriesColor(1);
            c2 = theme.getSeriesColor(2);
            c3 = theme.getSeriesColor(3);
        } else {
            Color base = theme.getAccentColor();
            c0 = ColorUtils.withAlpha(base, 0.25f);
            c1 = ColorUtils.withAlpha(base, 0.45f);
            c2 = ColorUtils.withAlpha(base, 0.65f);
            c3 = ColorUtils.withAlpha(base, 0.85f);
        }

        for (int i = 0; i < 256; i++) {
            float t = i / 255.0f;
            if (t < 0.33f) {
                colorLut[i] = ColorUtils.interpolate(c0, c1, t / 0.33f);
            } else if (t < 0.66f) {
                colorLut[i] = ColorUtils.interpolate(c1, c2, (t - 0.33f) / 0.33f);
            } else {
                colorLut[i] = ColorUtils.interpolate(c2, c3, (t - 0.66f) / 0.34f);
            }
        }
    }
}
