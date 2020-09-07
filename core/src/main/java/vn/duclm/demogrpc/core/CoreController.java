package vn.duclm.demogrpc.core;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@Slf4j
@RequiredArgsConstructor
public class CoreController {

    private final CoreService coreService;

    @Data
    @Builder
    static class Hello {
        private String message;
    }

    @GetMapping("hello")
    public Hello hello(@RequestParam("name") String name) {
        return Hello.builder()
                .message(coreService.callBankService(name))
                .build();
    }
}
