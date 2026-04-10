package com.k1sak1.goetyawaken.client.renderer.undead;

import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.client.model.undead.TowerWraithModel;
import com.k1sak1.goetyawaken.client.renderer.layers.TowerWraithGlowLayer;
import com.k1sak1.goetyawaken.common.entities.ally.undead.tower_wraith.AbstractTowerWraith;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TowerWraithRenderer extends MobRenderer<AbstractTowerWraith, TowerWraithModel<AbstractTowerWraith>> {
    private static final ResourceLocation TOWER_WRAITH_ICE_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/tower_wraith/tower_wraith_ice.png");
    private static final ResourceLocation TOWER_WRAITH_HELL_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/tower_wraith/tower_wraith_hell.png");
    private static final ResourceLocation TOWER_WRAITH_MAGIC_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/tower_wraith/tower_wraith_magic.png");
    private static final ResourceLocation TOWER_WRAITH_ACID_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/tower_wraith/tower_wraith_acid.png");
    private static final ResourceLocation HOSTILE_TOWER_WRAITH_ICE_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/tower_wraith/hostile_tower_wraith_ice.png");
    private static final ResourceLocation HOSTILE_TOWER_WRAITH_HELL_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/tower_wraith/hostile_tower_wraith_hell.png");
    private static final ResourceLocation HOSTILE_TOWER_WRAITH_MAGIC_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/tower_wraith/hostile_tower_wraith_magic.png");
    private static final ResourceLocation HOSTILE_TOWER_WRAITH_ACID_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/tower_wraith/hostile_tower_wraith_acid.png");
    private static final ResourceLocation TOWER_WRAITH_GLOW_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/tower_wraith/tower_wraith_glow.png");

    public TowerWraithRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new TowerWraithModel<AbstractTowerWraith>(
                        renderManagerIn.bakeLayer(ClientEventHandler.TOWER_WRAITH_LAYER)),
                0.5F);
        this.addLayer(new TowerWraithGlowLayer(this, TOWER_WRAITH_GLOW_TEXTURE));
        // this.addLayer(new TowerWraithSecretLayer(this,
        // renderManagerIn.getModelSet()));
    }

    @Override
    protected void scale(AbstractTowerWraith towerWraith, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.2F, 1.2F, 1.2F);
    }

    @Override
    public void render(AbstractTowerWraith entity, float entityYaw, float partialTicks, PoseStack matrixStack,
            MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractTowerWraith entity) {
        String mode = entity.getMode();
        boolean isHostile = entity.isHostile();

        if (isHostile) {
            switch (mode) {
                case "ice":
                    return HOSTILE_TOWER_WRAITH_ICE_TEXTURE;
                case "hell":
                    return HOSTILE_TOWER_WRAITH_HELL_TEXTURE;
                case "magic":
                    return HOSTILE_TOWER_WRAITH_MAGIC_TEXTURE;
                case "acid":
                    return HOSTILE_TOWER_WRAITH_ACID_TEXTURE;
                default:
                    return HOSTILE_TOWER_WRAITH_ICE_TEXTURE;
            }
        } else {
            switch (mode) {
                case "ice":
                    return TOWER_WRAITH_ICE_TEXTURE;
                case "hell":
                    return TOWER_WRAITH_HELL_TEXTURE;
                case "magic":
                    return TOWER_WRAITH_MAGIC_TEXTURE;
                case "acid":
                    return TOWER_WRAITH_ACID_TEXTURE;
                default:
                    return TOWER_WRAITH_ICE_TEXTURE;
            }
        }
    }
}
