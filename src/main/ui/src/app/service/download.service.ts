import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class DownloadService {

  constructor(private http: HttpClient) {
  }

  /**
   * 下载文件
   * @param url 请求地址
   */
  file(url: string): Observable<Blob> {
    return this.http.get(url, {responseType: 'blob'});
  }

}
