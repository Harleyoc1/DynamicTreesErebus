package com.harleyoconnor.dynamictreeserebus;

import com.ferreusveritas.dynamictrees.ModItems;
import com.ferreusveritas.dynamictrees.ModRecipes;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.WorldGenRegistry.BiomeDataBasePopulatorRegistryEvent;
import com.ferreusveritas.dynamictrees.api.client.ModelHelper;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.blocks.BlockSurfaceRoot;
import com.ferreusveritas.dynamictrees.blocks.LeavesPaging;
import com.ferreusveritas.dynamictrees.blocks.LeavesProperties;
import com.ferreusveritas.dynamictrees.items.DendroPotion.DendroPotionType;
import com.ferreusveritas.dynamictrees.systems.DirtHelper;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import com.harleyoconnor.dynamictreeserebus.trees.*;
import com.harleyoconnor.dynamictreeserebus.worldgen.BiomeDataBasePopulator;
import com.harleyoconnor.dynamictreeserebus.worldgen.BiomeDataBasePopulatorOld;
import erebus.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class to manage addon content.
 *
 * @author Harley O'Connor
 */
@Mod.EventBusSubscriber(modid = AddonConstants.MOD_ID)
@ObjectHolder(AddonConstants.MOD_ID)
public final class AddonContent {

	public static BlockSurfaceRoot asperRoot;

	public static ILeavesProperties asperLeavesProperties, mossbarkLeavesProperties, cypressLeavesProperties, mahoganyLeavesProperties, eucalyptusLeavesProperties, balsamLeavesProperties;

	public static ArrayList<TreeFamily> trees = new ArrayList<TreeFamily>();

	@SubscribeEvent
	public static void registerDataBasePopulators(final BiomeDataBasePopulatorRegistryEvent event) {
		event.register(new BiomeDataBasePopulator());
	}

	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();

		asperLeavesProperties = setUpLeaves(TreeAsper.primitiveLeaves, "dynamictreeserebus:asper");
		mossbarkLeavesProperties = setUpLeaves(TreeMossbark.primitiveLeaves, "dynamictreeserebus:mossbark");
		cypressLeavesProperties = setUpLeaves(TreeCypress.primitiveLeaves, "dynamictreeserebus:cypress");
		mahoganyLeavesProperties = setUpLeaves(TreeMahogany.primitiveLeaves, "deciduous");
		eucalyptusLeavesProperties = setUpLeaves(TreeEucalyptus.primitiveLeaves, "acacia");
		balsamLeavesProperties = setUpLeaves(TreeBalsam.primitiveLeaves, "acacia");

		LeavesPaging.getLeavesBlockForSequence(AddonConstants.MOD_ID, 0, asperLeavesProperties);
		LeavesPaging.getLeavesBlockForSequence(AddonConstants.MOD_ID, 1, mossbarkLeavesProperties);
		LeavesPaging.getLeavesBlockForSequence(AddonConstants.MOD_ID, 2, cypressLeavesProperties);
		LeavesPaging.getLeavesBlockForSequence(AddonConstants.MOD_ID, 3, mahoganyLeavesProperties);
		LeavesPaging.getLeavesBlockForSequence(AddonConstants.MOD_ID, 4, eucalyptusLeavesProperties);
		LeavesPaging.getLeavesBlockForSequence(AddonConstants.MOD_ID, 5, balsamLeavesProperties);

		TreeFamily asperTree = new TreeAsper();
		TreeFamily mossbarkTree = new TreeMossbark();
		TreeFamily cypressTree = new TreeCypress();
		TreeFamily mahoganyTree = new TreeMahogany();
		TreeFamily eucalyptusTree = new TreeEucalyptus();
		TreeFamily balsamTree = new TreeBalsam();
		Collections.addAll(trees, asperTree, mossbarkTree, cypressTree, mahoganyTree, eucalyptusTree, balsamTree);

		trees.forEach(tree -> tree.registerSpecies(Species.REGISTRY));
		ArrayList<Block> treeBlocks = new ArrayList<>();
		trees.forEach(tree -> tree.getRegisterableBlocks(treeBlocks));
		treeBlocks.addAll(LeavesPaging.getLeavesMapForModId(AddonConstants.MOD_ID).values());
		registry.registerAll(treeBlocks.toArray(new Block[treeBlocks.size()]));

		DirtHelper.registerSoil(ModBlocks.MUD, DirtHelper.MUDLIKE);
	}

	public static ILeavesProperties setUpLeaves (Block leavesBlock, String cellKit){
		return new LeavesProperties(
				leavesBlock.getDefaultState(),
				new ItemStack(leavesBlock, 1, 0),
				TreeRegistry.findCellKit(cellKit))
		{
			@Override public ItemStack getPrimitiveLeavesItemStack() {
				return new ItemStack(leavesBlock, 1, 0);
			}

			@Override
			public int getLightRequirement() {
				return 0;
			}
		};
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();

		ArrayList<Item> treeItems = new ArrayList<>();
		trees.forEach(tree -> tree.getRegisterableItems(treeItems));
		registry.registerAll(treeItems.toArray(new Item[treeItems.size()]));
	}

	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		setUpSeedRecipes("asper", new ItemStack(Block.getBlockFromName("erebus:sapling_asper"), 1, 0));
		setUpSeedRecipes("mossbark", new ItemStack(Block.getBlockFromName("erebus:sapling_mossbark"), 1, 0));
		setUpSeedRecipes("cypress", new ItemStack(Block.getBlockFromName("erebus:sapling_cypress"), 1, 0));
		setUpSeedRecipes("mahogany", new ItemStack(Block.getBlockFromName("erebus:sapling_mahogany"), 1, 0));
		setUpSeedRecipes("eucalyptus", new ItemStack(Block.getBlockFromName("erebus:sapling_eucalyptus"), 1, 0));
		setUpSeedRecipes("balsam", new ItemStack(Block.getBlockFromName("erebus:sapling_balsam"), 1, 0));
	}

	public static void setUpSeedRecipes (String name, ItemStack treeSapling){
		Species treeSpecies = TreeRegistry.findSpecies(new ResourceLocation(AddonConstants.MOD_ID, name));
		ItemStack treeSeed = treeSpecies.getSeedStack(1);
		ItemStack treeTransformationPotion = ModItems.dendroPotion.setTargetTree(new ItemStack(ModItems.dendroPotion, 1, DendroPotionType.TRANSFORM.getIndex()), treeSpecies.getFamily());
		BrewingRecipeRegistry.addRecipe(new ItemStack(ModItems.dendroPotion, 1, DendroPotionType.TRANSFORM.getIndex()), treeSeed, treeTransformationPotion);
		ModRecipes.createDirtBucketExchangeRecipes(treeSapling, treeSeed, true);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		for (TreeFamily tree : trees) {
			ModelHelper.regModel(tree.getDynamicBranch());
			ModelHelper.regModel(tree.getCommonSpecies().getSeed());
			ModelHelper.regModel(tree);
		}
		LeavesPaging.getLeavesMapForModId(AddonConstants.MOD_ID).forEach((key, leaves) -> ModelLoader.setCustomStateMapper(leaves, new StateMap.Builder().ignore(BlockLeaves.DECAYABLE).build()));
	}
}
