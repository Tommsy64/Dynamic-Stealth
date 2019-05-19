package com.fantasticsource.dynamicstealth.compat;

import com.fantasticsource.dynamicstealth.server.ai.edited.AIAttackMeleeEdit;
import com.fantasticsource.mctools.NPEAttackTargetTaskHolder;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;

import java.util.Set;

public class Compat
{
    public static boolean
            lycanites = false,
            ancientwarfare = false,
            customnpcs = false,
            neat = false,
            statues = false,
            dissolution = false,
            magma_monsters = false,
            abyssalcraft = false,
            emberroot = false,
            testdummy = false,
            primitivemobs = false,
            thermalfoundation = false;


    public static void cancelTasksRequiringAttackTarget(EntityAITasks tasks)
    {
        for (EntityAITasks.EntityAITaskEntry task : tasks.taskEntries)
        {
            if (badNullTargetHandling(task.action))
            {
                //Hard reset; set using to false, call resetTask(), and remove task from executingTasks
                task.using = false;
                task.action.resetTask();
                tasks.executingTaskEntries.remove(task);
            }
        }
    }


    private static boolean badNullTargetHandling(EntityAIBase ai)
    {
        if (ai instanceof EntityAIAttackMelee && !(ai instanceof AIAttackMeleeEdit)) return true;

        String aiClassname = ai.getClass().getName();
        if (lycanites && aiClassname.equals("com.lycanitesmobs.core.entity.ai.EntityAIAttackMelee")) return true;
        if (ancientwarfare && aiClassname.equals("net.shadowmage.ancientwarfare.npc.ai.vehicle.NpcAIAimVehicle")) return true;
        if (thermalfoundation && aiClassname.contains("cofh.thermalfoundation.entity.monster")) return true; //Should cover Basalz, Blitz, and Blizz
        if (abyssalcraft && aiClassname.contains("abyssalcraft.common.entity.ai")) return true;
        if (magma_monsters && aiClassname.contains("EntityMagmaMonster")) return true;
        if (primitivemobs && aiClassname.contains("AIFlameSpewAttack")) return true;
        if (emberroot && aiClassname.contains("EntityAIAttackOnCollideAggressive")) return true;

        return false;
    }


    public static void replaceNPEAttackTargetTasks(EntityLiving living)
    {
        EntityAITasks taskList = living.targetTasks;
        Set<EntityAITasks.EntityAITaskEntry> entrySet = taskList.taskEntries;
        for (EntityAITasks.EntityAITaskEntry task : entrySet.toArray(new EntityAITasks.EntityAITaskEntry[entrySet.size()]))
        {
            if (badNullTargetHandling(task.action))
            {
                taskList.addTask(task.priority, new NPEAttackTargetTaskHolder(living, task.action));
                taskList.removeTask(task.action);
            }
        }

        taskList = living.tasks;
        entrySet = taskList.taskEntries;
        for (EntityAITasks.EntityAITaskEntry task : entrySet.toArray(new EntityAITasks.EntityAITaskEntry[entrySet.size()]))
        {
            if (badNullTargetHandling(task.action))
            {
                taskList.addTask(task.priority, new NPEAttackTargetTaskHolder(living, task.action));
                taskList.removeTask(task.action);
            }
        }
    }
}
