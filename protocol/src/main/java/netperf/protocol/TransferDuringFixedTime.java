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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Obtain {@link SpeedMeasurement} sending all possible data during a fixed
 * amount of time. The variable is the transferred data size.
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 * 
 */
public class TransferDuringFixedTime extends TransferCommand {
	private static final byte LAST_MESSAGE = (byte) 0;
	private static final byte NOT_LAST_MESSAGE = (byte) 1;
	private long milliseconds;
	private byte[] kilobyte;

	/**
	 * @param milliseconds
	 *            the time limit when sending data
	 */
	public TransferDuringFixedTime(long milliseconds) {
		this.milliseconds = milliseconds;
		this.kilobyte = new byte[1024];
		for (int i = 0; i < kilobyte.length; i++) {
			kilobyte[i] = (byte) i;
		}
	}

	@Override
	public SpeedMeasurement executeRead(InputStream input) throws IOException {
		long start = System.currentTimeMillis();
		long readKilobytes = 0;
		do {
			readKilobyte(input, kilobyte);
			readKilobytes++;
		} while (kilobyte[0] != LAST_MESSAGE);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < kilobyte.length && i < 20; i++) {
			sb.append(kilobyte[i]);
			sb.append(' ');
		}
		return new SpeedMeasurement(start, start + milliseconds, readKilobytes);
	}

	@Override
	public SpeedMeasurement executeSend(OutputStream output) throws IOException {
		long sentKilobytes = 0;
		long start = System.currentTimeMillis();
		long end = start + milliseconds;
		kilobyte[0] = NOT_LAST_MESSAGE;
		while (System.currentTimeMillis() < end) {
			sendKilobyte(output, kilobyte);
			sentKilobytes++;
		}
		kilobyte[0] = LAST_MESSAGE;
		sendKilobyte(output, kilobyte);
		return new SpeedMeasurement(start, end, sentKilobytes + 1);
	}

	public long getMilliseconds() {
		return milliseconds;
	}

	@Override
	public String toString() {
		return "Transfer during " + milliseconds + " ms";
	}
}
