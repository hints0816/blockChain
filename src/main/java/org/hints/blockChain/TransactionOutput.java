package org.hints.blockChain;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.Data;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.security.PublicKey;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2024/3/15 17:02
 */
@Data
public class TransactionOutput {

    @Nullable
    protected Transaction parent;
    private byte[] scriptBytes;

    private Integer index;
    private String id;
    private BigDecimal value;
    private PublicKey recipient;
    private String parentTransactionId;
    private byte[] scriptPubKey;
    private int lockTime;

    public TransactionOutput(BigDecimal value, PublicKey recipient, String parentTransactionId, byte[] scriptPubKey, int lockTime) {
        this.value = value;
        this.recipient = recipient;
        this.parentTransactionId = parentTransactionId;
        this.scriptPubKey = scriptPubKey;
        this.lockTime = lockTime;
        this.id = calculateHash();
    }

    public String calculateHash() {
        String input = this.parentTransactionId + this.recipient + this.value + this.lockTime;
        return DigestUtil.sha256Hex(DigestUtil.sha256Hex(input));
    }

    public TransactionOutput(@Nullable Transaction parent, BigDecimal value, byte[] scriptBytes) {
        this.value = value;
        this.scriptBytes = scriptBytes;
        setParent(parent);
    }
}
