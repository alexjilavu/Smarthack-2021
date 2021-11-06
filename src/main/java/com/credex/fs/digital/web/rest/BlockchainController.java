package com.credex.fs.digital.web.rest;

import com.credex.fs.digital.service.BlockchainService;
import java.util.concurrent.ExecutionException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class BlockchainController {

    private final BlockchainService blockchainService;

    public BlockchainController(BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }

    @GetMapping("/test")
    public void test() throws ExecutionException, InterruptedException {
        blockchainService.test();
    }
}
