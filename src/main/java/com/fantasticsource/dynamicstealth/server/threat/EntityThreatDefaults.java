package com.fantasticsource.dynamicstealth.server.threat;

import java.util.ArrayList;

public class EntityThreatDefaults
{
    public static ArrayList<String> threatBypassDefaults = new ArrayList<>();
    public static ArrayList<String> passiveDefaults = new ArrayList<>();

    static
    {
        passiveDefaults.add("shulker, false");
        passiveDefaults.add("snowman, false");

        threatBypassDefaults.add("player");
        threatBypassDefaults.add("slime");
        threatBypassDefaults.add("magma_cube");
        threatBypassDefaults.add("ender_dragon");


        //Compat; these should be added absolutely, not conditionally
        passiveDefaults.add("ebwizardry:wizard, false");
        passiveDefaults.add("techguns:turret, false");
        passiveDefaults.add("rafradek_tf2_weapons:sentry, false");
        passiveDefaults.add("ghast, false");
        passiveDefaults.add("nex:ghastling, false");
        passiveDefaults.add("nex:ghast_queen, false");
        passiveDefaults.add("nex:ghastling, false");
        passiveDefaults.add("animania:mare_draft, true");
        passiveDefaults.add("animania:stallion_draft, true");
        passiveDefaults.add("animania:foal_draft, true");
        passiveDefaults.add("animania:peachick_charcoal, true");
        passiveDefaults.add("animania:peachick_opal, true");
        passiveDefaults.add("animania:peachick_peach, true");
        passiveDefaults.add("animania:peachick_purple, true");
        passiveDefaults.add("animania:peachick_taupe, true");
        passiveDefaults.add("animania:peachick_blue, true");
        passiveDefaults.add("animania:peachick_white, true");

        threatBypassDefaults.add("dissolution:player_corpse");
        threatBypassDefaults.add("millenaire:genericvillager");
        threatBypassDefaults.add("millenaire:genericsimmfemale");
        threatBypassDefaults.add("millenaire:genericasimmfemale");
        threatBypassDefaults.add("tconstruct:blueslime");
        threatBypassDefaults.add("primitivemobs:treasure_slime");
        threatBypassDefaults.add("pvj:pvj_icecube");
        threatBypassDefaults.add("defiledlands:slime_defiled");
        threatBypassDefaults.add("scp:killer_statue");
        threatBypassDefaults.add("cyclicmagic:robot");
    }
}
