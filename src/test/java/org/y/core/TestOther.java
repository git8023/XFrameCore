package org.y.core;

import org.junit.jupiter.api.Test;
import org.y.core.util.FileUtil;

import java.io.*;

public class TestOther {

    @Test
    void testLineNumberReader() throws Exception {
        // try (
        //         FileReader fr = new FileReader("J:/xframe/logger/spring.log");
        //         LineNumberReader lnr = new LineNumberReader(fr)
        // ) {
        //     lnr.setLineNumber(2);
        //     String s = lnr.readLine();
        //     System.out.println(s);
        // }
        System.out.println(File.separator);
    }

    @Test
    void testRAF() throws Exception {
        try (
                RandomAccessFile rf = new RandomAccessFile("J:/xframe/logger/spring.log", "r")
        ) {
            long len = rf.length();
            long start = rf.getFilePointer();
            System.out.println(start);
            long nextend = start + len - 1;
            String line;
            rf.seek(nextend);
            int c;
            while (nextend > start) {
                c = rf.read();
                if (c == '\n' || c == '\r') {
                    line = rf.readLine();
                    System.out.println(line);
                    nextend--;
                }
                nextend--;
                rf.seek(nextend);
                if (nextend == 0) {// 当文件指针退至文件开始处，输出第一行
                    System.out.println(rf.readLine());
                }
            }
        }
    }

    @Test
    void testOpenCmd() throws Exception {
        Runtime.getRuntime().exec(new String[]{
                "cmd", "/c",
                "D:/_repository/java/xframe/XFrameCore/src/test/java/org/y/core/t.bat"
        });
        // System.out.println(new File("").getAbsolutePath());

    }
}
