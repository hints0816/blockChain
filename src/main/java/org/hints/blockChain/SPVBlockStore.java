package org.hints.blockChain;

import org.junit.Test;

import java.nio.Buffer;
import java.nio.MappedByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2024/3/15 17:27
 */
public class SPVBlockStore {

    protected Block genesisBlock;
    protected volatile MappedByteBuffer buffer;
    protected static final int FILE_PROLOGUE_BYTES = 1024;

    @Test
    public void testSPVBlockStore() throws Exception {
        initNewStore(getGenesisBlock());
    }

    private void initNewStore(Block genesisBlock) throws Exception {
        put(genesisBlock);
    }

    public void put(Block block) {

    }


    public Block getGenesisBlock() {
        if (genesisBlock == null) {
            genesisBlock = Block.createGenesis(
                    Instant.ofEpochSecond(1296688602),
                    0x1d00ffffL, 414098458);
        }
        return genesisBlock;
    }


}
