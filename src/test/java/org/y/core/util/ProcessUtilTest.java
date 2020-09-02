package org.y.core.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProcessUtilTest {

    @Test
    void getAll() {
        List<ProcessUtil.ProcessEntity> ppe = ProcessUtil.getAll();
        System.out.println(ppe);
    }

    @Test
    void killProcess() {
        ProcessUtil.killProcess(10001);
    }
}