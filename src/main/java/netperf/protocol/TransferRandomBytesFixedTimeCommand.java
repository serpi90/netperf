package netperf.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * Obtain {@link SpeedMeasurement} sending all possible data during a fixed
 * amount of time. The variable is the transferred data size.
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 *
 */
public class TransferRandomBytesFixedTimeCommand extends TransferBytesCommand {
	private static final long serialVersionUID = -8146404233626286703L;
	private long milliseconds;
	private byte[] kilobyte;

	/**
	 * @param milliseconds
	 *            the time limit when sending data
	 */
	public TransferRandomBytesFixedTimeCommand(long milliseconds) {
		this.milliseconds = milliseconds;
		this.kilobyte = new byte[1024];
		for (int i = 0; i < kilobyte.length; i++) {
			kilobyte[i] = (byte) i;
		}
	}

	@Override
	public SpeedMeasurement executeRead(InputStream input) throws IOException {
		long start = System.currentTimeMillis();
		long end = start + milliseconds;
		long readKilobytes = 0;
		do {
			readKilobyte(input, kilobyte);
			System.out.println(kilobyte[0] + " " + kilobyte[1]);
			readKilobytes++;
		} while (kilobyte[0] != (byte) 0);
		return new SpeedMeasurement(start, end, readKilobytes);
	}

	@Override
	public SpeedMeasurement executeSend(OutputStream output) throws IOException {
		Random random = new Random(42);
		random.nextBytes(kilobyte);
		kilobyte[0] = (byte) 1;
		System.out.println(kilobyte[0] + " " + kilobyte[1]);
		long start = System.currentTimeMillis();
		long end = start + milliseconds;
		long sentKilobytes = 0;
		while (System.currentTimeMillis() < end) {
			sendKilobyte(output, kilobyte);
			sentKilobytes++;
		}
		kilobyte[0] = (byte) 0;
		sendKilobyte(output, kilobyte);
		return new SpeedMeasurement(start, end, sentKilobytes + 1);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Transfer during");
		builder.append(milliseconds);
		builder.append(" ms");
		return builder.toString();
	}
}
