package io.github.nerosro.elemancy.item.custom;

import com.google.common.collect.ImmutableMap;
import io.github.nerosro.elemancy.item.ModArmorMaterials;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Map;

/**
 * Created by Nerosro on 11/10/2023.
 */
public class ModArmorItem extends ArmorItem {

    private static final Map<ArmorMaterial, MobEffectInstance> MATERIAL_TO_EFFECT_MAP=
            (new ImmutableMap.Builder<ArmorMaterial, MobEffectInstance>())
                    .put(ModArmorMaterials.INFUSED_WOOL, new MobEffectInstance(MobEffects.BLINDNESS, 200, 1,
                            false, true, true)).build();

    public ModArmorItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if(!level.isClientSide()){
            if(hasFullSuitOfArmorOn(player)){
                evaluateArmorEffects(player);
            }
        }
    }

    private boolean hasFullSuitOfArmorOn(Player player) {
        ItemStack boots = player.getInventory().getArmor(0);
        ItemStack leggings = player.getInventory().getArmor(1);
        ItemStack chestpiece = player.getInventory().getArmor(2);
        ItemStack helmet = player.getInventory().getArmor(3);

        return !helmet.isEmpty() && !chestpiece.isEmpty()
                && !leggings.isEmpty() && !boots.isEmpty();
    }

    private void evaluateArmorEffects(Player player) {
        for(Map.Entry<ArmorMaterial, MobEffectInstance> entry : MATERIAL_TO_EFFECT_MAP.entrySet()){
            ArmorMaterial mapArmorMaterial = entry.getKey();
            MobEffectInstance mapStatusEffect = entry.getValue();

            if(hasCorrectArmorOn(mapArmorMaterial, player)){
                addStatusEffectForMaterial(player, mapArmorMaterial, mapStatusEffect);
            }
        }
    }

    private boolean hasCorrectArmorOn(ArmorMaterial material, Player player) {
        for(ItemStack armorStack : player.getInventory().armor){
            if(!(armorStack.getItem() instanceof ArmorItem)){
                return false;
            }
        }

        ArmorItem boots = (ArmorItem) player.getInventory().getArmor(0).getItem();
        ArmorItem leggings = (ArmorItem) player.getInventory().getArmor(1).getItem();
        ArmorItem chestpiece = (ArmorItem) player.getInventory().getArmor(2).getItem();
        ArmorItem helmet = (ArmorItem) player.getInventory().getArmor(3).getItem();

        return helmet.getMaterial() == material && chestpiece.getMaterial() == material
                && leggings.getMaterial() == material && boots.getMaterial() == material;

    }

    private void addStatusEffectForMaterial(Player player, ArmorMaterial material, MobEffectInstance effect) {
        boolean hasPlayerEffect = player.hasEffect(effect.getEffect());

        if(hasCorrectArmorOn(material, player) && !hasPlayerEffect){
            player.addEffect(new MobEffectInstance(effect));
        }
    }


}
