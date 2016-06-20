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
 * A command pattern implementation that sends one kilobyte at a time over a
 * connection and returns the {@link SpeedMeasurement} indicating the connection
 * speed.
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 * 
 */
public abstract class TransferCommand {

	/**
	 * Read the required kilobytes
	 * 
	 * @param out
	 *            the connection to read from
	 * @return the speed in which the required kilobytes were read
	 * @throws IOException
	 */
	public abstract SpeedMeasurement executeRead(InputStream out) throws IOException;

	/**
	 * Write the required kilobytes
	 * 
	 * @param out
	 *            the connection to write to
	 * @return the speed in which the required kilobytes were written
	 * @throws IOException
	 */
	public abstract SpeedMeasurement executeSend(OutputStream out) throws IOException;

	/**
	 * Read a single kilobyte and return it
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	protected byte[] readKilobyte(InputStream input, byte[] buffer) throws IOException {
		int read = 0;
		while (read < buffer.length) {
			read += input.read(buffer, read, buffer.length - read);
		}
		return buffer;
	}

	/**
	 * Write a single kilobyte
	 * 
	 * @param output
	 * @param kilobyte
	 * @throws IOException
	 */
	protected void sendKilobyte(OutputStream output, byte[] kilobyte) throws IOException {
		output.write(kilobyte, 0, kilobyte.length);
	}
}
