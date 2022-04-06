package com.meter.thorclient.core.model.blockchain;

import com.meter.thorclient.core.model.clients.Amount;
import com.meter.thorclient.core.model.clients.base.AbstractToken;
import com.meter.thorclient.core.model.clients.ERC20Token;


import java.io.Serializable;

/**
 * The Account information.
 */
public class Account implements Serializable {

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getEnergy() {
        return energy;
    }

    public void setEnergy(String energy) {
        this.energy = energy;
    }

    public boolean getHasCode() {
        return hasCode;
    }

    public void setHasCode(boolean hasCode) {
        this.hasCode = hasCode;
    }

    

    /**
     * Get MTR token {@link Balance} object
     * @return
     */
    public Amount MTRBalance(){
        Amount balance = Amount.createFromToken( AbstractToken.MTR );
        balance.setHexAmount( this.energy );
        return balance;
    }

    /**
     * On meter mainnet, it has two native currencies, one is MTR, the other is MTRG
     * @return
     */
    public Amount energyBalance(){
        Amount balance = Amount.createFromToken( ERC20Token.MTRG );
        balance.setHexAmount( this.energy );
        return balance;
    }

    private String balance;

    private String energy;

    private boolean hasCode;

}
