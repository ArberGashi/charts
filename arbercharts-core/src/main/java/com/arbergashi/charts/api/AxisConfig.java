package com.arbergashi.charts.api;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Axis configuration.
 * <p>
 * Provides formatting and behavior flags for axis rendering.
 *
 * @author Arber Gashi
 * @version 1.0.0
 * @since 2026-01-01
 */
public final class AxisConfig {

    private boolean showGrid = true;
    private int requestedTickCount = 6;
    private String labelFormatPattern = null;
    private String unitSuffix = ""; /* For units like %, CHF, m/s */
    private boolean autoScale = true;
    private Locale locale = Locale.getDefault();

    private transient NumberFormat cachedNumberFormat;
    private transient DecimalFormat cachedDecimalFormat;
    private transient String lastPattern;
    private transient Locale lastLocale;

    public AxisConfig() {
        /* Default constructor */
    }

    /**
     * Formats an axis value based on the selected pattern and locale.
     *
     * @param value The value to format.
     * @return The formatted value as a string.
     */
    public String formatValue(double value) {
        if (labelFormatPattern != null && !labelFormatPattern.isEmpty()) {
            DecimalFormat df = getDecimalFormat();
            return df.format(value) + (unitSuffix.isEmpty() ? "" : " " + unitSuffix);
        }

        NumberFormat nf = getNumberFormat();
        return nf.format(value) + (unitSuffix.isEmpty() ? "" : " " + unitSuffix);
    }

    /**
     * Retrieves a cached or newly created DecimalFormat instance configured with the current locale
     * and label format pattern. If the configuration changes (locale or pattern), the cached instance
     * is invalidated and a new one is created.
     *
     * @return a DecimalFormat instance configured with the current locale and label format pattern.
     */
    private DecimalFormat getDecimalFormat() {
        if (cachedDecimalFormat == null || !Objects.equals(labelFormatPattern, lastPattern) || !Objects.equals(locale, lastLocale)) {
            cachedDecimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
            cachedDecimalFormat.applyPattern(labelFormatPattern);
            lastPattern = labelFormatPattern;
            lastLocale = locale;
        }
        return cachedDecimalFormat;
    }

    /**
     * Retrieves a cached or newly created NumberFormat instance configured with the current locale.
     * If the configuration changes (locale), the cached instance is invalidated and a new one is created.
     *
     * @return a NumberFormat instance configured with the current locale.
     */
    private NumberFormat getNumberFormat() {
        if (cachedNumberFormat == null || !Objects.equals(locale, lastLocale)) {
            cachedNumberFormat = NumberFormat.getNumberInstance(locale);
            cachedNumberFormat.setMaximumFractionDigits(2);
            lastLocale = locale;
            lastPattern = null;
        }
        return cachedNumberFormat;
    }

    /**
     * Determines whether the grid is displayed on the axis.
     *
     * @return true if the grid is enabled and should be displayed; false otherwise.
     */
    public boolean isShowGrid() {
        return showGrid;
    }

    /**
     * Enables or disables grid rendering for this axis.
     *
     * @param showGrid true to show the grid
     * @return this config for chaining
     */
    public AxisConfig setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        return this;
    }

    /**
     * Retrieves the requested tick count for the axis.
     *
     * @return the requested tick count, or 0 if not set.
     */
    public int getRequestedTickCount() {
        return requestedTickCount;
    }

    /**
     * Sets the requested tick count for this axis.
     *
     * @param count desired tick count
     * @return this config for chaining
     */
    public AxisConfig setRequestedTickCount(int count) {
        this.requestedTickCount = count;
        return this;
    }

    /**
     * Retrieves the label format pattern for the axis.
     *
     * @return the label format pattern, or null if not set.
     */
    public String getLabelFormatPattern() {
        return labelFormatPattern;
    }

    /**
     * Sets the label format pattern for axis values.
     *
     * @param pattern DecimalFormat pattern (null clears the pattern)
     * @return this config for chaining
     */
    public AxisConfig setLabelFormatPattern(String pattern) {
        this.labelFormatPattern = pattern;
        return this;
    }

    /**
     * Retrieves the unit suffix for the axis.
     *
     * @return the unit suffix, or an empty string if not set.
     */
    public String getUnitSuffix() {
        return unitSuffix;
    }

    /**
     * Sets the unit suffix appended to formatted values.
     *
     * @param unitSuffix unit suffix (null becomes empty)
     * @return this config for chaining
     */
    public AxisConfig setUnitSuffix(String unitSuffix) {
        this.unitSuffix = Objects.requireNonNullElse(unitSuffix, "");
        return this;
    }

    /**
     * Determines whether the axis should automatically adjust its scale based on data.
     *
     * @return true if auto-scaling is enabled; false otherwise.
     */
    public boolean isAutoScale() {
        return autoScale;
    }

    /**
     * Enables or disables auto-scaling for this axis.
     *
     * @param autoScale true to enable auto-scaling
     * @return this config for chaining
     */
    public AxisConfig setAutoScale(boolean autoScale) {
        this.autoScale = autoScale;
        return this;
    }

    /**
     * Sets the locale for the axis.
     *
     * @param locale the locale to use for formatting
     * @return this
     */
    public AxisConfig setLocale(Locale locale) {
        this.locale = Objects.requireNonNull(locale);
        return this;
    }

    /**
     * Returns the locale used for formatting.
     *
     * @return locale for number formatting
     */
    public Locale getLocale() {
        return locale;
    }
}
