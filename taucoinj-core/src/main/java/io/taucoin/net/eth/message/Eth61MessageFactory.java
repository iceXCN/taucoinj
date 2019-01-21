package io.taucoin.net.eth.message;

import io.taucoin.net.message.Message;
import io.taucoin.net.message.MessageFactory;

import static io.taucoin.net.eth.EthVersion.*;

/**
 * Eth V60 message factory
 *
 * @author Mikhail Kalinin
 * @since 20.08.2015
 */
public class Eth61MessageFactory implements MessageFactory {

    @Override
    public Message create(byte code, byte[] encoded) {

        EthMessageCodes receivedCommand = EthMessageCodes.fromByte(code, V61);
        switch (receivedCommand) {
            case STATUS:
                return new StatusMessage(encoded);
            case NEW_BLOCK_HASHES:
                return new NewBlockHashesMessage(encoded);
            case TRANSACTIONS:
                return new TransactionsMessage(encoded);
            case GET_BLOCK_HASHES:
                return new GetBlockHashesMessage(encoded);
            case BLOCK_HASHES:
                return new BlockHashesMessage(encoded);
            case GET_BLOCKS:
                return new GetBlocksMessage(encoded);
            case BLOCKS:
                return new BlocksMessage(encoded);
            case NEW_BLOCK:
                return new NewBlockMessage(encoded);
            case GET_BLOCK_HASHES_BY_NUMBER:
                return new GetBlockHashesByNumberMessage(encoded);
            default:
                throw new IllegalArgumentException("No such message");
        }
    }
}
