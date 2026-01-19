package com.arbergashi.charts.api;

import com.arbergashi.charts.model.ChartModel;
import com.arbergashi.charts.model.DefaultChartModel;
import com.arbergashi.charts.render.ChartRenderer;
import com.arbergashi.charts.render.standard.LineRenderer;
import com.arbergashi.charts.ui.ArberChartPanel;
import com.arbergashi.charts.ui.grid.GridLayer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.ToDoubleFunction;

/**
 * Fluent builder for assembling an {@link com.arbergashi.charts.ui.ArberChartPanel}.
 *
 * <p>This is the recommended entry point for newcomers who want a concise, readable way to
 * create charts: add one or more layers (model + renderer), optionally set a theme, and then
 * {@link #build()} the panel.</p>
 *
 * <p><b>Framework contract:</b> The builder does not mutate global state. The selected
 * {@link ChartTheme} is applied to the created panel instance only.</p>
 *
 * @author Arber Gashi
 * @version 1.0.0
 * @since 2026-01-01
 */
public final class ArberChartBuilder {

    private String title = "ArberChart";
    private ChartTheme theme = ChartThemes.defaultDark();
    private final List<LayerConfig> layers = new ArrayList<>();
    private boolean legendVisible = true;
    private boolean tooltipsEnabled = true;
    private boolean animationsEnabled = true;
    private GridLayer gridLayer;
    private AxisConfig xAxisConfig;
    private AxisConfig yAxisConfig;
    private Locale locale;

    private record LayerConfig(ChartModel model, ChartRenderer renderer) {}

    public ArberChartBuilder() {
    }

    /**
     * Creates a new builder instance.
     *
     * @return a new {@link ArberChartBuilder}
     */
    public static ArberChartBuilder create() {
        return new ArberChartBuilder();
    }

    /**
     * Sets the window title used by {@link #display()}.
     *
     * @param title window title
     * @return this builder for chaining
     */
    public ArberChartBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the theme used by the chart panel.
     *
     * @param theme chart theme
     * @return this builder for chaining
     */
    public ArberChartBuilder withTheme(ChartTheme theme) {
        this.theme = theme;
        return this;
    }

    /**
     * Convenience method to use the default light theme.
     *
     * @return this builder for chaining
     */
    public ArberChartBuilder withLightMode() {
        this.theme = ChartThemes.defaultLight();
        return this;
    }

    /**
     * Convenience method to use the default dark theme.
     *
     * @return this builder for chaining
     */
    public ArberChartBuilder withDarkMode() {
        this.theme = ChartThemes.defaultDark();
        return this;
    }

    /**
     * Sets the locale used for axis labels and tooltips.
     *
     * @param locale locale to apply
     * @return this builder for chaining
     */
    public ArberChartBuilder withLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    /**
     * Adds a chart layer (model + renderer).
     *
     * @param model data model
     * @param renderer renderer instance
     * @return this builder for chaining
     */
    public ArberChartBuilder addLayer(ChartModel model, ChartRenderer renderer) {
        layers.add(new LayerConfig(model, renderer));
        return this;
    }

    /**
     * Adds a simple line series from raw arrays.
     *
     * @param name series name
     * @param x x-values
     * @param y y-values
     * @return this builder for chaining
     */
    public ArberChartBuilder addLineSeries(String name, double[] x, double[] y) {
        DefaultChartModel model = new DefaultChartModel(name);
        for (int i = 0; i < Math.min(x.length, y.length); i++) {
            model.addPoint(x[i], y[i], 0, null);
        }
        return addLayer(model, new LineRenderer());
    }

    /**
     * Adds a line series from a collection using extractors.
     *
     * @param name series name
     * @param data source collection
     * @param xExtractor x-value extractor
     * @param yExtractor y-value extractor
     * @param <T> element type
     * @return this builder for chaining
     */
    public <T> ArberChartBuilder addLineSeries(String name, Collection<T> data, 
                                             ToDoubleFunction<T> xExtractor, 
                                             ToDoubleFunction<T> yExtractor) {
        DefaultChartModel model = ChartModelBinder.bind(data, name, xExtractor, yExtractor);
        return addLayer(model, new LineRenderer());
    }

    /**
     * Shows or hides the legend.
     *
     * @param visible true to show, false to hide
     * @return this builder for chaining
     */
    public ArberChartBuilder withLegend(boolean visible) {
        this.legendVisible = visible;
        return this;
    }

    /**
     * Enables or disables tooltips.
     *
     * @param enabled true to enable tooltips
     * @return this builder for chaining
     */
    public ArberChartBuilder withTooltips(boolean enabled) {
        this.tooltipsEnabled = enabled;
        return this;
    }

    /**
     * Enables or disables smooth animations for zoom and pan operations.
     *
     * @param enabled {@code true} to enable animations (default), {@code false} for instant updates.
     * @return This builder for chaining.
     */
    public ArberChartBuilder withAnimations(boolean enabled) {
        this.animationsEnabled = enabled;
        return this;
    }

    /**
     * Sets the grid layer used by the chart.
     * <p>
     * The grid layer controls background grid style and is part of the chart composition.
     * If not set, the {@link com.arbergashi.charts.ui.ArberChartPanel} default grid is used.
     * </p>
     *
     * @param gridLayer the grid layer to use; if {@code null}, the panel default grid is kept
     * @return this builder for chaining
     */
    public ArberChartBuilder withGridLayer(GridLayer gridLayer) {
        this.gridLayer = gridLayer;
        return this;
    }

    /**
     * Configures the X-axis behavior and formatting.
     */
    public ArberChartBuilder withXAxisConfig(AxisConfig config) {
        this.xAxisConfig = config;
        return this;
    }

    /**
     * Configures the Y-axis behavior and formatting.
     */
    public ArberChartBuilder withYAxisConfig(AxisConfig config) {
        this.yAxisConfig = config;
        return this;
    }

    /**
     * Builds the chart panel.
     *
     * @return A configured {@link ArberChartPanel}.
     */
    public ArberChartPanel build() {
        if (layers.isEmpty()) {
            throw new IllegalStateException("At least one layer must be added to the chart.");
        }

        ArberChartPanel panel = new ArberChartPanel(layers.get(0).model, layers.get(0).renderer);
        panel.withTheme(theme);
        if (locale != null) {
            panel.withLocale(locale);
        }
        panel.withLegend(legendVisible);
        panel.withTooltips(tooltipsEnabled);
        panel.withAnimations(animationsEnabled);
        if (xAxisConfig != null) {
            panel.withXAxisConfig(xAxisConfig);
        }
        if (yAxisConfig != null) {
            panel.withYAxisConfig(yAxisConfig);
        }

        if (gridLayer != null) {
            panel.withGridLayer(gridLayer);
        }

        for (int i = 1; i < layers.size(); i++) {
            LayerConfig layer = layers.get(i);
            panel.addLayer(layer.model, layer.renderer);
        }

        return panel;
    }

    /**
     * Convenience method to wrap the chart in a JFrame and display it.
     */
    public void display() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(build());
            frame.pack();
            frame.setSize(1024, 768);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
