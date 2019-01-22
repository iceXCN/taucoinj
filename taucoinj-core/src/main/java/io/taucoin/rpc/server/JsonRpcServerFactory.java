package io.taucoin.rpc.server;

import io.taucoin.facade.Taucoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.taucoin.config.SystemProperties.CONFIG;

public class JsonRpcServerFactory {

    private static final Logger logger = LoggerFactory.getLogger("rpc");

    public static JsonRpcServer createJsonRpcServer(Taucoin taucoin) {
        String type = CONFIG.getRpcServerType();
        logger.info("Json rpc serer type {}", type);
        if ("full".equals(type)) {
            return new io.taucoin.rpc.server.full.JsonRpcServer(taucoin);
        } else {
            return new io.taucoin.rpc.server.light.JsonRpcServer(taucoin);
        }
    }
}
