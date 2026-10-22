package com.ruhioki.clientpulse.legacy;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import com.ruhioki.clientpulse.core.feature.ClientFeature;

public final class ClientPulseSettingsScreen extends GuiScreen {
    @Override
    public void initGui() {
        this.buttonList.clear();
        List<ClientFeature> features = ClientPulseLegacy.features().all();
        for (int index = 0; index < features.size(); index++) {
            ClientFeature feature = features.get(index);
            String state = feature.enabled() ? "ON" : "OFF";
            this.buttonList.add(new GuiButton(index, this.width / 2 - 115, this.height / 2 - 52 + index * 25,
                    230, 20, state + "  " + feature.name()));
        }
        this.buttonList.add(new GuiButton(100, this.width / 2 - 115, this.height / 2 + 60,
                230, 20, "Done"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        List<ClientFeature> features = ClientPulseLegacy.features().all();
        if (button.id >= 0 && button.id < features.size()) {
            ClientFeature feature = features.get(button.id);
            feature.setEnabled(!feature.enabled());
            ClientPulseLegacy.proxy.saveSettings();
            this.initGui();
        } else if (button.id == 100) {
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "ClientPulse | Minecraft 1.7.10",
                this.width / 2, this.height / 2 - 82, 0x74B7FF);
        this.drawCenteredString(this.fontRendererObj, "Toggle optional client HUD modules",
                this.width / 2, this.height / 2 - 68, 0xAAB8D0);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
