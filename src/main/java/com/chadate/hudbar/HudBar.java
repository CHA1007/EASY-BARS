package com.chadate.hudbar;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import com.chadate.hudbar.event.HealthBarEventHandler;

@Mod(HudBar.MODID)
public class HudBar
{

    public static final String MODID = "hudbar";

    private static final Logger LOGGER = LogUtils.getLogger();

    public HudBar(FMLJavaModLoadingContext context)
    {
        // 注册事件监听器
        MinecraftForge.EVENT_BUS.register(new HealthBarEventHandler());
    }

}
