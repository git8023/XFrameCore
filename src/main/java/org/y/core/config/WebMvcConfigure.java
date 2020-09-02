package org.y.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.y.core.web.converter.StringToDateConverter;
import org.y.core.web.converter.StringToEnumConverterFactory;

@Configuration
public class WebMvcConfigure implements WebMvcConfigurer {

    // @Autowired
    // private ErrorHttpInterceptor loginInterceptor;
    //
    // @Autowired
    // private SingleLoginInterceptor singleLoginInterceptor;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToDateConverter());
        registry.addConverterFactory(new StringToEnumConverterFactory());
    }

    // @Override
    // public void addInterceptors(InterceptorRegistry registry) {
    //     registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
    //     registry.addInterceptor(singleLoginInterceptor).addPathPatterns("/**");
    // }
}
