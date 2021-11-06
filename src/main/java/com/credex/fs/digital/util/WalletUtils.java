package com.credex.fs.digital.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Component
public class WalletUtils {

    public static final List<Pair<String, String>> wallets = Arrays.asList(
        Pair.of("0x296DaAfc51Fd44A2d6676A79a0FfaaB685bdCA05", "5c9b465dbee146b52a968590f2e4ac04f06e37b7e44ef7fd3369d3702bfd659c"),
        Pair.of("0x3AcecE1909Dd2360c4A34620d5308C557fFD7772", "64aed7fba5d0a0c2b5d0dbcba68db443a2126e454b588a1271a795fabf3d3160"),
        Pair.of("0x949d352fc92E611A57d2234eEd5CdDBD89F07a88", "52cbcd959ff1297be775376237eb4b8588ae9e3cedbb2aff795bd9b8ca3e6079")
    );

    public static Pair<String, String> asignWallet() {
        Random ran = new Random();
        return wallets.get(ran.nextInt(wallets.size()));
    }
}
