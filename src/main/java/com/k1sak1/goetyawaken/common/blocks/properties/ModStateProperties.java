package com.k1sak1.goetyawaken.common.blocks.properties;

import com.k1sak1.goetyawaken.common.blocks.entity.trial_spawner.TrialSpawnerState;
import com.k1sak1.goetyawaken.common.blocks.entity.vault.VaultState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ModStateProperties {
    public static final IntegerProperty LEVEL_TOWER = IntegerProperty.create("level", 0, 3);
    public static final IntegerProperty LEVEL_BREW = IntegerProperty.create("level", 0, 4);
    public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 5);
    public static final BooleanProperty FAILED = BooleanProperty.create("failed");
    public static final BooleanProperty GENERATED = BooleanProperty.create("generated");
    public static final BooleanProperty VOID = BooleanProperty.create("void");
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    public static final EnumProperty<TrialSpawnerState> TRIAL_SPAWNER_STATE = EnumProperty.create("trial_spawner_state", TrialSpawnerState.class);
    public static final EnumProperty<VaultState> VAULT_STATE = EnumProperty.create("vault_state", VaultState.class);
}
