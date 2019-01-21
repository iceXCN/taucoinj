package io.taucoin.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.taucoin.sync.SyncStateName.BLOCK_RETRIEVING;
import static io.taucoin.sync.SyncStateName.HASH_RETRIEVING;

/**
 * @author Mikhail Kalinin
 * @since 16.12.2015
 */
@Component
public class QueueStateInitiator implements StateInitiator {

    private final static Logger logger = LoggerFactory.getLogger("sync");

    @Autowired
    private SyncQueue queue;

    @Override
    public SyncStateName initiate() {
        if (queue.hasSolidBlocks()) {
            logger.info("It seems that BLOCK_RETRIEVING was interrupted, starting from this state now");
            return BLOCK_RETRIEVING;
        } else {
            return HASH_RETRIEVING;
        }
    }
}
