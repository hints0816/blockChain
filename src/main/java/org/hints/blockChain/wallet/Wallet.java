package org.hints.blockChain.wallet;

import cn.hutool.crypto.asymmetric.RSA;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hints.blockChain.TransactionOutput;

import java.io.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 钱包Wallet
 * @Author 180686
 * @Date 2023/8/25 17:01
 */
@Data
@Slf4j
@AllArgsConstructor
public class Wallet {

    private String network;

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
    private KeyChainGroup keyChainGroup;

    protected final ReentrantLock lock = new ReentrantLock(true);

    public Wallet() {

    }

    public void saveToFile(File f) throws IOException {
        File directory = f.getAbsoluteFile().getParentFile();
        if (!directory.exists()) {
            throw new FileNotFoundException(directory.getPath() + " (wallet directory not found)");
        }
        File temp = File.createTempFile("wallet", null, directory);
        saveToFile(temp, f);
    }

    public void saveToFile(File tempFile, File destFile) throws IOException {
        File tempParentFile = tempFile.getAbsoluteFile().getParentFile();
        if (!tempParentFile.exists()) {
            throw new FileNotFoundException(tempParentFile.getPath() + " (wallet directory not found)");
        }
        File destParentFile = destFile.getAbsoluteFile().getParentFile();
        if (!destParentFile.exists()) {
            throw new FileNotFoundException(destParentFile.getPath() + " (wallet directory not found)");
        }
        FileOutputStream stream = null;
        lock.lock();
        try{
            stream = new FileOutputStream(tempFile);
            saveToFileStream(stream);
            stream.flush();
            stream.getFD().sync();
            stream.close();
            stream = null;

            File canonical = destFile.getCanonicalFile();
            tempFile.renameTo(canonical);
        }catch (Exception e){
            log.error("Failed whilst saving wallet", e);
            throw e;
        }finally {
            lock.unlock();
            if (stream != null) {
                stream.close();
            }
        }
    }

    public void saveToFileStream(OutputStream f) throws IOException {
        lock.lock();
        try {
            new WalletProtobufSerializer().writeWallet(this, f);
        } finally {
            lock.unlock();
        }
    }

    public Wallet(String network, KeyChainGroup keyChainGroup) {
        generateKeyPair();
        this.network = network;
        this.keyChainGroup = keyChainGroup;
    }


    public void generateKeyPair() {
        RSA rsa = new RSA();
        privateKey = rsa.getPrivateKey();
        log.info("privateKey:{}", Base64.getEncoder().encodeToString(privateKey.getEncoded()));
        publicKey = rsa.getPublicKey();
        log.info("publicKey:{}", Base64.getEncoder().encodeToString(publicKey.getEncoded()));
    }

    public static Wallet loadFromFile(File file) throws Exception{
        try (FileInputStream stream = new FileInputStream(file)) {
            return loadFromFileStream(stream);
        } catch (IOException e) {
            throw new Exception("Could not open file");
        }
    }

    public static Wallet loadFromFileStream(InputStream stream) throws Exception {
        WalletProtobufSerializer loader = new WalletProtobufSerializer();
        Wallet wallet = loader.readWallet(stream);
        return wallet;
    }
}
