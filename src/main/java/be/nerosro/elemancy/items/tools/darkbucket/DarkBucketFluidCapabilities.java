package be.nerosro.elemancy.items.tools.darkbucket;

import be.nerosro.elemancy.items.ElemancyItems;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Exposes only the selected Dark Bucket compartment for fluid-aware item rendering.
 */
public final class DarkBucketFluidCapabilities {
    private DarkBucketFluidCapabilities() {
    }

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.Fluid.ITEM,
            (stack, _) -> new SelectedFluidHandler(stack),
            ElemancyItems.DARK_BUCKET.get());
    }

    private static final class SelectedFluidHandler implements ResourceHandler<FluidResource> {
        private final ItemStack darkBucket;

        private SelectedFluidHandler(ItemStack darkBucket) {
            this.darkBucket = darkBucket;
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public FluidResource getResource(int index) {
            validateIndex(index);
            var fluid = DarkBucketContents.getSelectedFluid(darkBucket);
            return fluid.isEmpty() ? FluidResource.EMPTY : FluidResource.of(fluid);
        }

        @Override
        public long getAmountAsLong(int index) {
            validateIndex(index);
            return getResource(index).isEmpty() ? 0 : FluidType.BUCKET_VOLUME;
        }

        @Override
        public long getCapacityAsLong(int index, FluidResource resource) {
            validateIndex(index);
            return FluidType.BUCKET_VOLUME;
        }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            validateIndex(index);
            return false;
        }

        @Override
        public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
            validateIndex(index);
            return 0;
        }

        @Override
        public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
            validateIndex(index);
            return 0;
        }

        private static void validateIndex(int index) {
            if (index != 0) {
                throw new IndexOutOfBoundsException(index);
            }
        }
    }
}