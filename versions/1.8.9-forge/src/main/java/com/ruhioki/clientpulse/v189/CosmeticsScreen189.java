package com.ruhioki.clientpulse.v189;

import com.ruhioki.clientpulse.core.cosmetic.CosmeticCategory;
import com.ruhioki.clientpulse.core.cosmetic.CosmeticDefinition;
import com.ruhioki.clientpulse.core.cosmetic.CosmeticProfile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public final class CosmeticsScreen189 extends GuiScreen {
    private static final int[] COLORS = {
        0x65B8FF, 0xA782FF, 0x5AE0C2, 0xFF8A9C, 0xFFC96B, 0xF0F4FA
    };

    private final GuiScreen parent;
    private final CosmeticProfile profile;
    private final ClickGuiTheme189 theme;
    private final List<CosmeticDefinition> visibleItems = new ArrayList<CosmeticDefinition>();
    private GuiTextField searchField;
    private CosmeticCategory category = CosmeticCategory.CAPES;
    private int panelLeft;
    private int panelTop;
    private int panelWidth;
    private int panelHeight;
    private int sidebarWidth;
    private int listLeft;
    private int listWidth;
    private int listTop;
    private int listBottom;
    private int previewLeft;
    private int previewWidth;
    private int categoryTop;
    private int categoryStride;
    private int listScroll;
    private float previewRotation;
    private float dragRotation;
    private int dragStartX;
    private boolean compactLayout;
    private boolean favoriteFilter;
    private boolean draggingScale;
    private String query = "";

    public CosmeticsScreen189(GuiScreen parent) {
        this.parent = parent;
        this.profile = Cosmetics189.profile();
        this.theme = new ClickGuiTheme189(ClientPulse189.modules());
    }

    @Override
    public void initGui() {
        panelWidth = Math.min(980, Math.max(1, width - 24));
        panelHeight = Math.min(620, Math.max(1, height - 20));
        panelLeft = (width - panelWidth) / 2;
        panelTop = (height - panelHeight) / 2;
        compactLayout = panelHeight < 410 || panelWidth < 620;
        sidebarWidth = Math.min(148, Math.max(72, Math.round(panelWidth * 0.18f)));
        listLeft = panelLeft + sidebarWidth + 10;
        int remainingWidth = panelWidth - sidebarWidth - 32;
        listWidth = Math.min(248, Math.max(104, Math.round(remainingWidth * 0.46f)));
        categoryTop = panelTop + (compactLayout ? 54 : 72);
        categoryStride = compactLayout
                ? Math.max(18, Math.min(32, (panelHeight - 90) / CosmeticCategory.values().length))
                : 34;
        listTop = panelTop + (compactLayout ? 104 : 92);
        listBottom = panelTop + panelHeight - 18;
        previewLeft = listLeft + listWidth + 10;
        previewWidth = Math.max(1, panelLeft + panelWidth - previewLeft - 10);
        searchField = new GuiTextField(1, fontRendererObj, listLeft + 5,
                panelTop + (compactLayout ? 45 : 62), Math.max(1, listWidth - 10), 18);
        searchField.setMaxStringLength(48);
        searchField.setEnableBackgroundDrawing(false);
        searchField.setCanLoseFocus(true);
        updateVisibleItems();
    }

    private void updateVisibleItems() {
        visibleItems.clear();
        String normalized = query.trim().toLowerCase();
        for (CosmeticDefinition item : com.ruhioki.clientpulse.core.cosmetic.CosmeticCatalog
                .inCategory(category)) {
            if (favoriteFilter && !profile.isFavorite(item.id())) {
                continue;
            }
            if (!normalized.isEmpty() && !item.name().toLowerCase().contains(normalized)) {
                continue;
            }
            visibleItems.add(item);
        }
        listScroll = Math.max(0, Math.min(listScroll, Math.max(0, visibleItems.size() - itemCapacity())));
    }

    private int itemCapacity() {
        return Math.max(1, (listBottom - listTop) / 31);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawRect(panelLeft, panelTop, panelLeft + panelWidth, panelTop + panelHeight, theme.panel());
        drawRect(panelLeft, panelTop, panelLeft + sidebarWidth, panelTop + panelHeight, theme.sidebar());
        fontRendererObj.drawString("CLIENTPULSE", panelLeft + 18, panelTop + 16, theme.accent());
        fontRendererObj.drawString("COSMETICS STUDIO", panelLeft + 18, panelTop + 34, theme.text());
        fontRendererObj.drawString("RESET", panelLeft + panelWidth - 124, panelTop + 20, theme.accent());
        fontRendererObj.drawString("ESC  BACK", panelLeft + panelWidth - 72, panelTop + 20, theme.muted());

        drawCategories(mouseX, mouseY);
        drawList(mouseX, mouseY);
        drawPreviewAndControls(mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawCategories(int mouseX, int mouseY) {
        int y = categoryTop;
        for (CosmeticCategory item : CosmeticCategory.values()) {
            boolean selected = item == category;
            int rowHeight = categoryStride - (compactLayout ? 2 : 8);
            boolean hovered = inside(mouseX, mouseY, panelLeft + 8, y, sidebarWidth - 16, rowHeight);
            int color = selected ? theme.accentArgb(0.24f) : hovered ? theme.cardHover() : theme.sidebar();
            drawRect(panelLeft + 8, y, panelLeft + sidebarWidth - 8, y + rowHeight, color);
            fontRendererObj.drawString(item.displayName(), panelLeft + 16,
                    y + Math.max(4, (rowHeight - fontRendererObj.FONT_HEIGHT) / 2),
                    selected ? theme.accent() : theme.text());
            y += categoryStride;
        }
    }

    private void drawList(int mouseX, int mouseY) {
        int searchTop = panelTop + (compactLayout ? 41 : 56);
        drawRect(listLeft, searchTop, listLeft + listWidth,
                searchTop + 27, theme.card());
        searchField.drawTextBox();
        String filterText = favoriteFilter ? "FAVORITES ON" : "ALL ITEMS";
        int filterTop = panelTop + (compactLayout ? 70 : 84);
        drawRect(listLeft + 4, filterTop, listLeft + listWidth - 4, filterTop + 21,
                favoriteFilter ? theme.accentArgb(0.24f) : theme.card());
        fontRendererObj.drawString(filterText, listLeft + 10, filterTop + 6,
                favoriteFilter ? theme.accent() : theme.muted());

        drawRect(listLeft, listTop - 8, listLeft + listWidth, listTop - 7, theme.accentArgb(0.5f));
        int visible = Math.min(itemCapacity(), visibleItems.size() - listScroll);
        for (int row = 0; row < visible; row++) {
            CosmeticDefinition item = visibleItems.get(row + listScroll);
            int y = listTop + row * 31;
            boolean selected = item.id().equals(profile.selected(category));
            boolean hovered = inside(mouseX, mouseY, listLeft + 3, y,
                    listWidth - 6, 28);
            int color = selected ? theme.accentArgb(0.2f)
                    : hovered ? theme.cardHover() : theme.card();
            drawRect(listLeft + 3, y, listLeft + listWidth - 3, y + 27, color);
            drawRect(listLeft + 8, y + 8, listLeft + 14, y + 14,
                    selected ? theme.accent() : theme.muted());
            fontRendererObj.drawString(item.name(), listLeft + 21, y + 9, theme.text());
            fontRendererObj.drawString(profile.isFavorite(item.id()) ? "*" : "+",
                    listLeft + listWidth - 19, y + 8,
                    profile.isFavorite(item.id()) ? theme.accent() : theme.muted());
        }
        if (visibleItems.isEmpty()) {
            fontRendererObj.drawString("No matching cosmetics", listLeft + 8, listTop + 8, theme.muted());
        }
    }

    private void drawPreviewAndControls(int mouseX, int mouseY) {
        int previewTop = panelTop + (compactLayout ? 45 : 56);
        int previewBottom = panelTop + (compactLayout ? 126 : 278);
        drawRect(previewLeft, previewTop, previewLeft + previewWidth, previewBottom, theme.card());
        fontRendererObj.drawString(compactLayout ? "PREVIEW" : "LIVE PREVIEW",
                previewLeft + 8, previewTop + 9, theme.text());
        if (!compactLayout) {
            fontRendererObj.drawString("Drag the preview to rotate", previewLeft + 12, panelTop + 82,
                    theme.muted());
        }

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player != null) {
            float rotation = previewRotation;
            if (profile.animationsEnabled()) {
                rotation += (float) Math.sin(System.currentTimeMillis() / 900.0) * 24.0f;
            }
            int centerX = previewLeft + previewWidth / 2;
            int baseY = compactLayout ? panelTop + 125 : panelTop + 264;
            int modelScale = compactLayout ? Math.max(20, Math.min(30, previewWidth / 4)) : 50;
            GL11.glPushMatrix();
            try {
                GuiInventory.drawEntityOnScreen(centerX, baseY, modelScale,
                        rotation, 0.0f, player);
            } finally {
                GL11.glPopMatrix();
            }
        }

        if (compactLayout) {
            drawCompactControls(mouseX, mouseY);
            return;
        }

        int controlsTop = panelTop + 291;
        fontRendererObj.drawString("Preview settings", previewLeft + 8, controlsTop, theme.text());
        int y = controlsTop + 20;
        drawButton(previewLeft + 8, y, previewWidth - 16, 24,
                "Cosmetics: " + (profile.enabled() ? "ON" : "OFF"), profile.enabled(), mouseX, mouseY);
        y += 30;
        drawButton(previewLeft + 8, y, previewWidth - 16, 24,
                "Animation: " + (profile.animationsEnabled() ? "ON" : "OFF"),
                profile.animationsEnabled(), mouseX, mouseY);
        y += 31;
        fontRendererObj.drawString("Tint  #" + String.format("%06X", Integer.valueOf(profile.color())),
                previewLeft + 8, y + 4, theme.text());
        drawRect(previewLeft + previewWidth - 62, y, previewLeft + previewWidth - 38, y + 20,
                theme.cardHover());
        drawRect(previewLeft + previewWidth - 33, y, previewLeft + previewWidth - 9, y + 20,
                theme.accentArgb(0.35f));
        fontRendererObj.drawString("-", previewLeft + previewWidth - 54, y + 6, theme.text());
        fontRendererObj.drawString("+", previewLeft + previewWidth - 25, y + 6, theme.text());
        y += 29;

        fontRendererObj.drawString("Scale  " + Math.round(profile.scale() * 100.0) + "%",
                previewLeft + 8, y + 4, theme.text());
        int sliderLeft = previewLeft + 92;
        int sliderRight = previewLeft + previewWidth - 10;
        drawRect(sliderLeft, y + 9, sliderRight, y + 13, theme.sidebar());
        int knobX = sliderLeft + Math.round((float) ((profile.scale() - 0.5) / 1.0)
                * Math.max(0, sliderRight - sliderLeft));
        drawRect(knobX - 3, y + 5, knobX + 4, y + 17, theme.accent());
        y += 27;

        fontRendererObj.drawString("Transition  " + profile.animationDuration() + " ms",
                previewLeft + 8, y + 4, theme.text());
        fontRendererObj.drawString("-", previewLeft + previewWidth - 44, y + 4, theme.muted());
        fontRendererObj.drawString("+", previewLeft + previewWidth - 20, y + 4, theme.accent());
        y += 24;
        fontRendererObj.drawString("PLACEMENT", previewLeft + 8, y + 2, theme.accent());
        drawPlacementRow("Cape height", profile.capeVerticalOffset(), y + 15);
        drawPlacementRow("Wings height", profile.wingVerticalOffset(), y + 37);
    }

    private void drawCompactControls(int mouseX, int mouseY) {
        int controlsTop = panelTop + 132;
        int buttonWidth = Math.max(1, (previewWidth - 18) / 2);
        drawButton(previewLeft + 4, controlsTop, buttonWidth, 18,
                profile.enabled() ? "ON" : "OFF", profile.enabled(), mouseX, mouseY);
        drawButton(previewLeft + 10 + buttonWidth, controlsTop, buttonWidth, 18,
                profile.animationsEnabled() ? "ANIM ON" : "ANIM OFF",
                profile.animationsEnabled(), mouseX, mouseY);

        int y = controlsTop + 22;
        fontRendererObj.drawString("#" + String.format("%06X", Integer.valueOf(profile.color())),
                previewLeft + 4, y + 5, theme.text());
        fontRendererObj.drawString("-", previewLeft + previewWidth - 42, y + 5, theme.muted());
        fontRendererObj.drawString("+", previewLeft + previewWidth - 20, y + 5, theme.accent());

        y += 21;
        fontRendererObj.drawString(Math.round(profile.scale() * 100.0) + "%",
                previewLeft + 4, y + 5, theme.text());
        int sliderLeft = previewLeft + 42;
        int sliderRight = previewLeft + previewWidth - 8;
        drawRect(sliderLeft, y + 9, sliderRight, y + 12, theme.sidebar());
        int knobX = sliderLeft + Math.round((float) ((profile.scale() - 0.5) / 1.0)
                * Math.max(0, sliderRight - sliderLeft));
        drawRect(knobX - 3, y + 5, knobX + 4, y + 16, theme.accent());

        y += 19;
        fontRendererObj.drawString(profile.animationDuration() + " ms",
                previewLeft + 4, y + 5, theme.text());
        fontRendererObj.drawString("-", previewLeft + previewWidth - 42, y + 5, theme.muted());
        fontRendererObj.drawString("+", previewLeft + previewWidth - 20, y + 5, theme.accent());
    }

    private void drawPlacementRow(String label, double offset, int y) {
        fontRendererObj.drawString(label + "  " + formatOffset(offset), previewLeft + 8, y + 4, theme.text());
        fontRendererObj.drawString("-", previewLeft + previewWidth - 44, y + 4, theme.muted());
        fontRendererObj.drawString("+", previewLeft + previewWidth - 20, y + 4, theme.accent());
    }

    private static String formatOffset(double offset) {
        return String.format("%+.2f", Double.valueOf(offset));
    }

    private void drawButton(int x, int y, int buttonWidth, int buttonHeight,
            String label, boolean active, int mouseX, int mouseY) {
        boolean hovered = inside(mouseX, mouseY, x, y, buttonWidth, buttonHeight);
        drawRect(x, y, x + buttonWidth, y + buttonHeight,
                active ? theme.accentArgb(0.22f) : hovered ? theme.cardHover() : theme.card());
        fontRendererObj.drawString(label, x + 9, y + (buttonHeight - fontRendererObj.FONT_HEIGHT) / 2,
                active ? theme.accent() : theme.text());
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton != 0) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            return;
        }
        int searchTop = panelTop + (compactLayout ? 41 : 56);
        if (inside(mouseX, mouseY, listLeft, searchTop, listWidth, 27)) {
            searchField.mouseClicked(mouseX, mouseY, mouseButton);
            return;
        }
        searchField.setFocused(false);

        if (inside(mouseX, mouseY, panelLeft + panelWidth - 132, panelTop + 12, 48, 22)) {
            profile.reset();
            listScroll = 0;
            favoriteFilter = false;
            query = "";
            searchField.setText("");
            updateVisibleItems();
            Cosmetics189.save();
            return;
        }

        if (inside(mouseX, mouseY, panelLeft + 8, categoryTop,
                sidebarWidth - 16, categoryStride * CosmeticCategory.values().length)) {
            int index = (mouseY - categoryTop) / categoryStride;
            if (index >= 0 && index < CosmeticCategory.values().length) {
                category = CosmeticCategory.values()[index];
                listScroll = 0;
                updateVisibleItems();
            }
            return;
        }
        int filterTop = panelTop + (compactLayout ? 70 : 84);
        if (inside(mouseX, mouseY, listLeft + 4, filterTop, listWidth - 8, 21)) {
            favoriteFilter = !favoriteFilter;
            listScroll = 0;
            updateVisibleItems();
            return;
        }

        int row = (mouseY - listTop) / 31;
        int itemIndex = row + listScroll;
        if (mouseX >= listLeft && mouseX < listLeft + listWidth
                && mouseY >= listTop && mouseY < listBottom
                && row >= 0 && row < itemCapacity() && itemIndex < visibleItems.size()) {
            CosmeticDefinition item = visibleItems.get(itemIndex);
            if (mouseX >= listLeft + listWidth - 24) {
                profile.setFavorite(item.id(), !profile.isFavorite(item.id()));
                updateVisibleItems();
            } else {
                profile.select(category, item.id().equals(profile.selected(category)) ? null : item.id());
            }
            Cosmetics189.save();
            return;
        }

        int controlsTop = panelTop + (compactLayout ? 132 : 311);
        int rowHeight = compactLayout ? 22 : 30;
        int enabledWidth = compactLayout ? Math.max(1, (previewWidth - 18) / 2) : previewWidth - 16;
        int enabledX = compactLayout ? previewLeft + 4 : previewLeft + 8;
        if (inside(mouseX, mouseY, enabledX, controlsTop, enabledWidth, compactLayout ? 18 : 24)) {
            profile.setEnabled(!profile.enabled());
            Cosmetics189.save();
            return;
        }
        int animationsX = compactLayout ? previewLeft + 10 + enabledWidth : previewLeft + 8;
        int animationsWidth = compactLayout ? enabledWidth : previewWidth - 16;
        int animationsY = compactLayout ? controlsTop : controlsTop + rowHeight;
        if (inside(mouseX, mouseY, animationsX, animationsY,
                animationsWidth, compactLayout ? 18 : 24)) {
            profile.setAnimationsEnabled(!profile.animationsEnabled());
            Cosmetics189.save();
            return;
        }

        int colorY = compactLayout ? controlsTop + 22 : controlsTop + rowHeight * 2;
        int colorMinusX = previewLeft + previewWidth - (compactLayout ? 48 : 62);
        int colorPlusX = previewLeft + previewWidth - (compactLayout ? 25 : 33);
        if (inside(mouseX, mouseY, colorMinusX, colorY, 24, 20)) {
            stepColor(-1);
            return;
        }
        if (inside(mouseX, mouseY, colorPlusX, colorY, 24, 20)) {
            stepColor(1);
            return;
        }

        int scaleY = compactLayout ? controlsTop + 43 : colorY + 29;
        int sliderLeft = previewLeft + (compactLayout ? 42 : 92);
        int sliderRight = previewLeft + previewWidth - (compactLayout ? 8 : 10);
        if (inside(mouseX, mouseY, sliderLeft - 4, scaleY, sliderRight - sliderLeft + 8, 22)) {
            draggingScale = true;
            setScaleFromMouse(mouseX, sliderLeft, sliderRight);
            return;
        }

        int durationY = compactLayout ? controlsTop + 62 : scaleY + 27;
        int durationMinusX = previewLeft + previewWidth - (compactLayout ? 48 : 52);
        int durationPlusX = previewLeft + previewWidth - (compactLayout ? 25 : 28);
        if (inside(mouseX, mouseY, durationMinusX, durationY, 24, 20)) {
            profile.setAnimationDuration(Math.max(200, profile.animationDuration() - 100));
            Cosmetics189.save();
            return;
        }
        if (inside(mouseX, mouseY, durationPlusX, durationY, 24, 20)) {
            profile.setAnimationDuration(Math.min(3000, profile.animationDuration() + 100));
            Cosmetics189.save();
            return;
        }

        if (!compactLayout) {
            int placementTop = durationY + 39;
            if (inside(mouseX, mouseY, previewLeft + previewWidth - 52, placementTop, 24, 20)) {
                profile.setCapeVerticalOffset(Math.max(-0.5, profile.capeVerticalOffset() - 0.05));
                Cosmetics189.save();
                return;
            }
            if (inside(mouseX, mouseY, previewLeft + previewWidth - 28, placementTop, 24, 20)) {
                profile.setCapeVerticalOffset(Math.min(0.5, profile.capeVerticalOffset() + 0.05));
                Cosmetics189.save();
                return;
            }
            if (inside(mouseX, mouseY, previewLeft + previewWidth - 52, placementTop + 22, 24, 20)) {
                profile.setWingVerticalOffset(Math.max(-0.5, profile.wingVerticalOffset() - 0.05));
                Cosmetics189.save();
                return;
            }
            if (inside(mouseX, mouseY, previewLeft + previewWidth - 28, placementTop + 22, 24, 20)) {
                profile.setWingVerticalOffset(Math.min(0.5, profile.wingVerticalOffset() + 0.05));
                Cosmetics189.save();
                return;
            }
        }

        int previewTop = panelTop + (compactLayout ? 45 : 56);
        int previewBottom = panelTop + (compactLayout ? 126 : 278);
        if (inside(mouseX, mouseY, previewLeft, previewTop,
                previewWidth, previewBottom - previewTop)) {
            dragRotation = previewRotation;
            dragStartX = mouseX;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void stepColor(int direction) {
        int current = 0;
        for (int index = 0; index < COLORS.length; index++) {
            if (COLORS[index] == profile.color()) {
                current = index;
                break;
            }
        }
        current = (current + direction + COLORS.length) % COLORS.length;
        profile.setColor(COLORS[current]);
        Cosmetics189.save();
    }

    private void setScaleFromMouse(int mouseX, int sliderLeft, int sliderRight) {
        float fraction = sliderRight <= sliderLeft ? 0.5f
                : Math.max(0.0f, Math.min(1.0f,
                        (mouseX - sliderLeft) / (float) (sliderRight - sliderLeft)));
        profile.setScale(0.5 + fraction);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedButton, long timeSinceLastClick) {
        if (draggingScale && clickedButton == 0) {
            setScaleFromMouse(mouseX, previewLeft + (compactLayout ? 42 : 92),
                    previewLeft + previewWidth - (compactLayout ? 8 : 10));
        } else if (clickedButton == 0
                && inside(mouseX, mouseY, previewLeft, panelTop + (compactLayout ? 45 : 56),
                        previewWidth, compactLayout ? 81 : 222)) {
            previewRotation = dragRotation + (mouseX - dragStartX) * 0.75f;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (draggingScale) {
            Cosmetics189.save();
        }
        draggingScale = false;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0 && inside(Mouse.getX() * width / mc.displayWidth,
                height - Mouse.getY() * height / mc.displayHeight - 1,
                listLeft, listTop, listWidth, listBottom - listTop)) {
            listScroll = Math.max(0, Math.min(listScroll + (wheel > 0 ? -2 : 2),
                    Math.max(0, visibleItems.size() - itemCapacity())));
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (searchField.textboxKeyTyped(typedChar, keyCode)) {
            String updated = searchField.getText();
            if (!query.equals(updated)) {
                query = updated;
                listScroll = 0;
                updateVisibleItems();
            }
            return;
        }
        if (searchField.isFocused()) {
            if (keyCode == 1) {
                searchField.setFocused(false);
            }
            return;
        }
        if (keyCode == 1) {
            mc.displayGuiScreen(parent);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private static boolean inside(int mouseX, int mouseY, int x, int y, int targetWidth, int targetHeight) {
        return mouseX >= x && mouseX < x + targetWidth
                && mouseY >= y && mouseY < y + targetHeight;
    }
}
