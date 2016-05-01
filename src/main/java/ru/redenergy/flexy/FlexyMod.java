package ru.redenergy.flexy;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ru.redenergy.flexy.permission.IProvider;

@Mod(modid = "flexy", acceptableRemoteVersions = "*")
public class FlexyMod {

    @Mod.Instance("flexy")
    public static FlexyMod instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        if(Loader.isModLoaded("sponge"))
            CommandBackend.setProvider(new IProvider.SpongeProvider());
        else if(Loader.isModLoaded("forge-vault"))
            CommandBackend.setProvider(new IProvider.BukkitProvider());
    }

}

