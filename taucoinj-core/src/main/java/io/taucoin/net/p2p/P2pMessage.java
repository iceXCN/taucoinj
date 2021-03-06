package io.taucoin.net.p2p;

import io.taucoin.net.message.Message;

public abstract class P2pMessage extends Message {

    public P2pMessage() {
    }

    public P2pMessage(byte[] encoded) {
        super(encoded);
    }

    public P2pMessageCodes getCommand() {
        return P2pMessageCodes.fromByte(code);
    }
}
