package de.jClipCorn.properties.types;

import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.stream.CCStreams;

import java.util.*;

public class OrderedEnumSet<T extends ContinoousEnum<T>> {
	public final List<T> Order;
	private final Set<T> enabled;

	public OrderedEnumSet(List<T> order, Collection<T> enabled) {
		this.Order   = Collections.unmodifiableList(new ArrayList<>(order));
		this.enabled = new HashSet<>(enabled);
	}

	public boolean isEnabled(T value) {
		return enabled.contains(value);
	}

	public List<T> getEnabledInOrder() {
		return CCStreams.iterate(Order).filter(enabled::contains).toList();
	}

	@SuppressWarnings("nls")
	public String serialize() {
		return CCStreams.iterate(Order).stringjoin(v -> (enabled.contains(v) ? "+" : "-") + v.asInt(), ";");
	}

	/**
	 * Format: "+id;-id;..." in display order, '+' = enabled.
	 * The older format "id;id;..." only lists the enabled values and carries no order.
	 * Values that are missing in data get the position and state they have in fallback.
	 */
	@SuppressWarnings("nls")
	public static <T extends ContinoousEnum<T>> Opt<OrderedEnumSet<T>> parse(EnumWrapper<T> source, String data, OrderedEnumSet<T> fallback) {
		var tokens = CCStreams.iterate(data.split(";")).filter(t -> !t.isEmpty()).toList();

		try {
			if (CCStreams.iterate(tokens).all(t -> Character.isDigit(t.charAt(0)))) {
				var enabled = new HashSet<T>();
				for (var t : tokens) {
					var v = source.findOrNull(Integer.parseInt(t));
					if (v != null) enabled.add(v);
				}
				return Opt.of(new OrderedEnumSet<>(fallback.Order, enabled));
			}

			var order = new ArrayList<T>();
			var enabled = new HashSet<T>();
			for (var t : tokens) {
				if (t.charAt(0) != '+' && t.charAt(0) != '-') return Opt.empty();

				var v = source.findOrNull(Integer.parseInt(t.substring(1)));
				if (v == null || order.contains(v)) continue;

				order.add(v);
				if (t.charAt(0) == '+') enabled.add(v);
			}

			for (var i = 0; i < fallback.Order.size(); i++) {
				var v = fallback.Order.get(i);
				if (order.contains(v)) continue;

				order.add(Math.min(i, order.size()), v);
				if (fallback.isEnabled(v)) enabled.add(v);
			}

			return Opt.of(new OrderedEnumSet<>(order, enabled));
		} catch (NumberFormatException e) {
			return Opt.empty();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof OrderedEnumSet<?> other)) return false;
		return Order.equals(other.Order) && enabled.equals(other.enabled);
	}

	@Override
	public int hashCode() {
		return Objects.hash(Order, enabled);
	}

	@Override
	public String toString() {
		return serialize();
	}
}
