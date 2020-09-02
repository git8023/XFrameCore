import {Pipe, PipeTransform} from '@angular/core';
import {datePoF} from "../util/utils";

@Pipe({
  name: 'dateFmt'
})
export class DateFmtPipe implements PipeTransform {

  constructor() {
  }

  transform(value: any, inFmt: string, outFmt: string): any {
    return datePoF(value, inFmt, outFmt);
  }

}
