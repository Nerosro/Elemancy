package io.github.nerosro.elemancy.item.custom;


import io.github.nerosro.elemancy.mana.PlayerMana;
import io.github.nerosro.elemancy.mana.PlayerManaProvider;
import io.github.nerosro.elemancy.networking.ModMessages;
import io.github.nerosro.elemancy.networking.packet.ManaDataSyncS2CPacket;
import io.github.nerosro.elemancy.recipe.ElemancyInfusingRecipe;
import io.github.nerosro.elemancy.sound.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class WandItem extends Item { //TODO make abstract/interface for different tiers of wands
    private static final int USE_DURATION = 10;
    private static final int MANA_PENALTY = 10;

    public WandItem(Properties properties) {
        super(properties);
        properties.defaultDurability(USE_DURATION);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var itemStack = player.getItemInHand(hand);

        if(hand == InteractionHand.OFF_HAND && !level.isClientSide){ //On server

            itemStack.hurtAndBreak(1, player, event-> event.broadcastBreakEvent(hand)); //TODO Wand takes damage even when not used to infuse
            var itemList = getItemsInArea(player, level);
            //System.out.println(itemList);

            infuse(level, player, itemList);

            return InteractionResultHolder.success(itemStack);
        }
        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);

        components.add(Component.translatable("tooltip.elemancy.energized_stick_tooltip"));
    }

    public List<ItemEntity> getItemsInArea(Player player, Level level){  //TODO improve check area

        var blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY).getBlockPos();
        //System.out.println("blockhitresult: " + blockhitresult);

        var area = new AABB(blockhitresult).inflate(1);
        /*
        System.out.println("Center: " + area.getCenter());
        System.out.println("Size X: " + area.getXsize());
        System.out.println("area: " + area);
        */

        return level.getEntitiesOfClass(ItemEntity.class, area);
    }

    private static CraftingContainer makeContainer(ItemEntity itemEntity) {
        CraftingContainer craftingcontainer = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
            /** Thank you sheep code
             * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the
             * player inventory and the other inventory(s).
             */
            public @NotNull ItemStack quickMoveStack(@NotNull Player player, int p_218265_) {
                return ItemStack.EMPTY;
            }

            /**
             * Determines whether supplied player can use this container
             */
            public boolean stillValid(@NotNull Player player) {
                return false;
            }
        }, 1, 1);
        craftingcontainer.setItem(0, new ItemStack(itemEntity.getItem().getItem()));
        return craftingcontainer;
    }

    private static void infuse(Level level, Player player, List<ItemEntity> itemList) {
        int currentMana = player.getCapability(PlayerManaProvider.PLAYER_MANA).map(PlayerMana::getCurrentMana).get();
        var lock = 0;

        for(var itemEntity : itemList){
            //player.sendSystemMessage(Component.literal(itemEntity.getItem().toString()));                   //5 gold_ingot
            System.out.println(itemEntity.getItem());

            var craftingcontainer = makeContainer(itemEntity);
            Optional<ElemancyInfusingRecipe> recipe = level.getRecipeManager().getRecipeFor(
                    ElemancyInfusingRecipe.Type.INSTANCE, craftingcontainer, level);

            if(recipe.isPresent() && lock != 1) {
                lock = 1;
                var manaUsage = recipe.get().getManaUsage();
                if (currentMana > manaUsage) {
                    itemEntity.getItem().shrink(1);

                    ItemEntity newItem = new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(),
                            recipe.get().getResultItem(level.registryAccess()));

                    newItem.setDefaultPickUpDelay();
                    level.addFreshEntity(newItem);

                    player.getCapability(PlayerManaProvider.PLAYER_MANA)
                            .map(playerMana -> {
                                ModMessages.sendToPlayer(
                                        new ManaDataSyncS2CPacket(
                                                playerMana.getMaxMana(),
                                                playerMana.getRegenAmount(),
                                                playerMana.getElement(),
                                                playerMana.getCurrentMana()
                                        ), (ServerPlayer) player);
                                return playerMana.useMana(manaUsage);
                            });
                    level.playSeededSound(null, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(),
                            ModSounds.INFUSED_WAND_CRAFT_SUCCESS.get(), SoundSource.BLOCKS,1f,1f,0);
                }
            }
            /* console logging
            System.out.println(itemEntity);             //  -> ItemEntity['Beetroot'/145, l='ServerLevel[Elemancy testing world]', x=-2.65, y=-60.00, z=34.69]
            player.sendSystemMessage(Component.literal(itemEntity.getItem().toString()));                   //5 gold_ingot
            player.sendSystemMessage(Component.literal(itemEntity.getItem().getDescriptionId()));           //item.minecraft.gold_ingot
            player.sendSystemMessage(Component.literal(String.valueOf(itemEntity.getItem().getCount())));   //5
            player.sendSystemMessage(Component.literal(itemEntity.getItem().getItem().toString()));         //gold_ingot
            */
        }
    }
}
