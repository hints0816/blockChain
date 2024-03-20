package org.hints.blockChain;

import lombok.extern.slf4j.Slf4j;
import org.hints.blockChain.wallet.WalletAppKit;

import java.io.File;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2023/9/1 13:47
 */
@Slf4j
public class SendRequestTest {

    @org.junit.Test
    public void sendRequestTest() {

        WalletAppKit kit = WalletAppKit.launch(new File("blockChain/"), "sendrequest-example");



    }
}
