package de.jClipCorn.features.log;

import de.jClipCorn.util.datetime.CCTime;
import de.jClipCorn.util.sqlwrapper.StatementType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CCSQLLogElement {
	private String method;
	private StatementType statementType;
	private String source;
	private CCTime time;

	public CCSQLLogElement(String mth, StatementType st, String sql) {
		method = mth;
		statementType = st;
		source = sql;

		time = new CCTime();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		CCSQLLogElement that = (CCSQLLogElement) o;

		return new EqualsBuilder()
				.append(method, that.method)
				.append(statementType, that.statementType)
				.append(source, that.source)
				.append(time, that.time)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(method)
				.append(statementType)
				.append(source)
				.append(time)
				.toHashCode();
	}

	public String getFormatted() {
		return "["+method+"]" + statementType; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getFullFormatted() {
		return source;
	}
}
