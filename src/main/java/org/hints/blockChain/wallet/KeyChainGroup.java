package org.hints.blockChain.wallet;

import java.util.LinkedList;
import java.util.List;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2024/3/20 17:05
 */
public class KeyChainGroup {

    public static KeyChainGroup.Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<DeterministicKeyChain> chains = new LinkedList<>();
        public KeyChainGroup build() {
            return new KeyChainGroup(chains);
        }
    }

    protected final LinkedList<DeterministicKeyChain> chains;

    private KeyChainGroup(List<DeterministicKeyChain> chains){
        this.chains = new LinkedList<>(chains);
    }

}
