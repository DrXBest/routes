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

package eu.tomylobo.routes.sign;

import java.util.Collection;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import eu.tomylobo.routes.util.Ini;

public class TrackedSign {
	private static final String UNMARKED_COLOR = "\u00a79";
	private static final String MARKED_COLOR = "\u00a7c";

	private final Block block;
	private final String[] entries;
	private int selected = -1;
	private final int entryCount;

	public TrackedSign(Sign sign) {
		block = sign.getBlock();
		String[] lines = sign.getLines();
		entries = new String[lines.length];

		int entryCount = 0;
		for (int i = 0; i < lines.length; ++i) {
			final String line = lines[i];

			if (line.startsWith("@@")) {
				entries[i] = line.substring(2);
				setMarked(sign, i, false);
				++entryCount;
			}
		}
		this.entryCount = entryCount;
		if (entryCount == 0)
			throw new IllegalArgumentException("A sign with no entries was passed.");

		sign.update();
	}

	public TrackedSign(Multimap<String, String> section) {
		block = Ini.loadLocation(section, "%s", false).getBlock();

		entries = new String[4];
		int entryCount = 0;
		for (int i = 0; i < 4; ++i) {
			final Collection<String> values = section.get("line"+i);
			if (values.size() != 1)
				continue;

			entries[i] = Ini.getOnlyValue(values);
			++entryCount;
		}
		this.entryCount = entryCount;
		if (entryCount == 0)
			throw new IllegalArgumentException("A sign with no entries was parsed.");
	}

	public Multimap<String, String> save() {
		Multimap<String, String> section = LinkedListMultimap.create();

		Ini.saveLocation(section, "%s", block.getLocation(), false);

		for (int i = 0; i < entries.length; ++i) {
			if (entries[i] == null)
				continue;

			section.put("line"+i, entries[i]);
		}

		return section;
	}

	/**
	 * Retrieves the block the sign is in.
	 *
	 * @return a block
	 */
	public Block getBlock() {
		return block;
	}

	/**
	 * Determines whether the specified index has an entry.
	 * 
	 * @param index The index to look at.
	 * @return true if there is an entry at the index, false if not.
	 */
	public boolean hasEntry(int index) {
		return entries[index] != null;
	}

	/**
	 * Retrieves the specified entry if there is one, otherwise null.
	 *
	 * @param index The index of the entry 0..3
	 * @return The entry, if it is tracked, otherwise null. 
	 */
	public String getEntry(int index) {
		return entries[index];
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TrackedSign))
			return false;

		TrackedSign trackedSign = (TrackedSign) obj;

		return block.equals(trackedSign.block);
	}

	@Override
	public int hashCode() {
		return block.hashCode();
	}

	/**
	 * Selects the specified entry and unmarks the previously selected one.
	 *
	 * @param index The index of the entry
	 */
	public void select(int index) {
		if (selected == index)
			return;

		final Sign sign = (Sign) block.getState();

		if (selected != -1) {
			setMarked(sign, selected, false);
		}
		selected = index;
		if (selected != -1) {
			if (entries[selected] == null) {
				selected = -1;
			}
			else {
				setMarked(sign, selected, true);
			}
		}

		sign.update();
	}

	private void setMarked(final Sign sign, int index, boolean marked) {
		sign.setLine(index, (marked ? MARKED_COLOR : UNMARKED_COLOR) + entries[index]);
	}

	/**
	 * Determines whether the specified entry is selected.
	 *
	 * @param index The index of the entry
	 * @return true if the entry is selected, false if not.
	 */
	public boolean isSelected(int index) {
		return selected == index;
	}

	/**
	 * Returns the number of valid entries.
	 *
	 * @return entry count 1..3
	 */
	public int getEntryCount() {
		return entryCount;
	}
}
