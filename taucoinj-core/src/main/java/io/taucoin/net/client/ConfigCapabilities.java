package io.taucoin.net.client;

import io.taucoin.config.SystemProperties;
import io.taucoin.net.eth.EthVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static io.taucoin.net.eth.EthVersion.fromCode;
import static io.taucoin.net.client.Capability.*;

/**
 * Created by Anton Nashatyrev on 13.10.2015.
 */
@Component
public class ConfigCapabilities {
    @Autowired
    SystemProperties config;

    private SortedSet<Capability> AllCaps = new TreeSet<>();

    @PostConstruct
    private void init() {
        if (config.syncVersion() != null) {
            EthVersion eth = fromCode(config.syncVersion());
            if (eth != null) AllCaps.add(new Capability(ETH, eth.getCode()));
        } else {
            for (EthVersion v : EthVersion.supported())
                AllCaps.add(new Capability(ETH, v.getCode()));
        }
    }

    /**
     * Gets the capabilities listed in 'peer.capabilities' config property
     * sorted by their names.
     */
    public List<Capability> getConfigCapabilities() {
        List<Capability> ret = new ArrayList<>();
        List<String> caps = config.peerCapabilities();
        for (Capability capability : AllCaps) {
            if (caps.contains(capability.getName())) {
                ret.add(capability);
            }
        }
        return ret;
    }

}
