package org.hints.blockChain;

import cn.hutool.crypto.digest.DigestUtil;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2024/3/15 17:01
 */
public class Transaction {

    private String id;
    private PublicKey sender;
    private PublicKey recipient;
    private BigDecimal value;
    private byte[] signature;
    private List<TransactionInput> inputs;
    private List<TransactionOutput> outputs;
    private static int sequence = 0;

    public Transaction() {
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
    }

    public void addOutput(TransactionOutput output) {
        this.outputs.add(output);
    }

    public Transaction(PublicKey sender,
                       PublicKey recipient,
                       BigDecimal value,
                       ArrayList<TransactionInput> transactionInputArrayList) {
        this.sender = sender;
        this.recipient = recipient;
        this.value = value;
        this.inputs = transactionInputArrayList;
        this.outputs = new ArrayList<TransactionOutput>();
        this.id = calculateHash();
    }

    public String calculateHash() {
        sequence++;
        String input = sender.getFormat() + recipient.getFormat() + value.toString() + sequence;
        return DigestUtil.sha256Hex(DigestUtil.sha256Hex(input));
    }
}
