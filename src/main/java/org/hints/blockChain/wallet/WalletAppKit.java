package org.hints.blockChain.wallet;

import com.google.common.util.concurrent.AbstractIdleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2024/3/15 16:53
 */
public class WalletAppKit extends AbstractIdleService {

    protected static final Logger log = LoggerFactory.getLogger(WalletAppKit.class);

    protected final String filePrefix;
    protected final File directory;
    protected volatile File vWalletFile;
    protected volatile Wallet vWallet;

    protected WalletProtobufSerializer.WalletFactory walletFactory = WalletProtobufSerializer.WalletFactory.DEFAULT;

    public WalletAppKit(File directory, String filePrefix) {
        this.directory = directory;
        this.filePrefix = filePrefix;
    }

    public static WalletAppKit launch(File directory, String filePrefix){
        WalletAppKit walletAppKit = new WalletAppKit(directory, filePrefix);

        // Connect to the network and start downloading transactions in Async Thread
        walletAppKit.startAsync();
        walletAppKit.awaitRunning();
        return walletAppKit;
    }

    @Override
    protected void startUp() throws Exception {
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Could not create directory " + directory.getAbsolutePath());
            }
        }
        log.info("startUp");

        File chainFile = new File(directory, filePrefix + ".spvchain");
        vWalletFile = new File(directory, filePrefix + ".wallet");
        vWallet = createOrLoadWallet();
    }

    // 加载钱包
    private Wallet createOrLoadWallet() throws Exception{
        Wallet wallet;

        maybeMoveOldWalletOutOfTheWay();

        if (vWalletFile.exists()) {
            wallet = loadWallet();
        }else{
            wallet = createWallet();
//            wallet.freshReceiveKey();
            wallet.saveToFile(vWalletFile);
            wallet = loadWallet();
        }
        return wallet;
    }

    private void maybeMoveOldWalletOutOfTheWay() {
        if (!vWalletFile.exists()) {return;}
        int counter = 1;
        File newName;
        do {
            newName = new File(vWalletFile.getParent(), "Backup " + counter + " for " + vWalletFile.getName());
            counter++;
        } while (newName.exists());
        log.info("Renaming old wallet file {} to {}", vWalletFile, newName);
        if (!vWalletFile.renameTo(newName)) {
            throw new RuntimeException("Failed to rename wallet for restore");
        }
    }

    protected Wallet createWallet() {
        KeyChainGroup.Builder kcg = KeyChainGroup.builder();
        return walletFactory.create("network", kcg.build());
    }

    private Wallet loadWallet() throws Exception {
        return Wallet.loadFromFile(vWalletFile);
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("shutDown");
    }
}
