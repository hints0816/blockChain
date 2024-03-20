package org.hints.blockChain;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2024/3/15 15:18
 */
@Data
@AllArgsConstructor
public class Block {

    private int index;
    private byte[] hash;
    private byte[] preHash;
    private long timeStamp;
    private long nonce;
    private long difficultyTarget;
    private ArrayList<Transaction> transactions ;

    public byte[] getHash() {
        if (hash == null){
            hash = calculateHash();
        }
        return hash;
    }

    Block(Instant time, long difficultyTarget, long nonce, List<Transaction> transactions) {
        this.difficultyTarget = difficultyTarget;
        this.nonce = Integer.valueOf(Long.toString(nonce));
        this.preHash = new byte[32];
        this.transactions = new ArrayList<>(Objects.requireNonNull(transactions));
    }

    public static Block createGenesis(Instant time, long difficultyTarget, long nonce) {
        return new Block(time, difficultyTarget, nonce, genesisTransactions());
    }

    private static List<Transaction> genesisTransactions() {
        Transaction tx = new Transaction();
        tx.addOutput(new TransactionOutput(tx, new BigDecimal(50), (byte[]) null));
        return Collections.singletonList(tx);
    }

    private byte[] calculateHash() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(80);
            writeHeader(bos);
            byte[] bytes = DigestUtil.sha256(DigestUtil.sha256(bos.toByteArray()));
            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e); // Cannot happen.
        }
    }

    private void writeHeader(OutputStream stream) throws IOException {
        stream.write(reverseBytes(preHash));
        writeInt32LE(difficultyTarget, stream);
        writeInt32LE(nonce, stream);
    }

    public static byte[] reverseBytes(byte[] bytes) {
        byte[] buf = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++){
            buf[i] = bytes[bytes.length - 1 - i];
        }
        return buf;
    }

    private void writeInt32LE(long val, OutputStream stream) throws IOException {
        byte[] buf = new byte[4];
        ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).putInt((int)val);
        stream.write(buf);
    }
}
