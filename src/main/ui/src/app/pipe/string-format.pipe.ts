import {Pipe, PipeTransform} from '@angular/core';
import {isFunction, isNumber} from 'util';

const handlers = {
  _exec_: (v, t) => {
    const fn = handlers[t];
    return isFunction(fn) ? fn(v) : '';
  }
};

/** 千分位分割数值 */
handlers['thousands'] = v => {
  if (!isNumber(v)) return 'NaN';
  let sv = v + '';
  let vs = sv.split('.');
  let fv = vs[1] || '';
  let intPart = vs[0].split('').reverse().reduce((acc, cur, i) => {
    return ((i % 3) ? cur : (cur + ',')) + acc;
  });
  return fv ? (`${intPart}.${fv}`) : intPart;
};

/**
 * 字符串格式化: <br>
 * <pre>
 * 当前提供:
 *     thousands: 数值千分位格式化
 * </pre>
 */
@Pipe({
  name: 'strFmt'
})
export class StringFormatPipe implements PipeTransform {

  transform(value: any, type: string): any {
    return handlers._exec_(value, type.toLowerCase());
  }

}
