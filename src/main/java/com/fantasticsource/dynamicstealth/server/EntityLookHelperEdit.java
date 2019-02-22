package com.fantasticsource.dynamicstealth.server;

import com.fantasticsource.dynamicstealth.common.DynamicStealth;
import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.util.math.MathHelper;

import java.lang.reflect.InvocationTargetException;

public class EntityLookHelperEdit extends EntityLookHelper
{
    private final EntityLiving entity;
    private float deltaLookYaw;
    private float deltaLookPitch;
    private boolean isLooking;
    private double posX;
    private double posY;
    private double posZ;

    public EntityLookHelperEdit(EntityLiving entitylivingIn)
    {
        super(entitylivingIn);
        entity = entitylivingIn;

        if (!entity.world.isRemote)
        {
            try
            {
                DynamicStealth.makeLivingLookDirection(entity, Math.random() * 360, 0);
            }
            catch (InvocationTargetException | IllegalAccessException e)
            {
                MCTools.crash(e, 146, false);
            }
        }
        else
        {
            entity.prevRotationYawHead = entity.rotationYawHead;
            entity.rotationYaw = entity.rotationYawHead;
            entity.prevRotationYaw = entity.rotationYawHead;
        }
    }

    public void setLookPositionWithEntity(Entity entityIn, float deltaYaw, float deltaPitch)
    {
        if (entityIn != null)
        {
            posX = entityIn.posX;

            if (entityIn instanceof EntityLivingBase) posY = entityIn.posY + (double) entityIn.getEyeHeight();
            else posY = (entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2;

            posZ = entityIn.posZ;
            deltaLookYaw = deltaYaw;
            deltaLookPitch = deltaPitch;
            isLooking = true;
        }
    }

    public void setLookPosition(double x, double y, double z, float deltaYaw, float deltaPitch)
    {
        posX = x;
        posY = y;
        posZ = z;
        deltaLookYaw = deltaYaw;
        deltaLookPitch = deltaPitch;
        isLooking = true;
    }

    public void onUpdateLook()
    {
        entity.rotationPitch = 0;

        if (isLooking)
        {
            isLooking = false;
            double d0 = posX - entity.posX;
            double d1 = posY - (entity.posY + (double) entity.getEyeHeight());
            double d2 = posZ - entity.posZ;
            double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
            float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
            float f1 = (float) (-(MathHelper.atan2(d1, d3) * (180D / Math.PI)));
            entity.rotationPitch = updateRotation(entity.rotationPitch, f1, deltaLookPitch);
            entity.rotationYawHead = updateRotation(entity.rotationYawHead, f, deltaLookYaw);
        }
        else
        {
            entity.rotationYawHead = updateRotation(entity.rotationYawHead, entity.renderYawOffset, 10);
        }

        float f2 = MathHelper.wrapDegrees(entity.rotationYawHead - entity.renderYawOffset);

        if (!entity.getNavigator().noPath())
        {
            if (f2 < -75) entity.rotationYawHead = entity.renderYawOffset - 75;
            else if (f2 > 75) entity.rotationYawHead = entity.renderYawOffset + 75;
        }
    }

    private float updateRotation(float angleStart, float angleGoal, float maxAmount)
    {
        float f = MathHelper.wrapDegrees(angleGoal - angleStart);

        if (f > maxAmount) f = maxAmount;
        else if (f < -maxAmount) f = -maxAmount;

        return angleStart + f;
    }

    public boolean getIsLooking()
    {
        return isLooking;
    }

    public double getLookPosX()
    {
        return posX;
    }

    public double getLookPosY()
    {
        return posY;
    }

    public double getLookPosZ()
    {
        return posZ;
    }
}