package com.arbergashi.charts.render.statistical;

import com.arbergashi.charts.api.ChartTheme;
import com.arbergashi.charts.api.PlotContext;
import com.arbergashi.charts.internal.HitTestUtils;
import com.arbergashi.charts.model.ChartModel;
import com.arbergashi.charts.render.BaseRenderer;
import com.arbergashi.charts.util.ChartScale;
import com.arbergashi.charts.util.ColorUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Optional;

/**
 * Professional, zero-allocation box plot renderer.
 *
 * @author Arber Gashi
 * @version 1.0.0
 * @since 2025-06-01
 */
public final class BoxPlotRenderer extends BaseRenderer {

    public BoxPlotRenderer() {
        super("boxplot");
    }

    @Override
    protected void drawData(Graphics2D g2, ChartModel model, PlotContext context) {
        final int n = model.getPointCount();
        if (n == 0) return;

        // Get theme for multi-color palette
        ChartTheme theme = resolveTheme(context);

        final double barWidth = ChartScale.scale(30.0);
        final double whiskerCap = ChartScale.scale(14.0);
        final float strokeWidth = ChartScale.scale(1.5f);

        g2.setStroke(getCachedStroke(strokeWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        final Rectangle2D viewBounds = g2.getClipBounds() != null ? g2.getClipBounds() : context.plotBounds();

        for (int i = 0; i < n; i++) {
            final double xVal = model.getX(i);
            final double median = model.getY(i);
            final double min = model.getValue(i, 3); // min
            final double max = model.getValue(i, 4); // max
            final double iqr = model.getValue(i, 2); // weight = iqr

            double[] buf = pBuffer();
            context.mapToPixel(xVal, median, buf);
            double x = buf[0];
            if (x < viewBounds.getMinX() - barWidth || x > viewBounds.getMaxX() + barWidth) {
                continue;
            }

            context.mapToPixel(xVal, max, buf);
            double pixMaxY = buf[1];
            context.mapToPixel(xVal, min, buf);
            double pixMinY = buf[1];

            double halfIqr = (iqr > 0) ? (iqr / 2.0) : 0.0;
            context.mapToPixel(xVal, median + halfIqr, buf);
            double pixQ3Y = buf[1];
            context.mapToPixel(xVal, median - halfIqr, buf);
            double pixQ1Y = buf[1];

            // Each box plot gets a distinct color from the theme palette
            Color boxColor = seriesOrBase(model, context, i);

            g2.setColor(boxColor);
            g2.draw(getLine(x, pixMaxY, x, pixQ3Y));
            g2.draw(getLine(x, pixMinY, x, pixQ1Y));

            double capHalf = whiskerCap / 2.0;
            g2.draw(getLine(x - capHalf, pixMaxY, x + capHalf, pixMaxY));
            g2.draw(getLine(x - capHalf, pixMinY, x + capHalf, pixMinY));

            double boxTopY = Math.min(pixQ3Y, pixQ1Y);
            double boxHeight = Math.abs(pixQ1Y - pixQ3Y);
            if (boxHeight < 1) boxHeight = 1;

            Shape boxShape = getRect(x - barWidth / 2.0, boxTopY, barWidth, boxHeight);
            g2.setPaint(getCachedGradient(boxColor, (float) boxHeight));
            g2.fill(boxShape);
            g2.setColor(boxColor);
            g2.draw(boxShape);

            g2.setColor(themeForeground(context));
            g2.setStroke(getCachedStroke(strokeWidth));
            g2.draw(getLine(x - barWidth / 2.0, buf[1], x + barWidth / 2.0, buf[1]));
        }
    }

    @Override
    public Optional<Integer> getPointAt(Point2D pixel, ChartModel model, PlotContext context) {
        return HitTestUtils.nearestPointIndex(pixel, model, context);
    }
}
