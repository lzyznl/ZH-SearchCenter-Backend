package com.yupi.springbootinit.Test;

import com.yupi.springbootinit.model.vo.searchVo.searchVo;
import com.yupi.springbootinit.service.searchAllService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.*;

@SpringBootTest
public class ThreadPoolExecutorTest {


    @Resource
    private searchAllService searchAllService;

    @Test
    void threadPoolExecutorTest1(){

    }
}
