package main.java.neiresources;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.*;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import main.java.neiresources.compatibility.cofh.CoFHCompat;

@Mod(modid = "neiresources", name = "NEI Resources", version = "NEIRESOURCES_VER", dependencies = "after:NotEnoughItems")
public class NEIResources
{

    @Instance(value = "neiresource")
    public static NEIResources INSTANCE;

    @EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {
        new CoFHCompat();
    }
}
