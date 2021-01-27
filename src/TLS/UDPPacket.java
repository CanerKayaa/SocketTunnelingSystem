package TLS;

public class UDPPacket implements Comparable<UDPPacket> {
	private final int sequenceNumber;
	private final byte[] message;

	public UDPPacket(final int sequenceNumber, final byte[] message) {
		this.sequenceNumber = sequenceNumber;
		this.message = message;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public byte[] getMessage() {
		return message;
	}

	@Override
	public int compareTo(final UDPPacket o) {
		// TODO Auto-generated method stub
		return this.sequenceNumber > o.sequenceNumber ? 1 : -1;
	}

}
