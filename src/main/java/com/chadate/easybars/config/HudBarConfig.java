package com.chadate.easybars.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class HudBarConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // 血条显示设置
    public static final ForgeConfigSpec.BooleanValue SHOW_HEALTH_BAR;
    public static final ForgeConfigSpec.BooleanValue SHOW_ARMOR_BAR;
    public static final ForgeConfigSpec.BooleanValue SHOW_HEALTH_TEXT;
    public static final ForgeConfigSpec.BooleanValue SHOW_ARMOR_TEXT;
    public static final ForgeConfigSpec.BooleanValue ENABLE_MOD;

    // 血条位置设置
    public static final ForgeConfigSpec.IntValue HEALTH_BAR_X_OFFSET;
    public static final ForgeConfigSpec.IntValue HEALTH_BAR_Y_OFFSET;
    public static final ForgeConfigSpec.IntValue ARMOR_BAR_X_OFFSET;
    public static final ForgeConfigSpec.IntValue ARMOR_BAR_Y_OFFSET;

    // 文本位置设置
    public static final ForgeConfigSpec.IntValue HEALTH_TEXT_X_OFFSET;
    public static final ForgeConfigSpec.IntValue HEALTH_TEXT_Y_OFFSET;
    public static final ForgeConfigSpec.IntValue ARMOR_TEXT_X_OFFSET;
    public static final ForgeConfigSpec.IntValue ARMOR_TEXT_Y_OFFSET;

    // 文本缩放设置
    public static final ForgeConfigSpec.DoubleValue HEALTH_TEXT_SCALE;
    public static final ForgeConfigSpec.DoubleValue ARMOR_TEXT_SCALE;

    // 饥饿条显示配置
    public static final ForgeConfigSpec.BooleanValue SHOW_HUNGER_BAR;
    public static final ForgeConfigSpec.BooleanValue SHOW_HUNGER_TEXT;
    public static final ForgeConfigSpec.IntValue HUNGER_BAR_X_OFFSET;
    public static final ForgeConfigSpec.IntValue HUNGER_BAR_Y_OFFSET;
    public static final ForgeConfigSpec.IntValue HUNGER_TEXT_X_OFFSET;
    public static final ForgeConfigSpec.IntValue HUNGER_TEXT_Y_OFFSET;
    public static final ForgeConfigSpec.DoubleValue HUNGER_TEXT_SCALE;

    // 文本显示格式设置
    public static final ForgeConfigSpec.BooleanValue SHOW_HEALTH_AS_PERCENTAGE;
    public static final ForgeConfigSpec.BooleanValue SHOW_HUNGER_AS_PERCENTAGE;

    // 氧气条显示配置
    public static final ForgeConfigSpec.BooleanValue SHOW_AIR_BAR;
    public static final ForgeConfigSpec.BooleanValue SHOW_AIR_TEXT;
    public static final ForgeConfigSpec.IntValue AIR_BAR_X_OFFSET;
    public static final ForgeConfigSpec.IntValue AIR_BAR_Y_OFFSET;
    public static final ForgeConfigSpec.IntValue AIR_TEXT_X_OFFSET;
    public static final ForgeConfigSpec.IntValue AIR_TEXT_Y_OFFSET;
    public static final ForgeConfigSpec.DoubleValue AIR_TEXT_SCALE;
    public static final ForgeConfigSpec.BooleanValue SHOW_AIR_AS_PERCENTAGE;

    static {
        BUILDER.push("HUD Bar Configuration");

        // 显示设置
        BUILDER.push("Display Settings");
        ENABLE_MOD = BUILDER
                .comment("是否启用模组")
                .define("enableMod", true);
        SHOW_HEALTH_BAR = BUILDER
                .comment("是否显示血条")
                .define("showHealthBar", true);
        SHOW_ARMOR_BAR = BUILDER
                .comment("是否显示护甲条")
                .define("showArmorBar", true);
        SHOW_HEALTH_TEXT = BUILDER
                .comment("是否显示血量文本")
                .define("showHealthText", true);
        SHOW_ARMOR_TEXT = BUILDER
                .comment("是否显示护甲文本")
                .define("showArmorText", true);
        SHOW_HEALTH_AS_PERCENTAGE = BUILDER
                .comment("是否以百分比形式显示血量")
                .define("showHealthAsPercentage", true);
        SHOW_HUNGER_AS_PERCENTAGE = BUILDER
                .comment("是否以百分比形式显示饥饿值")
                .define("showHungerAsPercentage", true);
        BUILDER.pop();

        // 位置设置
        BUILDER.push("Position Settings");
        HEALTH_BAR_X_OFFSET = BUILDER
                .comment("血条X轴偏移量")
                .defineInRange("healthBarXOffset", 0, -1000, 1000);
        HEALTH_BAR_Y_OFFSET = BUILDER
                .comment("血条Y轴偏移量")
                .defineInRange("healthBarYOffset", 0, -1000, 1000);
        ARMOR_BAR_X_OFFSET = BUILDER
                .comment("护甲条X轴偏移量")
                .defineInRange("armorBarXOffset", 0, -1000, 1000);
        ARMOR_BAR_Y_OFFSET = BUILDER
                .comment("护甲条Y轴偏移量")
                .defineInRange("armorBarYOffset", 0, -1000, 1000);
        BUILDER.pop();

        // 文本位置设置
        BUILDER.push("Text Position Settings");
        HEALTH_TEXT_X_OFFSET = BUILDER
                .comment("血量文本X轴偏移量")
                .defineInRange("healthTextXOffset", 0, -1000, 1000);
        HEALTH_TEXT_Y_OFFSET = BUILDER
                .comment("血量文本Y轴偏移量")
                .defineInRange("healthTextYOffset", 0, -1000, 1000);
        ARMOR_TEXT_X_OFFSET = BUILDER
                .comment("护甲文本X轴偏移量")
                .defineInRange("armorTextXOffset", 0, -1000, 1000);
        ARMOR_TEXT_Y_OFFSET = BUILDER
                .comment("护甲文本Y轴偏移量")
                .defineInRange("armorTextYOffset", 0, -1000, 1000);
        BUILDER.pop();

        // 文本缩放设置
        HEALTH_TEXT_SCALE = BUILDER
                .comment("血量文本缩放比例")
                .defineInRange("healthTextScale", 1.0, 0.5, 2.0);
        ARMOR_TEXT_SCALE = BUILDER
                .comment("护甲文本缩放比例")
                .defineInRange("armorTextScale", 1.0, 0.5, 2.0);

        // 饥饿条配置
        BUILDER.push("Hunger Bar Settings");
        SHOW_HUNGER_BAR = BUILDER
            .comment("是否显示饥饿条")
            .define("showHungerBar", true);
        SHOW_HUNGER_TEXT = BUILDER
            .comment("是否显示饥饿值文本")
            .define("showHungerText", true);
        HUNGER_BAR_X_OFFSET = BUILDER
            .comment("饥饿条X轴偏移")
            .defineInRange("hungerBarXOffset", 0, -100, 100);
        HUNGER_BAR_Y_OFFSET = BUILDER
            .comment("饥饿条Y轴偏移")
            .defineInRange("hungerBarYOffset", 0, -100, 100);
        HUNGER_TEXT_X_OFFSET = BUILDER
            .comment("饥饿值文本X轴偏移")
            .defineInRange("hungerTextXOffset", 0, -100, 100);
        HUNGER_TEXT_Y_OFFSET = BUILDER
            .comment("饥饿值文本Y轴偏移")
            .defineInRange("hungerTextYOffset", 0, -100, 100);
        HUNGER_TEXT_SCALE = BUILDER
            .comment("饥饿值文本缩放比例")
            .defineInRange("hungerTextScale", 0.75, 0.5, 2.0);
        BUILDER.pop();

        // 氧气条配置
        BUILDER.push("Air Bar Settings");
        SHOW_AIR_BAR = BUILDER
            .comment("是否显示氧气条")
            .define("showAirBar", true);
        SHOW_AIR_TEXT = BUILDER
            .comment("是否显示氧气值文本")
            .define("showAirText", true);
        AIR_BAR_X_OFFSET = BUILDER
            .comment("氧气条X轴偏移")
            .defineInRange("airBarXOffset", 0, -100, 100);
        AIR_BAR_Y_OFFSET = BUILDER
            .comment("氧气条Y轴偏移")
            .defineInRange("airBarYOffset", 0, -100, 100);
        AIR_TEXT_X_OFFSET = BUILDER
            .comment("氧气值文本X轴偏移")
            .defineInRange("airTextXOffset", 0, -100, 100);
        AIR_TEXT_Y_OFFSET = BUILDER
            .comment("氧气值文本Y轴偏移")
            .defineInRange("airTextYOffset", 0, -100, 100);
        AIR_TEXT_SCALE = BUILDER
            .comment("氧气值文本缩放比例")
            .defineInRange("airTextScale", 0.75, 0.5, 2.0);
        SHOW_AIR_AS_PERCENTAGE = BUILDER
            .comment("是否以百分比形式显示氧气值")
            .define("showAirAsPercentage", true);
        BUILDER.pop();

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "hudbar-config.toml");
    }
} 