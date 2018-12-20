package com.fantasticsource.dynamicstealth.newAI;

import com.fantasticsource.dynamicstealth.DynamicStealth;
import com.fantasticsource.dynamicstealth.ai.AITargetEdit;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AISearchLastKnownPosition extends EntityAIBase
{
    public final AIStoreKnownPosition knownPositionAI;

    public final EntityLiving searcher;
    public final PathNavigate navigator;
    public int phase, timer = 0, searchTicks, timeAtPos;
    public double speed;
    public boolean spinDirecion;
    public Path path = null;
    private Vec3d lastPos = null;
    private double startAngle, angleDif;
    private boolean spinMode;
    private static TrigLookupTable trigTable = DynamicStealth.TRIG_TABLE;



    public AISearchLastKnownPosition(EntityLiving living, int searchTicksIn, double speedIn)
    {
        searcher = living;
        navigator = living.getNavigator();
        speed = speedIn;
        searchTicks = searchTicksIn;

        knownPositionAI = findKnownPositionAI();
        if (knownPositionAI == null) throw new IllegalArgumentException("AISearchLastKnownPosition may only be added to an entity that already has an AIStoreKnownPosition in its targetTasks");

        setMutexBits(3);
    }

    private AIStoreKnownPosition findKnownPositionAI()
    {
        for (EntityAITasks.EntityAITaskEntry entry : searcher.targetTasks.taskEntries)
        {
            if (entry.action instanceof AIStoreKnownPosition) return (AIStoreKnownPosition) entry.action;
        }
        return null;
    }



    @Override
    public boolean shouldExecute()
    {
        if (AITargetEdit.isSuitableTarget(searcher, knownPositionAI.target))
        {
            searcher.setAttackTarget(knownPositionAI.target);
            knownPositionAI.lastKnownPosition = knownPositionAI.target.getPosition();
            return false;
        }

        return (knownPositionAI.target != null && knownPositionAI.lastKnownPosition != null);
    }

    @Override
    public void startExecuting()
    {
        phase = 0;
        timer = searchTicks;
        timeAtPos = 0;

        path = navigator.getPathToPos(knownPositionAI.lastKnownPosition);
        navigator.setPath(path, speed);
        lastPos = null;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return (shouldExecute() && timer > 0);
    }

    @Override
    public void updateTask()
    {
        if (phase == 0) //Goal; to reach searchPos, or the nearest reachable position to it
        {
            if (navigator.getPath() != path) navigator.setPath(path, speed);

            Vec3d currentPos = searcher.getPositionVector();
            if (lastPos != null && lastPos.squareDistanceTo(currentPos) < speed * 0.005) timeAtPos++;
            else timeAtPos = 0;

            if (timeAtPos > 20 || navigator.noPath() || (searcher.onGround && timeAtPos > 0))
            {
                if (timeAtPos > 20 || !newPath(knownPositionAI.lastKnownPosition.getX(), knownPositionAI.lastKnownPosition.getY(), knownPositionAI.lastKnownPosition.getZ()))
                {
                    phase = 1;
                    spinMode = true;
                    spinDirecion = searcher.getRNG().nextBoolean();
                    startAngle = searcher.rotationYaw;
                    angleDif = 0;

                    navigator.clearPath();
                    path = null;
                }
            }

            lastPos = currentPos;
        }

        if (phase == 1) //Goal; to search the area
        {
            timer--;
            if (searchTicks - timer < 20) return; //Pause when reaching destination

            if (spinMode) //Spin around after pausing
            {
                navigator.clearPath();

                if (spinDirecion) angleDif += 5;
                else angleDif -= 5;
                double angleRad = Tools.degtorad(startAngle + angleDif);
                searcher.getLookHelper().setLookPosition(searcher.posX - trigTable.sin(angleRad), lastPos.y + knownPositionAI.target.getEyeHeight(), searcher.posZ + trigTable.cos(angleRad), 30, 30);

                if (Math.abs(angleDif) - 360 > 5)
                {
                    if (randomPath(4, 2))
                    {
                        spinMode = false;
                        lastPos = null;
                    }
                }
            }
            else //Wander after spinning around
            {
                if (navigator.getPath() != path) navigator.setPath(path, speed);

                Vec3d currentPos = searcher.getPositionVector();
                if (navigator.noPath() || (searcher.onGround && lastPos != null && lastPos.squareDistanceTo(currentPos) < speed * 0.005))
                {
                    PathPoint finalPoint = path.getFinalPathPoint();
                    if (finalPoint == null || !newPath(finalPoint.x, finalPoint.y, finalPoint.z) || path.getFinalPathPoint() == null || pointDistSquared(finalPoint, path.getFinalPathPoint()) < 1)
                    {
                        spinMode = true;
                        spinDirecion = searcher.getRNG().nextBoolean();
                        startAngle = searcher.rotationYaw;
                        angleDif = 0;

                        path = null;
                        navigator.clearPath();
                    }
                }

                lastPos = currentPos;
            }
        }
    }

    private boolean newPath(double x, double y, double z)
    {
        Path newPath = navigator.getPathToXYZ(x, y, z);
        if (newPath == null) return false;

        PathPoint finalPoint = newPath.getFinalPathPoint();
        if (finalPoint == null || Math.pow(finalPoint.x - searcher.posX, 2) + Math.pow(finalPoint.y - searcher.posY, 2) + Math.pow(finalPoint.z - searcher.posZ, 2) < 1) return false;

        path = newPath;
        navigator.setPath(path, speed);
        return true;
    }

    @Override
    public void resetTask()
    {
        knownPositionAI.lastKnownPosition = null;
        knownPositionAI.target = null;
        timer = 0;

        if (path != null && path.equals(navigator.getPath())) navigator.clearPath();
        path = null;
    }

    private boolean randomPath(int xz, int y)
    {
        int x = searcher.getRNG().nextInt(xz * 2);
        int z = searcher.getRNG().nextInt(xz * 2);
        int yDir = searcher.getRNG().nextBoolean() ? 1 : -1;
        int yEnd = yDir == 1 ? y * 2 : 0;

        BlockPos currentPos = searcher.getPosition();

        int xCheck, yCheck, zCheck;
        BlockPos checkPos;
        for(int ix = 0; ix < xz * 2; ix++)
        {
            for(int iz = 0; iz < xz * 2; iz++)
            {
                for(int iy = yDir == 1 ? 0 : y * 2; iy != yEnd; iy += yDir)
                {
                    xCheck = (x + ix) % (xz * 2) - xz + currentPos.getX();
                    yCheck = (y + iy) % (y * 2) - y + currentPos.getY();
                    zCheck = (z + iz) % (xz * 2) - xz + currentPos.getZ();
                    checkPos = new BlockPos(xCheck, yCheck, zCheck);

                    if (navigator.canEntityStandOnPos(checkPos))
                    {
                        if (newPath(checkPos.getX(), checkPos.getY(), checkPos.getZ())) return true;
                    }
                }
            }
        }

        return false;
    }

    private double pointDistSquared(PathPoint a, PathPoint b)
    {
        return Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2) + Math.pow(a.z - b.z, 2);
    }
}
