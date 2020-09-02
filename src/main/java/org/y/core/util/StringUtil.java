package org.y.core.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public final class StringUtil extends StringUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtil.class);
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String WINDOWS_PREFIX = "windows";
    public static final String EMPTY_STRING = "";
    private static final String SEPARATOR_OF_UNIX_FILE = "/";
    private static final String SEPARATOR_OF_WINDOWS_FILE = "\\";

    /**
     * Windows 换行符
     */
    public static final String WINDOWS_NEXT_LINE = "\r\n";

    /**
     * 追加有效字符串(无效字符串:null/空字符串"")
     *
     * @param sb        字符串构建器
     * @param targetStr 目标字符串
     * @return boolean true-追加成功, false-追加失败
     */
    public static boolean appendEffectiveVal(StringBuilder sb, String targetStr) {
        if (null != sb && isNotEmpty(targetStr, true)) {
            sb.append(targetStr);
            return true;
        }
        return false;
    }

    /**
     * 获取对象类型属性的get方法名
     *
     * @param propertyName 属性名
     * @return String "get"开头且参数(propertyName)值首字母大写的字符串
     */
    public static String convertToReflectGetMethod(String propertyName) {
        return "get" + toFirstUpChar(propertyName);
    }

    /**
     * 获取对象类型属性的set方法名
     *
     * @param propertyName 属性名
     * @return String "set"开头且参数(propertyName)值首字母大写的字符串
     */
    public static String convertToReflectSetMethod(String propertyName) {
        return "set" + toFirstUpChar(propertyName);
    }

    /**
     * 转换为模糊查询条件值, 内部包含单引号(')或百分号(%)时, 使用转义符号进行转义(\'或\%), 并在前后添加百分号(%)
     *
     * @param likeVal 模糊参数, 值为null时, 返回null
     * @return String 转换后模糊条件值("%" + likeVal + "%")
     */
    public static String convertVagueCondition(String likeVal) {
        if (likeVal == null) return null;

        likeVal =
                likeVal
                        .replace(SEPARATOR_OF_WINDOWS_FILE, "\\\\'")
                        .replace("'", "\\'")
                        .replace("%", "\\%")
                        .replace("_", "\\_");
        return "%" + likeVal + "%";
    }

    /**
     * 计算源字符串包含目标字符串的数量
     *
     * @param source 源字符串
     * @param target 目标字符串
     * @return int 数量值, -1:任意参数非法时
     */
    public static int countSubstring(String source, String target) {
        if (source == null || target == null) {
            return -1;
        }

        int srcLen = source.length();
        int subLen = target.length();
        if (srcLen < subLen) {
            return 0;
        }

        if (source.equals(target)) {
            return 1;
        }

        char[] srcChs = source.toCharArray();
        char[] subChs = target.toCharArray();
        char[] tChs = new char[subChs.length];
        int count = 0;

        for (int i = 0, maxLen = srcLen - subLen + 1; i < maxLen; i++) {
            if (srcChs[i] == subChs[0] && srcChs[i + subLen - 1] == subChs[subLen - 1]) {
                System.arraycopy(srcChs, i, tChs, 0, subLen);
                if (Arrays.equals(tChs, subChs)) {
                    ++count;
                    i += subLen;
                }
            }
        }

        return count;
    }

    /**
     * 验证两个对象是否 equals
     *
     * @param src    第一个对象
     * @param target 第二个对象
     * @return boolean 两个对象equals返回true, 否则返回false
     */
    public static boolean equalsTwo(Object src, Object target) {
        return equalsTwo(src, target, false);
    }

    /**
     * 验证两个对象是否 equals
     *
     * @param src        第一个对象
     * @param target     第二个对象
     * @param ignoreCase 字符串比较时是否忽略大小写
     * @return boolean 两个对象equals返回true, 否则返回false
     */
    public static boolean equalsTwo(Object src, Object target, boolean ignoreCase) {
        // 两个都为null,
        // 或两个引用指向同一个地址
        if (target == src) {
            return true;
        }

        // 剩余条件: 两个对象不相等(或引用地址不同)
        // 其中一个为null
        if (null == target || null == src) {
            return false;
        }

        // 剩余条件: 两个对象都不为null,且不相等
        // src 是字符串
        if (src instanceof String) {
            if (target instanceof String && ignoreCase) {
                return ((String) src).equalsIgnoreCase((String) target);
            }
        }

        return src.equals(target);
    }

    /**
     * 获取 classpath 路径
     *
     * @return String classpath 路径, 总是以"/"结尾
     * @throws Exception
     */
    public static String getClassPath() throws Exception {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("");
        String basePath = resource.toURI().getPath();
        if (isWindowsSys()) {
            basePath = basePath.substring(1);
        }

        basePath = basePath.replace(SEPARATOR_OF_WINDOWS_FILE, SEPARATOR_OF_UNIX_FILE);
        if (!basePath.endsWith(SEPARATOR_OF_UNIX_FILE)) {
            basePath += SEPARATOR_OF_UNIX_FILE;
        }

        return basePath;
    }

    /**
     * 获取项目根目录, 总是以"/"结尾
     *
     * @return String 项目根目录
     */
    public static String getWebProjectPath() {
        try {
            return StringUtil.getClassPath().split("WEB-INF")[0];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取完整文件路径(盘符路径)
     *
     * @param path 文件路径, 如果以文件分隔符开头, 且不是 Windows系统时, 将原样返回
     * @return String 完整文件路径
     */
    public static String getFullFilePath(String path) {
        if (isEmpty(path, true)) throw new RuntimeException("路径不允许为空");

        String rPath = path.trim();
        boolean isWinSys = isWindowsSys();
        String classPath;
        try {
            classPath = getClassPath();
        } catch (Exception e) {
            throw new RuntimeException("ClassPath获取失败");
        }

        boolean isRootPath = isStartSeparator(rPath);
        if (isWinSys) {
            rPath = rPath.replace(SEPARATOR_OF_UNIX_FILE, SEPARATOR_OF_WINDOWS_FILE);
            if (isRootPath) rPath = rPath.substring(1);
            return rPath.matches("[a-zA-Z][:][\\\\].+") ? rPath : (classPath + rPath);
        } else {
            rPath = rPath.replace(SEPARATOR_OF_WINDOWS_FILE, SEPARATOR_OF_UNIX_FILE);
            return (rPath.startsWith(classPath) ? rPath : (classPath + rPath));
        }
    }

    /**
     * 是否完整路径
     *
     * @param path 目标路径
     * @return boolean true-完整路径, false-相对路径
     */
    public static boolean isFullPath(String path) {
        if (isEmpty(path, true)) {
            return false;
        }

        if (isWindowsSys()) {
            return path.matches("[a-zA-Z][:][\\\\].+");
        } else {
            return path.startsWith(SEPARATOR_OF_UNIX_FILE);
        }
    }

    /**
     * 检测字符串对象是否为 null 或 length() == 0
     *
     * @param target 字符串
     * @param isTrim 是否去掉前后空格
     * @return boolean true-空对象, false-非空对象
     */
    public static boolean isEmpty(Object target, boolean isTrim) {
        return null == target || isEmpty(String.valueOf(target), isTrim);
    }

    /**
     * 检测字符串对象是否为 null 或 length() == 0
     *
     * @param target 字符串
     * @param isTrim 是否去掉前后空格
     * @return boolean true-空对象, false-非空对象
     */
    public static boolean isEmpty(String target, boolean isTrim) {
        if (target == null) return true;
        if (isTrim) target = target.trim();
        return target.length() == 0;
    }

    /**
     * 验证指定字符串是否不时null或空串
     *
     * @param target 目标字符串
     * @param isTrim 验证时是否去掉前后空格
     * @return boolean true-不是null值且不是空串, false-是空串或null值
     */
    public static boolean isNotEmpty(String target, boolean isTrim) {
        return !isEmpty(target, isTrim);
    }

    /**
     * 是否纯数值字符串
     *
     * @param target 目标字符串
     * @return boolean true-是纯数值字符串(或整数, 或小数, 或指数), false-不是纯数值字符串(或null值, 或0长度值, 或全空白字符值)
     */
    public static boolean isNumber(String target) {
        final Pattern pattern = Pattern.compile("^[+-]?((\\d{0,}(\\.\\d*)?[f|F|d|D]?)|(\\d+[l|L]?))$");
        return !isEmpty(target, true) && pattern.matcher(target).matches();
    }

    /**
     * 路径是否以文件分隔符开头
     *
     * @param path 目标路径
     * @return boolean true-路径以文件分隔符开头, false-不是文件分隔符开头
     */
    private static boolean isStartSeparator(String path) {
        return path.startsWith(FILE_SEPARATOR);
    }

    /**
     * 判断当前系统是否 windows 系统
     *
     * @return boolean true-是 windows 系统, false-不是 windows 系统
     */
    public static boolean isWindowsSys() {
        return System.getProperty("os.name").toLowerCase().startsWith(WINDOWS_PREFIX);
    }

    /**
     * 将List<String>转为List<Integer>
     *
     * @param str List<String> List中的元素必须是数字字符串。
     * @return List<Integer>
     */
    public static List<Integer> listStringToInteger(List<String> str) {
        if (null != str && str.size() > 0) {
            List<Integer> listInt = new ArrayList<Integer>();
            for (int i = 0; i < str.size(); i++) {
                listInt.add(Integer.parseInt(str.get(i)));
            }
            return listInt;
        }
        return null;
    }

    /**
     * 将null字符串转换为空字符串("")
     *
     * @param target 目标字符串
     * @param isTrim <i>target</i>非空时，返回值是否去掉前后空格；true-去掉前后空格，false返回原字符串
     * @return String 转换后字符串
     */
    public static String nullToEmpty(String target, boolean isTrim) {
        if (isEmpty(target, isTrim)) {
            target = "";
        } else if (isTrim) {
            target = target.trim();
        }
        return target;
    }

    /**
     * 将数组转换为字符串
     *
     * @param targetArray 目标数组
     * @return String 数组为null时返回"null", 否则返回 Arrays.toString()
     */
    public static String toArrayString(Object[] targetArray) {
        return (null == targetArray ? "null" : Arrays.toString(targetArray));
    }

    /**
     * 将字符串的首字母大写
     *
     * @param target 目标字符串
     * @return String 首字母大写的字符串
     */
    public static String toFirstUpChar(String target) {
        StringBuilder sb = new StringBuilder(target);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * 字符串解析
     *
     * @param sb          字符串构建器
     * @param emptyToNull 字符串构建器为空或null时处理标识, true-返回null, false-返回空字符串
     * @return String 解析后字符串
     */
    public static String toString(StringBuilder sb, boolean emptyToNull) {
        if (null == sb || 0 == sb.length()) {
            return (emptyToNull ? null : "");
        }

        return sb.toString();
    }

    public static String toString(Collection<?> c, String separator) {
        StringBuilder buf = new StringBuilder();
        for (Object obj : c) if (null != obj) buf.append(separator).append(obj);
        if (0 == buf.length()) return null;
        String s = buf.toString();
        return s.substring(separator.length());
    }

    /**
     * 获取相对路径
     *
     * @param targetPath 目标路径
     * @return String 相对路径, null-<i>targetPath</i>非法或获取系统前缀失败
     */
    public static String getRelativePath(String targetPath) {
        try {
            if (isNotEmpty(targetPath, true)) {
                String classPath = getClassPath();
                String basePath = classPath.split("WEB-INF")[0];

                basePath = basePath.replace(SEPARATOR_OF_WINDOWS_FILE, SEPARATOR_OF_UNIX_FILE);
                targetPath = targetPath.replace(SEPARATOR_OF_WINDOWS_FILE, SEPARATOR_OF_UNIX_FILE);
                if (targetPath.startsWith(basePath)) {
                    int prefixLen = basePath.length();
                    return targetPath.substring(prefixLen);
                }
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        return null;
    }

    /**
     * 检测是否包含无效字符串(null或0长度)
     *
     * @param isTrim  true-检测时去掉前后空格
     * @param targets 目标字符串
     * @return boolean false-至少包含一个无效字符串, true-所有被检测字符串都有效
     */
    public static boolean valid(boolean isTrim, Object... targets) {
        if (null == targets || 0 >= targets.length) {
            return false;
        }

        for (int i = 0; i < targets.length; i++) {
            Object target = targets[i];
            if (isEmpty(target, isTrim)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 转换为UNIX文件路径分隔符
     *
     * @param targetPath 目标路径
     * @return String 转换后路径, <i>targetPath</i>非法时返回null
     */
    public static String convertToUnixPath(String targetPath) {
        return (isEmpty(targetPath, true)
                ? null
                : targetPath.replace(SEPARATOR_OF_WINDOWS_FILE, SEPARATOR_OF_UNIX_FILE));
    }

    /**
     * 获取文件名
     *
     * @param filePath 文件路径, 路径非法时总是返回null值
     * @return String 路径中最后一个文件分隔符之后的字符串值
     */
    public static String getFileName(String filePath) {
        if (isNotEmpty(filePath, true)) {
            filePath = convertToUnixPath(filePath);
            String fileName =
                    filePath.substring(filePath.lastIndexOf(StringUtil.SEPARATOR_OF_UNIX_FILE) + 1);
            return fileName;
        } else {
            return null;
        }
    }

    /**
     * 转换为字符串
     *
     * @param valOjb 目标对象, null时返回{@link #EMPTY_STRING}
     * @param isTrim 是否去掉前后空格
     * @return String 转换后字符串
     */
    public static String toStringEmpty(Object valOjb, boolean isTrim) {
        String val = EMPTY_STRING;
        if (null != valOjb) {
            val = String.valueOf(valOjb);
        }

        if (isTrim) {
            val = val.trim();
        }

        return val;
    }

    /**
     * MD5摘要
     *
     * @param str 目标数据
     * @return 获取成功返回MD5摘要字符串, 否则抛出异常
     */
    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成GUID
     *
     * @return GUID
     */
    public static String genGUID() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    /**
     * 数字转字符串, 不够位数时前置填充"0"
     *
     * @param val         目标值
     * @param tenMultiple 10的倍数
     * @return 数字字符串
     */
    public static String toNumberString(int val, int tenMultiple) {
        if (1 > tenMultiple) return String.valueOf(val);
        int incr = tenMultiple;

        long tv = 10;
        while (--incr > 0) tv *= 10;
        if (val >= tv) return String.valueOf(val);

        String sv = String.valueOf(val);
        StringBuilder ret = new StringBuilder();
        for (int i = tenMultiple - sv.length(); i > 0; i--) ret.append("0");
        return ret.toString() + val;
    }

    /**
     * 字符串转码
     *
     * @param s     字符串
     * @param code1 当前编码
     * @param code2 转码后
     * @return 转码后字符串
     */
    public static String encode(String s, String code1, String code2) {
        try {
            return new String(s.getBytes(code1), code2);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取第一个有效字符串
     *
     * <pre>isNotBlank(args[0]) || isNotBlank(args[1]) ... || args[args.length-1]</pre>
     *
     * @param args 可选值列表, 不允许为空且至少需要一个值
     * @return 第一个有效字符串, 如果都无效返回最后一个字符串
     */
    public static String or(String... args) {
        String last = args[args.length - 1];
        for (String s : args) if (isNotBlank(s)) return s;
        return last;
    }

    /**
     * 长整型转千分位
     *
     * @param v 数值
     * @return 千分位分割
     */
    public static String toThousandsSeparator(long v) {
        DecimalFormat df = new DecimalFormat(",###");
        return df.format(v);
    }

    /**
     * 长整型转千分位
     *
     * @param v 数值
     * @return 千分位分割
     */
    public static String toThousandsSeparator(double v) {
        DecimalFormat df = new DecimalFormat(",###.00");
        return df.format(v);
    }

    /**
     * 替换模板字符串
     *
     * @param ruleDict 规则字典
     * @param tplStr   模板字符串
     * @return 替换后字符串
     */
    public static String replace(Map<String, String> ruleDict, String tplStr) {
        for (Map.Entry<String, String> me : ruleDict.entrySet()) {
            String rule = me.getKey();
            String ruleValue = me.getValue();
            tplStr = tplStr.replaceAll(rule, ruleValue);
        }
        return tplStr;
    }

    /**
     * 对象序列化为字符串
     *
     * @param obj 目标都西昂
     * @return 序列化字符串
     */
    public static <T extends Serializable> String serialize(T obj) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            return byteArrayOutputStream.toString("ISO-8859-1");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(objectOutputStream);
            IOUtils.closeQuietly(byteArrayOutputStream);
        }
    }

    /**
     * 对象反序列化
     *
     * @param str 序列化字符串
     * @param <T> 对象类型
     * @return 目标对象
     * @see #serialize(Serializable)
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T serializeToObject(String str) {
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return (T) objectInputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(objectInputStream);
            IOUtils.closeQuietly(byteArrayInputStream);
        }
    }

    /**
     * Base64编码
     *
     * @param src 源字符串
     * @return 编码字符串
     */
    public static String base64Enc(String src) {
        byte[] bytes = Base64.getEncoder().encode(src.getBytes(StandardCharsets.UTF_8));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Base64解码
     *
     * @param src 编码字符串
     * @return 源字符串
     */
    public static String base64Dec(String src) {
        byte[] bytes = Base64.getDecoder().decode(src.getBytes(StandardCharsets.UTF_8));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 生成数字随机数
     *
     * @param length 长度
     * @return 随机数
     */
    public static String randomNumber(int length) {
        double r = Math.random();
        String s = Double.toString(r);
        StringBuilder buf = new StringBuilder(s).reverse();
        return buf.substring(0, length);
    }
}
