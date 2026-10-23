package com.ruhioki.clientpulse.v189;

import com.ruhioki.clientpulse.core.animation.Animation;
import com.ruhioki.clientpulse.core.animation.Easings;
import com.ruhioki.clientpulse.core.setting.BooleanSetting;
import com.ruhioki.clientpulse.core.setting.ColorSetting;
import com.ruhioki.clientpulse.core.setting.EnumSetting;
import com.ruhioki.clientpulse.core.setting.KeybindSetting;
import com.ruhioki.clientpulse.core.setting.NumberSetting;
import com.ruhioki.clientpulse.core.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

public final class SettingControl189 {
    private final Setting<?> setting;
    private final Animation hover = new Animation(0.0f);
    private final StringBuilder colorInput = new StringBuilder(7);
    private boolean wasHovered;
    private boolean dragging;
    private boolean listening;
    private boolean editingColor;
    private boolean colorValid = true;

    public SettingControl189(Setting<?> setting) {
        this.setting = setting;
        if (setting instanceof ColorSetting) {
            colorInput.append(((ColorSetting) setting).hexValue());
        }
    }

    public Setting<?> setting() {
        return setting;
    }

    public boolean listening() {
        return listening;
    }

    public boolean editingColor() {
        return editingColor;
    }

    public boolean dragging() {
        return dragging;
    }

    public void draw(Minecraft minecraft, ClickGuiTheme189 theme, int x, int y, int width, int height,
            int mouseX, int mouseY, float alpha) {
        boolean inside = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        if (inside != wasHovered) {
            wasHovered = inside;
            hover.animateTo(inside ? 1.0f : 0.0f, theme.animationDuration(), Easings.EASE_OUT_CUBIC);
        }
        float hoverAmount = theme.animationsEnabled() ? hover.value() : (inside ? 1.0f : 0.0f);
        int background = blend(0xFF1A2230, 0xFF263246, hoverAmount);
        Gui.drawRect(x, y, x + width, y + height,
                CategorySidebar189.withAlpha(background, alpha));
        Gui.drawRect(x, y, x + 2, y + height,
                CategorySidebar189.withAlpha(theme.accentArgb(0.4f + hoverAmount * 0.6f), alpha));

        int labelColor = CategorySidebar189.withAlpha(theme.text(), alpha);
        int detailColor = CategorySidebar189.withAlpha(theme.muted(), alpha);
        minecraft.fontRendererObj.drawString(setting.name(), x + 10, y + 7, labelColor);
        minecraft.fontRendererObj.drawString(setting.description(), x + 10, y + 20, detailColor);
        drawValue(minecraft, theme, x, y, width, height, alpha);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button, int x, int y, int width, int height) {
        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height) {
            return false;
        }
        if (setting instanceof BooleanSetting) {
            BooleanSetting toggle = (BooleanSetting) setting;
            toggle.setValue(Boolean.valueOf(!toggle.value().booleanValue()));
        } else if (setting instanceof NumberSetting) {
            if (button == 0) {
                dragging = true;
                updateNumber(mouseX, x, width);
            }
        } else if (setting instanceof EnumSetting) {
            if (button == 0) {
                EnumSetting choice = (EnumSetting) setting;
                choice.setValue(choice.next());
            }
        } else if (setting instanceof KeybindSetting) {
            listening = true;
        } else if (setting instanceof ColorSetting) {
            editingColor = true;
            colorValid = true;
            colorInput.setLength(0);
        }
        return true;
    }

    public void mouseDragged(int mouseX, int x, int width) {
        if (dragging) {
            updateNumber(mouseX, x, width);
        }
    }

    public void mouseReleased() {
        dragging = false;
    }

    public boolean keyTyped(char typedChar, int keyCode) {
        if (listening) {
            KeybindSetting keybind = (KeybindSetting) setting;
            if (keyCode == Keyboard.KEY_ESCAPE) {
                keybind.setValue(Integer.valueOf(KeybindSetting.UNBOUND));
                listening = false;
            } else if (keyCode > 0 && keyCode <= 255) {
                keybind.setValue(Integer.valueOf(keyCode));
                listening = false;
            }
            return true;
        }
        if (!editingColor) {
            return false;
        }
        if (keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_RETURN) {
            editingColor = false;
            return true;
        }
        if (keyCode == Keyboard.KEY_BACK) {
            if (colorInput.length() > 0) {
                colorInput.deleteCharAt(colorInput.length() - 1);
            }
            updateColor();
            return true;
        }
        if (isHex(typedChar) && colorInput.length() < 7
                && (typedChar != '#' || colorInput.length() == 0)) {
            colorInput.append(Character.toUpperCase(typedChar));
            updateColor();
            return true;
        }
        return true;
    }

    private void drawValue(Minecraft minecraft, ClickGuiTheme189 theme,
            int x, int y, int width, int height, float alpha) {
        int right = x + width - 12;
        if (setting instanceof BooleanSetting) {
            boolean enabled = ((BooleanSetting) setting).value().booleanValue();
            int switchColor = enabled ? theme.accent() : 0xFF596373;
            Gui.drawRect(right - 28, y + 14, right, y + 27,
                    CategorySidebar189.withAlpha(switchColor, alpha));
            int knobX = enabled ? right - 13 : right - 26;
            Gui.drawRect(knobX, y + 16, knobX + 9, y + 25,
                    CategorySidebar189.withAlpha(0xFFFFFFFF, alpha));
            minecraft.fontRendererObj.drawString(enabled ? "ON" : "OFF",
                    right - 68, y + 17, CategorySidebar189.withAlpha(enabled ? theme.accent() : theme.muted(), alpha));
        } else if (setting instanceof NumberSetting) {
            NumberSetting number = (NumberSetting) setting;
            int sliderLeft = x + width / 2;
            int sliderRight = right;
            int sliderY = y + 27;
            double range = number.maximum() - number.minimum();
            float ratio = (float) ((number.doubleValue() - number.minimum()) / range);
            Gui.drawRect(sliderLeft, sliderY, sliderRight, sliderY + 3,
                    CategorySidebar189.withAlpha(0xFF4B5667, alpha));
            Gui.drawRect(sliderLeft, sliderY, sliderLeft + Math.round((sliderRight - sliderLeft) * ratio),
                    sliderY + 3, CategorySidebar189.withAlpha(theme.accentArgb(alpha), alpha));
            minecraft.fontRendererObj.drawString(formatNumber(number.doubleValue()), sliderRight - 44, y + 7,
                    CategorySidebar189.withAlpha(theme.accent(), alpha));
        } else if (setting instanceof EnumSetting) {
            String current = ((EnumSetting) setting).value();
            minecraft.fontRendererObj.drawString("<  " + current + "  >",
                    right - minecraft.fontRendererObj.getStringWidth(current) - 30, y + 16,
                    CategorySidebar189.withAlpha(theme.accent(), alpha));
        } else if (setting instanceof KeybindSetting) {
            KeybindSetting keybind = (KeybindSetting) setting;
            String value = listening ? "Press a key..." : keybind.keyCode() == 0
                    ? "None" : Keyboard.getKeyName(keybind.keyCode());
            minecraft.fontRendererObj.drawString(value,
                    right - minecraft.fontRendererObj.getStringWidth(value), y + 16,
                    CategorySidebar189.withAlpha(listening ? 0xFFFFCF70 : theme.accent(), alpha));
        } else if (setting instanceof ColorSetting) {
            ColorSetting color = (ColorSetting) setting;
            int swatchLeft = right - 116;
            Gui.drawRect(swatchLeft, y + 10, swatchLeft + 20, y + 30,
                    CategorySidebar189.withAlpha(0xFF000000 | color.rgb(), alpha));
            String value = editingColor
                    ? colorInput.length() == 0 ? "Type #RRGGBB" : colorInput.toString()
                    : color.hexValue();
            int colorText = colorValid ? theme.accent() : 0xFFFF7777;
            minecraft.fontRendererObj.drawString(value, swatchLeft + 27, y + 16,
                    CategorySidebar189.withAlpha(colorText, alpha));
        }
    }

    private void updateNumber(int mouseX, int x, int width) {
        NumberSetting number = (NumberSetting) setting;
        int sliderLeft = x + width / 2;
        int sliderRight = x + width - 12;
        float ratio = Math.max(0.0f, Math.min(1.0f,
                (float) (mouseX - sliderLeft) / Math.max(1, sliderRight - sliderLeft)));
        double value = number.minimum() + (number.maximum() - number.minimum()) * ratio;
        number.setValue(Double.valueOf(value));
    }

    private void updateColor() {
        colorValid = (colorInput.length() == 6
                || colorInput.length() == 7 && colorInput.charAt(0) == '#')
                && ((ColorSetting) setting).restore(colorInput.toString());
    }

    private static String formatNumber(double value) {
        double rounded = Math.round(value * 100.0) / 100.0;
        return Double.toString(rounded);
    }

    private static boolean isHex(char value) {
        return value >= '0' && value <= '9'
                || value >= 'a' && value <= 'f'
                || value >= 'A' && value <= 'F'
                || value == '#';
    }

    private static int blend(int first, int second, float amount) {
        int red = Math.round(((first >> 16) & 0xFF) + (((second >> 16) & 0xFF) - ((first >> 16) & 0xFF)) * amount);
        int green = Math.round(((first >> 8) & 0xFF) + (((second >> 8) & 0xFF) - ((first >> 8) & 0xFF)) * amount);
        int blue = Math.round((first & 0xFF) + ((second & 0xFF) - (first & 0xFF)) * amount);
        return 0xFF000000 | red << 16 | green << 8 | blue;
    }
}
