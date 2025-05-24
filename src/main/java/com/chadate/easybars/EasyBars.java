package com.chadate.easybars;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import com.chadate.easybars.config.HudBarConfig;
import com.chadate.easybars.event.HealthBarEventHandler;

@Mod(EasyBars.MOD_ID)
public class EasyBars
{

    public static final String MOD_ID = "easybars";

    private static final Logger LOGGER = LogUtils.getLogger();

    public EasyBars()
    {
        // 注册配置
        HudBarConfig.register();
        
        // 注册事件监听器
        MinecraftForge.EVENT_BUS.register(new HealthBarEventHandler());
        
        // 注册mod加载事件
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // 初始化代码
    }

}
