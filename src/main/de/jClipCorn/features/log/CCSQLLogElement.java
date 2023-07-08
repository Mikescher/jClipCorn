package de.jClipCorn.features.log;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCTime;
import de.jClipCorn.util.sqlwrapper.StatementType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CCSQLLogElement {
	public final String        method;
	public final StatementType statementType;
	public final String        source;
	public final CCTime        time;
	public final long          startMillis;
	public final long          endMillis;
	public final String        error;

	public CCSQLLogElement(String mth, StatementType st, String sql, long startMillis, long endMillis, String error) {
		this.method        = mth;
		this.statementType = st;
		this.source        = sql;

		this.startMillis   = startMillis;
		this.endMillis     = endMillis;
		this.error         = error;

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
				.append(startMillis, that.startMillis)
				.append(endMillis, that.endMillis)
				.append(error, that.error)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(method)
				.append(statementType)
				.append(source)
				.append(time)
				.append(startMillis)
				.append(endMillis)
				.append(error)
				.toHashCode();
	}

	public String getFormatted() {
		if (error != null)
		{
			return "{ERR} ["+method+"] " + statementType; //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			return "{OK}  ["+method+"] " + statementType; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
