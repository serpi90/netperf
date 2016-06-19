package netperf.protocol;

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
public class TrasferRandomBytesFixedSizeCommand extends TransferBytesCommand {
	private static final long serialVersionUID = -4864614369602066693L;
	private long kilobytesToSend;
	private byte[] kilobyte;

	/**
	 * @param kilobytesToSend
	 *            The amount of random data in kilobytes to transfer
	 */
	public TrasferRandomBytesFixedSizeCommand(long kilobytesToSend) {
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Transfer ");
		builder.append(kilobytesToSend);
		builder.append(" kb");
		return builder.toString();
	}
}
