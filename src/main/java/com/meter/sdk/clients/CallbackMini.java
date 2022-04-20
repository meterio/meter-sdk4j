package com.meter.sdk.clients;

import com.meter.sdk.core.model.blockchain.EventSubscribingResponse;
import org.eclipse.jetty.websocket.api.Session;

public interface CallbackMini {
    void onClose(int statusCode, String reason);

    void onConnect(Session session);

    Class<EventSubscribingResponse> responseClass();

    void onSubscribe(EventSubscribingResponse response);
}
