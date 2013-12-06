package net.coasterman10.Annihilation.maps;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class VoidGenerator extends ChunkGenerator {
	@Override
	public Location getFixedSpawnLocation(World world, Random rand) {
		return new Location(world, 0, world.getHighestBlockYAt(0, 0), 0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public byte[] generate(World world, Random rand, int chunkX, int chunkZ) {
		byte[] chunk = new byte[16 * 16 * 128];

		if ((chunkX == 0) && (chunkZ == 0)) {
			chunk[0] = (byte) Material.BEDROCK.getId();
		}

		return chunk;
	}
}
