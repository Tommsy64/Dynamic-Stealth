package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIDefendVillage;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.village.Village;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

public class AIDefendVillageEdit extends AITargetEdit
{
    private static Field villageReputationsField = ReflectionTool.getField(Village.class, "field_82693_j", "playerReputation");


    EntityIronGolem irongolem;
    EntityLivingBase villageAgressorTarget;

    public AIDefendVillageEdit(EntityAIDefendVillage oldAI)
    {
        super(oldAI);
        irongolem = (EntityIronGolem) attacker;
        setMutexBits(1);
    }

    @Override
    public boolean shouldExecute()
    {
        Village village = irongolem.getVillage();
        if (village == null) return false;

        villageAgressorTarget = village.findNearestVillageAggressor(irongolem);

        if (villageAgressorTarget instanceof EntityCreeper)
        {
            return false;
        }
        else if (isSuitableTarget(villageAgressorTarget))
        {
            return true;
        }
        else if (attacker.getRNG().nextInt(20) == 0)
        {
            try
            {
                Map<UUID, Integer> map = (Map<UUID, Integer>) villageReputationsField.get(village);
                ExplicitPriorityQueue<EntityPlayer> queue = new ExplicitPriorityQueue<>(map.size());
                UUID uuid;
                for (Map.Entry<UUID, Integer> entry : map.entrySet())
                {
                    uuid = entry.getKey();
                    if (village.isPlayerReputationTooLow(uuid))
                    {
                        EntityPlayer player = irongolem.world.getPlayerEntityByUUID(uuid);
                        queue.add(player, player.getDistanceSq(irongolem));
                    }
                }

                if (queue.isEmpty()) return false;

                villageAgressorTarget = queue.poll();
                while (villageAgressorTarget != null && !isSuitableTarget(villageAgressorTarget)) villageAgressorTarget = queue.poll();
            }
            catch (IllegalAccessException e)
            {
                MCTools.crash(e, 136, false);
            }

            return villageAgressorTarget != null;
        }

        return false;
    }

    @Override
    public void startExecuting()
    {
        if (villageAgressorTarget == null) Compat.clearAttackTargetAndReplaceAITasks(irongolem);
        else irongolem.setAttackTarget(villageAgressorTarget);
        super.startExecuting();
    }
}