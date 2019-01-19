package org.ethereum.rpc.server.full.method;

import com.thetransactioncompany.jsonrpc2.*;
import com.thetransactioncompany.jsonrpc2.server.*;
import org.ethereum.rpc.server.full.JsonRpcServerMethod;
import org.ethereum.core.Repository;
import org.ethereum.facade.Ethereum;
import org.spongycastle.util.encoders.Hex;
import java.util.List;

public class eth_getCode extends JsonRpcServerMethod {

    public eth_getCode (Ethereum ethereum) {
        super(ethereum);
    }

    protected JSONRPC2Response worker(JSONRPC2Request req, MessageContext ctx) {

        List<Object> params = req.getPositionalParams();
        if (params.size() != 2) {
            return new JSONRPC2Response(JSONRPC2Error.INVALID_PARAMS, req.getID());
        } else {
            byte[] address = jsToAddress((String)params.get(0));
            String height = (String)params.get(1);

            long blockNumber = getBlockNumber(height);

            byte[] root = ethereum.getBlockchain().getBestBlock().getStateRoot();

            if (blockNumber >= 0) {
                Repository repository = (Repository)ethereum.getRepository();
                repository.syncToRoot(ethereum.getBlockchain().getBlockByNumber(blockNumber).getStateRoot());
            }

            String tmp = "0x" + Hex.toHexString(ethereum.getRepository().getCode(address));

            if (blockNumber >= 0) {
                Repository repository = (Repository)ethereum.getRepository();
                repository.syncToRoot(root);
            }

            JSONRPC2Response res = new JSONRPC2Response(tmp, req.getID());
            return res;
        }

    }
}