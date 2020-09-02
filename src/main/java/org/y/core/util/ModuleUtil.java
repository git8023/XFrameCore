package org.y.core.util;

import lombok.extern.slf4j.Slf4j;
import org.y.core.model.entity.Module;
import org.y.core.model.entity.UploadInfo;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 模块工具
 */
@Slf4j
public abstract class ModuleUtil {

    /**
     * bat执行目录
     */
    public static String BAT_DIRECTORY;

    /**
     * 模块文件
     */
    public static class FILE {

        /**
         * 获取指定模块日志文件
         *
         * @param module 模块
         * @return 模块已设置日志目录返回日志文件 {module.logPath}/spring.log, 否则返回null
         */
        public static File log(Module module) {
            String logPath = module.getLogPath();
            return StringUtil.isBlank(logPath) ? null : new File(logPath, "spring.log");
        }
    }

    /**
     * 命令行指令
     */
    public static class CMD {

        /**
         * 启动模块
         *
         * @param module 模块
         */
        public static void startup(Module module) {
            UploadInfo jarInfo = module.getJarInfo();

            String jarPath = jarInfo.getPath();
            if (!new File(jarPath).exists())
                throw new NullPointerException("指定路径不存在: " + jarPath);

            Runtime runtime = Runtime.getRuntime();
            try {
                String[] cmdArr = {
                        "start",
                        "javaw",
                        "-jar", jarPath, "--server.port=" + module.getWebPort(),
                        "-Dfile.encoding=UTF-8",
                        "--server.servlet.session.cookie.name=" + StringUtil.genGUID(),
                        "--mid=" + module.getId(),
                        "--token=" + module.getToken(),
                        "-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
                };
                String cmd = CollectionUtil.join(Arrays.asList(cmdArr), " ");
                // cmd = "start cmd \"" + cmd + "\"";

                // 在用户临时目录下生成批处理文件
                File bat = new File(BAT_DIRECTORY, StringUtil.genGUID() + ".bat");
                FileUtil.createFileParentDir(bat);
                List<String> contentList = Arrays.asList("@echo off", cmd, "@echo on", "exit");
                FileUtil.writeLines(bat, contentList);
                // try (
                //         FileOutputStream fos = new FileOutputStream(bat);
                //         OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                //         BufferedWriter bw = new BufferedWriter(osw)
                // ) {
                //     bw.write("@echo off");
                //     bw.write(cmd);
                //     bw.write("@echo on");
                //     bw.flush();
                // }

                // 执行bat文件
                runtime.exec(new String[]{"cmd ", "/c ", bat.getAbsolutePath()});
                log.info("Module[" + module.getId() + "/" + module.getName() + "]启动文件内容: " + contentList);
                // Process process = runtime.exec(bat.getAbsolutePath());
                // new Thread(() -> {
                //     try {
                //         process.waitFor();
                //     } catch (Exception e) {
                //         log.warn("批处理执行失败: " + e.getMessage());
                //     } finally {
                //         List<String> cs = FileUtil.readLines(bat, StandardCharsets.UTF_8.toString());
                //         log.info("Module[" + module.getId() + "/" + module.getName() + "]启动文件内容: " + cs);
                //         // FileUtil.deleteQuietly(bat);
                //     }
                // }).start();
                log.info("已执行模块启动命令!");

                // Process process = runtime.exec(cmd);
                // int result = process.waitFor();
                // if (0 != result) {
                //     ErrorCode.SYSTEM_ERROR.breakOff();
                //     return;
                // }
                //
                // try (
                //         InputStream is = process.getInputStream();
                //         Reader r = new InputStreamReader(is);
                //         BufferedReader br = new BufferedReader(r)
                // ) {
                //     String s;
                //     while (null != (s = br.readLine()))
                //         log.info(">>> " + s);
                // }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 框架内交互 <br>
     * 协议: http <br>
     * 域名: localhost <br>
     * e.g: http://localhost[:port]/
     */
    public static class URL {
        /**
         * 获取主机地址, e.g: http://localhost[:port]
         *
         * @param module 模块
         * @return 主机连接地址
         */
        public static String getHost(Module module) {
            return "http://localhost:" + module.getWebPort();
        }

        /**
         * 获取关机地址, e.g: http://localhost[:port]/shutdown/{id}/{token}
         *
         * @param module 模块
         * @return 关机地址
         */
        public static String getShutdown(Module module) {
            return getHost(module) + "/shutdown/" + idAndToken(module);
        }

        /**
         * 首页连接地址, e.g: http://localhost[:port]/index/{id}/{token}
         *
         * @param module 模块信息
         * @return 首页连接地址
         */
        public static String indexUrl(Module module) {
            return getHost(module) + "/" + indexUri(module);
        }

        /**
         * 首页连接地址, e.g: index/{id}/{token}
         *
         * @param module 模块
         * @return 首页地址
         */
        public static String indexUri(Module module) {
            return "index/" + idAndToken(module);
        }
    }

    /**
     * 拼接ID/Token, e.g: {id}/{token}
     *
     * @param module 模块信息
     * @return {id}/{token}
     */
    private static String idAndToken(Module module) {
        return module.getId() + "/" + module.getToken();
    }

}
