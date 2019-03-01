package io.taucoin.net.rlpx.discover.table;

/**
 * Created by kest on 5/25/15.
 */
public class KademliaOptions {
    public static final int BUCKET_SIZE = 16;
    public static final int ALPHA = 3;
    public static final int BINS = 256;
    public static final int MAX_STEPS = 8;

    public static final long REQ_TIMEOUT = 300;
    public static final long BUCKET_REFRESH = 2 * 60 * 1000;     //bucket refreshing interval in millis
    public static final long DISCOVER_CYCLE = 2 * 60;       //discovery cycle interval in seconds
}
