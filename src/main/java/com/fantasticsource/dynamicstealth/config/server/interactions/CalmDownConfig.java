package com.fantasticsource.dynamicstealth.config.server.interactions;

import net.minecraftforge.common.config.Config;

public class CalmDownConfig
{
    @Config.Name("Recover all HP")
    @Config.Comment("If set to true, all hp is instantly recovered when calming down")
    public boolean fullHPRecovery = false;

    @Config.Name("CNPCs Warp Home")
    @Config.Comment("If set to true, cnpcs warp home when calming down")
    public boolean cnpcsWarpHome = false;

    @Config.Name("Potion Effects")
    @Config.Comment(
            {
                    "Potion effects to apply when something calms down after fleeing",
                    "",
                    "This applies strength 2 for 200 ticks (10 seconds):",
                    "strength.200.2",
                    "",
                    "This applies soul sight for 100 ticks (5 seconds):",
                    "dynamicstealth:soulsight.100"
            })
    public String[] potionEffects =
            {
                    "regeneration.400"
            };
}
