package org.y.core.util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilTest {

    @Test
    void reverseReadLines() {
        File file = new File("J:/xframe/logger/spring.log");
        List<String> list = FileUtil.reverseReadLines(file, 5);
        System.out.println(CollectionUtil.join(list, "\n"));
    }
}