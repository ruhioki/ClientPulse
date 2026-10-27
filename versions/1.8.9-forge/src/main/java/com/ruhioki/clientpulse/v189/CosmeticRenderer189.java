package com.ruhioki.clientpulse.v189;

import com.ruhioki.clientpulse.core.cosmetic.CosmeticCategory;
import com.ruhioki.clientpulse.core.cosmetic.CosmeticProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public final class CosmeticRenderer189 {
    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.entityPlayer;
        if (player == null || player != Minecraft.getMinecraft().thePlayer) {
            return;
        }
        CosmeticProfile profile = Cosmetics189.profile();
        if (!profile.enabled()) {
            return;
        }

        float scale = (float) profile.scale();
        long time = System.currentTimeMillis();
        float phase = profile.animationsEnabled() ? time / (float) profile.animationDuration() : 0.0f;
        int color = profile.color();

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_CURRENT_BIT
                | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_LINE_BIT | GL11.GL_POINT_BIT);
        try {
            GL11.glScalef(scale, scale, scale);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDepthMask(false);

            if (profile.selected(CosmeticCategory.CAPES) != null) {
                renderCape(color, phase, player, (float) profile.capeVerticalOffset());
            }
            if (profile.selected(CosmeticCategory.WINGS) != null) {
                renderWings(color, phase, (float) profile.wingVerticalOffset());
            }
            if (profile.selected(CosmeticCategory.HATS) != null) {
                renderHalo(color, phase);
            }
            if (profile.selected(CosmeticCategory.TRAILS) != null) {
                renderTrail(color, phase);
            }
            if (profile.selected(CosmeticCategory.BADGES) != null) {
                renderBadge(color, phase);
            }
        } finally {
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

    private static void renderCape(int color, float phase, EntityPlayer player, float verticalOffset) {
        float sway = (float) Math.sin(phase * 1.3f) * 0.07f
                + Math.min(0.12f, (float) (Math.abs(player.motionX) + Math.abs(player.motionZ)) * 0.4f);
        GL11.glPushMatrix();
        try {
            GL11.glTranslatef(0.0f, 1.38f + verticalOffset, 0.0f);
            GL11.glBegin(GL11.GL_TRIANGLES);
            for (int row = 0; row < 8; row++) {
                float top = -row * 0.16f;
                float bottom = -(row + 1) * 0.16f;
                float topSwing = sway * row * 0.12f;
                float bottomSwing = sway * (row + 1) * 0.12f;
                setColor(color, 0.94f - row * 0.035f, 1.0f);
                vertex(-0.31f, top, 0.17f + topSwing);
                vertex(0.31f, top, 0.17f + topSwing);
                vertex(0.27f, bottom, 0.19f + bottomSwing);
                vertex(-0.31f, top, 0.17f + topSwing);
                vertex(0.27f, bottom, 0.19f + bottomSwing);
                vertex(-0.27f, bottom, 0.19f + bottomSwing);
                vertex(-0.31f, top, 0.15f + topSwing);
                vertex(-0.27f, bottom, 0.17f + bottomSwing);
                vertex(0.27f, bottom, 0.17f + bottomSwing);
                vertex(-0.31f, top, 0.15f + topSwing);
                vertex(0.27f, bottom, 0.17f + bottomSwing);
                vertex(0.31f, top, 0.15f + topSwing);
            }
            GL11.glEnd();
        } finally {
            GL11.glPopMatrix();
        }
    }

    private static void renderWings(int color, float phase, float verticalOffset) {
        float flap = (float) Math.sin(phase * 2.0f) * 18.0f;
        for (int side = -1; side <= 1; side += 2) {
            GL11.glPushMatrix();
            try {
                GL11.glTranslatef(side * 0.08f, 1.35f + verticalOffset, 0.15f);
                GL11.glRotatef(side * (18.0f + flap), 0.0f, 1.0f, 0.0f);
                GL11.glBegin(GL11.GL_TRIANGLES);
                setColor(color, 1.0f, 0.92f);
                vertex(0.0f, 0.1f, 0.0f);
                vertex(side * 0.66f, 0.34f, 0.02f);
                vertex(side * 0.79f, -0.1f, 0.01f);
                setColor(color, 0.69f, 0.9f);
                vertex(0.0f, 0.1f, 0.005f);
                vertex(side * 0.79f, -0.1f, 0.01f);
                vertex(side * 0.47f, -0.53f, 0.025f);
                GL11.glEnd();
            } finally {
                GL11.glPopMatrix();
            }
        }
    }

    private static void renderHalo(int color, float phase) {
        float bob = (float) Math.sin(phase * 2.0f) * 0.025f;
        GL11.glPushMatrix();
        try {
            GL11.glTranslatef(0.0f, 1.88f + bob, 0.0f);
            GL11.glLineWidth(2.5f);
            setColor(color, 1.0f, 0.95f);
            GL11.glBegin(GL11.GL_LINE_LOOP);
            for (int point = 0; point < 32; point++) {
                double angle = Math.PI * 2.0 * point / 32.0;
                GL11.glVertex3d(Math.cos(angle) * 0.29, 0.0, Math.sin(angle) * 0.12);
            }
            GL11.glEnd();
        } finally {
            GL11.glPopMatrix();
        }
    }

    private static void renderTrail(int color, float phase) {
        GL11.glPointSize(4.0f);
        setColor(color, 1.0f, 0.9f);
        GL11.glBegin(GL11.GL_POINTS);
        for (int point = 0; point < 12; point++) {
            float angle = phase * (float) Math.PI * 2.0f + point * 0.5235988f;
            float height = 1.3f - (point % 6) * 0.19f;
            float radius = 0.37f + (point % 3) * 0.045f;
            GL11.glVertex3f((float) Math.cos(angle) * radius, height,
                    (float) Math.sin(angle) * radius);
        }
        GL11.glEnd();
    }

    private static void renderBadge(int color, float phase) {
        float pulse = 1.0f + (float) Math.sin(phase * 2.0f) * 0.06f;
        GL11.glPushMatrix();
        try {
            GL11.glTranslatef(0.0f, 1.03f, -0.16f);
            GL11.glScalef(pulse, pulse, 1.0f);
            setColor(color, 1.0f, 0.95f);
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            vertex(0.0f, 0.0f, 0.0f);
            vertex(0.0f, 0.12f, 0.0f);
            vertex(0.10f, 0.0f, 0.0f);
            vertex(0.0f, -0.12f, 0.0f);
            vertex(-0.10f, 0.0f, 0.0f);
            vertex(0.0f, 0.12f, 0.0f);
            GL11.glEnd();
        } finally {
            GL11.glPopMatrix();
        }
    }

    private static void setColor(int color, float brightness, float alpha) {
        GL11.glColor4f(((color >> 16) & 0xFF) / 255.0f * brightness,
                ((color >> 8) & 0xFF) / 255.0f * brightness,
                (color & 0xFF) / 255.0f * brightness, alpha);
    }

    private static void vertex(float x, float y, float z) {
        GL11.glVertex3f(x, y, z);
    }
}
