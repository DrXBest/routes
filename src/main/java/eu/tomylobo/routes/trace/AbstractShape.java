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
import org.bukkit.util.Vector;

public abstract class AbstractShape implements Shape {

	public AbstractShape() {
		super();
	}

	@Override
	public TraceResult trace(Location location) {
		return trace(location.toVector(), location.getDirection());
	}

	@Override
	public TraceResult traceToPoint(Vector start, Vector end) {
		return trace(start, end.clone().subtract(start));
	}

}