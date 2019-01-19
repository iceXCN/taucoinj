package org.ethereum.rpc.server.full.method;

import com.thetransactioncompany.jsonrpc2.*;
import com.thetransactioncompany.jsonrpc2.server.*;
import org.ethereum.rpc.server.full.JsonRpcServerMethod;
import org.ethereum.core.AccountState;
import org.ethereum.core.Repository;
import org.ethereum.facade.Ethereum;
import org.spongycastle.util.encoders.Hex;
import java.math.BigInteger;
import java.util.List;

public class eth_getBalance extends JsonRpcServerMethod {

    public eth_getBalance (Ethereum ethereum) {
        super(ethereum);
    }

    protected JSONRPC2Response worker(JSONRPC2Request req, MessageContext ctx) {

        List<Object> params = req.getPositionalParams();
        if (params.size() != 2) {
            return new JSONRPC2Response(JSONRPC2Error.INVALID_PARAMS, req.getID());
        } else {
            byte[] address = jsToAddress((String) params.get(0));
            String height = (String)params.get(1);

            long blockNumber = getBlockNumber(height);

            byte[] root = ethereum.getBlockchain().getBestBlock().getStateRoot();

            if (blockNumber >= 0) {
                Repository repository = (Repository)ethereum.getRepository();
                repository.syncToRoot(ethereum.getBlockchain().getBlockByNumber(blockNumber).getStateRoot());
            }

            BigInteger balance = ethereum.getRepository().getBalance(address);

            if (blockNumber == -2) {
                BigInteger tmpB = ethereum.getWallet().getBalance(address);
                balance = tmpB != BigInteger.ZERO ? tmpB : balance;
            }

            if (blockNumber >= 0) {
                Repository repository = (Repository)ethereum.getRepository();
                repository.syncToRoot(root);
            }

            String tmp = "0x" + balance.toString(16);
            JSONRPC2Response res = new JSONRPC2Response(tmp, req.getID());
            return res;
        }

    }
}