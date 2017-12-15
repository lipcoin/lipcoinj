/*
 * Copyright 2013 Google Inc.
 * Copyright 2014 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lipcoinj.params;

import org.bitcoinj.core.Utils;
import org.spongycastle.util.encoders.Hex;

import static com.google.common.base.Preconditions.checkState;
import java.io.ByteArrayOutputStream;
import org.bitcoinj.core.AltcoinBlock;
import org.bitcoinj.core.Block;
import static org.bitcoinj.core.Coin.COIN;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptOpCodes;

/**
 * Parameters for the testnet, a separate public instance of Litecoin that has
 * relaxed rules suitable for development and testing of applications and new
 * Litecoin versions.
 */
public class LipcoinTestNet3Params extends AbstractLipcoinParams {
    public static final int TESTNET_MAJORITY_WINDOW = 100;
    public static final int TESTNET_MAJORITY_REJECT_BLOCK_OUTDATED = 75;
    public static final int TESTNET_MAJORITY_ENFORCE_BLOCK_UPGRADE = 51;

    public LipcoinTestNet3Params() {
        super();
        id = ID_LIPC_TESTNET;
        // Genesis hash is f75f6198b8bec85ceaca3c6b87ae7b7aa7f19db4661f29f22252b2d5e9ea294e
        packetMagic = 0xfcc1b7dc;

        maxTarget = Utils.decodeCompactBits(0x1e0fffffL);
        port = 19155;
        addressHeader = 111;
        p2shHeader = 196;
        acceptableAddressCodes = new int[] { addressHeader, p2shHeader };
        dumpedPrivateKeyHeader = 239;

        this.genesisBlock = createGenesis(this);
        spendableCoinbaseDepth = 30;
        subsidyDecreaseBlockCount = 100000;

        String genesisHash = genesisBlock.getHashAsString();
        checkState(genesisHash.equals("f75f6198b8bec85ceaca3c6b87ae7b7aa7f19db4661f29f22252b2d5e9ea294e"));
        alertSigningKey = Hex.decode("194630194fd114ba2020490c26b3a49c8f13f8d45b8c857fbcbb8bc4a8e4d3eb4c10f3d4618ff08dce601bbf0f470216ae1b51850b4bcf21b179c48304ac7b03a1");

        majorityEnforceBlockUpgrade = TESTNET_MAJORITY_ENFORCE_BLOCK_UPGRADE;
        majorityRejectBlockOutdated = TESTNET_MAJORITY_REJECT_BLOCK_OUTDATED;
        majorityWindow = TESTNET_MAJORITY_WINDOW;

        dnsSeeds = new String[] {
            "testnet-seed.lipcoins.org"
                    };

        bip32HeaderPub = 0x043587CF;
        bip32HeaderPriv = 0x04358394;
    }

    private static AltcoinBlock createGenesis(NetworkParameters params) {
        AltcoinBlock genesisBlock = new AltcoinBlock(params, Block.BLOCK_VERSION_GENESIS);
        Transaction t = new Transaction(params);
        try {
            byte[] bytes = Hex.decode
                    ("04ffff001d0104404e592054696d65732030352f4f63742f32303131205374657665204a6f62732c204170706c65e280997320566973696f6e6172792c2044696573206174203536");
            t.addInput(new TransactionInput(params, t, bytes));
            ByteArrayOutputStream scriptPubKeyBytes = new ByteArrayOutputStream();
            Script.writeBytes(scriptPubKeyBytes, Hex.decode
                    ("194630194fd114ba2020490c26b3a49c8f13f8d45b8c857fbcbb8bc4a8e4d3eb4c10f3d4618ff08dce601bbf0f470216ae1b51850b4bcf21b179c48304ac7b03a1"));
            scriptPubKeyBytes.write(ScriptOpCodes.OP_CHECKSIG);
            t.addOutput(new TransactionOutput(params, t, COIN.multiply(50), scriptPubKeyBytes.toByteArray()));
        } catch (Exception e) {
            // Cannot happen.
            throw new RuntimeException(e);
        }
        genesisBlock.addTransaction(t);
        genesisBlock.setTime(1317798646L);
        genesisBlock.setDifficultyTarget(0x1e0ffff0L);
        genesisBlock.setNonce(385270584);
        return genesisBlock;
    }

    private static LipcoinTestNet3Params instance;
    public static synchronized LipcoinTestNet3Params get() {
        if (instance == null) {
            instance = new LipcoinTestNet3Params();
        }
        return instance;
    }

    @Override
    public String getPaymentProtocolId() {
        return ID_LIPC_TESTNET;
    }

    @Override
    public boolean isTestNet() {
        return true;
    }
}
