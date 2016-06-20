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
 * Obtain {@link SpeedMeasurement} sending a fixed amount of data, the measured
 * variable is time.
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 * 
 */
public class TransferWithFixedSize extends TransferCommand {
	private long kilobytesToSend;
	private byte[] kilobyte;

	/**
	 * @param kilobytesToSend
	 *            The amount of random data in kilobytes to transfer
	 */
	public TransferWithFixedSize(long kilobytesToSend) {
		super();
		this.kilobytesToSend = kilobytesToSend;
		this.kilobyte = new byte[1024];
		for (int i = 0; i < kilobyte.length; i++) {
			kilobyte[i] = (byte) i;
		}
	}

	@Override
	public SpeedMeasurement executeRead(InputStream input) throws IOException {
		long start = System.currentTimeMillis();
		for (long i = kilobytesToSend; i > 0; i = i - 1) {
			readKilobyte(input, kilobyte);
		}
		return new SpeedMeasurement(start, System.currentTimeMillis(), kilobytesToSend);
	}

	@Override
	public SpeedMeasurement executeSend(OutputStream output) throws IOException {
		long start = System.currentTimeMillis();
		for (long i = kilobytesToSend; i > 0; i = i - 1) {
			sendKilobyte(output, this.kilobyte);
		}
		return new SpeedMeasurement(start, System.currentTimeMillis(), kilobytesToSend);
	}

	/**
	 * @return amount of data in kilobytes to send during
	 *         {@link #executeSend(OutputStream)}
	 */
	public long getKilobytesToSend() {
		return kilobytesToSend;
	}

	@Override
	public String toString() {
		return "Transfer " + kilobytesToSend + " kb";
	}
}
