package com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.namelessquotes;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NamelessOneSubtitles {
	private static final String PLACEHOLDER_LINE = "PLACEHOLDER_LINE";
	private final Map<Double, String> map = new HashMap<>();
	private final double duration;
	private NamelessOneQuote quote = null;
	private int placeholderCounter = 1;

	public NamelessOneSubtitles(double duration) {
		this(duration, PLACEHOLDER_LINE + 1);
		this.placeholderCounter++;
	}

	public NamelessOneSubtitles(double duration, String firstLine) {
		this.map.put(0.0, firstLine);
		this.duration = duration;
	}

	public NamelessOneSubtitles add(double time) {
		return this.add(time, PLACEHOLDER_LINE + (this.placeholderCounter++));
	}

	public NamelessOneSubtitles add(double time, String line) {
		this.map.put(time, line);
		return this;
	}

	public double getDuration() {
		return this.duration;
	}

	public void setQuote(NamelessOneQuote quote) {
		this.quote = quote;
	}

	@OnlyIn(Dist.CLIENT)
	public String getLine(double time) {
		String line = "ERROR";
		double bestTime = -1;

		for (Entry<Double, String> entry : this.map.entrySet()) {
			if (entry.getKey() <= time && entry.getKey() > bestTime) {
				line = entry.getValue();
				bestTime = entry.getKey();
			}
		}

		if (line.startsWith(PLACEHOLDER_LINE)) {
			String quoteName = this.quote.getName();
			int lastUnderscoreIndex = quoteName.lastIndexOf("_");
			String formattedName;
			if (lastUnderscoreIndex != -1) {
				String prefix = quoteName.substring(0, lastUnderscoreIndex);
				String number = quoteName.substring(lastUnderscoreIndex + 1);
				formattedName = prefix + "." + number;
			} else {
				formattedName = quoteName;
			}
			String key = "goetyawaken.namelessone.quote." + formattedName;
			return I18n.get(key);
		}

		return line;
	}
}