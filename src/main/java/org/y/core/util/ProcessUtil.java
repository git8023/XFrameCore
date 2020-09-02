package org.y.core.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 进程工具
 */
@Slf4j
public class ProcessUtil {

    /**
     * 获取所有jvm进程jps信息
     *
     * @return jvm进程信息
     */
    public static List<ProcessEntity> getAll() {

        // 执行jps
        Process process;
        try {
            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec("jps");
        } catch (IOException e) {
            throw new RuntimeException("执行指令jps失败:" + e.getMessage(), e);
        }

        // 获取所有进程相关信息
        List<String> processNote = Lists.newArrayList();
        try (
                InputStream in = process.getInputStream();
                Reader r = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(r)
        ) {
            String tmp;
            while (null != (tmp = br.readLine()))
                processNote.add(tmp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 解析进程描述
        List<ProcessEntity> pes = Lists.newArrayList();
        for (String pn : processNote) {
            String[] sp = pn.split(" ");
            int pid = Integer.parseInt(sp[0]);
            String pname = "";
            if (1 < sp.length) {
                List<String> sps = Lists.newArrayList(Arrays.asList(sp));
                List<String> names = sps.subList(1, sps.size());
                pname = CollectionUtil.join(names, " ");
            }

            ProcessEntity pe = new ProcessEntity(pid, pname);
            pes.add(pe);
        }

        return pes;
    }

    /**
     * 强制杀死指定占用指定端口的进程
     *
     * @param port 端口号
     */
    public static void killProcess(int port) {
        log.debug("正在查找端口[" + port + "]占用情况");

        // windows
        if ("\\".equals(File.separator)) {
            Runtime rt = Runtime.getRuntime();
            try {
                String[] commands = {
                        // 系统指令前缀
                        "cmd", "/c",
                        // 具体指令
                        "netstat", "-aon", "|findstr", String.valueOf(port)
                };
                Process prc = rt.exec(commands);
                Set<WindowsNetStat> wnss = Sets.newHashSet();
                try (
                        InputStream in = prc.getInputStream();
                        InputStreamReader isr = new InputStreamReader(in, "GBK");
                        BufferedReader br = new BufferedReader(isr)
                ) {
                    String tmp;
                    if (null != (tmp = br.readLine())) {
                        // TCP    0.0.0.0:10001          0.0.0.0:0              LISTENING       9384
                        tmp = tmp.replaceAll("([ ]+)", " ").trim();
                        WindowsNetStat wns = new WindowsNetStat(tmp);

                        if (wns.matchLocalPort(port))
                            wnss.add(wns);
                    }
                }

                if (wnss.isEmpty()) {
                    log.debug("未找到占用端口[" + port + "]的进程信息");
                    return;
                }

                for (WindowsNetStat wns : wnss) {
                    String cmd = "cmd /c TASKKILL /PID " + wns.pid + " /T /F";
                    System.out.println(cmd);
                    Process p = rt.exec(cmd);
                    try (
                            InputStream in = p.getInputStream();
                            InputStreamReader isr = new InputStreamReader(in, "GBK");
                            BufferedReader br = new BufferedReader(isr)
                    ) {
                        String tmp;
                        while (null != (tmp = br.readLine()))
                            log.info(tmp);
                    }

                    try (
                            InputStream in = p.getErrorStream();
                            InputStreamReader isr = new InputStreamReader(in, "GBK");
                            BufferedReader br = new BufferedReader(isr)
                    ) {
                        String tmp;
                        while (null != (tmp = br.readLine())) {
                            log.debug(tmp);
                        }
                    }
                }

            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }

    }

    /**
     * 获取当前进程ID
     *
     * @return 当前进程ID
     */
    public static int getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName(); // format: "pid@hostname"
        try {
            return Integer.parseInt(name.substring(0, name.indexOf('@')));
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 进程信息实体
     */
    public static class ProcessEntity {
        public final int id;
        public final String name;

        ProcessEntity(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    // TCP 0.0.0.0:10001 0.0.0.0:0 LISTENING 9384
    @ToString
    @EqualsAndHashCode(of = "pid")
    public static class WindowsNetStat {
        // TCP|UDP
        public final String protocol;
        // 本地地址:端口
        public final String local;
        // 远程地址:端口
        public final String remote;
        // 状态
        public final String status;
        // PID
        public final String pid;

        WindowsNetStat(String s) {
            String[] sp = s.split(" ");
            protocol = sp[0];
            local = sp[1];
            remote = sp[2];
            status = sp[3];
            pid = sp[4];
        }

        /**
         * 匹配本地端口
         *
         * @param port 端口
         * @return true-匹配成功, false-匹配失败
         */
        boolean matchLocalPort(int port) {
            return local.endsWith(":" + port);
        }
    }

}
