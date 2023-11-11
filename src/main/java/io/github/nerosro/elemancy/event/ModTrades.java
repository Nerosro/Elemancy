package io.github.nerosro.elemancy.event;

import io.github.nerosro.elemancy.Elemancy;
import io.github.nerosro.elemancy.block.ModBlocks;
import io.github.nerosro.elemancy.item.ModItems;
import io.github.nerosro.elemancy.villager.ModVillagers;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * Created by Nerosro on 10/11/2023.
 */
@Mod.EventBusSubscriber(modid = Elemancy.MOD_ID)
public class ModTrades {
    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event){
        if(event.getType() == VillagerProfession.FARMER){
            //Farmer trades
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            //level 1 trade
            trades.get(1).add(
                    (pTrader, pRandom) -> new MerchantOffer(
                            new ItemStack(Items.EMERALD, 1),
                            new ItemStack(ModItems.STRAWBERRY_SEEDS.get(), 5),
                            15,8,0.035f
                    )
            );
        }
        if(event.getType() == ModVillagers.ELEMANCER.get()){
            //Elemancer trades
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            //level 1 trade
            trades.get(1).add(
                    (pTrader, pRandom) -> new MerchantOffer(
                            new ItemStack(Blocks.WHITE_WOOL, 1),
                            new ItemStack(ModBlocks.INFUSED_WOOL.get(), 3),
                            16,8,0.2f
                    )
            );
            trades.get(1).add(
                    (pTrader, pRandom) -> new MerchantOffer(
                            new ItemStack(Items.EMERALD, 1),
                            new ItemStack(ModItems.ICECREAM_COCOA.get(), 1),
                            5,18,0.2f
                    )
            );
        }

        if(event.getType() == VillagerProfession.LIBRARIAN){
            //Librarian trades
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            ItemStack enchantedBook = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.RIPTIDE, 2));
            //level 1 trade
            trades.get(1).add(
                    (pTrader, pRandom) -> new MerchantOffer(
                            new ItemStack(Items.NETHER_STAR, 10),
                            enchantedBook,
                            2,8,0.02f
                    )
            );
        }
    }

    @SubscribeEvent
    public static void addCustomWanderingTrades (WandererTradesEvent event){
        List<VillagerTrades.ItemListing> genericTrades = event.getGenericTrades();
        List<VillagerTrades.ItemListing> rareTrades = event.getRareTrades();

        genericTrades.add(
                (pTrader, pRandom) -> new MerchantOffer(
                        new ItemStack(Blocks.WHITE_WOOL, 3),
                        new ItemStack(ModBlocks.INFUSED_WOOL.get(), 1),
                        2,8,0.2f
                )
        );

        rareTrades.add(
                (pTrader, pRandom) -> new MerchantOffer(
                        new ItemStack(Items.EMERALD, 8),
                        new ItemStack(ModItems.ICECREAM_COCOA.get(), 1),
                        2,18,0.2f
                )
        );
    }
}
