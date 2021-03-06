package com.meter.sdk.core.model.blockchain;

import java.io.Serializable;

@Deprecated
public class TransactionAttributes implements Serializable {

    public enum TransactionType {

        MTR("MTR"), MTRG("MTRG");

        private String value;

        TransactionType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public enum ERC20ContractMethod {

        BALANCEOF("70a08231"),

        TRANSFER("a9059cbb");

        private String id;

        ERC20ContractMethod(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
