package net.ion.websocket.common.api;

import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ObjectId implements Comparable, Serializable {

	private static final long serialVersionUID = -2896306630060349126L;

	static final boolean D = false;
	private final int _time;
	private final int _machine;
	private final int _inc;
	private boolean _new;
	private static AtomicInteger _nextInc = new AtomicInteger((new Random()).nextInt());
	private static final String _incLock = new String("ObjectId._incLock");
	private static final int _genmachine;

	static {
		try {
			StringBuilder sb = new StringBuilder();
			NetworkInterface ni;
			for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); sb.append(ni.toString()))
				ni = (NetworkInterface) e.nextElement();

			int machinePiece = sb.toString().hashCode() << 16;
			int processPiece = ManagementFactory.getRuntimeMXBean().getName().hashCode() & 65535;
			_genmachine = machinePiece | processPiece;
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	
	public static ObjectId get() {
		return new ObjectId();
	}

	public static boolean isValid(String s) {
		if (s == null)
			return false;
		int len = s.length();
		if (len < 18 || len > 24)
			return false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if ((c < '0' || c > '9') && (c < 'a' || c > 'f') && (c < 'A' || c > 'F'))
				return false;
		}

		return true;
	}

	protected static ObjectId massageToObjectId(Object o) {
		if (o == null)
			return null;
		if (o instanceof ObjectId)
			return (ObjectId) o;
		if (o instanceof String) {
			String s = o.toString();
			if (isValid(s))
				return new ObjectId(s);
		}
		return null;
	}

	public ObjectId(Date time) {
		_time = _flip((int) (time.getTime() / 1000L));
		_machine = _genmachine;
		_inc = _nextInc.getAndIncrement();
		_new = false;
	}

	public ObjectId(Date time, int inc) {
		this(time, _genmachine, inc);
	}

	public ObjectId(Date time, int machine, int inc) {
		_time = _flip((int) (time.getTime() / 1000L));
		_machine = machine;
		_inc = inc;
		_new = false;
	}

	public ObjectId(String s) {
		this(s, false);
	}

	public ObjectId(String s, boolean babble) {
		if (!isValid(s))
			throw new IllegalArgumentException((new StringBuilder()).append("invalid ObjectId [").append(s).append("]").toString());
		if (babble)
			s = babbleToMongod(s);
		byte b[] = new byte[12];
		for (int i = 0; i < b.length; i++)
			b[b.length - (i + 1)] = (byte) Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);

		ByteBuffer bb = ByteBuffer.wrap(b);
		_inc = bb.getInt();
		_machine = bb.getInt();
		_time = bb.getInt();
		_new = false;
	}

	public ObjectId(byte b[]) {
		if (b.length != 12) {
			throw new IllegalArgumentException("need 12 bytes");
		} else {
			reverse(b);
			ByteBuffer bb = ByteBuffer.wrap(b);
			_inc = bb.getInt();
			_machine = bb.getInt();
			_time = bb.getInt();
			return;
		}
	}

	public ObjectId(int time, int machine, int inc) {
		_time = time;
		_machine = machine;
		_inc = inc;
		_new = false;
	}

	public ObjectId() {
		_time = _curtime();
		_machine = _genmachine;
		_inc = _nextInc.getAndIncrement();
		_new = true;
	}

	public int hashCode() {
		return _inc;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		ObjectId other = massageToObjectId(o);
		if (other == null)
			return false;
		else
			return _time == other._time && _machine == other._machine && _inc == other._inc;
	}

	public String toStringBabble() {
		return babbleToMongod(toStringMongod());
	}

	public String toStringMongod() {
		byte b[] = toByteArray();
		StringBuilder buf = new StringBuilder(24);
		for (int i = 0; i < b.length; i++) {
			int x = b[i] & 255;
			String s = Integer.toHexString(x);
			if (s.length() == 1)
				buf.append("0");
			buf.append(s);
		}

		return buf.toString();
	}

	public byte[] toByteArray() {
		byte b[] = new byte[12];
		ByteBuffer bb = ByteBuffer.wrap(b);
		bb.putInt(_inc);
		bb.putInt(_machine);
		bb.putInt(_time);
		reverse(b);
		return b;
	}

	static void reverse(byte b[]) {
		for (int i = 0; i < b.length / 2; i++) {
			byte t = b[i];
			b[i] = b[b.length - (i + 1)];
			b[b.length - (i + 1)] = t;
		}

	}

	static String _pos(String s, int p) {
		return s.substring(p * 2, p * 2 + 2);
	}

	public static String babbleToMongod(String b) {
		if (!isValid(b))
			throw new IllegalArgumentException((new StringBuilder()).append("invalid object id: ").append(b).toString());
		StringBuilder buf = new StringBuilder(24);
		for (int i = 7; i >= 0; i--)
			buf.append(_pos(b, i));

		for (int i = 11; i >= 8; i--)
			buf.append(_pos(b, i));

		return buf.toString();
	}

	public String toString() {
		return toStringMongod();
	}

	public int compareTo(ObjectId id) {
		if (id == null)
			return -1;
		long xx = id.getTime() - getTime();
		if (xx > 0L)
			return -1;
		if (xx < 0L)
			return 1;
		int x = id._machine - _machine;
		if (x != 0)
			return -x;
		x = id._inc - _inc;
		if (x != 0)
			return -x;
		else
			return 0;
	}

	public int getMachine() {
		return _machine;
	}

	public long getTime() {
		long z = _flip(_time);
		return z * 1000L;
	}

	public int getInc() {
		return _inc;
	}

	public int _time() {
		return _time;
	}

	public int _machine() {
		return _machine;
	}

	public int _inc() {
		return _inc;
	}

	public boolean isNew() {
		return _new;
	}

	public void notNew() {
		_new = false;
	}

	public static int _flip(int x) {
		int z = 0;
		z |= x << 24 & -16777216;
		z |= x << 8 & 16711680;
		z |= x >> 8 & 65280;
		z |= x >> 24 & 255;
		return z;
	}

	private static int _curtime() {
		return _flip((int) (System.currentTimeMillis() / 1000L));
	}

	public static void main(String args[]) {
		int z = _nextInc.getAndIncrement();
		System.out.println(Integer.toHexString(z));
		System.out.println(Integer.toHexString(_flip(z)));
		System.out.println(Integer.toHexString(_flip(_flip(z))));
	}

	public int compareTo(Object x0) {
		return compareTo((ObjectId) x0);
	}

}