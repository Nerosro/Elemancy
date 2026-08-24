package be.nerosro.elemancy.client.tome.rendering;

import be.nerosro.elemancy.client.tome.TomeConstants;

/**
 * Layout calculations for the Tome screen. Precomputes all positions from screen dimensions.
 */
public final class TomeLayout {
    public final int screenW;
    public final int screenH;
    public final int bookX;
    public final int bookY;
    public final int bookW;
    public final int bookH;
    public final int tabX;
    public final int footerY;

    public TomeLayout(int screenWidth, int screenHeight) {
        this.screenW = screenWidth;
        this.screenH = screenHeight;
        this.bookY = 24;
        this.bookW = TomeConstants.Layout.BOOK_WIDTH;
        this.bookH = screenHeight - (TomeConstants.Layout.MARGIN * 2) - 18;
        this.bookX = (screenWidth - TomeConstants.Layout.BOOK_WIDTH - TomeConstants.Layout.TAB_RAIL_WIDTH) / 2;
        this.tabX = bookX + TomeConstants.Layout.BOOK_WIDTH;
        this.footerY = bookY + bookH - TomeConstants.Layout.FOOTER_HEIGHT + 6;
    }

    public int prevButtonX() {
        return bookX + 14;
    }

    public int nextButtonX(int buttonWidth) {
        return bookX + bookW - 14 - buttonWidth;
    }

    public int backButtonX() {
        return bookX + (bookW / 2) - 62;
    }

    public int backButtonY() {
        return footerY - 24;
    }

    /**
     * Computes traits detail scroll region height.
     */
    public int traitsRegionH() {
        // regionTop = bookY + 38, regionBottom = footerY - 4
        return (footerY - 4) - (bookY + 38);
    }

    public static boolean isInside(int mouseX, int mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }
}
