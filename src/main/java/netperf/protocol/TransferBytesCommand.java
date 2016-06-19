package netperf.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * A command pattern implementation that sends one kilobyte at a time over a
 * connection and returns the {@link SpeedMeasurement} indicating the connection
 * speed.
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 *
 */
public abstract class TransferBytesCommand implements Serializable {

	private static final long serialVersionUID = -2347121434729317847L;

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
