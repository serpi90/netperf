package netperf.protocol;

/*
 * #%L
 * Protocol
 * %%
 * Copyright (C) 2016 Julián Maestri
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.util.Formatter;

/**
 * A measurement of the connection speed in kilobytes per millisecond
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 * 
 */
public class SpeedMeasurement {
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
			return formatter.format("%.2f kb/s (%d kb / %d ms)", getSpeed(),
					getKilobytes(), getEnd() - getStart()).toString();
		}
	}
}
