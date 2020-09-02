import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'jsonIndex'
})
export class JsonIndexPipe implements PipeTransform {
  transform(k: string, json: object): any {
    return json[k];
  }
}
