package com.lakala.ips.router.szt;

import com.lakala.amber.channel.CommunicationException;
import com.lakala.amber.channel.Gateway;

/**
 * Created by Administrator on 2016/11/30.
 */
public interface GatewayExtend extends Gateway {
    void closesocket(String id) throws CommunicationException;

}