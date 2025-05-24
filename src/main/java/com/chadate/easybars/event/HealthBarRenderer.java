package com.chadate.easybars.event;

import com.chadate.easybars.config.HudBarConfig;
import com.chadate.easybars.util.TextRenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class HealthBarRenderer {
    // 缓存Minecraft实例
    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    private static final Font FONT = MINECRAFT.font;
    private static final MultiBufferSource.BufferSource BUFFER = MINECRAFT.renderBuffers().bufferSource();
    
    // 动画相关变量
    private static float currentOffset = 0f;
    private static float targetOffset = 0f;
    private static final float ANIMATION_SPEED = 0.05f; // 降低动画速度
    private static final float CREATIVE_OFFSET = 8f; // 创造模式的目标偏移量
    
    // 缓存配置值
    private static boolean showHealthBar = true;
    private static boolean showArmorBar = true;
    private static boolean showHungerBar = true;
    private static boolean showHealthText = true;
    private static boolean showArmorText = true;
    private static boolean showHungerText = true;
    private static boolean showHealthAsPercentage = true;
    private static boolean showHungerAsPercentage = true;
    
    // 缓存常用字符串
    private static final String PERCENT_SYMBOL = "%";
    private static final String SLASH = "/";
    private static final String HUNGER_LABEL = "饥饿";
    private static final String ARMOR_LABEL = "护甲";
    private static final String TOUGHNESS_LABEL = "韧性";
    private static final String HEALTH_LABEL = "生命";
    private static final StringBuilder TEXT_BUILDER = new StringBuilder(16);
    
    // 常量定义
    private static final int INVENTORY_WIDTH = 180;
    private static final int BAR_HEIGHT = 5;
    private static final int ARMOR_BAR_HEIGHT = 5;
    private static final int BAR_SPACING = 4;
    private static final int HORIZONTAL_SPACING = 8;
    private static final int BACKGROUND_COLOR = 0x4F000000;
    private static final int BORDER_COLOR = 0xFF404040;
    private static final int HEALTH_COLOR_HIGH = 0xFF4CAF50; // 草绿色
    private static final int HEALTH_COLOR_MEDIUM = 0xFFFFA500; // 橙色
    private static final int HEALTH_COLOR_LOW = 0xFFFF0000; // 红色
    private static final int POISON_COLOR = 0xFF006400; // 深绿色
    private static final int WITHER_COLOR = 0xFF404040; // 深灰色
    private static final int ARMOR_COLOR = 0xFFFFFFFF; // 白色
    private static final int ARMOR_TOUGHNESS_COLOR = 0xFF808080; // 更深的灰色
    private static final int HUNGER_COLOR_HIGH = 0xFFFFD700;
    private static final int HUNGER_COLOR_MEDIUM = 0xFFFFA500;
    private static final int HUNGER_COLOR_LOW = 0xFFFF4500;
    private static final int SHADOW_COLOR = 0x40000000;
    private static final int TEXT_PADDING = 2;
    private static final float TEXT_SCALE = 0.75f;
    
    // 预计算颜色数组
    private static final int[] HEALTH_COLORS = new int[101];
    private static final int[] POISON_COLORS = new int[101]; // 中毒状态的颜色数组
    private static final int[] WITHER_COLORS = new int[101]; // 凋零状态的颜色数组
    private static final int[] ARMOR_COLORS = new int[101];
    private static final int[] HUNGER_COLORS = new int[101];
    
    // 缓存常用文本宽度
    private static final int[] PERCENT_TEXT_WIDTHS = new int[101];
    private static final int[] VALUE_TEXT_WIDTHS = new int[21]; // 0-20的值
    
    // 氧气条相关常量
    private static final int AIR_BAR_WIDTH = 5; // 氧气条宽度
    private static final int AIR_BAR_HEIGHT = 20; // 降低氧气条高度
    private static final int AIR_BAR_SPACING = 4; // 与快捷栏的间距
    
    // 氧气条颜色
    private static final int AIR_COLOR_HIGH = 0xFF00FFFF; // 浅蓝色（高氧气值）
    private static final int AIR_COLOR_MEDIUM = 0xFF00BFFF; // 深天蓝色（中等氧气值）
    private static final int AIR_COLOR_LOW = 0xFF0080FF; // 天蓝色（低氧气值）
    
    // 危险状态动画相关变量
    private static float dangerAnimationTime = 0f;
    private static final float DANGER_ANIMATION_SPEED = 0.002f; // 进一步降低动画速度
    private static final int DANGER_COLOR = 0xFFFF0000; // 鲜红色
    private static final int DANGER_GLOW_COLOR = 0x40FF0000; // 半透明红色
    private static final float DANGER_THRESHOLD = 0.2f; // 20%血量触发
    
    static {
        // 预计算颜色
        for (int i = 0; i <= 100; i++) {
            float percentage = i / 100f;
            // 血条颜色使用渐变
            HEALTH_COLORS[i] = getHealthColor(percentage);
            // 中毒状态使用深绿色渐变
            POISON_COLORS[i] = interpolateColor(POISON_COLOR, 0xFF4CAF50, percentage * 0.3f);
            // 凋零状态使用深灰色渐变
            WITHER_COLORS[i] = interpolateColor(WITHER_COLOR, 0xFF4CAF50, percentage * 0.3f);
            // 盔甲条使用白色渐变
            ARMOR_COLORS[i] = interpolateColor(ARMOR_COLOR, 0xFFE0E0E0, percentage * 0.3f);
            // 盔甲韧性使用淡灰色渐变
            HUNGER_COLORS[i] = getHungerColor(percentage);
        }
        
        // 预计算文本宽度
        for (int i = 0; i <= 100; i++) {
            String percentText = i + PERCENT_SYMBOL;
            PERCENT_TEXT_WIDTHS[i] = FONT.width(percentText);
        }
        
        for (int i = 0; i <= 20; i++) {
            String valueText = i + SLASH + "20";
            VALUE_TEXT_WIDTHS[i] = FONT.width(valueText);
        }
    }
    
    @SubscribeEvent
    public static void onConfigChanged(ModConfigEvent event) {
        if (event.getConfig().getSpec() == HudBarConfig.SPEC) {
            showHealthBar = HudBarConfig.SHOW_HEALTH_BAR.get();
            showArmorBar = HudBarConfig.SHOW_ARMOR_BAR.get();
            showHungerBar = HudBarConfig.SHOW_HUNGER_BAR.get();
            showHealthText = HudBarConfig.SHOW_HEALTH_TEXT.get();
            showArmorText = HudBarConfig.SHOW_ARMOR_TEXT.get();
            showHungerText = HudBarConfig.SHOW_HUNGER_TEXT.get();
            showHealthAsPercentage = HudBarConfig.SHOW_HEALTH_AS_PERCENTAGE.get();
            showHungerAsPercentage = HudBarConfig.SHOW_HUNGER_AS_PERCENTAGE.get();
        }
    }
    
    public static void renderHealthBar(PoseStack poseStack, int x, int y, Player player) {
        if (!showHealthBar && !showArmorBar && !showHungerBar) {
            return;
        }

        GuiGraphics guiGraphics = new GuiGraphics(MINECRAFT, BUFFER);
        
        // 获取当前血量和最大血量
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float armor = player.getArmorValue();
        float maxArmor = 20.0f; // 最大盔甲值
        float armorToughness = (float)player.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        float hunger = player.getFoodData().getFoodLevel();
        float maxHunger = 20.0f; // 最大饥饿值
        float air = player.getAirSupply();
        float maxAir = player.getMaxAirSupply();
        
        // 计算填充比例
        float healthPercentage = health / maxHealth;
        float armorPercentage = armor / maxArmor;
        float hungerPercentage = hunger / maxHunger;
        float airPercentage = air / maxAir;
        
        // 根据血量百分比选择颜色
        int healthColor = getHealthColor(healthPercentage);
        
        // 计算状态条宽度（只考虑血量和饥饿值）
        int totalBars = 0;
        if (showHealthBar) totalBars++;
        if (showHungerBar) totalBars++;
        
        int barWidth = (INVENTORY_WIDTH - (HORIZONTAL_SPACING * (totalBars - 1))) / totalBars;
        
        // 应用X轴偏移
        x += HudBarConfig.HEALTH_BAR_X_OFFSET.get();
        
        // 更新动画
        targetOffset = player.getAbilities().instabuild ? CREATIVE_OFFSET : 0f;
        if (currentOffset != targetOffset) {
            float diff = targetOffset - currentOffset;
            currentOffset += diff * ANIMATION_SPEED;
            // 当差值很小时直接设置为目标值，避免无限接近
            if (Math.abs(diff) < 0.01f) {
                currentOffset = targetOffset;
            }
        }
        
        // 应用动画偏移
        y += currentOffset;
        
        // 渲染盔甲条和文本
        renderArmorBar(poseStack, guiGraphics, x, y, armor, armorToughness, barWidth, MINECRAFT);
        
        // 渲染血条和文本
        renderHealthBar(poseStack, guiGraphics, x, y, health, maxHealth, healthPercentage, healthColor, barWidth, MINECRAFT);
        
        // 渲染饥饿条和文本
        if (showHungerBar) {
            int hungerX = x + barWidth + HORIZONTAL_SPACING + HudBarConfig.HUNGER_BAR_X_OFFSET.get();
            renderHungerBar(poseStack, guiGraphics, hungerX, y, hunger, maxHunger, hungerPercentage, barWidth, MINECRAFT);
        }

        // 渲染氧气条和文本（现在在快捷栏左侧）
        if (player.isInWater()) {
            int airX = x - AIR_BAR_WIDTH - AIR_BAR_SPACING;
            renderAirBar(poseStack, guiGraphics, airX, y, air, maxAir, airPercentage, barWidth, MINECRAFT);
        }
    }

    private static void renderArmorBar(PoseStack poseStack, GuiGraphics guiGraphics, int x, int y, float armor, float armorToughness, int barWidth, Minecraft minecraft) {
        if (!showArmorBar) return;
        
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
            renderBar(guiGraphics, x, armorY, armorPercentage, ARMOR_COLOR, ARMOR_LABEL, barWidth, true, false, false);
        }
        
        // 只有当韧性值大于0时才绘制韧性条，覆盖在盔甲条上
        if (armorToughness > 0) {
            // 计算韧性条的宽度，确保不超过盔甲条
            int toughnessWidth = (int)(barWidth * Math.min(toughnessPercentage, armorPercentage));
            // 绘制韧性条，使用更高的不透明度
            int toughnessColor = (ARMOR_TOUGHNESS_COLOR & 0xFFFFFF) | 0xB3000000; // 70%不透明度
            guiGraphics.fill(x, armorY, x + toughnessWidth, armorY + ARMOR_BAR_HEIGHT, toughnessColor);
        }
        
        if (showArmorText) {
            // 绘制盔甲文本（盔甲值/盔甲韧性格式）
            String armorText = String.format("%.0f/%.0f", armor, armorToughness);
            int textWidth = FONT.width(armorText);
            
            // 计算文本位置，右对齐到盔甲条左边，垂直居中
            int textX = x - TEXT_PADDING - textWidth + 2;
            // 计算文本垂直居中位置，考虑文本缩放
            int textY = armorY + (ARMOR_BAR_HEIGHT - (int)(FONT.lineHeight * TEXT_SCALE)) / 2 + HudBarConfig.ARMOR_TEXT_Y_OFFSET.get() - 1;
            
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

    private static void renderHealthBar(PoseStack poseStack, GuiGraphics guiGraphics, int x, int y, float health, float maxHealth, float healthPercentage, int healthColor, int barWidth, Minecraft minecraft) {
        if (!showHealthBar) return;
        
        int healthY = y + HudBarConfig.HEALTH_BAR_Y_OFFSET.get();
        
        // 检查玩家是否中毒或凋零
        boolean isPoisoned = minecraft.player != null && minecraft.player.hasEffect(net.minecraft.world.effect.MobEffects.POISON);
        boolean isWithered = minecraft.player != null && minecraft.player.hasEffect(net.minecraft.world.effect.MobEffects.WITHER);
        
        // 绘制血条
        renderBar(guiGraphics, x, healthY, healthPercentage, healthColor, "生命", barWidth, true, isPoisoned, isWithered);
        
        if (showHealthText) {
            // 根据配置选择显示格式
            String healthText;
            if (showHealthAsPercentage) {
                healthText = String.format("%.0f%%", healthPercentage * 100);
            } else {
                healthText = String.format("%.0f/%.0f", health, maxHealth);
            }
            int textWidth = FONT.width(healthText);
            
            // 计算文本位置，右对齐到血条左边，垂直居中
            int textX = x - TEXT_PADDING - textWidth + 2;
            int textY = healthY + (BAR_HEIGHT - FONT.lineHeight) / 2 + HudBarConfig.HEALTH_TEXT_Y_OFFSET.get();
            
            // 选择文本颜色
            int textColor;
            if (isWithered) {
                textColor = WITHER_COLOR;
            } else if (isPoisoned) {
                textColor = POISON_COLOR;
            } else {
                textColor = healthColor;
            }
            
            // 使用新的文本渲染工具类渲染带缩放的文本
            TextRenderHelper.renderScaledTextWithShadow(
                poseStack,
                guiGraphics,
                healthText,
                textX,
                textY,
                textColor,
                SHADOW_COLOR,
                TEXT_SCALE
            );
        }
    }

    private static void renderHungerBar(PoseStack poseStack, GuiGraphics guiGraphics, int x, int y, float hunger, float maxHunger, float hungerPercentage, int barWidth, Minecraft minecraft) {
        if (!showHungerBar) return;
        
        int hungerY = y + HudBarConfig.HUNGER_BAR_Y_OFFSET.get();
        
        // 绘制饥饿条
        renderBar(guiGraphics, x, hungerY, hungerPercentage, getHungerColor(hungerPercentage), HUNGER_LABEL, barWidth, false, false, false);
        
        if (showHungerText) {
            // 根据配置选择显示格式
            String hungerText;
            if (showHungerAsPercentage) {
                hungerText = String.format("%.0f%%", hungerPercentage * 100);
            } else {
                hungerText = String.format("%.0f/%.0f", hunger, maxHunger);
            }
            int textWidth = FONT.width(hungerText);
            
            // 计算文本位置，左对齐到饥饿条右边，垂直居中
            int textX = x + barWidth + TEXT_PADDING + 2;
            int textY = hungerY + (BAR_HEIGHT - FONT.lineHeight) / 2 + HudBarConfig.HUNGER_TEXT_Y_OFFSET.get();
            
            // 使用新的文本渲染工具类渲染带缩放的文本
            TextRenderHelper.renderScaledTextWithShadow(
                poseStack,
                guiGraphics,
                hungerText,
                textX,
                textY,
                getHungerColor(hungerPercentage),
                SHADOW_COLOR,
                TEXT_SCALE
            );
        }
    }

    private static void renderBar(GuiGraphics guiGraphics, int x, int y, float percentage, int color, String label, int width, boolean fillFromLeft, boolean isPoisoned, boolean isWithered) {
        // 一次性绘制背景和边框
        guiGraphics.fill(x - 1, y - 1, x + width + 1, y + BAR_HEIGHT + 1, BORDER_COLOR);
        guiGraphics.fill(x, y, x + width, y + BAR_HEIGHT, BACKGROUND_COLOR);
        
        // 使用预计算的颜色
        int fillWidth = (int) (width * percentage);
        int colorIndex = (int) (percentage * 100);
        
        // 根据标签选择正确的颜色
        int segmentColor;
        if (label.equals(HUNGER_LABEL)) {
            segmentColor = HUNGER_COLORS[colorIndex];
        } else if (label.equals(ARMOR_LABEL)) {
            segmentColor = ARMOR_COLORS[colorIndex];
        } else if (label.equals(TOUGHNESS_LABEL)) {
            segmentColor = ARMOR_TOUGHNESS_COLOR;
        } else if (label.equals(HEALTH_LABEL)) {
            if (isWithered) {
                segmentColor = WITHER_COLORS[colorIndex];
            } else if (isPoisoned) {
                segmentColor = POISON_COLORS[colorIndex];
            } else {
                segmentColor = HEALTH_COLORS[colorIndex];
            }
        } else {
            segmentColor = HEALTH_COLORS[colorIndex];
        }
        
        // 一次性绘制填充
        if (fillFromLeft) {
            guiGraphics.fill(x, y, x + fillWidth, y + BAR_HEIGHT, segmentColor);
            // 在血量条上渲染危险效果
            if (label.equals(HEALTH_LABEL)) {
                renderDangerEffect(guiGraphics, x, y, width, percentage);
            }
        } else {
            guiGraphics.fill(x + width - fillWidth, y, x + width, y + BAR_HEIGHT, segmentColor);
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

    private static int getHungerColor(float percentage) {
        if (percentage > 0.6f) {
            return HUNGER_COLOR_HIGH;
        } else if (percentage > 0.3f) {
            return HUNGER_COLOR_MEDIUM;
        } else {
            return HUNGER_COLOR_LOW;
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

    private static String formatPercentage(float value) {
        TEXT_BUILDER.setLength(0);
        TEXT_BUILDER.append((int)(value * 100)).append(PERCENT_SYMBOL);
        return TEXT_BUILDER.toString();
    }
    
    private static String formatValue(float value, float maxValue) {
        TEXT_BUILDER.setLength(0);
        TEXT_BUILDER.append((int)value).append(SLASH).append((int)maxValue);
        return TEXT_BUILDER.toString();
    }

    private static int getTextWidth(String text, boolean isPercentage) {
        if (isPercentage) {
            int value = (int)(Float.parseFloat(text) * 100);
            return PERCENT_TEXT_WIDTHS[value];
        } else {
            int value = (int)Float.parseFloat(text.split(SLASH)[0]);
            return VALUE_TEXT_WIDTHS[value];
        }
    }

    private static void renderAirBar(PoseStack poseStack, GuiGraphics guiGraphics, int x, int y, float air, float maxAir, float airPercentage, int barWidth, Minecraft minecraft) {
        if (!HudBarConfig.SHOW_AIR_BAR.get()) {
            return;
        }

        // 如果氧气值等于最大值，不渲染氧气条
        if (air >= maxAir) {
            return;
        }

        // 计算氧气条位置（在快捷栏左侧）
        int airX = minecraft.getWindow().getGuiScaledWidth() / 2 - INVENTORY_WIDTH / 2 - AIR_BAR_WIDTH - AIR_BAR_SPACING; // 快捷栏左侧
        int airY = minecraft.getWindow().getGuiScaledHeight() - AIR_BAR_HEIGHT - 2; // 贴合屏幕底部，留2像素间距

        // 获取氧气条颜色
        int airColor;
        if (airPercentage > 0.6f) {
            airColor = AIR_COLOR_HIGH;
        } else if (airPercentage > 0.3f) {
            airColor = AIR_COLOR_MEDIUM;
        } else {
            airColor = AIR_COLOR_LOW;
        }

        // 绘制背景和边框
        guiGraphics.fill(airX - 1, airY - 1, airX + AIR_BAR_WIDTH + 1, airY + AIR_BAR_HEIGHT + 1, BORDER_COLOR);
        guiGraphics.fill(airX, airY, airX + AIR_BAR_WIDTH, airY + AIR_BAR_HEIGHT, BACKGROUND_COLOR);

        // 计算填充高度（从下往上填充）
        int fillHeight = (int)(AIR_BAR_HEIGHT * airPercentage);
        guiGraphics.fill(airX, airY + AIR_BAR_HEIGHT - fillHeight, airX + AIR_BAR_WIDTH, airY + AIR_BAR_HEIGHT, airColor);

        // 渲染氧气值文本
        if (HudBarConfig.SHOW_AIR_TEXT.get()) {
            String airText = HudBarConfig.SHOW_AIR_AS_PERCENTAGE.get() 
                ? String.format("%.0f%%", airPercentage * 100)
                : String.format("%d/%d", (int)air, (int)maxAir);
            
            int textX = airX - 2;
            int textY = airY + AIR_BAR_HEIGHT / 2;
            float scale = HudBarConfig.AIR_TEXT_SCALE.get().floatValue();
            
            poseStack.pushPose();
            poseStack.scale(scale, scale, 1.0f);
            textX = (int)(textX / scale);
            textY = (int)(textY / scale);
            
            int textWidth = minecraft.font.width(airText);
            guiGraphics.drawString(minecraft.font, airText, textX - textWidth, textY, airColor);
            poseStack.popPose();
        }
    }

    private static void renderDangerEffect(GuiGraphics guiGraphics, int x, int y, int width, float healthPercentage) {
        if (healthPercentage > DANGER_THRESHOLD) return;

        // 更新动画时间
        dangerAnimationTime += DANGER_ANIMATION_SPEED;
        if (dangerAnimationTime > 2 * Math.PI) {
            dangerAnimationTime -= 2 * Math.PI;
        }

        // 计算脉冲效果的不透明度（降低最大不透明度）
        float pulseIntensity = (float)(Math.sin(dangerAnimationTime) * 0.3 + 0.3); // 0.0 到 0.6
        int glowColor = (DANGER_GLOW_COLOR & 0xFFFFFF) | ((int)(pulseIntensity * 0x40) << 24);

        // 绘制发光效果
        int glowSize = 2;
        guiGraphics.fill(x - glowSize, y - glowSize, x + width + glowSize, y + BAR_HEIGHT + glowSize, glowColor);

        // 绘制闪烁边框（降低不透明度）
        if (pulseIntensity > 0.3f) {
            int borderColor = (DANGER_COLOR & 0xFFFFFF) | ((int)(pulseIntensity * 0x80) << 24); // 降低边框不透明度
            guiGraphics.fill(x - 1, y - 1, x + width + 1, y + BAR_HEIGHT + 1, borderColor);
        }
    }

    public static void render(PoseStack poseStack, GuiGraphics guiGraphics, Player player) {
        if (!HudBarConfig.ENABLE_MOD.get()) {
            return;
        }

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        // 计算基础位置
        int baseX = screenWidth / 2 - INVENTORY_WIDTH / 2 + HudBarConfig.HEALTH_BAR_X_OFFSET.get();
        int baseY = screenHeight - 40 + HudBarConfig.HEALTH_BAR_Y_OFFSET.get();

        // 如果是创造模式，应用动画偏移
        if (player.getAbilities().instabuild) {
            targetOffset = CREATIVE_OFFSET;
        } else {
            targetOffset = 0f;
        }
        currentOffset += (targetOffset - currentOffset) * ANIMATION_SPEED;
        baseY += currentOffset;

        // 获取当前血量和最大血量
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float armor = player.getArmorValue();
        float maxArmor = 20.0f; // 最大盔甲值
        float armorToughness = (float)player.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        float hunger = player.getFoodData().getFoodLevel();
        float maxHunger = 20.0f; // 最大饥饿值
        float air = player.getAirSupply();
        float maxAir = player.getMaxAirSupply();
        
        // 计算填充比例
        float healthPercentage = health / maxHealth;
        float armorPercentage = armor / maxArmor;
        float hungerPercentage = hunger / maxHunger;
        float airPercentage = air / maxAir;
        
        // 根据血量百分比选择颜色
        int healthColor = getHealthColor(healthPercentage);
        
        // 计算状态条宽度（只考虑血量和饥饿值）
        int totalBars = 0;
        if (showHealthBar) totalBars++;
        if (showHungerBar) totalBars++;
        
        int barWidth = (INVENTORY_WIDTH - (HORIZONTAL_SPACING * (totalBars - 1))) / totalBars;
        
        // 渲染盔甲条和文本
        renderArmorBar(poseStack, guiGraphics, baseX, baseY, armor, armorToughness, barWidth, MINECRAFT);
        
        // 渲染血条和文本
        renderHealthBar(poseStack, guiGraphics, baseX, baseY, health, maxHealth, healthPercentage, healthColor, barWidth, MINECRAFT);
        
        // 渲染饥饿条和文本
        if (showHungerBar) {
            int hungerX = baseX + barWidth + HORIZONTAL_SPACING + HudBarConfig.HUNGER_BAR_X_OFFSET.get();
            renderHungerBar(poseStack, guiGraphics, hungerX, baseY, hunger, maxHunger, hungerPercentage, barWidth, MINECRAFT);
        }

        // 渲染氧气条和文本（现在在快捷栏左侧）
        if (player.isInWater()) {
            int airX = baseX - AIR_BAR_WIDTH - AIR_BAR_SPACING;
            renderAirBar(poseStack, guiGraphics, airX, baseY, air, maxAir, airPercentage, barWidth, MINECRAFT);
        }
    }
} 