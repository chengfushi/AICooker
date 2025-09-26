package com.chengfu.aicooker.controller;

import com.chengfu.aicooker.common.BaseResponse;
import com.chengfu.aicooker.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 * @author: cheng fu
 **/
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/")
    public BaseResponse<String> health() {
        return ResultUtils.success("ok");
    }
}

