package com.meter.thorclient.core.model.clients;

/**
 * A to-clause pojo for one transaction.
 */
public class ToClause {
    /**
     * {@link Address} a address instance to use.
     */
    private  Address to;

    /**
     * {@link Amount} a amount instance for to-clause to use.
     */
    private  Amount value;

    /**
     * {@link ToData} a data instance for to-clause to use.
     */
    private  ToData data;

      /**
     * {@link Token} a token instance for to-clause to use.
     */
    private  Token token;




    public Address getTo() {
        return to;
    }

    public void setTo(Address to) {
        this.to = to;
    }

    public Amount getValue() {
        return value;
    }

    public void setValue(Amount value) {
        this.value = value;
    }

    public ToData getData() {
        return data;
    }

    public void setData(ToData data) {
        this.data = data;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    /**
     * Constructor to new a instance.
     * @param to {@link Address}
     * @param value {@link Amount}
     * @param data {@link ToData}
     * @param token {@link Token}
     */
    public ToClause(Address to, Amount value, ToData data, Token token){
        this.to = to;
        this.value = value;
        this.data = data;
        this.token = token;
    }


}
