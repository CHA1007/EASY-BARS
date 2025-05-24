package com.chadate.easybars.event;

import com.chadate.easybars.config.HudBarConfig;
import com.chadate.easybars.util.TextRenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class HealthBarRenderer {
    private static final int INVENTORY_WIDTH = 182; // 物品栏宽度
    private static final int BAR_HEIGHT = 5;
    private static final int ARMOR_BAR_HEIGHT = 5; // 盔甲条高度
    private static final int BAR_SPACING = 4; // 血条和盔甲条之间的间距
    private static final int HORIZONTAL_SPACING = 8; // 水平间距
    private static final int BACKGROUND_COLOR = 0x4F000000; // 半透明黑色
    private static final int BORDER_COLOR = 0xFF404040; // 深灰色边框
    private static final int HEALTH_COLOR_HIGH = 0xFF00FF7F; // 翠绿色（高血量）
    private static final int HEALTH_COLOR_MEDIUM = 0xFFFFD700; // 金色（中等血量）
    private static final int HEALTH_COLOR_LOW = 0xFFFF4500; // 橙红色（低血量）
    private static final int ARMOR_COLOR = 0xFF4169E1; // 皇家蓝（盔甲）
    private static final int ARMOR_TOUGHNESS_COLOR = 0xFF00BFFF; // 深天蓝（盔甲韧性）
    private static final int HUNGER_COLOR = 0xFFFFA500; // 橙色（饥饿值）
    private static final int SHADOW_COLOR = 0x40000000; // 半透明黑色阴影
    private static final int TEXT_PADDING = 0; // 文本与血条之间的最小间距
    private static final float TEXT_SCALE = 0.75f; // 文本缩放比例（3/4）

    public static void renderHealthBar(PoseStack poseStack, int x, int y, Player player) {
        if (!HudBarConfig.SHOW_HEALTH_BAR.get() && !HudBarConfig.SHOW_ARMOR_BAR.get() && !HudBarConfig.SHOW_HUNGER_BAR.get()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        GuiGraphics guiGraphics = new GuiGraphics(minecraft, minecraft.renderBuffers().bufferSource());
        
        // 获取当前血量和最大血量
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float armor = player.getArmorValue();
        float maxArmor = 20.0f; // 最大盔甲值
        float armorToughness = (float)player.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        float hunger = player.getFoodData().getFoodLevel();
        float maxHunger = 20.0f; // 最大饥饿值
        
        // 计算填充比例
        float healthPercentage = health / maxHealth;
        float armorPercentage = armor / maxArmor;
        float hungerPercentage = hunger / maxHunger;
        
        // 根据血量百分比选择颜色
        int healthColor = getHealthColor(healthPercentage);
        
        // 计算状态条宽度
        int totalBars = 0;
        if (HudBarConfig.SHOW_HEALTH_BAR.get()) totalBars++;
        if (HudBarConfig.SHOW_HUNGER_BAR.get()) totalBars++;
        
        int barWidth = (INVENTORY_WIDTH - (HORIZONTAL_SPACING * (totalBars - 1))) / totalBars;
        
        // 应用X轴偏移
        x += HudBarConfig.HEALTH_BAR_X_OFFSET.get();
        
        // 渲染盔甲条和文本
        renderArmorBar(poseStack, guiGraphics, x, y, armor, armorToughness, barWidth, minecraft);
        
        // 渲染血条和文本
        renderHealthBar(poseStack, guiGraphics, x, y, health, maxHealth, healthPercentage, healthColor, barWidth, minecraft);
        
        // 渲染饥饿条和文本
        if (HudBarConfig.SHOW_HUNGER_BAR.get()) {
            int hungerX = x + barWidth + HORIZONTAL_SPACING + HudBarConfig.HUNGER_BAR_X_OFFSET.get();
            renderHungerBar(poseStack, guiGraphics, hungerX, y, hunger, maxHunger, hungerPercentage, barWidth, minecraft);
        }
    }

    private static void renderArmorBar(PoseStack poseStack, GuiGraphics guiGraphics, int x, int y, float armor, float armorToughness, int barWidth, Minecraft minecraft) {
        if (!HudBarConfig.SHOW_ARMOR_BAR.get()) return;
        
        // 如果盔甲值和韧性都为0，则不渲染
        if (armor <= 0 && armorToughness <= 0) return;
        
        int armorY = (int)(y - ARMOR_BAR_HEIGHT - BAR_SPACING + HudBarConfig.ARMOR_BAR_Y_OFFSET.get() - 0.5f);
        
        // 绘制盔甲条
        float armorPercentage = armor / 20.0f; // 假设最大盔甲值为20
        float toughnessPercentage = armorToughness / 20.0f; // 假设最大韧性值为20
        
        // 绘制背景和边框
        guiGraphics.fill(x - 1, armorY - 1, x + barWidth + 1, armorY + ARMOR_BAR_HEIGHT + 1, BORDER_COLOR);
        guiGraphics.fill(x, armorY, x + barWidth, armorY + ARMOR_BAR_HEIGHT, BACKGROUND_COLOR);
        
        // 只有当盔甲值大于0时才绘制盔甲值条
        if (armor > 0) {
            renderArmorBar(guiGraphics, x, armorY, armorPercentage, ARMOR_COLOR, "护甲", barWidth, true);
        }
        
        // 只有当韧性值大于0时才绘制韧性条
        if (armorToughness > 0) {
            renderArmorBar(guiGraphics, x, armorY, toughnessPercentage, ARMOR_TOUGHNESS_COLOR, "韧性", barWidth, true);
        }
        
        if (HudBarConfig.SHOW_ARMOR_TEXT.get()) {
            // 绘制盔甲文本（盔甲值/盔甲韧性格式）
            String armorText = String.format("%.0f/%.0f", armor, armorToughness);
            int textWidth = minecraft.font.width(armorText);
            
            // 计算文本位置，右对齐到盔甲条左边，垂直居中
            int textX = x - TEXT_PADDING - textWidth + 2;
            // 计算文本垂直居中位置，考虑文本缩放
            int textY = armorY + (ARMOR_BAR_HEIGHT - (int)(minecraft.font.lineHeight * TEXT_SCALE)) / 2 + HudBarConfig.ARMOR_TEXT_Y_OFFSET.get() - 1;
            
            // 使用新的文本渲染工具类渲染带缩放的文本
            TextRenderHelper.renderScaledTextWithShadow(
                poseStack,
                guiGraphics,
                armorText,
                textX,
                textY,
                ARMOR_COLOR,
                SHADOW_COLOR,
                TEXT_SCALE
            );
        }
    }

    private static void renderArmorBar(GuiGraphics guiGraphics, int x, int y, float percentage, int color, String label, int width, boolean fillFromLeft) {
        // 绘制填充（带渐变效果）
        int fillWidth = (int) (width * percentage);
        if (fillFromLeft) {
            for (int i = 0; i < fillWidth; i++) {
                float segmentPercentage = (float) i / fillWidth;
                int segmentColor = interpolateColor(color, 0xFFFFFFFF, segmentPercentage * 0.3f);
                guiGraphics.fill(x + i, y, x + i + 1, y + ARMOR_BAR_HEIGHT, segmentColor);
            }
        } else {
            for (int i = 0; i < fillWidth; i++) {
                float segmentPercentage = (float) i / fillWidth;
                int segmentColor = interpolateColor(color, 0xFFFFFFFF, segmentPercentage * 0.3f);
                guiGraphics.fill(x + width - fillWidth + i, y, x + width - fillWidth + i + 1, y + ARMOR_BAR_HEIGHT, segmentColor);
            }
        }
    }

    private static void renderHealthBar(PoseStack poseStack, GuiGraphics guiGraphics, int x, int y, float health, float maxHealth, float healthPercentage, int healthColor, int barWidth, Minecraft minecraft) {
        if (!HudBarConfig.SHOW_HEALTH_BAR.get()) return;
        
        int healthY = y + HudBarConfig.HEALTH_BAR_Y_OFFSET.get();
        
        // 绘制血条
        renderBar(guiGraphics, x, healthY, healthPercentage, healthColor, "生命", barWidth, true);
        
        if (HudBarConfig.SHOW_HEALTH_TEXT.get()) {
            // 根据配置选择显示格式
            String healthText;
            if (HudBarConfig.SHOW_HEALTH_AS_PERCENTAGE.get()) {
                healthText = String.format("%.0f%%", healthPercentage * 100);
            } else {
                healthText = String.format("%.0f/%.0f", health, maxHealth);
            }
            int textWidth = minecraft.font.width(healthText);
            
            // 计算文本位置，右对齐到血条左边，垂直居中
            int textX = x - TEXT_PADDING - textWidth + 2;
            int textY = healthY + (BAR_HEIGHT - minecraft.font.lineHeight) / 2 + HudBarConfig.HEALTH_TEXT_Y_OFFSET.get();
            
            // 使用新的文本渲染工具类渲染带缩放的文本
            TextRenderHelper.renderScaledTextWithShadow(
                poseStack,
                guiGraphics,
                healthText,
                textX,
                textY,
                healthColor,
                SHADOW_COLOR,
                TEXT_SCALE
            );
        }
    }

    private static void renderHungerBar(PoseStack poseStack, GuiGraphics guiGraphics, int x, int y, float hunger, float maxHunger, float hungerPercentage, int barWidth, Minecraft minecraft) {
        if (!HudBarConfig.SHOW_HUNGER_BAR.get()) return;
        
        int hungerY = y + HudBarConfig.HUNGER_BAR_Y_OFFSET.get();
        
        // 绘制饥饿条
        renderBar(guiGraphics, x, hungerY, hungerPercentage, HUNGER_COLOR, "饥饿", barWidth, false);
        
        if (HudBarConfig.SHOW_HUNGER_TEXT.get()) {
            // 根据配置选择显示格式
            String hungerText;
            if (HudBarConfig.SHOW_HUNGER_AS_PERCENTAGE.get()) {
                hungerText = String.format("%.0f%%", hungerPercentage * 100);
            } else {
                hungerText = String.format("%.0f/%.0f", hunger, maxHunger);
            }
            int textWidth = minecraft.font.width(hungerText);
            
            // 计算文本位置，左对齐到饥饿条右边，垂直居中
            int textX = x + barWidth + TEXT_PADDING + 2;
            int textY = hungerY + (BAR_HEIGHT - minecraft.font.lineHeight) / 2 + HudBarConfig.HUNGER_TEXT_Y_OFFSET.get();
            
            // 使用新的文本渲染工具类渲染带缩放的文本
            TextRenderHelper.renderScaledTextWithShadow(
                poseStack,
                guiGraphics,
                hungerText,
                textX,
                textY,
                HUNGER_COLOR,
                SHADOW_COLOR,
                TEXT_SCALE
            );
        }
    }

    private static void renderBar(GuiGraphics guiGraphics, int x, int y, float percentage, int color, String label, int width, boolean fillFromLeft) {
        // 绘制边框
        guiGraphics.fill(x - 1, y - 1, x + width + 1, y + BAR_HEIGHT + 1, BORDER_COLOR);
        
        // 绘制背景
        guiGraphics.fill(x, y, x + width, y + BAR_HEIGHT, BACKGROUND_COLOR);
        
        // 绘制填充（带渐变效果）
        int fillWidth = (int) (width * percentage);
        if (fillFromLeft) {
            for (int i = 0; i < fillWidth; i++) {
                float segmentPercentage = (float) i / fillWidth;
                int segmentColor = interpolateColor(color, 0xFFFFFFFF, segmentPercentage * 0.3f);
                guiGraphics.fill(x + i, y, x + i + 1, y + BAR_HEIGHT, segmentColor);
            }
        } else {
            for (int i = 0; i < fillWidth; i++) {
                float segmentPercentage = (float) i / fillWidth;
                int segmentColor = interpolateColor(color, 0xFFFFFFFF, segmentPercentage * 0.3f);
                guiGraphics.fill(x + width - fillWidth + i, y, x + width - fillWidth + i + 1, y + BAR_HEIGHT, segmentColor);
            }
        }
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