package com.chadate.hudbar.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent.Pre;

public class HealthBarEventHandler {
    @SubscribeEvent
    public void onRenderGuiOverlay(Pre event) {
        // 取消原版血条和盔甲条的渲染
        if (event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type() || 
            event.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type()) {
            event.setCanceled(true);
            return;
        }
        
        // 确保在聊天栏渲染之后渲染我们的血条
        if (event.getOverlay() == VanillaGuiOverlay.CHAT_PANEL.type()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
                // 计算血条位置，使其与原版血条位置重合，并向上偏移
                int screenWidth = minecraft.getWindow().getGuiScaledWidth();
                int screenHeight = minecraft.getWindow().getGuiScaledHeight();
                int x = (screenWidth - 182) / 2;
                int y = screenHeight - 39; // 原来是35，现在改为37，向上移动2像素

                HealthBarRenderer.renderHealthBar(
                    event.getGuiGraphics().pose(),
                    x,
                    y,
                    minecraft.player
                );
            }
        }
    }
} 