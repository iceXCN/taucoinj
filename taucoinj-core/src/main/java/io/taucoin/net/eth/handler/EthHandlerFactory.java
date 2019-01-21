package io.taucoin.net.eth.handler;

import io.taucoin.net.eth.EthVersion;

/**
 * @author Mikhail Kalinin
 * @since 20.08.2015
 */
public interface EthHandlerFactory {

    /**
     * Creates EthHandler by requested Eth version
     *
     * @param version Eth version
     * @return created handler
     *
     * @throws IllegalArgumentException if provided Eth version is not supported
     */
    EthHandler create(EthVersion version);

}
