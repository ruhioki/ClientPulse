package com.ruhioki.clientpulse.v189;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ruhioki.clientpulse.core.feature.ClientFeature;
import com.ruhioki.clientpulse.core.feature.FeatureCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Mouse;

public final class ClientPulseSettingsScreen189 extends GuiScreen {
    private static final int SIDEBAR_WIDTH = 126;
    private static final int CARD_HEIGHT = 48;
    private static final int CARD_GAP = 8;
    private static final int CATEGORY_BUTTON_BASE = 100;
    private static final int DONE_BUTTON_ID = 200;

    private FeatureCategory selectedCategory;
    private GuiTextField searchField;
    private List<ClientFeature> visibleFeatures = new ArrayList<ClientFeature>();
    private int scrollOffset;
    private int panelLeft;
    private int panelTop;
    private int panelWidth;
    private int panelHeight;
    private int contentLeft;
    private int contentWidth;
    private int listTop;
    private int visibleCardCount;

    public ClientPulseSettingsScreen189() {
        this.selectedCategory = null;
    }

    @Override
    public void initGui() {
        this.panelWidth = Math.min(580, this.width - 24);
        this.panelHeight = Math.min(420, this.height - 24);
        this.panelLeft = (this.width - this.panelWidth) / 2;
        this.panelTop = (this.height - this.panelHeight) / 2;
        this.contentLeft = this.panelLeft + SIDEBAR_WIDTH + 16;
        this.contentWidth = this.panelWidth - SIDEBAR_WIDTH - 32;
        this.listTop = this.panelTop + 112;
        this.visibleCardCount = Math.max(1,
                (this.panelTop + this.panelHeight - 48 - this.listTop + CARD_GAP) / (CARD_HEIGHT + CARD_GAP));
        this.searchField = new GuiTextField(0, this.fontRendererObj, this.contentLeft,
                this.panelTop + 68, this.contentWidth, 20);
        this.searchField.setMaxStringLength(48);
        this.buttonList.clear();
        rebuildButtons();
    }

    private void rebuildButtons() {
        String query = searchField == null ? "" : searchField.getText();
        this.buttonList.clear();

        FeatureCategory[] categories = FeatureCategory.values();
        for (int index = 0; index < categories.length; index++) {
            FeatureCategory category = categories[index];
            int x = this.panelLeft + 12;
            int y = this.panelTop + 82 + index * 34;
            this.buttonList.add(new PaletteButton(CATEGORY_BUTTON_BASE + index,
                    x, y, SIDEBAR_WIDTH - 24, 28, category.displayName(),
                    category == selectedCategory, true));
        }
        this.buttonList.add(new PaletteButton(CATEGORY_BUTTON_BASE + categories.length,
                this.panelLeft + 12, this.panelTop + 82 + categories.length * 34,
                SIDEBAR_WIDTH - 24, 28, "All features", selectedCategory == null, true));

        this.visibleFeatures = ClientPulse189.features().filter(this.selectedCategory, query);
        if (scrollOffset > Math.max(0, visibleFeatures.size() - visibleCardCount)) {
            scrollOffset = Math.max(0, visibleFeatures.size() - visibleCardCount);
        }

        int count = Math.min(visibleCardCount, visibleFeatures.size() - scrollOffset);
        for (int index = 0; index < count; index++) {
            ClientFeature feature = visibleFeatures.get(scrollOffset + index);
            int y = this.listTop + index * (CARD_HEIGHT + CARD_GAP);
            this.buttonList.add(new PaletteButton(index, this.contentLeft, y,
                    this.contentWidth, CARD_HEIGHT,
                    (feature.enabled() ? "ON   " : "OFF   ") + feature.name(),
                    false, feature.enabled()));
        }

        this.buttonList.add(new PaletteButton(DONE_BUTTON_ID, this.contentLeft,
                this.panelTop + this.panelHeight - 38, this.contentWidth, 26,
                "Done", false, false));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id >= CATEGORY_BUTTON_BASE
                && button.id <= CATEGORY_BUTTON_BASE + FeatureCategory.values().length) {
            int categoryIndex = button.id - CATEGORY_BUTTON_BASE;
            this.selectedCategory = categoryIndex == FeatureCategory.values().length
                    ? null : FeatureCategory.values()[categoryIndex];
            this.scrollOffset = 0;
            rebuildButtons();
        } else if (button.id == DONE_BUTTON_ID) {
            this.mc.displayGuiScreen(null);
        } else if (button.id >= 0 && button.id < visibleCardCount) {
            int featureIndex = this.scrollOffset + button.id;
            if (featureIndex >= 0 && featureIndex < visibleFeatures.size()) {
                ClientFeature feature = visibleFeatures.get(featureIndex);
                feature.setEnabled(!feature.enabled());
                ClientPulse189.proxy.saveSettings();
                rebuildButtons();
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (searchField.textboxKeyTyped(typedChar, keyCode)) {
            this.scrollOffset = 0;
            rebuildButtons();
            return;
        }
        if (ClientPulse189.proxy.isOpenSettingsKey(keyCode) && !searchField.isFocused()) {
            this.mc.displayGuiScreen(null);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0 && visibleFeatures.size() > visibleCardCount) {
            int contentRight = this.contentLeft + this.contentWidth;
            int contentBottom = this.panelTop + this.panelHeight - 44;
            int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            if (mouseX >= this.contentLeft && mouseX < contentRight
                    && mouseY >= this.listTop && mouseY < contentBottom) {
                this.scrollOffset += wheel > 0 ? -1 : 1;
                this.scrollOffset = Math.max(0,
                        Math.min(this.scrollOffset, visibleFeatures.size() - visibleCardCount));
                rebuildButtons();
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        drawRect(panelLeft, panelTop, panelLeft + panelWidth, panelTop + panelHeight, 0xF0101722);
        drawRect(panelLeft, panelTop, panelLeft + SIDEBAR_WIDTH, panelTop + panelHeight, 0xF51A2432);
        drawRect(panelLeft, panelTop, panelLeft + panelWidth, panelTop + 3, 0xFF54B8FF);
        drawRect(panelLeft + SIDEBAR_WIDTH, panelTop + 58,
                panelLeft + panelWidth, panelTop + 59, 0x553F5369);

        this.drawString(this.fontRendererObj, "CLIENTPULSE", panelLeft + 14, panelTop + 18, 0xFF76C5FF);
        this.drawString(this.fontRendererObj, "CLIENT GUI", panelLeft + 14, panelTop + 34, 0xFF8596A8);
        this.drawString(this.fontRendererObj, "FEATURES", contentLeft, panelTop + 19, 0xFFFFFFFF);
        this.drawString(this.fontRendererObj, selectedCategory == null ? "All features" : selectedCategory.displayName(),
                contentLeft, panelTop + 37, 0xFF8FA6BB);

        searchField.drawTextBox();
        if (searchField.getText().isEmpty() && !searchField.isFocused()) {
            this.drawString(this.fontRendererObj, "Search modules...",
                    contentLeft + 5, panelTop + 74, 0xFF77889B);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (visibleFeatures.isEmpty()) {
            this.drawString(this.fontRendererObj, "No features match this search.",
                    contentLeft, listTop + 8, 0xFF9BAABD);
        } else {
            int count = Math.min(visibleCardCount, visibleFeatures.size() - scrollOffset);
            for (int index = 0; index < count; index++) {
                ClientFeature feature = visibleFeatures.get(scrollOffset + index);
                int y = listTop + index * (CARD_HEIGHT + CARD_GAP);
                this.drawString(this.fontRendererObj, feature.description(),
                        contentLeft + 10, y + 29, 0xFF9BAABD);
            }
        }
        this.drawString(this.fontRendererObj,
                visibleFeatures.size() + " feature" + (visibleFeatures.size() == 1 ? "" : "s"),
                contentLeft, panelTop + panelHeight - 20, 0xFF75889C);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private static final class PaletteButton extends GuiButton {
        private final boolean selected;
        private final boolean active;

        private PaletteButton(int id, int x, int y, int width, int height,
                String label, boolean selected, boolean active) {
            super(id, x, y, width, height, label);
            this.selected = selected;
            this.active = active;
        }

        @Override
        public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
            if (!this.visible) {
                return;
            }
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition
                    && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int background = selected ? 0xFF294966 : (hovered ? 0xFF2B3542 : 0xFF202A36);
            int accent = selected ? 0xFF64C4FF : (active ? 0xFF63D4A2 : 0xFF596779);
            drawRect(this.xPosition, this.yPosition,
                    this.xPosition + this.width, this.yPosition + this.height, background);
            drawRect(this.xPosition, this.yPosition,
                    this.xPosition + 3, this.yPosition + this.height, accent);
            int textColor = active || selected ? 0xFFFFFFFF : 0xFFD1D9E2;
            minecraft.fontRendererObj.drawString(this.displayString,
                    this.xPosition + 10,
                    this.yPosition + (this.height - 8) / 2,
                    textColor);
        }
    }
}
