/*
 * Copyright (C) 2012 TomyLobo
 *
 * This file is part of Routes.
 *
 * Routes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.tomylobo.routes.trace;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.util.Vector;

import eu.tomylobo.routes.util.Workarounds;

public class SignShape extends Plane {
	private static final double SIGN_SCALE = 2.0 / 3.0;
	private static final double FONT_SCALE = SIGN_SCALE / 60.0;

	public SignShape(Sign sign) {
		this(getOriginLocation(sign));
	}

	private SignShape(Location originLocation) {
		super(originLocation.toVector(), originLocation.getDirection());
	}

	private static Location getOriginLocation(Sign sign) {
		final Location originLocation = Workarounds.getLocation(sign).add(0.5, 0.75*SIGN_SCALE, 0.5);

		double yOffset = 0.5 * SIGN_SCALE;
		double zOffset = 0.07 * SIGN_SCALE;

		switch (sign.getType()) {
		case SIGN_POST:
			originLocation.setYaw((sign.getRawData() * 360) / 16f);
			break;

		case WALL_SIGN:
			switch (sign.getRawData()) {
			case 2:
				originLocation.setYaw(180);
				break;

			case 4:
				originLocation.setYaw(90);
				break;

			case 5:
				originLocation.setYaw(-90);
				break;

			}

			yOffset -= 0.3125;
			zOffset -= 0.4375;
			break;

		default:
			throw new IllegalArgumentException("Expected a sign, got something else.");
		}

		final Vector normal = originLocation.getDirection();
		originLocation.add(0, yOffset, 0);
		originLocation.add(normal.clone().multiply(zOffset));

		return originLocation;
	}


	@Override
	public SignTraceResult trace(Location location) {
		return augument(super.trace(location));
	}

	@Override
	public SignTraceResult trace(Vector start, Vector direction) {
		return augument(super.trace(start, direction));
	}

	@Override
	public SignTraceResult traceToPoint(Vector start, Vector end) {
		return augument(super.traceToPoint(start, end));
	}

	private SignTraceResult augument(TraceResult trace) {
		final int index = (int) Math.floor(2.1 - trace.relativePosition.getY() / FONT_SCALE / 10.0);

		return new SignTraceResult(trace, index);
	}
}
