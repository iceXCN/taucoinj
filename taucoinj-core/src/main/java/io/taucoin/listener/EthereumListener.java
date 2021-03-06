package io.taucoin.listener;

import io.taucoin.core.*;
import io.taucoin.net.rlpx.Node;
import io.taucoin.net.tau.message.StatusMessage;
import io.taucoin.net.message.Message;
import io.taucoin.net.p2p.HelloMessage;
import io.taucoin.net.server.Channel;

import java.util.List;
import java.util.Set;

/**
 * @author Roman Mandeleil
 * @since 27.07.2014
 */
public interface EthereumListener {

    void trace(String output);

    void onNodeDiscovered(Node node);

    void onHandShakePeer(Channel channel, HelloMessage helloMessage);

    void onEthStatusUpdated(Channel channel, StatusMessage status);

    void onRecvMessage(Channel channel, Message message);

    void onSendMessage(Channel channel, Message message);

    void onBlock(Block block);

    void onPeerDisconnect(String host, long port);

    void onPendingTransactionsReceived(List<Transaction> transactions);

    void onPendingStateChanged(PendingState pendingState);

    void onSyncDone();

    void onNoConnections();

    void onPeerAddedToSyncPool(Channel peer);
}
