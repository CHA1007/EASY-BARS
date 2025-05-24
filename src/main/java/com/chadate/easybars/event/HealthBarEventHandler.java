package com.chadate.easybars.event;

import com.chadate.easybars.config.HudBarConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HealthBarEventHandler {
    
    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        // 检查模组是否启用
        if (!HudBarConfig.ENABLE_MOD.get()) {
            return;
        }

        // 获取玩家
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;

        // 获取屏幕尺寸
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        // 计算血条位置（与原版血条位置对齐）
        int x = screenWidth / 2 - 91; // 原版血条宽度的一半
        int y = screenHeight - 40; // 向上移动5像素

        // 取消原版血条和护甲条的渲染
        if (event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type() ||
            event.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type() ||
            event.getOverlay() == VanillaGuiOverlay.FOOD_LEVEL.type()) {
            event.setCanceled(true);
        }

        // 渲染自定义血条
        HealthBarRenderer.renderHealthBar(event.getGuiGraphics().pose(), x, y, player);
    }
} 