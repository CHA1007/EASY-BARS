package com.chadate.easybars.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;

public class TextRenderHelper {
    
    /**
     * 渲染带缩放的文本
     * @param poseStack 矩阵栈
     * @param guiGraphics GUI图形对象
     * @param text 要渲染的文本
     * @param x X坐标
     * @param y Y坐标
     * @param color 文本颜色
     * @param scale 缩放比例
     */
    public static void renderScaledText(PoseStack poseStack, GuiGraphics guiGraphics, String text, int x, int y, int color, float scale) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();
        
        // 保存当前矩阵状态
        poseStack.pushPose();
        
        // 移动到文本位置
        poseStack.translate(x, y, 0); // 移除垂直微调
        
        // 应用缩放
        poseStack.scale(scale, scale, 1.0f);
        
        // 渲染文本
        font.drawInBatch(
            text,
            0,          // 左对齐
            0,          // 垂直位置
            color,
            false,
            poseStack.last().pose(),
            buffer,
            Font.DisplayMode.NORMAL,
            0,
            15728880
        );
        
        // 刷新缓冲区
        buffer.endBatch();
        
        // 恢复矩阵状态
        poseStack.popPose();
    }
    
    /**
     * 渲染带缩放的文本（带阴影）
     * @param poseStack 矩阵栈
     * @param guiGraphics GUI图形对象
     * @param text 要渲染的文本
     * @param x X坐标
     * @param y Y坐标
     * @param color 文本颜色
     * @param shadowColor 阴影颜色
     * @param scale 缩放比例
     */
    public static void renderScaledTextWithShadow(PoseStack poseStack, GuiGraphics guiGraphics, String text, int x, int y, int color, int shadowColor, float scale) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();
        
        // 保存当前矩阵状态
        poseStack.pushPose();
        
        // 移动到文本位置
        poseStack.translate(x, y + 1.5f, 0); // 微调Y轴位置
        
        // 应用缩放
        poseStack.scale(scale, scale, 1.0f);
        
        // 渲染阴影
        font.drawInBatch(
            text,
            1,
            1,
            shadowColor,
            false,
            poseStack.last().pose(),
            buffer,
            Font.DisplayMode.NORMAL,
            0,
            15728880
        );
        
        // 渲染文本
        font.drawInBatch(
            text,
            0,
            0,
            color,
            false,
            poseStack.last().pose(),
            buffer,
            Font.DisplayMode.NORMAL,
            0,
            15728880
        );
        
        // 刷新缓冲区
        buffer.endBatch();
        
        // 恢复矩阵状态
        poseStack.popPose();
    }

    /**
     * 在状态条上方居中渲染文本
     * @param poseStack 矩阵栈
     * @param guiGraphics GUI图形对象
     * @param text 要渲染的文本
     * @param barX 状态条X坐标
     * @param barY 状态条Y坐标
     * @param barWidth 状态条宽度
     * @param color 文本颜色
     * @param shadowColor 阴影颜色
     * @param scale 缩放比例
     */
    public static void renderCenteredTextAboveBar(PoseStack poseStack, GuiGraphics guiGraphics, String text, int barX, int barY, int barWidth, int color, int shadowColor, float scale) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();
        
        // 保存当前矩阵状态
        poseStack.pushPose();
        
        // 计算文本在状态条上方的位置
        int textX = barX + barWidth / 2;  // 水平居中
        int textY = barY - 8;  // 在状态条上方8像素
        
        // 移动到文本位置
        poseStack.translate(textX, textY, 0); // 移除垂直微调
        
        // 应用缩放
        poseStack.scale(scale, scale, 1.0f);
        
        // 计算文本宽度
        int width = font.width(text);
        
        // 渲染阴影
        font.drawInBatch(
            text,
            -width / 2 + 1,  // 水平居中，阴影偏移
            1,
            shadowColor,
            false,
            poseStack.last().pose(),
            buffer,
            Font.DisplayMode.NORMAL,
            0,
            15728880
        );
        
        // 渲染文本
        font.drawInBatch(
            text,
            -width / 2,
            0,
            color,
            false,
            poseStack.last().pose(),
            buffer,
            Font.DisplayMode.NORMAL,
            0,
            15728880
        );
        
        // 刷新缓冲区
        buffer.endBatch();
        
        // 恢复矩阵状态
        poseStack.popPose();
    }
} 