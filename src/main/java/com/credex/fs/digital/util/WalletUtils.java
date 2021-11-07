package com.credex.fs.digital.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Component
public class WalletUtils {

    public static final List<Pair<String, String>> wallets = Arrays.asList(
        Pair.of("0x3AcecE1909Dd2360c4A34620d5308C557fFD7772", "64aed7fba5d0a0c2b5d0dbcba68db443a2126e454b588a1271a795fabf3d3160"),
        Pair.of("0x949d352fc92E611A57d2234eEd5CdDBD89F07a88", "52cbcd959ff1297be775376237eb4b8588ae9e3cedbb2aff795bd9b8ca3e6079"),
        Pair.of("0xEeFd155AC1c044f0D40108720e22b5621635E2E1", "364393512dc1263fb095162371962ad72d7c06e88adb36ee81c67691d8941f12"),
        Pair.of("0xFB9A8818A36Cf4bcA3Ca5daCE4bf311E599CF0Ac", "d5a76eca16599888cdccaf7f70d785a516c28cec4ce96b7169ff86c62bdbb819"),
        Pair.of("0x87c0EdB63cBa259338Ca14D30FF66D14A3e1efFa", "8959c24425e86f0113a1a6bf6bdff80e391af211a61787b9880c50b320e7ad72"),
        Pair.of("0xa64AFEC09BC0e8F373c3dB85FDD3B335b8D957bE", "ad5d69300ea9d4af8bb7976fb8d1b6498926a6867f4d0d869681fbcd9c3122d5")
    );

    public static Pair<String, String> asignWallet() {
        Random ran = new Random();
        return wallets.get(ran.nextInt(wallets.size()));
    }
}
