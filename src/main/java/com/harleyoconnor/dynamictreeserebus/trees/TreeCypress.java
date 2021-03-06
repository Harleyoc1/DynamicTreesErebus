package com.harleyoconnor.dynamictreeserebus.trees;

import com.ferreusveritas.dynamictrees.systems.DirtHelper;
import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import com.harleyoconnor.dynamictreeserebus.AddonConstants;
import com.harleyoconnor.dynamictreeserebus.AddonContent;
import com.harleyoconnor.dynamictreeserebus.util.NumberUtils;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;

/**
 * Cypress tree class.
 *
 * @author Harley O'Connor
 */
public final class TreeCypress extends TreeFamily {

    public static final Block primitiveLog = Block.getBlockFromName("erebus:log_cypress");
    public static final Block primitiveLeaves = Block.getBlockFromName("erebus:leaves_cypress");

    public static final class SpeciesCypress extends Species {

        public SpeciesCypress(TreeFamily treeFamily) {
            super(treeFamily.getName(), treeFamily, AddonContent.cypressLeavesProperties);

            // Set growing parameters.
            this.setBasicGrowingParameters(0.1f, 20.0f, 30, 2, 1.1f);

            // Set environment factors.
            this.envFactor(BiomeDictionary.Type.HOT, 0.5F);
            this.envFactor(BiomeDictionary.Type.DRY, 0.25F);
            this.envFactor(BiomeDictionary.Type.WET, 0.75F);

            // Setup seeds.
            this.generateSeed();
            this.setupStandardSeedDropping();
            addAcceptableSoils(DirtHelper.MUDLIKE);
        }

        @Override
        protected int[] customDirectionManipulation(World world, BlockPos pos, int radius, GrowSignal signal, int probMap[]) {
            probMap = super.customDirectionManipulation(world, pos, radius, signal, probMap); // Get default prob map.

            final int signalHeight = (pos.getY() - signal.rootPos.getY()); // Get height of signal.
            probMap[EnumFacing.DOWN.getIndex()] = 0; // Disallow growing downwards.

            // Allow chance of branching off.
            for (EnumFacing dir : EnumFacing.HORIZONTALS) probMap[dir.getIndex()] = NumberUtils.getRandomIntBetween(1, 15) == 1 ? 100 : 0;

            if (signal.numTurns > 0) {
                // Allow branches to grow further.
                if (signal.numTurns >= 2 && signalHeight > 8 && NumberUtils.getRandomIntBetween(1, 6) != 1) for (EnumFacing dir : EnumFacing.HORIZONTALS) probMap[dir.getIndex()] = 0;
                probMap[EnumFacing.UP.getIndex()] = 0;
            }

            probMap[signal.dir.getOpposite().getIndex()] = 0; // Disable the direction we came from.

            return probMap;
        }

        @Override
        public float getEnergy(World world, BlockPos pos) {
            long day = world.getWorldTime() / 24000L;
            int month = (int) day / 30; // Change the hashs every in-game month

            return super.getEnergy(world, pos) * biomeSuitability(world, pos) + (CoordUtils.coordHashCode(pos.up(month), 3) % 4); // Vary the height energy by a psuedorandom hash function
        }

    }

    public TreeCypress() {
        super(new ResourceLocation(AddonConstants.MOD_ID, "cypress"));

        this.setPrimitiveLog(primitiveLog.getDefaultState(), new ItemStack(primitiveLog, 1, 0));
        AddonContent.cypressLeavesProperties.setTree(this);

        this.addConnectableVanillaLeaves(((state) -> state.getBlock() == primitiveLeaves));
    }

    @Override
    public void createSpecies() {
        this.setCommonSpecies(new SpeciesCypress(this));
    }

    @Override
    public ItemStack getPrimitiveLogItemStack(int qty) {
        return new ItemStack(primitiveLog, qty, 0);
    }

}
