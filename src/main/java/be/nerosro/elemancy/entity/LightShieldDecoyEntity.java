package be.nerosro.elemancy.entity;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Temporary nonphysical placeholder for a Light Shield projection.
 */
public final class LightShieldDecoyEntity extends Avatar {

    public static final int LIFETIME_TICKS = 100;
    private static final int MAX_ACTIVE_DECOYS = 2;
    private static final Map<UUID, Deque<LightShieldDecoyEntity>> ACTIVE_DECOYS = new HashMap<>();
    private static final EntityDataAccessor<String> OWNER_ID = define(EntityDataSerializers.STRING);
    private static final EntityDataAccessor<ResolvableProfile> PROFILE = define(EntityDataSerializers.RESOLVABLE_PROFILE);
    private static final EntityDataAccessor<ItemStack> MAIN_HAND_ITEM = define(EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> OFF_HAND_ITEM = define(EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> HEAD_ITEM = define(EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> CHEST_ITEM = define(EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> LEGS_ITEM = define(EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> FEET_ITEM = define(EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> BLOCKING_WITH_OFF_HAND = define(EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> BODY_YAW = define(EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEAD_YAW = define(EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> PITCH = define(EntityDataSerializers.FLOAT);

    private static <T> EntityDataAccessor<T> define(EntityDataSerializer<T> serializer) {
        return SynchedEntityData.defineId(LightShieldDecoyEntity.class, serializer);
    }

    public LightShieldDecoyEntity(EntityType<? extends LightShieldDecoyEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
    }

    public static LightShieldDecoyEntity spawn(ServerLevel level, Player owner, InteractionHand blockingHand) {
        Deque<LightShieldDecoyEntity> activeDecoys = ACTIVE_DECOYS.computeIfAbsent(owner.getUUID(), ignored -> new ArrayDeque<>());
        activeDecoys.removeIf(LightShieldDecoyEntity::isRemoved);
        while (activeDecoys.size() >= MAX_ACTIVE_DECOYS) {
            LightShieldDecoyEntity oldestDecoy = activeDecoys.removeFirst();
            oldestDecoy.discard();
        }

        LightShieldDecoyEntity decoy = new LightShieldDecoyEntity(EntityTypes.LIGHT_SHIELD_DECOY.get(), level);
        decoy.setOwnerId(owner.getUUID());
        decoy.setPos(owner.position());
        decoy.setYRot(owner.getYRot());
        decoy.setYHeadRot(owner.getYHeadRot());
        decoy.setXRot(owner.getXRot());
        decoy.setPose(owner.getPose());
        decoy.setMainArm(owner.getMainArm());
        decoy.captureAppearance(owner, blockingHand);
        level.addFreshEntity(decoy);
        activeDecoys.addLast(decoy);
        return decoy;
    }

    public UUID getOwnerId() {
        String ownerId = entityData.get(OWNER_ID);
        return ownerId.isEmpty() ? null : UUID.fromString(ownerId);
    }

    @Override
    public ResolvableProfile getProfile() {
        return entityData.get(PROFILE);
    }

    public ItemStack getSnapshotItem(EquipmentSlot slot) {
        return switch (slot) {
            case MAINHAND -> entityData.get(MAIN_HAND_ITEM);
            case OFFHAND -> entityData.get(OFF_HAND_ITEM);
            case HEAD -> entityData.get(HEAD_ITEM);
            case CHEST -> entityData.get(CHEST_ITEM);
            case LEGS -> entityData.get(LEGS_ITEM);
            case FEET -> entityData.get(FEET_ITEM);
            default -> ItemStack.EMPTY;
        };
    }

    public InteractionHand getBlockingHand() {
        return entityData.get(BLOCKING_WITH_OFF_HAND) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

    @Override
    public ItemStack getItemInHand(InteractionHand hand) {
        return getSnapshotItem(hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return getSnapshotItem(slot);
    }

    @Override
    public boolean isUsingItem() {
        return true;
    }

    @Override
    public InteractionHand getUsedItemHand() {
        return getBlockingHand();
    }

    @Override
    public ItemStack getUseItem() {
        return getItemInHand(getBlockingHand());
    }

    @Override
    public int getUseItemRemainingTicks() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isModelPartShown(net.minecraft.world.entity.player.PlayerModelPart part) {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide() && tickCount >= LIFETIME_TICKS) {
            unregister();
            discard();
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(OWNER_ID, "");
        builder.define(PROFILE, ResolvableProfile.createUnresolved("Light Shield Decoy"));
        builder.define(MAIN_HAND_ITEM, ItemStack.EMPTY);
        builder.define(OFF_HAND_ITEM, ItemStack.EMPTY);
        builder.define(HEAD_ITEM, ItemStack.EMPTY);
        builder.define(CHEST_ITEM, ItemStack.EMPTY);
        builder.define(LEGS_ITEM, ItemStack.EMPTY);
        builder.define(FEET_ITEM, ItemStack.EMPTY);
        builder.define(BLOCKING_WITH_OFF_HAND, false);
        builder.define(BODY_YAW, 0.0F);
        builder.define(HEAD_YAW, 0.0F);
        builder.define(PITCH, 0.0F);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
    }

    private void unregister() {
        UUID ownerId = getOwnerId();
        if (ownerId == null) {
            return;
        }
        Deque<LightShieldDecoyEntity> activeDecoys = ACTIVE_DECOYS.get(ownerId);
        if (activeDecoys == null) {
            return;
        }
        activeDecoys.remove(this);
        if (activeDecoys.isEmpty()) {
            ACTIVE_DECOYS.remove(ownerId);
        }
    }

    private void setOwnerId(UUID ownerId) {
        entityData.set(OWNER_ID, ownerId.toString());
    }

    private void captureAppearance(Player owner, InteractionHand blockingHand) {
        setCustomName(owner.getName());
        setCustomNameVisible(true);
        entityData.set(PROFILE, ResolvableProfile.createResolved(owner.getGameProfile()));
        entityData.set(MAIN_HAND_ITEM, owner.getMainHandItem().copy());
        entityData.set(OFF_HAND_ITEM, owner.getOffhandItem().copy());
        entityData.set(HEAD_ITEM, owner.getItemBySlot(EquipmentSlot.HEAD).copy());
        entityData.set(CHEST_ITEM, owner.getItemBySlot(EquipmentSlot.CHEST).copy());
        entityData.set(LEGS_ITEM, owner.getItemBySlot(EquipmentSlot.LEGS).copy());
        entityData.set(FEET_ITEM, owner.getItemBySlot(EquipmentSlot.FEET).copy());
        entityData.set(BLOCKING_WITH_OFF_HAND, blockingHand == InteractionHand.OFF_HAND);
        entityData.set(BODY_YAW, owner.getYRot());
        entityData.set(HEAD_YAW, owner.getYHeadRot());
        entityData.set(PITCH, owner.getXRot());
    }
}