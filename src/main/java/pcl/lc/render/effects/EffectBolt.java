package pcl.lc.render.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import pcl.common.util.Vector3;
import net.minecraft.world.World;

public class EffectBolt {

	private class BoltVector3 {
		public final Vector3 point, base, offset;

		public BoltVector3(Vector3 base, Vector3 offset) {
			this.base = base;
			this.offset = offset;
			this.point = this.base.add(offset);
		}
	}

	private class BoltSegment {
		public BoltVector3 start, end;
		public BoltSegment parent, child;
		public Vector3 idiff, pdiff, ndiff;
		public float isin_p, isin_n;
		public float ilight;
		public int n, s;

		public BoltSegment(BoltVector3 start, BoltVector3 end, float ilight, int n, int s) {
			this.start = start;
			this.end = end;
			this.ilight = ilight;
			this.n = n;
			this.s = s;
			this.idiff = end.point.sub(start.point);
		}

		public void vbuild() {
			if (this.parent != null) {
				Vector3 pdn = this.parent.idiff.unitV();
				Vector3 tdn = this.idiff.unitV();
				this.pdiff = tdn.add(pdn).unitV();
				this.isin_p = (float) Math.sin(tdn.anglePNorm(pdn.mul(-1.0D)) / 2.0F);
			} else {
				this.pdiff = this.idiff.unitV();
				this.isin_p = 1.0F;
			}
			
			if (this.child != null) {
				Vector3 ndn = this.child.idiff.unitV();
				Vector3 tdn = this.idiff.unitV();
				this.ndiff = tdn.add(ndn).unitV();
				this.isin_n = (float) Math.sin(tdn.anglePNorm(ndn.mul(-1.0D)) / 2.0F);
			} else {
				this.ndiff = this.idiff.unitV();
				this.isin_n = 1.0F;
			}
		}
	}

	private final ArrayList<BoltSegment> segments = new ArrayList<BoltSegment>();
	private final Vector3 origin, destination, delta;
	private final double length;
	private int ticks, maxTicks;
	private final Random random;
	private final World world;

	public EffectBolt(World world, Vector3 origin, Vector3 destination, Random source) {
		this.world = world;
		this.random = source;
		this.origin = origin;
		this.destination = destination;
		this.delta = this.destination.sub(this.origin);
		this.length = this.delta.mag();

		this.maxTicks = (2 + this.random.nextInt(2));
		this.ticks = (int) (-length * 2.0D);
	}
	
	private void buildAndDiff() {
		
	}

}