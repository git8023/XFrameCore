package org.y.core.web.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

public class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

  @Override
  public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
    return new EnumConverter<>(targetType);
  }

  private class EnumConverter<T extends Enum> implements Converter<String, T> {

    private Class<T> targetType;

    private T[] constantFields;

    EnumConverter(Class<T> targetType) {
      this.targetType = targetType;
      constantFields = this.targetType.getEnumConstants();
    }

    @Override
    public T convert(String source) {
      if (StringUtils.isBlank(source)) return null;
      boolean byOrdinal = StringUtils.isNumeric(source);
      if (byOrdinal) return convertByOrdinal(Integer.valueOf(source));
      return convertByName(source);
    }

    private T convertByName(String source) {
      try {
        return (T) Enum.valueOf(this.targetType, source);
      } catch (Exception e) {
        // e.printStackTrace();
      }
      return null;
    }

    private T convertByOrdinal(Integer ordinal) {
      try {
        return (ordinal >= 0 && constantFields.length - 1 >= ordinal)
            ? constantFields[ordinal]
            : null;
      } catch (Exception e) {
        // e.printStackTrace();
      }
      return null;
    }
  }
}
