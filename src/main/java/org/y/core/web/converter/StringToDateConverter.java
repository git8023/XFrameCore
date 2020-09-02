package org.y.core.web.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.y.core.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 字符串到日期转换器 当前已支持 pattern 列表
 *
 * <pre>
 * final String[] FORMAT_PATTERNS = { &quot;yyyy-MM-dd HH:mm:ss&quot;, &quot;yyyy-MM-dd HH:mm&quot;, &quot;yyyy-MM-dd&quot;, &quot;yy-MM-dd&quot;,
 *         &quot;MM/dd/yyyy HH:mm:ss&quot;, &quot;MM/dd/yyyy HH:mm&quot;, &quot;MM/dd/yyyy&quot; };
 * </pre>
 *
 * @author Huang.Yong
 * @version 1.0
 */
@Slf4j
public class StringToDateConverter implements Converter<String, Date> {

    private final String[] FORMAT_PATTERNS = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd",
            "yy-MM-dd",
            "MM/dd/yyyy HH:mm:ss",
            "MM/dd/yyyy HH:mm",
            "MM/dd/yyyy",
            "HH:mm:ss",
            "HH:mm"
    };

    @Override
    public Date convert(String source) {
        if (StringUtil.isEmpty(source, true)) return null;

        boolean isNum = StringUtil.isNumber(source);
        if (isNum) {
            try {
                return new Date(Long.valueOf(source));
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }

        for (String pattern : FORMAT_PATTERNS) {
            try {
                return new SimpleDateFormat(pattern).parse(source);
            } catch (Exception e) {
                log.debug(e.getMessage(), e);
            }
        }

        return null;
    }
}
