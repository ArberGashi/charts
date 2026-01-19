package com.arbergashi.charts.api;

import org.junit.jupiter.api.Test;

import java.awt.geom.Rectangle2D;

import static org.junit.jupiter.api.Assertions.*;

public class PlotContextTest {

    @Test
    public void cartesianInverseTransform() {
        Rectangle2D.Double bounds = new Rectangle2D.Double(10, 20, 300, 200);
        CartesianPlotContext ctx = new CartesianPlotContext(bounds, 0.0, 10.0, 0.0, 100.0);

        double[] pix = new double[2];
        ctx.mapToPixel(5.0, 50.0, pix);
        assertTrue(pix[0] >= bounds.getX() && pix[0] <= bounds.getMaxX());
        assertTrue(pix[1] >= bounds.getY() && pix[1] <= bounds.getMaxY());

        double[] data = new double[2];
        ctx.mapToData(pix[0], pix[1], data);
        assertEquals(5.0, data[0], 1e-6);
        assertEquals(50.0, data[1], 1e-6);

        // plotBounds should return the same instance we passed in
        assertSame(bounds, ctx.plotBounds());
    }

    @Test
    public void zeroRangeAxesAreStable() {
        Rectangle2D.Double bounds = new Rectangle2D.Double(0, 0, 400, 300);
        DefaultPlotContext ctx = new DefaultPlotContext(
                bounds,
                5.0,
                5.0,
                2.0,
                2.0,
                false,
                com.arbergashi.charts.util.NiceScale.ScaleMode.LINEAR,
                com.arbergashi.charts.util.NiceScale.ScaleMode.LINEAR
        );

        double[] pix = new double[2];
        ctx.mapToPixel(5.0, 2.0, pix);
        assertTrue(Double.isFinite(pix[0]));
        assertTrue(Double.isFinite(pix[1]));

        double[] data = new double[2];
        ctx.mapToData(pix[0], pix[1], data);
        assertTrue(Double.isFinite(data[0]));
        assertTrue(Double.isFinite(data[1]));
    }
}
