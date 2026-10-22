package com.ruhioki.clientpulse.screen;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import com.ruhioki.clientpulse.ClientPulseClient;
import com.ruhioki.clientpulse.core.feature.ClientFeature;
import com.ruhioki.clientpulse.core.feature.FeatureCategory;
import com.ruhioki.clientpulse.core.feature.FeatureRegistry;

public final class ClientPulseScreen extends Screen {
    private static final int PANEL_COLOR = 0xE5101628;
    private static final int MUTED_COLOR = 0xFF9BAAC5;
    private static final int ACCENT_COLOR = 0xFF74B7FF;

    private final Screen parent;
    private final FeatureRegistry features;
    private FeatureCategory selectedCategory;
    private String searchQuery = "";
    private boolean saveFailed;
    private EditBox searchBox;
    private final List<Button> featureButtons = new ArrayList<>();
    private final List<Button> categoryButtons = new ArrayList<>();

    public ClientPulseScreen(Screen parent, FeatureRegistry features) {
        super(Component.literal("ClientPulse"));
        this.parent = parent;
        this.features = features;
    }

    @Override
    protected void init() {
        int panelWidth = Math.min(680, this.width - 24);
        int panelLeft = (this.width - panelWidth) / 2;
        int panelTop = Math.max(24, (this.height - 300) / 2);
        int sidebarWidth = Math.min(142, Math.max(106, panelWidth / 3));
        int listLeft = panelLeft + sidebarWidth + 14;
        int listWidth = Math.max(80, panelWidth - sidebarWidth - 24);

        searchBox = new EditBox(this.font, listLeft, panelTop + 36, listWidth, 20,
                Component.literal("Search features"));
        searchBox.setHint(Component.literal("Search features..."));
        searchBox.setValue(searchQuery);
        searchBox.setResponder(value -> {
            searchQuery = value;
            refreshFeatureButtons(listLeft, panelTop + 68, listWidth);
        });
        this.addRenderableWidget(searchBox);

        int categoryY = panelTop + 36;
        categoryButtons.clear();
        for (FeatureCategory category : FeatureCategory.values()) {
            addCategoryButton(category, panelLeft, categoryY, sidebarWidth);
            categoryY += 27;
        }
        addAllCategoryButton(panelLeft, categoryY, sidebarWidth);
        refreshFeatureButtons(listLeft, panelTop + 68, listWidth);
    }

    private void addCategoryButton(FeatureCategory category, int x, int y, int width) {
        String label = (selectedCategory == category ? "● " : "") + category.displayName();
        Button button = Button.builder(Component.literal(label), ignored -> {
            selectedCategory = category;
            refreshCategoryButtons(x, y - category.ordinal() * 27, width);
            int panelWidth = Math.min(680, this.width - 24);
            int panelLeft = (this.width - panelWidth) / 2;
            int sidebarWidth = Math.min(142, Math.max(106, panelWidth / 3));
            refreshFeatureButtons(panelLeft + sidebarWidth + 14,
                    Math.max(24, (this.height - 300) / 2) + 68,
                    Math.max(80, panelWidth - sidebarWidth - 24));
        }).bounds(x, y, width, 20).build();
        categoryButtons.add(this.addRenderableWidget(button));
    }

    private void addAllCategoryButton(int x, int y, int width) {
        String label = (selectedCategory == null ? "● " : "") + "All features";
        Button button = Button.builder(Component.literal(label), ignored -> {
            selectedCategory = null;
            refreshCategoryButtons(x, y - FeatureCategory.values().length * 27, width);
            int panelWidth = Math.min(680, this.width - 24);
            int panelLeft = (this.width - panelWidth) / 2;
            int sidebarWidth = Math.min(142, Math.max(106, panelWidth / 3));
            refreshFeatureButtons(panelLeft + sidebarWidth + 14,
                    Math.max(24, (this.height - 300) / 2) + 68,
                    Math.max(80, panelWidth - sidebarWidth - 24));
        }).bounds(x, y, width, 20).build();
        categoryButtons.add(this.addRenderableWidget(button));
    }

    private void refreshCategoryButtons(int x, int y, int width) {
        for (Button button : categoryButtons) {
            this.removeWidget(button);
        }
        categoryButtons.clear();
        for (FeatureCategory category : FeatureCategory.values()) {
            addCategoryButton(category, x, y, width);
            y += 27;
        }
        addAllCategoryButton(x, y, width);
    }

    private void refreshFeatureButtons(int x, int startY, int width) {
        for (Button button : featureButtons) {
            this.removeWidget(button);
        }
        featureButtons.clear();

        int y = startY;
        for (ClientFeature feature : features.filter(selectedCategory, searchQuery)) {
            if (y + 42 > this.height - 24) {
                break;
            }

            Button button = Button.builder(toggleLabel(feature), ignored -> {
                feature.setEnabled(!feature.enabled());
                saveFailed = !ClientPulseClient.saveConfig();
                refreshFeatureButtons(x, startY, width);
            }).bounds(x, y, width, 20).build();
            featureButtons.add(this.addRenderableWidget(button));
            y += 48;
        }
    }

    private static Component toggleLabel(ClientFeature feature) {
        return Component.literal((feature.enabled() ? "ON  " : "OFF  ") + feature.name());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        int panelWidth = Math.min(680, this.width - 24);
        int panelLeft = (this.width - panelWidth) / 2;
        int panelTop = Math.max(24, (this.height - 300) / 2);
        int panelHeight = Math.min(340, this.height - panelTop - 12);
        graphics.fill(panelLeft - 12, panelTop - 20,
                panelLeft + panelWidth + 12, panelTop + panelHeight, PANEL_COLOR);

        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.text(this.font, "CLIENTPULSE", panelLeft, panelTop - 15, ACCENT_COLOR, true);
        graphics.text(this.font, "Client-side features", panelLeft + 122, panelTop - 15, MUTED_COLOR, true);

        int sidebarWidth = Math.min(142, Math.max(106, panelWidth / 3));
        int listLeft = panelLeft + sidebarWidth + 14;
        int descriptionY = panelTop + 90;
        List<ClientFeature> visibleFeatures = features.filter(selectedCategory, searchQuery);
        if (visibleFeatures.isEmpty()) {
            String emptyMessage = searchQuery.isBlank()
                    ? "No features in this category yet."
                    : "No features match your search.";
            graphics.text(this.font, emptyMessage, listLeft + 4, panelTop + 72, MUTED_COLOR, false);
        }
        for (ClientFeature feature : visibleFeatures) {
            if (descriptionY + 10 > this.height - 24) {
                break;
            }
            graphics.text(this.font, feature.description(), listLeft + 4, descriptionY, MUTED_COLOR, false);
            descriptionY += 48;
        }
        String status = saveFailed ? "Could not save settings. Check the log." : "Right Shift or Escape to close";
        graphics.text(this.font, status, panelLeft, this.height - 22,
                saveFailed ? 0xFFFF8A80 : MUTED_COLOR, false);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(parent);
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (!searchBox.isFocused() && ClientPulseClient.openSettingsKeyMatches(keyEvent)) {
            onClose();
            return true;
        }
        return super.keyPressed(keyEvent);
    }
}
