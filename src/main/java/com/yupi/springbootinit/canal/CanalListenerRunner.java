package com.yupi.springbootinit.canal;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


public class CanalListenerRunner implements ApplicationRunner {

    @Resource
    private CanalDataSynchronization canalDataSynchronization;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        canalDataSynchronization.dataSynchronization();
    }
}
