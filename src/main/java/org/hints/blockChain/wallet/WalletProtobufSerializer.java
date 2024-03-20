package org.hints.blockChain.wallet;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2023/9/4 17:14
 */
public class WalletProtobufSerializer {

    private static final int WALLET_SIZE_LIMIT = 512 * 1024 * 1024;
    private final WalletFactory factory;

    /**
     * 函数式接口:wallet构造函数可动
     */
    @FunctionalInterface
    public interface WalletFactory {
        WalletFactory DEFAULT = Wallet::new;

        Wallet create(String network, KeyChainGroup keyChainGroup);
    }

    public WalletProtobufSerializer() {
        this(WalletFactory.DEFAULT);
    }

    public WalletProtobufSerializer(WalletFactory factory) {
        this.factory = factory;
    }

    public void writeWallet(Wallet wallet, OutputStream output) throws IOException {
        Protos.Wallet walletProto = walletToProto(wallet);
        final CodedOutputStream codedOutput = CodedOutputStream.newInstance(output, CodedOutputStream.DEFAULT_BUFFER_SIZE);
        walletProto.writeTo(codedOutput);
        codedOutput.flush();
    }

    public Protos.Wallet walletToProto(Wallet wallet) {
        Protos.Wallet.Builder walletBuilder = Protos.Wallet.newBuilder();
        walletBuilder.setPrivateKey(ByteString.copyFrom(wallet.getPrivateKey().getEncoded()));
        walletBuilder.setPublicKey(ByteString.copyFrom(wallet.getPublicKey().getEncoded()));

        return walletBuilder.build();
    }

    public Wallet readWallet(InputStream input) {
        Protos.Wallet walletProto = null;
        Wallet wallet = new Wallet();
        try {
            walletProto = parseToProto(input);
            ByteString privateKey = walletProto.getPrivateKey();
            ByteString publicKey = walletProto.getPublicKey();


            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKey.toByteArray());
            wallet.setPrivateKey(keyFactory.generatePrivate(privateKeySpec));

            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey.toByteArray());
            wallet.setPublicKey(keyFactory.generatePublic(publicKeySpec));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return wallet;
    }

    public static Protos.Wallet parseToProto(InputStream input) throws IOException {
        CodedInputStream codedInput = CodedInputStream.newInstance(input);
        codedInput.setSizeLimit(WALLET_SIZE_LIMIT);
        return Protos.Wallet.parseFrom(codedInput);
    }

}
