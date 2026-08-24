package be.nerosro.elemancy.items;

import java.util.Map;

import com.google.common.collect.Maps;

import be.nerosro.elemancy.Elemancy;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

/**
 * Custom armor materials for Elemancy.
 */
public interface ElemancyArmorMaterials {

    ResourceKey<EquipmentAsset> INFUSED_WOOL_ASSET = ResourceKey.create(
        EquipmentAssets.ROOT_ID,
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "infused_wool")
    );

    /**
     * Infused Wool armor material.
     * Leather durability (multiplier 5), one defense below leather on each piece,
     * not enchantable (0), not repairable, no toughness, no knockback resistance.
     * Uses elytra equip sound for a light fabric feel.
     */
    ArmorMaterial INFUSED_WOOL = new ArmorMaterial(
        5,
        Maps.newEnumMap(Map.of(
            ArmorType.BOOTS, 0,
            ArmorType.LEGGINGS, 1,
            ArmorType.CHESTPLATE, 2,
            ArmorType.HELMET, 0,
            ArmorType.BODY, 0
        )),
        0,
        SoundEvents.ARMOR_EQUIP_ELYTRA,
        0.0F,
        0.0F,
        ItemTags.WOOL,  // Tag exists but repair is disabled via enchantability 0 and no repairable() on properties
        INFUSED_WOOL_ASSET
    );
}
