package com.k1sak1.goetyawaken.common.blocks.entity.vault;

import com.k1sak1.goetyawaken.init.ModSounds;
import com.k1sak1.goetyawaken.common.blocks.entity.VaultBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;

public enum VaultState implements StringRepresentable {
    INACTIVE("inactive", VaultState.Light.HALF_LIT) {
        @Override
        protected void onChangedTo(ServerLevel world, BlockPos pos, VaultConfig config, VaultSharedData sharedData) {
            sharedData.setDisplayItem(ItemStack.EMPTY);
            VaultBlockEntity.Client.spawnDeactivateParticles(world, pos);
            world.playSound(null, pos, ModSounds.VAULT_DEACTIVATE.get(), SoundSource.BLOCKS, 1.0F,
                    (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F + 1.0F);
        }
    },
    ACTIVE("active", VaultState.Light.LIT) {
        @Override
        protected void onChangedTo(ServerLevel world, BlockPos pos, VaultConfig config, VaultSharedData sharedData) {
            if (!sharedData.hasDisplayItem()) {
                VaultBlockEntity.Server.updateDisplayItem(world, this, config, sharedData, pos);
            }

            if (world.getBlockEntity(pos) instanceof VaultBlockEntity vaultBlockEntity) {
                VaultBlockEntity.Client.spawnActivateParticles(
                        world,
                        vaultBlockEntity.getBlockPos(),
                        vaultBlockEntity.getBlockState(),
                        vaultBlockEntity.getSharedData());
                world.playSound(null, pos, ModSounds.VAULT_ACTIVATE.get(), SoundSource.BLOCKS, 1.0F,
                        (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F + 1.0F);
            }

        }
    },
    UNLOCKING("unlocking", VaultState.Light.LIT) {
        @Override
        protected void onChangedTo(ServerLevel world, BlockPos pos, VaultConfig config, VaultSharedData sharedData) {
            world.playSound(null, pos, ModSounds.VAULT_INSERT.get(), SoundSource.BLOCKS);
        }
    },
    EJECTING("ejecting", VaultState.Light.LIT) {
        @Override
        protected void onChangedTo(ServerLevel world, BlockPos pos, VaultConfig config, VaultSharedData sharedData) {
            world.playSound(null, pos, ModSounds.VAULT_OPEN_SHUTTER.get(), SoundSource.BLOCKS);
        }

        @Override
        protected void onChangedFrom(ServerLevel world, BlockPos pos, VaultConfig config, VaultSharedData sharedData) {
            world.playSound(null, pos, ModSounds.TRIAL_SPAWNER_CLOSE_SHUTTER.get(), SoundSource.BLOCKS);
        }
    };

    private final String id;
    private final VaultState.Light light;

    VaultState(String id, VaultState.Light light) {
        this.id = id;
        this.light = light;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }

    public int getLuminance() {
        return this.light.luminance;
    }

    public VaultState update(ServerLevel world, BlockPos pos, VaultConfig config, VaultServerData serverData,
            VaultSharedData sharedData) {
        return switch (this) {
            case INACTIVE -> updateActiveState(world, pos, config, serverData, sharedData, config.activationRange());
            case ACTIVE -> updateActiveState(world, pos, config, serverData, sharedData, config.deactivationRange());
            case UNLOCKING -> {
                serverData.setStateUpdatingResumeTime(world.getGameTime() + 20L);
                yield EJECTING;
            }
            case EJECTING -> {
                if (serverData.getItemsToEject().isEmpty()) {
                    serverData.finishEjecting();
                    yield updateActiveState(world, pos, config, serverData, sharedData, config.deactivationRange());
                } else {
                    float f = serverData.getEjectSoundPitchModifier();
                    this.ejectItem(world, pos, serverData.getItemToEject(), f);
                    sharedData.setDisplayItem(serverData.getItemToDisplay());
                    boolean bl = serverData.getItemsToEject().isEmpty();
                    int i = bl ? 20 : 20;
                    serverData.setStateUpdatingResumeTime(world.getGameTime() + i);
                    yield EJECTING;
                }
            }
        };
    }

    private static VaultState updateActiveState(ServerLevel world, BlockPos pos, VaultConfig config,
            VaultServerData serverData, VaultSharedData sharedData, double radius) {
        sharedData.updateConnectedPlayers(world, pos, serverData, config, radius);
        serverData.setStateUpdatingResumeTime(world.getGameTime() + 20L);
        return sharedData.hasConnectedPlayers() ? ACTIVE : INACTIVE;
    }

    public void onStateChange(ServerLevel world, BlockPos pos, VaultState newState, VaultConfig config,
            VaultSharedData sharedData) {
        this.onChangedFrom(world, pos, config, sharedData);
        newState.onChangedTo(world, pos, config, sharedData);
    }

    protected void onChangedTo(ServerLevel world, BlockPos pos, VaultConfig config, VaultSharedData sharedData) {
    }

    protected void onChangedFrom(ServerLevel world, BlockPos pos, VaultConfig config, VaultSharedData sharedData) {
    }

    private void ejectItem(ServerLevel world, BlockPos pos, ItemStack stack, float pitchModifier) {
        DefaultDispenseItemBehavior.spawnItem(world, stack, 2, Direction.UP,
                Vec3.atBottomCenterOf(pos).relative(Direction.UP, 1.2));
        RegistryObject<SoundEvent>[] ejectSounds = new RegistryObject[] {
                ModSounds.VAULT_EJECT_1,
                ModSounds.VAULT_EJECT_2,
                ModSounds.VAULT_EJECT_3
        };
        RegistryObject<SoundEvent> randomSound = ejectSounds[world.getRandom().nextInt(ejectSounds.length)];
        world.playSound(null, pos, randomSound.get(), SoundSource.BLOCKS, 1.0F,
                0.8F + 0.4F * pitchModifier);
    }

    static enum Light {
        HALF_LIT(6),
        LIT(12);

        final int luminance;

        private Light(int luminance) {
            this.luminance = luminance;
        }
    }
}
