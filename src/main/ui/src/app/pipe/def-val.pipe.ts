import {Pipe, PipeTransform} from '@angular/core';
import {isNullOrUndefined} from 'util';

@Pipe({
  name: 'defVal'
})
export class DefValPipe implements PipeTransform {

  transform(...vs: any[]): any {
    for (let i in vs)
      if (!isNullOrUndefined(vs[i]))
        return vs[i];
    if (vs.length)
      return vs[vs.length - 1];
  }

}
