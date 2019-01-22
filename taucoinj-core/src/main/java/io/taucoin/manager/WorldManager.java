package io.taucoin.manager;

import io.taucoin.config.SystemProperties;
import io.taucoin.core.*;
import io.taucoin.crypto.HashUtil;
import io.taucoin.db.BlockStore;
import io.taucoin.db.ByteArrayWrapper;
import io.taucoin.listener.CompositeEthereumListener;
import io.taucoin.listener.EthereumListener;
import io.taucoin.net.client.PeerClient;
import io.taucoin.sync.SyncManager;
import io.taucoin.net.peerdiscovery.PeerDiscovery;
import io.taucoin.net.rlpx.discover.NodeManager;
import io.taucoin.net.server.ChannelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import static io.taucoin.crypto.HashUtil.EMPTY_TRIE_HASH;

/**
 * WorldManager is a singleton containing references to different parts of the system.
 *
 * @author Roman Mandeleil
 * @since 01.06.2014
 */
@Component
public class WorldManager {

    private static final Logger logger = LoggerFactory.getLogger("general");

    @Autowired
    private EthereumListener listener;

    @Autowired
    private Blockchain blockchain;

    @Autowired
    private Repository repository;

    @Autowired
    private Wallet wallet;

    @Autowired
    private PeerClient activePeer;

    @Autowired
    private PeerDiscovery peerDiscovery;

    @Autowired
    private BlockStore blockStore;

    @Autowired
    private ChannelManager channelManager;

    @Autowired
    private AdminInfo adminInfo;

    @Autowired
    private NodeManager nodeManager;

    @Autowired
    private SyncManager syncManager;

    @Autowired
    private PendingState pendingState;

    @Autowired
    SystemProperties config;

    @PostConstruct
    public void init() {
        byte[] cowAddr = HashUtil.sha3("cow".getBytes());
        //wallet.importKey(cowAddr);

        String secret = config.coinbaseSecret();
        byte[] cbAddr = HashUtil.sha3(secret.getBytes());
        //wallet.importKey(cbAddr);

        loadBlockchain();
    }

    public void addListener(EthereumListener listener) {
        logger.info("Ethereum listener added");
        ((CompositeEthereumListener) this.listener).addListener(listener);
    }

    public void startPeerDiscovery() {
        if (!peerDiscovery.isStarted())
            peerDiscovery.start();
    }

    public void stopPeerDiscovery() {
        if (peerDiscovery.isStarted())
            peerDiscovery.stop();
    }

    public ChannelManager getChannelManager() {
        return channelManager;
    }

    public PeerDiscovery getPeerDiscovery() {
        return peerDiscovery;
    }

    public EthereumListener getListener() {
        return listener;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public io.taucoin.facade.Repository getRepository() {
        return (io.taucoin.facade.Repository)repository;
    }

    public Blockchain getBlockchain() {
        return blockchain;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setActivePeer(PeerClient peer) {
        this.activePeer = peer;
    }

    public PeerClient getActivePeer() {
        return activePeer;
    }

    public BlockStore getBlockStore() {
        return blockStore;
    }

    public PendingState getPendingState() {
        return pendingState;
    }

    public void loadBlockchain() {

        if (!config.databaseReset())
            blockStore.load();

        Block bestBlock = blockStore.getBestBlock();
        if (bestBlock == null) {
            logger.info("DB is empty - adding Genesis");

            Genesis genesis = (Genesis)Genesis.getInstance(config);
            for (ByteArrayWrapper key : genesis.getPremine().keySet()) {
                repository.createAccount(key.getData());
                repository.addBalance(key.getData(), genesis.getPremine().get(key).getBalance());
            }

            blockStore.saveBlock(Genesis.getInstance(config), Genesis.getInstance(config).getCumulativeDifficulty(), true);

            blockchain.setBestBlock(Genesis.getInstance(config));
            blockchain.setTotalDifficulty(Genesis.getInstance(config).getCumulativeDifficulty());

            listener.onBlock(Genesis.getInstance(config));
            repository.dumpState(Genesis.getInstance(config), 0, 0, null);

            logger.info("Genesis block loaded");
        } else {

            blockchain.setBestBlock(bestBlock);

            BigInteger totalDifficulty = blockStore.getTotalDifficulty();
            blockchain.setTotalDifficulty(totalDifficulty);

            logger.info("*** Loaded up to block [{}] totalDifficulty [{}] with stateRoot [{}]",
                    blockchain.getBestBlock().getNumber(),
                    blockchain.getTotalDifficulty().toString(),
                    Hex.toHexString(blockchain.getBestBlock().getHash()));
        }

        if (config.rootHashStart() != null) {

            // update world state by dummy hash
            byte[] rootHash = Hex.decode(config.rootHashStart());
            logger.info("Loading root hash from property file: [{}]", config.rootHashStart());
            this.repository.syncToRoot(rootHash);

        } else {

            // Update world state to latest loaded block from db
            // if state is not generated from empty premine list
            // to word state we should be watch out...
            // todo this is just a workaround, move EMPTY_TRIE_HASH logic to Trie implementation
            if (!Arrays.equals(blockchain.getBestBlock().getHash(), EMPTY_TRIE_HASH)) {
                this.repository.syncToRoot(blockchain.getBestBlock().getHash());
            }
        }

/* todo: return it when there is no state conflicts on the chain
        boolean dbValid = this.repository.getWorldState().validate() || bestBlock.isGenesis();
        if (!dbValid){
            logger.error("The DB is not valid for that blockchain");
            System.exit(-1); //  todo: reset the repository and blockchain
        }
*/
    }


    @PreDestroy
    public void close() {
        stopPeerDiscovery();
        repository.close();
        blockchain.close();
    }

}
