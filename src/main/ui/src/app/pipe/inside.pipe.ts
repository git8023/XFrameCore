import {Pipe, PipeTransform} from '@angular/core';
import {eachO} from "../util/utils";

@Pipe({
  name: 'inside'
})
export class InsidePipe implements PipeTransform {

  transform(value: any, o: Array<any> | object): any {
    let found = false;
    eachO(o, v => !(found = value === v));
    return found;
  }

}
