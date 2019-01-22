package io.taucoin.net.tau.handler;

import io.taucoin.net.tau.TauVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Default factory implementation
 *
 * @author Mikhail Kalinin
 * @since 20.08.2015
 */
@Component
public class TauHandlerFactoryImpl implements TauHandlerFactory {

    @Autowired
    private ApplicationContext ctx;

    @Override
    public TauHandler create(TauVersion version) {
        switch (version) {
            case V60:   return ctx.getBean(Tau60.class);
            case V61:   return ctx.getBean(Tau61.class);
            case V62:   return ctx.getBean(Tau62.class);
            default:    throw new IllegalArgumentException("Eth " + version + " is not supported");
        }
    }
}
