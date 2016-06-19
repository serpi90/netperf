package netperf.protocol;

import java.io.Serializable;
import java.util.Formatter;

/**
 * A measurement of the connection speed in kilobytes per millisecond
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 *
 */
public class SpeedMeasurement implements Serializable {
	private static final long serialVersionUID = 3656593104441422136L;
	private long end;
	private long kilobytes;
	private long start;

	/**
	 * @param start
	 *            when the transfer started
	 * @param end
	 *            when the transfer ended
	 * @param kilobytes
	 *            amount of kilobytes transferred
	 */
	public SpeedMeasurement(long start, long end, long kilobytes) {
		this.start = start;
		this.end = end;
		this.kilobytes = kilobytes;
	}

	/**
	 * @return when the transfer ended
	 */
	public long getEnd() {
		return end;
	}

	/**
	 * @return the transferred amount in kilobytes
	 */
	public long getKilobytes() {
		return kilobytes;
	}

	/**
	 * @return the transfer speed in kilobytes per second
	 */
	public float getSpeed() {
		return (float) kilobytes / ((end - start) / 1000);
	}

	/**
	 * @return when the transfer started
	 */
	public long getStart() {
		return start;
	}

	@Override
	public String toString() {
		try (Formatter formatter = new Formatter()) {
			return formatter.format("%.2f kb/s (%d kb / %d ms)", getSpeed(), getKilobytes(), getEnd() - getStart())
					.toString();
		}
	}
}
