package pcl.lc.worldgen;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import pcl.common.helpers.ConfigurationHelper;
import pcl.lc.LanteaCraft;
import pcl.lc.LanteaCraft.Blocks;
import pcl.lc.core.OreTypes;
import cpw.mods.fml.common.IWorldGenerator;

public class NaquadahOreWorldGen implements IWorldGenerator {

	static int genUnderLavaOdds = 4;
	static int maxNodesUnderLava = 8;
	static int genIsolatedOdds = 8;
	static int maxIsolatedNodes = 4;

	// TODO: Unpack from config
	private EnumSet<OreTypes> enabledOreSpawns = EnumSet.of(OreTypes.NAQUADAH, OreTypes.TRINIUM);

	public static void configure(ConfigurationHelper cfg) {
		// TODO: Replace me!
		genUnderLavaOdds = cfg.getInteger("naquadah", "genUnderLavaOdds", genUnderLavaOdds);
		maxNodesUnderLava = cfg.getInteger("naquadah", "maxNodesUnderLava", maxNodesUnderLava);
		genIsolatedOdds = cfg.getInteger("naquadah", "genIsolatedOdds", genIsolatedOdds);
		maxIsolatedNodes = cfg.getInteger("naquadah", "maxIsolatedNodes", maxIsolatedNodes);
	}

	public void readChunk(ChunkData data, Chunk chunk) {
		for (OreTypes typeof : enabledOreSpawns) {
			if (!data.getOreGenerated(typeof)) {
				generateChunk(typeof, chunk.worldObj, chunk);
			}
		}
	}

	private Random requestRandomFor(int x, int z, World world, int oreTypeof) {
		return new Random((1 + oreTypeof) * (x + z) ^ world.getSeed());
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
			IChunkProvider chunkProvider) {
		Chunk target = world.getChunkFromChunkCoords(chunkX, chunkZ);
		ChunkData data = ChunkData.forChunk(target);
		for (OreTypes typeof : enabledOreSpawns) {
			if (!data.getOreGenerated(typeof)) {
				generateChunk(typeof, world, target);
			}
		}
	}

	public void regenerate(Chunk chunk) {
		ChunkData data = ChunkData.forChunk(chunk);
		for (OreTypes typeof : enabledOreSpawns) {
			if (!data.getOreGenerated(typeof)) {
				generateChunk(typeof, chunk.worldObj, chunk);
			}
		}
	}

	private int getBlock(Chunk chunk, int x, int y, int z) {
		return chunk.getBlockID(x, y, z);
	}

	private void setBlock(Chunk chunk, int x, int y, int z, int id, int metadata) {
		chunk.setBlockIDWithMetadata(x, y, z, id, metadata);
	}

	void generateNode(Chunk chunk, World world, Random random, int id, int metadata, int cx, int cy, int cz, int density) {
		LanteaCraft.getLogger().log(
				Level.INFO,
				String.format("Node generator building node around %s %s %s with density %s typeof %s", cx
						+ (16 * chunk.getChunkCoordIntPair().chunkXPos), cy, cz
						+ (16 * chunk.getChunkCoordIntPair().chunkZPos), density, metadata));
		int tries = 0;
		main: while (density > 0) {
			int tx = cx, ty = cy, tz = cz;
			expand: while (true) {
				int trans = random.nextInt(6);
				switch (trans) {
				case 0:
					tx++;
					break;
				case 1:
					tx--;
					break;
				case 2:
					ty++;
					break;
				case 3:
					ty--;
					break;
				case 4:
					tz++;
					break;
				case 5:
					tz--;
					break;
				}

				// catch illegal; one vector out of bounds, reset scan
				if (tx > 15 || tz > 15 || 0 > tx || 0 > ty || 0 > tz) {
					LanteaCraft.getLogger().log(Level.INFO, "Can't expand, out of bounds!");
					continue main;
				}
				// expand; this block is already an ore of type id so we can
				// continue our search
				else if (getBlock(chunk, tx, ty, tz) == id) {
					continue expand;
				}
				// legal; we wandered here and it's stone, so we'll place
				// something
				else if (getBlock(chunk, tx, ty, tz) == Block.stone.blockID) {
					LanteaCraft.getLogger().log(Level.INFO, "Done, can expand this way.");
					break expand;
				}
				// illegal; detected some other block (air, other ores, etc),
				// so reset the scan and try again.
				else {
					LanteaCraft.getLogger().log(Level.INFO, "Strange case: " + getBlock(chunk, tx, tz, tz) + ", well?");
					break main;
				}
			}

			if (getBlock(chunk, tx, ty, tz) == Block.stone.blockID) {
				setBlock(chunk, tx, ty, tz, id, metadata);
				density--;
				tries = 0;
				continue main;
			}

			if (tries++ > 5)
				break main;
		}
	}

	private boolean odds(Random random, int n) {
		return random.nextInt(n) == 0;
	}

	void generateChunk(OreTypes typeof, World world, Chunk chunk) {
		Random random = requestRandomFor(chunk.getChunkCoordIntPair().chunkXPos,
				chunk.getChunkCoordIntPair().chunkZPos, world, typeof.ordinal());
		if (odds(random, genIsolatedOdds) || true) {
			int n = 1; // random.nextInt(maxIsolatedNodes) + 1;
			for (int i = 0; i < n; i++) {
				int x = random.nextInt(16);
				int y = random.nextInt(64);
				int z = random.nextInt(16);
				if (getBlock(chunk, x, y, z) == Block.stone.blockID) {
					LanteaCraft.getLogger().log(Level.INFO,
							String.format("Attempting to place Naquadah node at %s %s %s", x, y, z));
					generateNode(chunk, world, random, Blocks.lanteaOre.blockID, typeof.ordinal(), x, y, z, 6);
				}
			}
		}
		ChunkData metadata = ChunkData.forChunk(chunk);
		if (metadata == null)
			throw new RuntimeException("URGH STUPID");
		metadata.markOreGenerated(OreTypes.NAQUADAH);
	}

}
