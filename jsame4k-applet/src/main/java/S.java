/*
 * JSame4k
 *
 * Copyright (c) 2009-2010 Gabor Bata
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public final class S extends JApplet implements MouseListener, MouseMotionListener, Runnable {

    private static final int TABLE_WIDTH = 20;
    private static final int TABLE_HEIGHT = 15;
    private static final int BLOCK_SIZE = 26;
    private static final int STATUS_SIZE = 20;
    private static final int BORDER_SIZE = 4;

    private static final int[] COLORS = { 0x914e3b, 0x7b8376, 0x3d6287, 0xaf8652, 0x262C4b };
    private static final Image[] BLOCK_GFX = new Image[9];
    static {
        for (int i = 0; i < COLORS.length; i++) {
            BLOCK_GFX[i] = createBlockImage(new Color(COLORS[i]), true);
            BLOCK_GFX[8 - i] = createBlockImage(new Color(COLORS[i]), false);
        }
    }

    private static final Color STATUS_BORDER_COLOR = new Color(0x484d50);
    private static final Color STATUS_BACKGROUND_COLOR = new Color(0x62696a);
    private static final Color STATUS_TEXT_COLOR = new Color(0xffffff);
    private static final Color CANVAS_BACKGROUND_COLOR = new Color(COLORS[4]);
    private static final Font FONT = new Font(Font.SANS_SERIF, Font.BOLD, 12);

    private int[] table;
    private String[] texts;
    private Random random;
    private Image screenBuffer;
    private JComponent canvas;
    private int recordX;
    private int recordY;
    private int score;
    private int markedAmount;
    private boolean bonusAdded;

    @Override
    public void init() {
        try {
            SwingUtilities.invokeAndWait(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        table = new int[TABLE_WIDTH * TABLE_HEIGHT];
        texts = new String[] { "New Game", null, null };
        random = new Random();
        screenBuffer = new BufferedImage(TABLE_WIDTH * BLOCK_SIZE + BORDER_SIZE * 2, TABLE_HEIGHT * BLOCK_SIZE + BORDER_SIZE * 2 + STATUS_SIZE, BufferedImage.TYPE_INT_RGB);
        canvas = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                paintTableGraphics(g);
            }
        };

        recordX = -1;
        recordY = -1;

        reset();

        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        getContentPane().add(canvas);
    }

    private void paintTableGraphics(Graphics g) {
        Graphics2D buffer = (Graphics2D) screenBuffer.getGraphics();
        buffer.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // background
        buffer.setColor(CANVAS_BACKGROUND_COLOR);
        buffer.fillRect(0, 0, TABLE_WIDTH * BLOCK_SIZE + BORDER_SIZE * 2, TABLE_HEIGHT * BLOCK_SIZE + BORDER_SIZE * 2);

        // draw status bar
        buffer.setColor(STATUS_BORDER_COLOR);
        buffer.fillRect(0, TABLE_HEIGHT * BLOCK_SIZE + BORDER_SIZE * 2, TABLE_WIDTH * BLOCK_SIZE + BORDER_SIZE * 2, STATUS_SIZE);

        // draw 3 column on status bar and texts
        buffer.setFont(FONT);
        texts[1] = markedAmount > 1 ? String.format("Marked: %s (%s points)", String.valueOf(markedAmount), String.valueOf(countPoints(markedAmount))) : (canvas.isEnabled() ? "" : "Game Over!");
        texts[2] = String.format("Score: %s", String.valueOf(score));
        for (int i = 0; i < texts.length; i++) {
            buffer.setColor(STATUS_BACKGROUND_COLOR);
            buffer.fillRect((TABLE_WIDTH * BLOCK_SIZE + BORDER_SIZE * 2) / 3 * i + 1, TABLE_HEIGHT * BLOCK_SIZE + BORDER_SIZE * 2 + 1, (TABLE_WIDTH * BLOCK_SIZE + BORDER_SIZE * 2) / 3 - 2, STATUS_SIZE - 2);
            buffer.setColor(STATUS_TEXT_COLOR);
            buffer.drawString(texts[i], (TABLE_WIDTH * BLOCK_SIZE + BORDER_SIZE * 2) / 6 * (2 * i + 1) - buffer.getFontMetrics().stringWidth(texts[i]) / 2, TABLE_HEIGHT * BLOCK_SIZE + BORDER_SIZE * 2 + STATUS_SIZE / 2 + buffer.getFontMetrics().getHeight() / 4);
        }

        // draw tiles
        for (int x = 0; x < TABLE_WIDTH; x++) {
            for (int y = 0; y < TABLE_HEIGHT; y++) {
                buffer.drawImage(BLOCK_GFX[table[TABLE_WIDTH * y + x] + 4], x * BLOCK_SIZE + BORDER_SIZE, y * BLOCK_SIZE + BORDER_SIZE, null);
            }
        }

        // draw the buffered image
        g.drawImage(screenBuffer, 0, 0, null);
    }

    private void refreshTable() {
        canvas.repaint();
    }

    private void reset() {
        markedAmount = 0;
        score = 0;
        bonusAdded = false;

        for (int i = 0; i < table.length; i++) {
            table[i] = random.nextInt(4) + 1;
        }
    }

    private int mark(int x, int y) {
        return mark(x, y, getColor(x, y));
    }

    private int mark(int x, int y, int c) {
        if (isMarked(x, y) || c != getColor(x, y) || isRemoved(x, y)) {
            return 0;
        }
        int blocks = 1;
        table[TABLE_WIDTH * y + x] = -1 * getColor(x, y);
        blocks += mark(x - 1, y, getColor(x, y));
        blocks += mark(x, y - 1, getColor(x, y));
        blocks += mark(x + 1, y, getColor(x, y));
        blocks += mark(x, y + 1, getColor(x, y));
        return blocks;
    }

    private void unmark() {
        for (int i = 0; i < table.length; i++) {
            table[i] = table[i] < 0 ? -1 * table[i] : table[i];
        }
    }

    private void swap(int x1, int y1, int x2, int y2) {
        int pos1 = TABLE_WIDTH * y1 + x1;
        int pos2 = TABLE_WIDTH * y2 + x2;
        int temp = table[pos1];
        table[pos1] = table[pos2];
        table[pos2] = temp;
    }

    private void markBlocks(MouseEvent event, boolean force) {
        int x = event.getX() < BORDER_SIZE ? -1 : (event.getX() - BORDER_SIZE) / BLOCK_SIZE;
        int y = event.getY() < BORDER_SIZE ? -1 : (event.getY() - BORDER_SIZE) / BLOCK_SIZE;
        if (((recordX != x || recordY != y) && !isMarked(x, y)) || force) {
            if (isRemoved(x, y) && isRemoved(recordX, recordY)) {
                recordX = x;
                recordY = y;
                return;
            }
            unmark();
            markedAmount = mark(x, y);
            refreshTable();
            recordX = x;
            recordY = y;
        }
    }

    private void unmarkBlocks(MouseEvent event, boolean force) {
        int x = event.getX() < BORDER_SIZE ? -1 : (event.getX() - BORDER_SIZE) / BLOCK_SIZE;
        int y = event.getY() < BORDER_SIZE ? -1 : (event.getY() - BORDER_SIZE) / BLOCK_SIZE;
        if (recordX != x || recordY != y || force) {
            unmark();
            markedAmount = 0;
            refreshTable();
            recordX = x;
            recordY = y;
        }
    }

    private boolean isMarked(int x, int y) {
        return getState(x, y) < 0;
    }

    private boolean isRemoved(int x, int y) {
        return getState(x, y) == 0;
    }

    private int getColor(int x, int y) {
        return getState(x, y) < 0 ? -1 * getState(x, y) : getState(x, y);
    }

    private int getState(int x, int y) {
        if (x < 0 || y < 0 || x > TABLE_WIDTH - 1 || y > TABLE_HEIGHT - 1) {
            return 0;
        }
        return table[TABLE_WIDTH * y + x];
    }

    private int countPoints(int removed) {
        return removed * removed - 4 * removed + 4;
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(MouseEvent event) {
        markBlocks(event, false);
    }

    @Override
    public void mouseExited(MouseEvent event) {
        unmarkBlocks(event, true);

    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (event.getX() > 0 && event.getX() < (TABLE_WIDTH * BLOCK_SIZE + BORDER_SIZE * 2) / 3 - 1
                && event.getY() > TABLE_HEIGHT * BLOCK_SIZE + BORDER_SIZE * 2
                && event.getY() < TABLE_HEIGHT * BLOCK_SIZE + BORDER_SIZE * 2 + STATUS_SIZE - 1) {
            reset();
            canvas.setEnabled(true);
            markedAmount = 0;
            refreshTable();
        }

        int x = event.getX() < BORDER_SIZE ? -1 : (event.getX() - BORDER_SIZE) / BLOCK_SIZE;
        int y = event.getY() < BORDER_SIZE ? -1 : (event.getY() - BORDER_SIZE) / BLOCK_SIZE;

        int removed = 0;
        for (int i = 0; i < table.length; i++) {
            if (table[i] < 0) {
                removed++;
            }
        }
        if (removed < 2) {
            return;
        }
        for (int i = 0; i < table.length; i++) {
            if (table[i] < 0) {
                table[i] = 0;
            }
        }
        boolean canMove;
        for (int remx = 0; remx < TABLE_WIDTH; remx++) {
            canMove = true;
            while (canMove) {
                canMove = false;
                for (int remy = 1; remy < TABLE_HEIGHT; remy++) {
                    if (isRemoved(remx, remy) && !isRemoved(remx, remy - 1)) {
                        swap(remx, remy, remx, remy - 1);
                        canMove = true;
                    }
                }
            }
        }

        canMove = true;
        while (canMove) {
            canMove = false;
            for (int remx = 1; remx < TABLE_WIDTH; remx++) {
                if (isRemoved(remx - 1, TABLE_HEIGHT - 1) && !isRemoved(remx, TABLE_HEIGHT - 1)) {
                    for (int remy = 0; remy < TABLE_HEIGHT; remy++) {
                        swap(remx, remy, remx - 1, remy);
                    }
                    canMove = true;
                }
            }
        }

        score += countPoints(removed);

        canMove = true;
        removed = 0;
        for (int tableWidth = 0; tableWidth < TABLE_WIDTH; tableWidth++) {
            for (int tableHeight = 0; tableHeight < TABLE_HEIGHT; tableHeight++) {
                if (isRemoved(tableWidth, tableHeight)) {
                    continue;
                }
                removed++;
                int color = getColor(tableWidth, tableHeight);
                if (getColor(tableWidth - 1, tableHeight) == color || getColor(tableWidth, tableHeight - 1) == color
                        || getColor(tableWidth + 1, tableHeight) == color || getColor(tableWidth, tableHeight + 1) == color) {
                    canMove = false;
                    break;
                }
            }
        }

        if (removed == 0 && !bonusAdded) {
            bonusAdded = true;
            score += 1000;
        }

        markedAmount = 0;
        if (!canMove) {
            markedAmount = mark(x, y);
        }

        if (canMove && canvas.isEnabled()) {
            canvas.setEnabled(false);
            markedAmount = 0;
        }

        refreshTable();
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        markBlocks(event, true);
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        unmarkBlocks(event, false);
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        markBlocks(event, false);
    }

    private static Image createBlockImage(Color color, boolean marked) {
        Image image = new BufferedImage(BLOCK_SIZE, BLOCK_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();

        Color original = marked ? color.brighter() : color;
        Color brighter = original.brighter();
        Color darker = original.darker();

        g.setColor(darker);
        g.fillRect(0, 0, BLOCK_SIZE, BLOCK_SIZE);

        g.setColor(original);
        g.fillRect(0, 0, BLOCK_SIZE - 1, BLOCK_SIZE - 1);

        g.setColor(brighter);
        g.fillRect(1, 1, BLOCK_SIZE - 3, BLOCK_SIZE - 3);

        g.setPaint(new GradientPaint(2, 2, original, BLOCK_SIZE - 4, BLOCK_SIZE - 4, brighter));
        g.fillRect(2, 2, BLOCK_SIZE - 4, BLOCK_SIZE - 4);

        if (marked) {
            g.setColor(darker);
            g.fillRect(8, 8, BLOCK_SIZE - 16, BLOCK_SIZE - 16);
        }

        return image;
    }

}
