package com.ruhioki.clientpulse.v189;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ruhioki.clientpulse.core.animation.Animation;
import com.ruhioki.clientpulse.core.animation.Easings;
import com.ruhioki.clientpulse.core.module.ClientModule;
import com.ruhioki.clientpulse.core.module.ModuleCategory;
import com.ruhioki.clientpulse.core.module.ModuleRegistry;
import com.ruhioki.clientpulse.core.setting.KeybindSetting;
import com.ruhioki.clientpulse.core.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public final class ClientPulseSettingsScreen189 extends GuiScreen {
    private static final int PANEL_MAX_WIDTH = 900;
    private static final int PANEL_MAX_HEIGHT = 540;
    private static final int MODULE_ROW_HEIGHT = 48;
    private static final int MODULE_ROW_GAP = 10;
    private static final int SETTING_ROW_HEIGHT = 42;
    private static final int SETTING_ROW_GAP = 6;
    private static final int TOOLBAR_HEIGHT = 94;
    private static final int FOOTER_HEIGHT = 32;

    private final ModuleRegistry modules = ClientPulse189.modules();
    private final ClickGuiTheme189 theme = new ClickGuiTheme189(modules);
    private final CategorySidebar189 sidebar = new CategorySidebar189(modules);
    private final Animation transition = new Animation(0.0f);
    private final Animation moduleScroll = new Animation(0.0f);
    private final Animation settingScroll = new Animation(0.0f);
    private final Map<String, ModuleButton189> moduleButtons = new LinkedHashMap<String, ModuleButton189>();
    private final Map<String, SettingControl189> settingControls = new LinkedHashMap<String, SettingControl189>();
    private final List<ClientModule> visibleModules = new ArrayList<ClientModule>();

    private ModuleCategory selectedCategory;
    private String selectedModuleId;
    private GuiTextField searchField;
    private String previousSearch = "";
    private float moduleScrollTarget;
    private float settingScrollTarget;
    private int panelLeft;
    private int panelTop;
    private int panelWidth;
    private int panelHeight;
    private int sidebarWidth;
    private int categoryRowStride;
    private int contentLeft;
    private int contentWidth;
    private int listTop;
    private int listBottom;
    private int moduleListWidth;
    private int settingsLeft;
    private int settingsWidth;
    private boolean compact;
    private boolean compactShowSettings;
    private boolean closing;
    private boolean draggingPanel;
    private boolean changedWhileDragging;
    private int dragOffsetX;
    private int dragOffsetY;
    private int transformedMouseX;
    private int transformedMouseY;

    @Override
    public void initGui() {
        layoutPanel();
        searchField = new GuiTextField(0, fontRendererObj,
                contentLeft, panelTop + 62, contentWidth, 18);
        searchField.setMaxStringLength(48);
        searchField.setEnableBackgroundDrawing(false);
        searchField.setCanLoseFocus(true);
        updateVisibleModules();

        transition.reset(0.0f);
        if (theme.animationsEnabled()) {
            transition.animateTo(1.0f, theme.animationDuration(), Easings.EASE_OUT_CUBIC);
        } else {
            transition.reset(1.0f);
        }
    }

    private void layoutPanel() {
        panelWidth = Math.min(PANEL_MAX_WIDTH, Math.max(1, width - 24));
        panelHeight = Math.min(PANEL_MAX_HEIGHT, Math.max(1, height - 24));
        panelLeft = (width - panelWidth) / 2;
        panelTop = (height - panelHeight) / 2;
        sidebarWidth = Math.min(156, Math.max(90, Math.round(panelWidth * 0.19f)));
        categoryRowStride = Math.min(36, Math.max(20, (panelHeight - 116) / 5));
        contentLeft = panelLeft + sidebarWidth + 16;
        contentWidth = Math.max(1, panelWidth - sidebarWidth - 32);
        listTop = panelTop + TOOLBAR_HEIGHT;
        listBottom = panelTop + panelHeight - FOOTER_HEIGHT;
        compact = contentWidth < 440;
        moduleListWidth = compact ? contentWidth : Math.round((contentWidth - 12) * 0.49f);
        settingsLeft = compact ? contentLeft : contentLeft + moduleListWidth + 12;
        settingsWidth = compact ? contentWidth : contentWidth - moduleListWidth - 12;
    }

    private void updateVisibleModules() {
        visibleModules.clear();
        String query = searchField == null ? "" : searchField.getText();
        visibleModules.addAll(modules.filter(selectedCategory, query));
        float maximum = Math.max(0.0f, visibleModules.size() - visibleModuleCapacity());
        moduleScrollTarget = Math.max(0.0f, Math.min(moduleScrollTarget, maximum));
        if (moduleScrollTarget == 0.0f) {
            moduleScroll.reset(0.0f);
        } else {
            moduleScroll.animateTo(moduleScrollTarget, animationDuration(), Easings.EASE_IN_OUT);
        }
        if (selectedModuleId != null && !containsModule(selectedModuleId)) {
            selectedModuleId = null;
            compactShowSettings = false;
        }
    }

    private int visibleModuleCapacity() {
        return Math.max(1, (listBottom - listTop + MODULE_ROW_GAP)
                / (MODULE_ROW_HEIGHT + MODULE_ROW_GAP));
    }

    private long animationDuration() {
        return theme.animationsEnabled() ? theme.animationDuration() : 0L;
    }

    private boolean containsModule(String id) {
        for (ClientModule module : visibleModules) {
            if (module.id().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private ClientModule selectedModule() {
        if (selectedModuleId == null) {
            return null;
        }
        for (ClientModule module : visibleModules) {
            if (module.id().equals(selectedModuleId)) {
                return module;
            }
        }
        return null;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (closing) {
            return;
        }
        if (searchField != null && searchField.textboxKeyTyped(typedChar, keyCode)) {
            if (!previousSearch.equals(searchField.getText())) {
                previousSearch = searchField.getText();
                moduleScrollTarget = 0.0f;
                moduleScroll.reset(0.0f);
                updateVisibleModules();
            }
            return;
        }

        ClientModule selected = selectedModule();
        if (selected != null) {
            for (Setting<?> setting : selected.settings()) {
                SettingControl189 control = settingControl(selected, setting);
                if (control.keyTyped(typedChar, keyCode)) {
                    ClientPulse189.proxy.saveSettings();
                    return;
                }
            }
        }

        if (searchField != null && searchField.isFocused()) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                searchField.setFocused(false);
            }
            return;
        }
        if (keyCode == Keyboard.KEY_C) {
            mc.displayGuiScreen(new CosmeticsScreen189(this));
            return;
        }
        if (ClientPulse189.proxy.isOpenSettingsKey(keyCode)) {
            beginClose();
            return;
        }
        if (keyCode == Keyboard.KEY_ESCAPE) {
            beginClose();
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        transformMouseCoordinates(mouseX, mouseY);
        mouseX = transformedMouseX;
        mouseY = transformedMouseY;
        if (closing) {
            return;
        }

        if (searchField != null && inside(mouseX, mouseY,
                contentLeft, panelTop + 60, contentWidth, 22)) {
            searchField.mouseClicked(mouseX, mouseY, mouseButton);
            return;
        }
        if (searchField != null) {
            searchField.setFocused(false);
        }

        if (inside(mouseX, mouseY, panelLeft + panelWidth - 34, panelTop + 12, 22, 22)) {
            beginClose();
            return;
        }
        if (hasCosmeticsButton()
                && inside(mouseX, mouseY, panelLeft + panelWidth - 126, panelTop + 12, 82, 22)) {
            mc.displayGuiScreen(new CosmeticsScreen189(this));
            return;
        }
        if (inside(mouseX, mouseY, panelLeft + 4, panelTop + 4, panelWidth - 44, 48)) {
            draggingPanel = true;
            dragOffsetX = mouseX - panelLeft;
            dragOffsetY = mouseY - panelTop;
            return;
        }

        int sidebarTop = panelTop + 78;
        if (inside(mouseX, mouseY, panelLeft, sidebarTop, sidebarWidth, categoryRowStride * 5)) {
            selectedCategory = sidebar.categoryAt(mouseX, mouseY,
                    panelLeft + 8, sidebarTop, sidebarWidth - 16, categoryRowStride);
            moduleScrollTarget = 0.0f;
            moduleScroll.reset(0.0f);
            updateVisibleModules();
            return;
        }

        if (compact && compactShowSettings) {
            if (inside(mouseX, mouseY, contentLeft, panelTop + 84, 100, 20)) {
                compactShowSettings = false;
                return;
            }
            handleSettingClick(mouseX, mouseY, mouseButton);
            return;
        }

        int listWidth = compact ? contentWidth : moduleListWidth;
        if (inside(mouseX, mouseY, contentLeft, listTop, listWidth, listBottom - listTop)) {
            ClientModule module = moduleAt(mouseX, mouseY);
            if (module != null) {
                ModuleButton189 button = moduleButton(module);
                int rowY = moduleY(module);
                if (button.toggleAt(mouseX, mouseY, contentLeft, rowY,
                        listWidth, MODULE_ROW_HEIGHT)) {
                    ClientPulse189.proxy.saveSettings();
                } else {
                    selectedModuleId = module.id();
                    settingScrollTarget = 0.0f;
                    settingScroll.reset(0.0f);
                    if (compact) {
                        compactShowSettings = true;
                    }
                }
            }
            return;
        }

        if (!compact && selectedModuleId != null
                && inside(mouseX, mouseY, settingsLeft, listTop, settingsWidth, listBottom - listTop)) {
            handleSettingClick(mouseX, mouseY, mouseButton);
        }
    }

    private void handleSettingClick(int mouseX, int mouseY, int mouseButton) {
        ClientModule selected = selectedModule();
        if (selected == null || !inside(mouseX, mouseY,
                settingsLeft, listTop, settingsWidth, listBottom - listTop)) {
            return;
        }
        for (int index = 0; index < selected.settings().size(); index++) {
            int y = settingY(index);
            if (!inside(mouseX, mouseY, settingsLeft, y, settingsWidth, SETTING_ROW_HEIGHT)
                    || y < listTop || y + SETTING_ROW_HEIGHT > listBottom) {
                continue;
            }
            Setting<?> setting = selected.settings().get(index);
            if (settingControl(selected, setting).mouseClicked(
                    mouseX, mouseY, mouseButton, settingsLeft + 6, y,
                    Math.max(1, settingsWidth - 12), SETTING_ROW_HEIGHT)) {
                ClientPulse189.proxy.saveSettings();
                return;
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        transformMouseCoordinates(mouseX, mouseY);
        mouseX = transformedMouseX;
        mouseY = transformedMouseY;
        if (draggingPanel) {
            panelLeft = Math.max(0, Math.min(width - panelWidth, mouseX - dragOffsetX));
            panelTop = Math.max(0, Math.min(height - panelHeight, mouseY - dragOffsetY));
            updateLayoutForDraggedPanel();
            return;
        }
        ClientModule selected = selectedModule();
        if (selected != null) {
            for (int index = 0; index < selected.settings().size(); index++) {
                Setting<?> setting = selected.settings().get(index);
                SettingControl189 control = settingControl(selected, setting);
                int y = settingY(index);
                control.mouseDragged(mouseX, settingsLeft + 6, Math.max(1, settingsWidth - 12));
                if (control.dragging() && y >= listTop && y + SETTING_ROW_HEIGHT <= listBottom) {
                    changedWhileDragging = true;
                }
            }
        }
    }

    private void updateLayoutForDraggedPanel() {
        contentLeft = panelLeft + sidebarWidth + 16;
        listTop = panelTop + TOOLBAR_HEIGHT;
        listBottom = panelTop + panelHeight - FOOTER_HEIGHT;
        settingsLeft = compact ? contentLeft : contentLeft + moduleListWidth + 12;
        if (!compact) {
            settingsWidth = contentWidth - moduleListWidth - 12;
        }
        searchField.xPosition = contentLeft;
        searchField.yPosition = panelTop + 62;
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        draggingPanel = false;
        for (SettingControl189 control : settingControls.values()) {
            control.mouseReleased();
        }
        if (changedWhileDragging) {
            changedWhileDragging = false;
            ClientPulse189.proxy.saveSettings();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel == 0) {
            return;
        }
        int mouseX = Mouse.getEventX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        transformMouseCoordinates(mouseX, mouseY);
        mouseX = transformedMouseX;
        mouseY = transformedMouseY;

        int delta = wheel > 0 ? -1 : 1;
        if (inside(mouseX, mouseY, contentLeft, listTop,
                compact && compactShowSettings ? 0 : compact ? contentWidth : moduleListWidth,
                listBottom - listTop)) {
            moduleScrollTarget = clamp(moduleScrollTarget + delta,
                    0.0f, Math.max(0.0f, visibleModules.size() - visibleModuleCapacity()));
            moduleScroll.animateTo(moduleScrollTarget, animationDuration(), Easings.EASE_IN_OUT);
        } else if (selectedModuleId != null && (!compact || compactShowSettings)
                && inside(mouseX, mouseY, settingsLeft, listTop, settingsWidth, listBottom - listTop)) {
            ClientModule selected = selectedModule();
            if (selected != null) {
                float maximum = Math.max(0.0f, selected.settings().size() - visibleSettingCapacity());
                settingScrollTarget = clamp(settingScrollTarget + delta, 0.0f, maximum);
                settingScroll.animateTo(settingScrollTarget, animationDuration(), Easings.EASE_IN_OUT);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (closing && transition.isFinished()) {
            mc.displayGuiScreen(null);
            return;
        }

        drawDefaultBackground();
        transformMouseCoordinates(mouseX, mouseY);
        mouseX = transformedMouseX;
        mouseY = transformedMouseY;
        float progress = transition.value();
        float scale = 0.92f + 0.08f * progress;

        GlStateManager.pushMatrix();
        GlStateManager.translate(width / 2.0f, height / 2.0f, 0.0f);
        GlStateManager.scale(scale, scale, 1.0f);
        GlStateManager.translate(-width / 2.0f, -height / 2.0f, 0.0f);

        Gui.drawRect(panelLeft, panelTop, panelLeft + panelWidth, panelTop + panelHeight,
                alpha(theme.panel(), progress));
        Gui.drawRect(panelLeft, panelTop, panelLeft + sidebarWidth, panelTop + panelHeight,
                alpha(theme.sidebar(), progress));
        Gui.drawRect(panelLeft, panelTop, panelLeft + panelWidth, panelTop + 3,
                theme.accentArgb(progress));
        Gui.drawRect(panelLeft + sidebarWidth, panelTop + 54,
                panelLeft + panelWidth, panelTop + 55, alpha(0x55425267, progress));

        drawHeader(mouseX, mouseY, progress);
        drawSidebar(mouseX, mouseY, progress);
        drawMainContent(mouseX, mouseY, progress);
        drawFooter(progress);
        GlStateManager.popMatrix();
    }

    private void drawHeader(int mouseX, int mouseY, float alpha) {
        fontRendererObj.drawString("CLIENTPULSE", panelLeft + 17, panelTop + 15,
                CategorySidebar189.withAlpha(theme.accent(), alpha));
        fontRendererObj.drawString("1.8.9  /  CLIENT", panelLeft + 17, panelTop + 31,
                CategorySidebar189.withAlpha(theme.muted(), alpha));
        fontRendererObj.drawString("CLICK GUI", contentLeft, panelTop + 17,
                CategorySidebar189.withAlpha(theme.text(), alpha));
        fontRendererObj.drawString("Search and filter modules", contentLeft, panelTop + 34,
                CategorySidebar189.withAlpha(theme.muted(), alpha));

        if (hasCosmeticsButton()) {
            int cosmeticsButtonX = panelLeft + panelWidth - 126;
            boolean cosmeticsHovered = inside(mouseX, mouseY, cosmeticsButtonX, panelTop + 12, 82, 22);
            Gui.drawRect(cosmeticsButtonX, panelTop + 12, cosmeticsButtonX + 82, panelTop + 34,
                    CategorySidebar189.withAlpha(cosmeticsHovered ? theme.accent() : 0xFF27303E, alpha));
            fontRendererObj.drawString("COSMETICS", cosmeticsButtonX + 6, panelTop + 19,
                    CategorySidebar189.withAlpha(theme.text(), alpha));
        }

        Gui.drawRect(panelLeft + panelWidth - 34, panelTop + 12,
                panelLeft + panelWidth - 12, panelTop + 34,
                CategorySidebar189.withAlpha(inside(mouseX, mouseY,
                        panelLeft + panelWidth - 34, panelTop + 12, 22, 22)
                        ? 0xFF8B4552 : 0xFF27303E, alpha));
        fontRendererObj.drawString("x", panelLeft + panelWidth - 27,
                panelTop + 18, CategorySidebar189.withAlpha(theme.text(), alpha));

        Gui.drawRect(contentLeft, panelTop + 60, contentLeft + contentWidth,
                panelTop + 81, CategorySidebar189.withAlpha(0xFF1B2330, alpha));
        if (searchField != null) {
            searchField.drawTextBox();
            if (searchField.getText().isEmpty() && !searchField.isFocused()) {
                fontRendererObj.drawString("Type to filter modules...",
                        contentLeft + 6, panelTop + 67,
                        CategorySidebar189.withAlpha(theme.muted(), alpha));
            }
        }
    }

    private void drawSidebar(int mouseX, int mouseY, float alpha) {
        int sidebarTop = panelTop + 78;
        sidebar.draw(mc, theme, panelLeft + 8, sidebarTop,
                sidebarWidth - 16, categoryRowStride, selectedCategory, mouseX, mouseY, alpha);
    }

    private void drawMainContent(int mouseX, int mouseY, float alpha) {
        ClientModule selected = selectedModule();
        boolean showSettingsOnly = compact && compactShowSettings && selected != null;
        if (showSettingsOnly) {
            fontRendererObj.drawString("< Modules", contentLeft, panelTop + 85,
                    CategorySidebar189.withAlpha(theme.accent(), alpha));
            drawSettingsPanel(selected, settingsLeft, mouseX, mouseY, alpha);
            return;
        }

        fontRendererObj.drawString("MODULES", contentLeft, panelTop + 84,
                CategorySidebar189.withAlpha(theme.text(), alpha));
        if (!compact) {
            fontRendererObj.drawString("SETTINGS", settingsLeft, panelTop + 84,
                    CategorySidebar189.withAlpha(theme.text(), alpha));
        }
        drawModuleList(mouseX, mouseY, alpha);
        if (!compact) {
            if (selected != null) {
                drawSettingsPanel(selected, settingsLeft, mouseX, mouseY, alpha);
            } else {
                fontRendererObj.drawString("Select a module to view its settings.",
                        settingsLeft, listTop + 12, CategorySidebar189.withAlpha(theme.muted(), alpha));
            }
        }
    }

    private void drawModuleList(int mouseX, int mouseY, float alpha) {
        Gui.drawRect(contentLeft, listTop - 1, contentLeft + (compact ? contentWidth : moduleListWidth),
                listBottom, alpha(0x55313C4D, alpha));
        float scroll = theme.animationsEnabled() ? moduleScroll.value() : moduleScrollTarget;
        for (int index = 0; index < visibleModules.size(); index++) {
            int rowY = listTop + Math.round((index - scroll) * (MODULE_ROW_HEIGHT + MODULE_ROW_GAP));
            if (rowY < listTop || rowY + MODULE_ROW_HEIGHT > listBottom) {
                continue;
            }
            ClientModule module = visibleModules.get(index);
            ModuleButton189 button = moduleButton(module);
            button.draw(mc, theme, contentLeft, rowY,
                    compact ? contentWidth : moduleListWidth, MODULE_ROW_HEIGHT,
                    mouseX, mouseY, alpha);
            if (module.id().equals(selectedModuleId)) {
                Gui.drawRect(contentLeft + (compact ? contentWidth : moduleListWidth) - 54,
                        rowY + 3, contentLeft + (compact ? contentWidth : moduleListWidth) - 52,
                        rowY + MODULE_ROW_HEIGHT - 3, theme.accentArgb(alpha));
            }
        }
        if (visibleModules.isEmpty()) {
            fontRendererObj.drawString("No modules match this search.",
                    contentLeft + 8, listTop + 10, CategorySidebar189.withAlpha(theme.muted(), alpha));
        }
    }

    private void drawSettingsPanel(ClientModule module, int x, int mouseX, int mouseY, float alpha) {
        int width = compact ? contentWidth : settingsWidth;
        Gui.drawRect(x, listTop - 1, x + width, listBottom, alpha(0x55313C4D, alpha));
        fontRendererObj.drawString(module.name(), x + 8, listTop + 8,
                CategorySidebar189.withAlpha(theme.accent(), alpha));
        fontRendererObj.drawString(module.description(), x + 8, listTop + 22,
                CategorySidebar189.withAlpha(theme.muted(), alpha));

        int settingTop = listTop + 40;
        int settingBottom = listBottom - 6;
        float scroll = theme.animationsEnabled() ? settingScroll.value() : settingScrollTarget;
        for (int index = 0; index < module.settings().size(); index++) {
            int y = settingTop + Math.round((index - scroll) * (SETTING_ROW_HEIGHT + SETTING_ROW_GAP));
            if (y < settingTop || y + SETTING_ROW_HEIGHT > settingBottom) {
                continue;
            }
            Setting<?> setting = module.settings().get(index);
            settingControl(module, setting).draw(mc, theme,
                    x + 6, y, Math.max(1, width - 12), SETTING_ROW_HEIGHT,
                    mouseX, mouseY, alpha);
        }
        if (module.settings().isEmpty()) {
            fontRendererObj.drawString("This module has no settings.",
                    x + 8, settingTop + 8, CategorySidebar189.withAlpha(theme.muted(), alpha));
        }
    }

    private void drawFooter(float alpha) {
        int keyCode = ((KeybindSetting) modules.get("interface-theme").setting("open-key")).keyCode();
        String keyName = keyCode == 0 ? "UNBOUND" : Keyboard.getKeyName(keyCode);
        fontRendererObj.drawString(keyName.toUpperCase(java.util.Locale.ROOT) + "  /  close",
                panelLeft + 13, panelTop + panelHeight - 20,
                CategorySidebar189.withAlpha(theme.muted(), alpha));
        fontRendererObj.drawString(visibleModules.size() + " MODULES",
                panelLeft + panelWidth - 86, panelTop + panelHeight - 20,
                CategorySidebar189.withAlpha(theme.muted(), alpha));
    }

    private void beginClose() {
        if (closing) {
            return;
        }
        if (!theme.animationsEnabled()) {
            mc.displayGuiScreen(null);
            return;
        }
        closing = true;
        transition.animateTo(0.0f, theme.animationDuration(), Easings.EASE_IN_OUT);
    }

    private boolean hasCosmeticsButton() {
        return panelWidth >= 300;
    }

    private int visibleSettingCapacity() {
        return Math.max(1, (listBottom - listTop - 46 + SETTING_ROW_GAP)
                / (SETTING_ROW_HEIGHT + SETTING_ROW_GAP));
    }

    private int settingY(int settingIndex) {
        int settingTop = listTop + 40;
        float scroll = theme.animationsEnabled() ? settingScroll.value() : settingScrollTarget;
        return settingTop + Math.round((settingIndex - scroll) * (SETTING_ROW_HEIGHT + SETTING_ROW_GAP));
    }

    private int moduleY(ClientModule module) {
        int index = 0;
        while (index < visibleModules.size() && visibleModules.get(index) != module) {
            index++;
        }
        float scroll = theme.animationsEnabled() ? moduleScroll.value() : moduleScrollTarget;
        return listTop + Math.round((index - scroll) * (MODULE_ROW_HEIGHT + MODULE_ROW_GAP));
    }

    private ClientModule moduleAt(int mouseX, int mouseY) {
        for (int index = 0; index < visibleModules.size(); index++) {
            int rowY = moduleY(visibleModules.get(index));
            if (inside(mouseX, mouseY, contentLeft, rowY,
                    compact ? contentWidth : moduleListWidth, MODULE_ROW_HEIGHT)
                    && rowY >= listTop && rowY + MODULE_ROW_HEIGHT <= listBottom) {
                return visibleModules.get(index);
            }
        }
        return null;
    }

    private ModuleButton189 moduleButton(ClientModule module) {
        ModuleButton189 button = moduleButtons.get(module.id());
        if (button == null) {
            button = new ModuleButton189(module);
            moduleButtons.put(module.id(), button);
        }
        return button;
    }

    private SettingControl189 settingControl(ClientModule module, Setting<?> setting) {
        String key = module.id() + "." + setting.id();
        SettingControl189 control = settingControls.get(key);
        if (control == null) {
            control = new SettingControl189(setting);
            settingControls.put(key, control);
        }
        return control;
    }

    private void transformMouseCoordinates(int mouseX, int mouseY) {
        float progress = transition.value();
        float scale = 0.92f + 0.08f * progress;
        transformedMouseX = Math.round((mouseX - width / 2.0f) / scale + width / 2.0f);
        transformedMouseY = Math.round((mouseY - height / 2.0f) / scale + height / 2.0f);
    }

    private static boolean inside(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private static int alpha(int color, float amount) {
        return CategorySidebar189.withAlpha(color, amount);
    }

    private static float clamp(float value, float minimum, float maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
