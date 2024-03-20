package org.hints.blockChain;

import lombok.Data;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2024/3/15 17:02
 */
@Data
public class TransactionInput {
    private String transactionOutputId;
    private TransactionOutput unspentTransactionOutput;
    private String signature;
    private String publicKey;

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }

}