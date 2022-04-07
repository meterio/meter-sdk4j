package com.meter.thorclient.clients;
import com.meter.thorclient.core.model.blockchain.EventSubscribingResponse;
import org.eclipse.jetty.websocket.api.Session;

public interface CallbackMini {
    void onClose(int statusCode, String reason);
    void onConnect(Session session);
    Class<EventSubscribingResponse> responseClass();
    void onSubscribe(EventSubscribingResponse response);
}

