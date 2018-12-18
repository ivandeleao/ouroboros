/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;

/**
 *
 * @author ivand
 */
public class ScrollablePanel extends JPanel implements Scrollable {

    public Dimension getPreferredSize() {
        return getPreferredScrollableViewportSize();
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        if (getParent() == null) {
            return getSize();
        }
        Dimension d = getParent().getSize();
        int c = (int) Math
                .floor((d.width - getInsets().left - getInsets().right) / 50.0);
        if (c == 0) {
            return d;
        }
        int r = 20 / c;
        if (r * c < 20) {
            ++r;
        }
        return new Dimension(c * 50, r * 50);
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 50;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return getParent() != null ? getParent().getSize().width > getPreferredSize().width : true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

}
