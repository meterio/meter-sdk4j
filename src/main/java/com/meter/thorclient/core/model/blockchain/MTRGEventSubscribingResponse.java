
package com.meter.thorclient.core.model.blockchain;

import java.util.ArrayList;

public class MTRGEventSubscribingResponse {
  
 
  
    private boolean obsolete;
    private LogMeta meta;
    private String sender;
    private String recipient;
    private String amount;
    private ArrayList<String> topics;
    private int token;

   

    public LogMeta getMeta() {
        return meta;
    }

    public void setMeta(LogMeta meta) {
        this.meta = meta;
    }

   
    public boolean isObsolete() {
        return obsolete;
    }

    public void setObsolete(boolean obsolete) {
        this.obsolete = obsolete;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public ArrayList<String> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<String> topics) {
        this.topics = topics;
    }
}
