package be.nerosro.elemancy.items;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.element.ElemancyElementKeys;
import be.nerosro.elemancy.items.robes.ElemancyArmorMaterials;
import be.nerosro.elemancy.items.tome.TomeItem;
import be.nerosro.elemancy.items.tome.TomeTooltip;
import be.nerosro.elemancy.items.tools.darkbucket.DarkBucketContents;
import be.nerosro.elemancy.items.tools.darkbucket.DarkBucketItem;
import be.nerosro.elemancy.items.tools.darkbucket.DarkBucketTooltip;
import be.nerosro.elemancy.items.trinket.ManaStatTrinketItem;
import be.nerosro.elemancy.items.wands.WandAspect;
import be.nerosro.elemancy.items.wands.WandItem;
import be.nerosro.soulmark.element.Element;
import be.nerosro.soulmark.element.ElementRegistry;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ElemancyItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Elemancy.MOD_ID);

    // Use registerSimpleItem/registerItem so NeoForge can attach the resource key to Item.Properties.
    public static final DeferredItem<Item> ENERGIZED_STICK = ITEMS.registerItem(
        "energized_stick",
        props -> new WandItem(props.durability(10), WandAspect.NONE)
    );

    public static final DeferredItem<Item> INFUSED_INGOT = ITEMS.registerSimpleItem("infused_ingot");

    public static final DeferredItem<Item> PROPOLIS = ITEMS.registerSimpleItem("propolis");

    public static final DeferredItem<Item> TOME = ITEMS.registerItem(
        "tome",
        TomeItem::new,
        props -> props.stacksTo(1)
            .component(ElemancyDataComponents.TOME_TOOLTIP.get(), TomeTooltip.INSTANCE)
    );

    public static final DeferredItem<Item> AFFINITY_PAPER = ITEMS.registerItem(
        "affinity_paper",
        AffinityPaperItem::new,
        props -> props.stacksTo(1)
    );

    public static final DeferredItem<Item> ASHEN_STICK = ITEMS.registerSimpleItem("ashen_stick");

    public static final DeferredItem<Item> ASHEN_WAND = ITEMS.registerItem(
        "ashen_wand",
        props -> new WandItem(props.durability(256), WandAspect.ALL)
    );

    public static final DeferredItem<Item> ARCANE_VESSEL = ITEMS.registerSimpleItem(
        "arcane_vessel",
        props -> props.stacksTo(1)
    );

    public static final DeferredItem<Item> SOULVIAL = ITEMS.registerItem(
        "soulvial",
        Item::new,
        props -> props.stacksTo(1)
    );

    // ==================== SOULVIALS ====================
    // Attuned Soulvials are separate items so recipes can require a specific element. The base
    // Soulvial remains the unattuned ritual catalyst and preserves its existing registry ID.

    private static final Map<String, DeferredItem<Item>> ATTUNED_SOULVIALS_BY_KEY = new LinkedHashMap<>();

    static {
        for (String key : ElemancyElementKeys.BASE_ELEMENT_KEYS) {
            ATTUNED_SOULVIALS_BY_KEY.put(key, ITEMS.registerItem(
                "soulvial_" + key,
                Item::new,
                props -> props.stacksTo(1)
            ));
        }
    }

    /**
     * Returns the attuned Soulvial matching the given base element, or null otherwise.
     */
    public static DeferredItem<Item> getAttunedSoulvial(Element element) {
        Identifier id = ElementRegistry.ELEMENT_REGISTRY.getKey(element);
        String key = id != null ? id.getPath() : "unknown";
        return ATTUNED_SOULVIALS_BY_KEY.get(key);
    }

    public static boolean isSoulvial(Item item) {
        return item == SOULVIAL.get() || ATTUNED_SOULVIALS_BY_KEY.values().stream()
            .anyMatch(soulvial -> soulvial.get() == item);
    }

    public static boolean isUnattunedSoulvial(Item item) {
        return item == SOULVIAL.get();
    }

    public static boolean isAttunedSoulvial(Item item) {
        return ATTUNED_SOULVIALS_BY_KEY.values().stream().anyMatch(soulvial -> soulvial.get() == item);
    }

    // ==================== ELEMETAL INGOTS ====================
    // One item per base element (Fire/Water/Earth/Air/Light/Dark), not a single item with
    // hidden state - see Attunement.md's "Elemetal Items" section. All 6 share the exact same
    // displayed name ("Elemetal Ingot").

    private static final Map<String, DeferredItem<Item>> ELEMETAL_INGOTS_BY_KEY = new LinkedHashMap<>();

    static {
        for (String key : ElemancyElementKeys.BASE_ELEMENT_KEYS) {
            ELEMETAL_INGOTS_BY_KEY.put(key, ITEMS.registerSimpleItem("elemetal_ingot_" + key));
        }
    }

    /**
     * Returns the Elemetal Ingot matching the given element, or null if not a base element.
     */
    public static DeferredItem<Item> getElemetalIngot(Element element) {
        Identifier id = ElementRegistry.ELEMENT_REGISTRY.getKey(element);
        String key = id != null ? id.getPath() : "unknown";
        return ELEMETAL_INGOTS_BY_KEY.get(key);
    }

    /**
     * All registered Elemetal Ingots, keyed by their element registry key string - for datagen looping without needing to resolve Element objects.
     */
    public static Map<String, DeferredItem<Item>> getElemetalIngotsByKey() {
        return ELEMETAL_INGOTS_BY_KEY;
    }

    // ==================== INFUSED ROBES ====================

    public static final DeferredItem<Item> ROBE_HELMET = ITEMS.registerItem(
        "robe_helmet", props -> new Item(robeProperties(props, ArmorType.HELMET))
    );

    public static final DeferredItem<Item> ROBE_CHESTPLATE = ITEMS.registerItem(
        "robe_chestplate", props -> new Item(robeProperties(props, ArmorType.CHESTPLATE))
    );

    public static final DeferredItem<Item> ROBE_LEGGINGS = ITEMS.registerItem(
        "robe_leggings", props -> new Item(robeProperties(props, ArmorType.LEGGINGS))
    );

    public static final DeferredItem<Item> ROBE_BOOTS = ITEMS.registerItem(
        "robe_boots", props -> new Item(robeProperties(props, ArmorType.BOOTS))
    );

    // ==================== INFUSED TOOLS ====================

    public static final DeferredItem<Item> INFUSED_PICKAXE = ITEMS.registerItem(
        "infused_pickaxe", props -> new NonEnchantableItem(infusedPickaxeProperties(props))
    );

    public static final DeferredItem<Item> DARK_BUCKET = ITEMS.registerItem(
        "dark_bucket",
        DarkBucketItem::new,
        props -> props.stacksTo(1)
            .component(DataComponents.CUSTOM_MODEL_DATA, DarkBucketContents.DEFAULT_MODEL_DATA)
            .component(ElemancyDataComponents.DARK_BUCKET_TOOLTIP.get(), DarkBucketTooltip.INSTANCE)
            .component(DataComponents.TOOLTIP_DISPLAY,
                TooltipDisplay.DEFAULT.withHidden(DataComponents.CONTAINER, true))
    );

    // ==================== CONSUMABLES ====================

    public static final DeferredItem<Item> ICECREAM_COCOA = ITEMS.registerItem(
        "icecream_cocoa",
        IceCreamItem::new,
        props -> props.food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3f).alwaysEdible().build())
    );

    // ==================== TIER 0 TRINKETS ====================

    public static final DeferredItem<Item> AMULET_OF_DEEP_FOCUS = ITEMS.registerItem(
        "amulet_of_deep_focus",
        props -> new ManaStatTrinketItem(props, ManaStatTrinketItem.ManaModifierType.COST_REDUCTION, 0.05f)
    );

    public static final DeferredItem<Item> CHARM_OF_STEADY_FLOW = ITEMS.registerItem(
        "charm_of_steady_flow",
        props -> new ManaStatTrinketItem(props, ManaStatTrinketItem.ManaModifierType.REGEN_BOOST, 0.05f)
    );

    public static final DeferredItem<Item> BRACELET_OF_ENDURING_MANA = ITEMS.registerItem(
        "bracelet_of_enduring_mana",
        props -> new ManaStatTrinketItem(props, ManaStatTrinketItem.ManaModifierType.POOL_BOOST, 0.05f)
    );

    public static final DeferredItem<Item> BELT_OF_ROLLING_TIDES = ITEMS.registerItem(
        "belt_of_rolling_tides",
        props -> new ManaStatTrinketItem(props, ManaStatTrinketItem.ManaModifierType.REGEN_BOOST, 0.03f)
    );

    public static final DeferredItem<Item> NECKLACE_OF_SUNKEN_RESERVES = ITEMS.registerItem(
        "necklace_of_sunken_reserves",
        props -> new ManaStatTrinketItem(props, ManaStatTrinketItem.ManaModifierType.POOL_BOOST, 0.03f)
    );

    public static final DeferredItem<Item> GAUNTLET_OF_SUBTLE_WEAVE = ITEMS.registerItem(
        "gauntlet_of_subtle_weave",
        props -> new ManaStatTrinketItem(props, ManaStatTrinketItem.ManaModifierType.COST_REDUCTION, 0.03f)
    );

    /**
     * Builds properties for an Infused Robe piece.
     * Mirrors {@code humanoidArmor()} but omits enchantable and repairable —
     * robes are temporary Stage 0 support gear, not worth enchanting or repairing.
     */
    private static Item.Properties robeProperties(Item.Properties props, ArmorType type) {
        ArmorMaterial material = ElemancyArmorMaterials.INFUSED_WOOL;
        return props
            .durability(type.getDurability(material.durability()))
            .attributes(material.createAttributes(type))
            .component(DataComponents.EQUIPPABLE,
                Equippable.builder(type.getSlot())
                    .setEquipSound(material.equipSound())
                    .setAsset(material.assetId())
                    .build()
            );
    }

    /**
     * Builds properties for the Infused Pickaxe.
     * Mirrors {@code Item.Properties.pickaxe()} but omits enchantable and repairable —
     * the pickaxe is a temporary Stage 0 tool, not worth investing enchantments into.
     */
    private static Item.Properties infusedPickaxeProperties(Item.Properties props) {
        ToolMaterial material = ElemancyToolMaterials.INFUSED_METAL;
        HolderGetter<Block> blockLookup = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        return props
            .durability(material.durability())
            .component(DataComponents.TOOL, new Tool(
                List.of(
                    Tool.Rule.deniesDrops(blockLookup.getOrThrow(material.incorrectBlocksForDrops())),
                    Tool.Rule.minesAndDrops(blockLookup.getOrThrow(BlockTags.MINEABLE_WITH_PICKAXE), material.speed())
                ),
                1.0F, 1, true
            ))
            .attributes(ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID,
                        1.0F + material.attackDamageBonus(),
                        AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                    new AttributeModifier(Item.BASE_ATTACK_SPEED_ID,
                        -2.8F,
                        AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND)
                .build()
            )
            .component(DataComponents.WEAPON, new Weapon(2, 0.0F));
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
