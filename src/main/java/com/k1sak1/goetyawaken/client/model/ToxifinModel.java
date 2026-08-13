package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.common.entities.ally.ToxifinServant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ToxifinModel extends HierarchicalModel<ToxifinServant> {
   private static final float[] SPIKE_X_ROT = new float[] { 1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F,
         0.75F, 0.0F, 0.0F };
   private static final float[] SPIKE_Y_ROT = new float[] { 0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F,
         0.0F, 0.0F, 0.0F };
   private static final float[] SPIKE_Z_ROT = new float[] { 0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
         0.0F, 0.75F, 1.25F };
   private static final float[] SPIKE_X;
   private static final float[] SPIKE_Y;
   private static final float SPIKE_Y_BASE = 16.0F;
   private static final float[] SPIKE_Z;
   private static final float SPIKE_LENGTH = 9.4F;
   private static final float A2;
   private static final float A12;
   private static final float[] SPIKE_X_ROT_SLAB;
   private static final float[] SPIKE_Y_ROT_SLAB;
   private static final float[] SPIKE_Z_ROT_TOP_SLAB;
   private static final float[] SPIKE_Z_ROT_BOTTOM_SLAB;
   private static final float[] SPIKE_X_SLAB_OFFSET;
   private static final float[] SPIKE_X_SLAB;
   private static final float[] SPIKE_Y_SLAB;
   private static final float SPIKE_Y_BASE_SLAB = 19.0F;
   private static final float[] SPIKE_Z_SLAB;
   private static final String EYE = "eye";
   private static final String TAIL_0 = "tail0";
   private static final String TAIL_1 = "tail1";
   private static final String TAIL_2 = "tail2";
   private final ModelPart root;
   private final ModelPart head;
   private final ModelPart eye;
   private final ModelPart[] spikeParts;
   private final ModelPart[] tailParts;

   public ToxifinModel(ModelPart modelPart) {
      this.root = modelPart;
      this.spikeParts = new ModelPart[12];
      this.head = modelPart.getChild("head");

      for (int i = 0; i < this.spikeParts.length; ++i) {
         this.spikeParts[i] = this.head.getChild(createSpikeName(i));
      }

      this.eye = this.head.getChild("eye");
      this.tailParts = new ModelPart[3];
      this.tailParts[0] = this.head.getChild("tail0");
      this.tailParts[1] = this.tailParts[0].getChild("tail1");
      this.tailParts[2] = this.tailParts[1].getChild("tail2");
   }

   private static String createSpikeName(int i) {
      return "spike" + i;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition rootPart = mesh.getRoot();
      boolean isSlabStyle = true;
      int slabAdjust = isSlabStyle ? 3 : 0;
      PartDefinition headPart = rootPart.addOrReplaceChild("head",
            CubeListBuilder.create().texOffs(0, 0)
                  .addBox(-6.0F, (float) (10 + slabAdjust * 2), -8.0F, 12.0F, (float) (12 - slabAdjust * 2), 16.0F)
                  .texOffs(0, 28)
                  .addBox(-8.0F, (float) (10 + slabAdjust * 2), -6.0F, 2.0F, (float) (12 - slabAdjust * 2), 12.0F)
                  .texOffs(0, 28)
                  .addBox(6.0F, (float) (10 + slabAdjust * 2), -6.0F, 2.0F, (float) (12 - slabAdjust * 2), 12.0F, true)
                  .texOffs(16, 40)
                  .addBox(-6.0F, (float) (8 + slabAdjust * 2), -6.0F, 12.0F, 2.0F, 12.0F).texOffs(16, 40)
                  .addBox(-6.0F, 22.0F, -6.0F, 12.0F, 2.0F, 12.0F),
            PartPose.ZERO);
      CubeListBuilder spikeShape = CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -4.5F, -1.0F, 2.0F, 9.0F,
            2.0F);

      for (int spikeIdx = 0; spikeIdx < 12; ++spikeIdx) {
         if (isSlabStyle) {
            float posX = SPIKE_X_SLAB[spikeIdx] + SPIKE_X_SLAB_OFFSET[spikeIdx];
            float posY = 19.0F + SPIKE_Y_SLAB[spikeIdx];
            float posZ = SPIKE_Z_SLAB[spikeIdx];
            float rotX = SPIKE_X_ROT_SLAB[spikeIdx];
            float rotY = SPIKE_Y_ROT_SLAB[spikeIdx];
            float rotZ = SPIKE_Z_ROT_TOP_SLAB[spikeIdx];
            headPart.addOrReplaceChild(createSpikeName(spikeIdx), spikeShape,
                  PartPose.offsetAndRotation(posX, posY, posZ, rotX, rotY, rotZ));
         } else {
            float posX = SPIKE_X[spikeIdx];
            float posY = 16.0F + SPIKE_Y[spikeIdx];
            float posZ = SPIKE_Z[spikeIdx];
            float rotX = SPIKE_X_ROT[spikeIdx];
            float rotY = SPIKE_Y_ROT[spikeIdx];
            float rotZ = SPIKE_Z_ROT[spikeIdx];
            headPart.addOrReplaceChild(createSpikeName(spikeIdx), spikeShape,
                  PartPose.offsetAndRotation(posX, posY, posZ, rotX, rotY, rotZ));
         }
      }

      headPart.addOrReplaceChild("eye",
            CubeListBuilder.create().texOffs(8, 0).addBox(-1.0F, (float) (15 + slabAdjust), 0.0F, 2.0F, 2.0F, 1.0F),
            PartPose.offset(0.0F, 0.0F, -8.25F));
      PartDefinition tailBase = headPart.addOrReplaceChild("tail0",
            CubeListBuilder.create().texOffs(40, 0).addBox(-2.0F, (float) (14 + slabAdjust), 7.0F, 4.0F, 4.0F, 8.0F),
            PartPose.ZERO);
      PartDefinition tailMid = tailBase.addOrReplaceChild("tail1",
            CubeListBuilder.create().texOffs(0, 54).addBox(0.0F, (float) (14 + slabAdjust), 0.0F, 3.0F, 3.0F, 7.0F),
            PartPose.offset(-1.5F, 0.5F, 14.0F));
      tailMid.addOrReplaceChild("tail2",
            CubeListBuilder.create().texOffs(41, 32).addBox(0.0F, (float) (14 + slabAdjust), 0.0F, 2.0F, 2.0F, 6.0F)
                  .texOffs(25, 19).addBox(1.0F, 10.5F + (float) slabAdjust, 3.0F, 1.0F, 9.0F, 9.0F),
            PartPose.offset(0.5F, 0.5F, 6.0F));
      return LayerDefinition.create(mesh, 64, 64);
   }

   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(ToxifinServant entity, float limbSwing, float limbSwingAmount, float ageInTicks,
         float netHeadYaw, float headPitch) {
      float partialTick = ageInTicks - (float) entity.tickCount;
      this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
      this.head.xRot = headPitch * ((float) Math.PI / 180F);
      float spikeFactor = (1.0F - entity.getSpikesAnimation(partialTick)) * 0.55F;
      this.setupSpikesToxic(ageInTicks, spikeFactor, entity.isVehicle(), entity.isPassenger());
      Entity cameraTarget = Minecraft.getInstance().getCameraEntity();
      if (entity.hasActiveAttackTarget()) {
         cameraTarget = entity.getActiveAttackTarget();
      }

      if (cameraTarget != null) {
         Vec3 targetEyePos = cameraTarget.getEyePosition(0.0F);
         Vec3 selfEyePos = entity.getEyePosition(0.0F);
         double yDiff = targetEyePos.y - selfEyePos.y;
         this.eye.y = yDiff > (double) 0.0F ? 0.0F : 1.0F;
         Vec3 lookVec = entity.getViewVector(0.0F);
         lookVec = new Vec3(lookVec.x, (double) 0.0F, lookVec.z);
         Vec3 toTarget = (new Vec3(selfEyePos.x - targetEyePos.x, (double) 0.0F, selfEyePos.z - targetEyePos.z))
               .normalize()
               .yRot(((float) Math.PI / 2F));
         double dotProduct = lookVec.dot(toTarget);
         this.eye.x = Mth.sqrt((float) Math.abs(dotProduct)) * 2.0F * (float) Math.signum(dotProduct);
      }

      this.eye.visible = true;
      float tailAnim = entity.getTailAnimation(partialTick);
      this.tailParts[0].yRot = Mth.sin(tailAnim) * (float) Math.PI * 0.05F;
      this.tailParts[1].yRot = Mth.sin(tailAnim) * (float) Math.PI * 0.1F;
      this.tailParts[2].yRot = Mth.sin(tailAnim) * (float) Math.PI * 0.15F;
   }

   private void setupSpikes(float time, float spikeAnim) {
      for (int idx = 0; idx < 12; ++idx) {
         this.spikeParts[idx].x = SPIKE_X[idx] * getSpikeOffset(idx, time, spikeAnim);
         this.spikeParts[idx].y = 16.0F + SPIKE_Y[idx] * getSpikeOffset(idx, time, spikeAnim);
         this.spikeParts[idx].z = SPIKE_Z[idx] * getSpikeOffset(idx, time, spikeAnim);
         this.spikeParts[idx].zRot = SPIKE_Z_ROT[idx];
      }

      for (int visIdx = 0; visIdx < 4; ++visIdx) {
         this.spikeParts[visIdx].visible = true;
      }

   }

   private void setupSpikesToxic(float time, float spikeAnim, boolean isVehicle, boolean isPassenger) {
      int signFactor = isVehicle ? -1 : 1;
      float[] zRotArray = isVehicle ? SPIKE_Z_ROT_BOTTOM_SLAB : SPIKE_Z_ROT_TOP_SLAB;

      for (int spikeIdx = 0; spikeIdx < 12; ++spikeIdx) {
         this.spikeParts[spikeIdx].x = SPIKE_X_SLAB[spikeIdx] * getSpikeOffset(spikeIdx, time, spikeAnim)
               + SPIKE_X_SLAB_OFFSET[spikeIdx];
         this.spikeParts[spikeIdx].y = 19.0F
               + (float) signFactor * SPIKE_Y_SLAB[spikeIdx] * getSpikeOffset(spikeIdx, time, spikeAnim);
         this.spikeParts[spikeIdx].z = SPIKE_Z_SLAB[spikeIdx] * getSpikeOffset(spikeIdx, time, spikeAnim);
         this.spikeParts[spikeIdx].zRot = zRotArray[spikeIdx];
      }

      if (isVehicle && isPassenger) {
         for (int idx = 0; idx < 4; ++idx) {
            this.spikeParts[idx].visible = false;
         }
      } else {
         for (int idx = 0; idx < 4; ++idx) {
            this.spikeParts[idx].visible = true;
         }
      }

   }

   private static float getSpikeOffset(int spikeIdx, float time, float spikeFactor) {
      return 1.0F + Mth.cos(time * 1.5F + (float) spikeIdx) * 0.01F - spikeFactor;
   }

   static {
      for (int idx = 0; idx < 12; ++idx) {
         SPIKE_X_ROT[idx] = (float) Math.PI * SPIKE_X_ROT[idx];
         SPIKE_Y_ROT[idx] = (float) Math.PI * SPIKE_Y_ROT[idx];
         SPIKE_Z_ROT[idx] = (float) Math.PI * SPIKE_Z_ROT[idx];
      }

      SPIKE_X = new float[] { 0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F };
      SPIKE_Y = new float[] { -8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F };
      SPIKE_Z = new float[] { 8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F };
      A2 = (float) Math.atan2((double) 2.0F, (double) 1.0F);
      A12 = (float) Math.atan2((double) 1.0F, (double) 2.0F);
      SPIKE_X_ROT_SLAB = new float[] { A2, A12, -A12, -A2, A2, A12, -A12, -A2, A2, A12, -A12, -A2 };
      SPIKE_Y_ROT_SLAB = new float[] { 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F };
      SPIKE_Z_ROT_TOP_SLAB = new float[] { 0.0F, 0.0F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, -0.5F, -0.5F, -0.5F, -0.5F };
      SPIKE_Z_ROT_BOTTOM_SLAB = new float[] { 1.0F, 1.0F, 1.0F, 1.0F, 0.5F, 0.5F, 0.5F, 0.5F, -0.5F, -0.5F, -0.5F,
            -0.5F };
      SPIKE_X_SLAB_OFFSET = new float[] { 0.0F, 0.0F, 0.0F, 0.0F, 3.0F, 3.0F, 3.0F, 3.0F, -3.0F, -3.0F, -3.0F, -3.0F };
      SPIKE_X_SLAB = new float[] { 0.0F, 0.0F, 0.0F, 0.0F, Mth.cos(A2), Mth.cos(A12), Mth.cos(A12), Mth.cos(A2),
            -Mth.cos(A2), -Mth.cos(A12), -Mth.cos(A12), -Mth.cos(A2) };
      SPIKE_Y_SLAB = new float[] { -Mth.cos(A2), -Mth.cos(A12), -Mth.cos(A12), -Mth.cos(A2), 0.0F, 0.0F, 0.0F, 0.0F,
            0.0F, 0.0F, 0.0F, 0.0F };
      SPIKE_Z_SLAB = new float[] { -Mth.sin(A2), -Mth.sin(A12), Mth.sin(A12), Mth.sin(A2), -Mth.sin(A2), -Mth.sin(A12),
            Mth.sin(A12), Mth.sin(A2), -Mth.sin(A2), -Mth.sin(A12), Mth.sin(A12), Mth.sin(A2) };

      int prevIdx;
      for (int curIdx = 0; curIdx < 12; SPIKE_Z_SLAB[prevIdx] *= 9.4F) {
         SPIKE_Z_ROT_TOP_SLAB[curIdx] *= (float) Math.PI;
         SPIKE_Z_ROT_BOTTOM_SLAB[curIdx] *= (float) Math.PI;
         SPIKE_X_SLAB[curIdx] *= 9.4F;
         SPIKE_Y_SLAB[curIdx] *= 9.4F;
         prevIdx = curIdx++;
      }

   }

}
