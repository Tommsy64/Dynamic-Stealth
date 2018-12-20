package com.fantasticsource.dynamicstealth.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.passive.EntityTameable;

public class AIOwnerHurtTargetEdit extends AITargetEdit
{
    EntityTameable tameable;
    EntityLivingBase ownerEnemy;
    private int timestamp;

    public AIOwnerHurtTargetEdit(EntityAIOwnerHurtTarget oldAI) throws IllegalAccessException
    {
        super(oldAI);
        tameable = (EntityTameable) attacker;
        setMutexBits(1);
    }

    @Override
    public boolean shouldExecute()
    {
        if (!tameable.isTamed()) return false;

        EntityLivingBase owner = tameable.getOwner();
        if (owner == null) return false;

        ownerEnemy = owner.getLastAttackedEntity();
        return owner.getLastAttackedEntityTime() != timestamp && isSuitableTarget(ownerEnemy) && tameable.shouldAttackEntity(ownerEnemy, owner);
    }

    @Override
    public void startExecuting()
    {
        attacker.setAttackTarget(ownerEnemy);

        EntityLivingBase entitylivingbase = tameable.getOwner();
        if (entitylivingbase != null) timestamp = entitylivingbase.getLastAttackedEntityTime();

        super.startExecuting();
    }
}