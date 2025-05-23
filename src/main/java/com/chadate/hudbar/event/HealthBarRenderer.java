package com.chadate.hudbar.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

public class HealthBarRenderer {
    private static final int BAR_WIDTH = 91; // 原版血条宽度的一半
    private static final int BAR_HEIGHT = 5;
    private static final int BAR_SPACING = 2; // 血条和盔甲条之间的间距
    private static final int BACKGROUND_COLOR = 0x4F000000; // 半透明黑色
    private static final int BORDER_COLOR = 0xFF404040; // 深灰色边框
    private static final int HEALTH_COLOR_HIGH = 0xFF00FF7F; // 翠绿色（高血量）
    private static final int HEALTH_COLOR_MEDIUM = 0xFFFFD700; // 金色（中等血量）
    private static final int HEALTH_COLOR_LOW = 0xFFFF4500; // 橙红色（低血量）
    private static final int ARMOR_COLOR = 0xFF4169E1; // 皇家蓝（盔甲）
    private static final int SHADOW_COLOR = 0x40000000; // 半透明黑色阴影
    private static final int HIGHLIGHT_COLOR = 0x33FFFFFF; // 高光效果

    public static void renderHealthBar(PoseStack poseStack, int x, int y, Player player) {
        Minecraft minecraft = Minecraft.getInstance();
        GuiGraphics guiGraphics = new GuiGraphics(minecraft, minecraft.renderBuffers().bufferSource());
        
        // 获取当前血量和最大血量
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float armor = player.getArmorValue();
        float maxArmor = 20.0f; // 最大盔甲值
        
        // 计算填充比例
        float healthPercentage = health / maxHealth;
        float armorPercentage = armor / maxArmor;
        
        // 根据血量百分比选择颜色
        int healthColor = getHealthColor(healthPercentage);
        
        // 只在有盔甲值或玩家在创造模式下显示盔甲条
        if (armor > 0 || player.getAbilities().invulnerable) {
            // 绘制盔甲文本（百分比格式）
            String armorText = String.format("%.0f%%", armorPercentage * 100);
            // 绘制阴影
            guiGraphics.drawString(minecraft.font, armorText, x - 19, y - BAR_HEIGHT - BAR_SPACING - 2, SHADOW_COLOR, false);
            // 绘制文本
            guiGraphics.drawString(minecraft.font, armorText, x - 20, y - BAR_HEIGHT - BAR_SPACING -1, ARMOR_COLOR, false);
            
            renderBar(guiGraphics, x, y - BAR_HEIGHT - BAR_SPACING, armorPercentage, ARMOR_COLOR, "护甲");
        }
        
        // 绘制血量文本（百分比格式）
        String healthText = String.format("%.0f%%", healthPercentage * 100);
        // 绘制阴影
        guiGraphics.drawString(minecraft.font, healthText, x - 19, y + 1, SHADOW_COLOR, false);
        // 绘制文本
        guiGraphics.drawString(minecraft.font, healthText, x - 20, y, healthColor, false);
        
        // 绘制血条
        renderBar(guiGraphics, x, y + 2, healthPercentage, healthColor, "生命");
    }

    private static void renderBar(GuiGraphics guiGraphics, int x, int y, float percentage, int color, String label) {
        // 绘制边框
        guiGraphics.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, BORDER_COLOR);
        
        // 绘制背景
        guiGraphics.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, BACKGROUND_COLOR);
        
        // 绘制填充（带渐变效果）
        int fillWidth = (int) (BAR_WIDTH * percentage);
        for (int i = 0; i < fillWidth; i++) {
            float segmentPercentage = (float) i / fillWidth;
            int segmentColor = interpolateColor(color, 0xFFFFFFFF, segmentPercentage * 0.3f);
            guiGraphics.fill(x + i, y, x + i + 1, y + BAR_HEIGHT, segmentColor);
        }
        
        // 添加高光效果
        guiGraphics.fill(x, y, x + BAR_WIDTH, y + 1, HIGHLIGHT_COLOR);
    }

    private static int getHealthColor(float percentage) {
        if (percentage > 0.6f) {
            return HEALTH_COLOR_HIGH;
        } else if (percentage > 0.3f) {
            return HEALTH_COLOR_MEDIUM;
        } else {
            return HEALTH_COLOR_LOW;
        }
    }

    private static int interpolateColor(int color1, int color2, float factor) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        
        int r = (int) (r1 + (r2 - r1) * factor);
        int g = (int) (g1 + (g2 - g1) * factor);
        int b = (int) (b1 + (b2 - b1) * factor);
        
        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }
} 