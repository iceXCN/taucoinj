package io.taucoin.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;

import static io.taucoin.util.BIUtil.*;

/**
 * @author Roman Mandeleil
 * @since 19.12.2014
 */
public class TransactionExecutor {

    private static final Logger logger = LoggerFactory.getLogger("execute");

    private Transaction tx;
    private Repository track;
    private byte[] coinbase;

    long basicTxAmount = 0;
    long basicTxFee = 0;

    //constructor
    public TransactionExecutor(Transaction tx, Repository track) {
        this.tx= tx;
        this.track= track;
    }

    /**
     * Do all the basic validation
     */
    public boolean init() {

		// Check In Transaction Amount
        basicTxAmount = toBI(tx.getAmount()).longValue();
        if (basicTxAmount < 0 ) {
            if (logger.isWarnEnabled())
                logger.warn("Transaction amount [{}] is invalid!", basicTxAmount);
            return false;
        }

        // Check In Transaction Fee
        basicTxFee = toBI(tx.transactionCost()).longValue();
        if (basicTxFee < 0 ) {
            if (logger.isWarnEnabled())
                logger.warn("Transaction fee [{}] is invalid!", basicTxFee);
            return false;
        }

        BigInteger totalCost = toBI(tx.getAmount()).add(toBI(tx.transactionCost()));
        BigInteger senderBalance = track.getBalance(tx.getSender());

        if (!isCovers(senderBalance, totalCost)) {
            if (logger.isWarnEnabled())
                logger.warn("No enough balance: Require: {}, Sender's balance: {}", totalCost, senderBalance);
            return false;
        }

        return true;
    }

    /**
     * Do the executation
     * 1. add balance to received address 
     * 2. add transaction fee to actually miner 
     */
    public void executeFinal() {

		// Sender subtract balance
        BigInteger totalCost = toBI(tx.getAmount()).add(toBI(tx.transactionCost()));
        logger.info("in executation sender is "+Hex.toHexString(tx.getSender()));
        track.addBalance(tx.getSender(), totalCost.negate());

		// Receiver add balance
        track.addBalance(tx.getReceiveAddress(), toBI(tx.getAmount()));

        // Transfer fees to miner
        track.addBalance(coinbase, toBI(tx.transactionCost()));

        // Increase forge power.
        track.increaseforgePower(tx.getSender());

        logger.info("Pay fees to miner: [{}], feesEarned: [{}]", Hex.toHexString(coinbase), basicTxFee);
    }

    public void undoTransaction() {
        // add sender balance
        BigInteger totalCost = toBI(tx.getAmount()).add(toBI(tx.transactionCost()));
        logger.info("Tx sender is "+Hex.toHexString(tx.getSender()));
        track.addBalance(tx.getSender(), totalCost);

        // Subtract receiver balance
        track.addBalance(tx.getReceiveAddress(), toBI(tx.getAmount()).negate());

        // Transfer fees to miner
        track.addBalance(coinbase, toBI(tx.transactionCost()).negate());

        // Increase forge power.
        track.reduceForgePower(tx.getSender());
    }

	/**
	 * Set Miner Address
	 */
	public void setCoinbase(byte [] address){
        this.coinbase= address;
    }
}
