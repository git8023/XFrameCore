import {Component, OnInit, ViewChild, ViewContainerRef} from '@angular/core';
import {Module} from "../../../model/entity/Module";
import {FormBuilder, FormGroup} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {catchErr, createHttpParams, handleError, handleResult, post} from "../../../util/utils";
import {NzModalService, NzNotificationService} from "ng-zorro-antd";
import {Page} from "../../../model/result/Page";
import {ModuleCondition} from "../../../model/condition/ModuleCondition";
import {catchError} from "rxjs/operators";
import {Result} from "../../../model/result/Result";
import {DownloadService} from "../../../service/download.service";
import {Router} from '@angular/router';

@Component({
  selector: 'app-store',
  templateUrl: './store.component.html',
  styleUrls: ['./store.component.scss']
})
export class StoreComponent implements OnInit {
  @ViewChild('dyRoot', {static: true, read: ViewContainerRef}) dyRoot: ViewContainerRef;

  pageData: Array<Module> = [
    {id: 1, status: 'RUNNING', type: 'SERVICE'},
    {id: 2, status: 'STOP', type: 'APPLICATION', iconInfo: {id: 4}},
    {id: 3, status: 'START_FAILED', type: 'SERVICE'}
  ];
  sortCondition: Module = {id: -1};
  rowTotal: number = 1000;
  pageIndex: number = 1;
  pageSize: number = 10;
  loading: boolean = false;
  form: FormGroup;
  boxLoading = false;
  condition: ModuleCondition = {};
  logModalVisible: boolean = false;
  logs: Array<string> = [
    "2020-07-28 20:38:34.945 INFO 7204 --- [main] org.y.logger.LoggerApplication : Starting LoggerApplication v0.0.1-SNAPSHOT on HY-Z2 with PID 7204 (J:\\xframe\\jars\\e0c99a6fe54b4931992e9353af05f884.jar started by jinlo in J:\\repository\\java\\xFrame\\XFrameCore)",
    "2020-07-28 20:38:34.948 INFO 7204 --- [main] org.y.logger.LoggerApplication : The following profiles are active: dev",
    "2020-07-28 20:38:34.945 INFO 7204 --- [main] org.y.logger.LoggerApplication : Starting LoggerApplication v0.0.1-SNAPSHOT on HY-Z2 with PID 7204 (J:\\xframe\\jars\\e0c99a6fe54b4931992e9353af05f884.jar started by jinlo in J:\\repository\\java\\xFrame\\XFrameCore)",
    "2020-07-28 20:38:34.948 INFO 7204 --- [main] org.y.logger.LoggerApplication : The following profiles are active: dev",
    "2020-07-28 20:38:34.945 INFO 7204 --- [main] org.y.logger.LoggerApplication : Starting LoggerApplication v0.0.1-SNAPSHOT on HY-Z2 with PID 7204 (J:\\xframe\\jars\\e0c99a6fe54b4931992e9353af05f884.jar started by jinlo in J:\\repository\\java\\xFrame\\XFrameCore)",
    "2020-07-28 20:38:34.948 INFO 7204 --- [main] org.y.logger.LoggerApplication : The following profiles are active: dev",
    "2020-07-28 20:38:34.945 INFO 7204 --- [main] org.y.logger.LoggerApplication : Starting LoggerApplication v0.0.1-SNAPSHOT on HY-Z2 with PID 7204 (J:\\xframe\\jars\\e0c99a6fe54b4931992e9353af05f884.jar started by jinlo in J:\\repository\\java\\xFrame\\XFrameCore)",
    "2020-07-28 20:38:34.948 INFO 7204 --- [main] org.y.logger.LoggerApplication : The following profiles are active: dev",
    "2020-07-28 20:38:34.945 INFO 7204 --- [main] org.y.logger.LoggerApplication : Starting LoggerApplication v0.0.1-SNAPSHOT on HY-Z2 with PID 7204 (J:\\xframe\\jars\\e0c99a6fe54b4931992e9353af05f884.jar started by jinlo in J:\\repository\\java\\xFrame\\XFrameCore)",
    "2020-07-28 20:38:34.948 INFO 7204 --- [main] org.y.logger.LoggerApplication : The following profiles are active: dev",
    "2020-07-28 20:38:34.945 INFO 7204 --- [main] org.y.logger.LoggerApplication : Starting LoggerApplication v0.0.1-SNAPSHOT on HY-Z2 with PID 7204 (J:\\xframe\\jars\\e0c99a6fe54b4931992e9353af05f884.jar started by jinlo in J:\\repository\\java\\xFrame\\XFrameCore)",
    "2020-07-28 20:38:34.948 INFO 7204 --- [main] org.y.logger.LoggerApplication : The following profiles are active: dev",
    "2020-07-28 20:38:34.945 INFO 7204 --- [main] org.y.logger.LoggerApplication : Starting LoggerApplication v0.0.1-SNAPSHOT on HY-Z2 with PID 7204 (J:\\xframe\\jars\\e0c99a6fe54b4931992e9353af05f884.jar started by jinlo in J:\\repository\\java\\xFrame\\XFrameCore)",
    "2020-07-28 20:38:34.948 INFO 7204 --- [main] org.y.logger.LoggerApplication : The following profiles are active: dev",
    "2020-07-28 20:38:34.945 INFO 7204 --- [main] org.y.logger.LoggerApplication : Starting LoggerApplication v0.0.1-SNAPSHOT on HY-Z2 with PID 7204 (J:\\xframe\\jars\\e0c99a6fe54b4931992e9353af05f884.jar started by jinlo in J:\\repository\\java\\xFrame\\XFrameCore)",
    "2020-07-28 20:38:34.948 INFO 7204 --- [main] org.y.logger.LoggerApplication : The following profiles are active: dev",
    "2020-07-28 20:38:34.945 INFO 7204 --- [main] org.y.logger.LoggerApplication : Starting LoggerApplication v0.0.1-SNAPSHOT on HY-Z2 with PID 7204 (J:\\xframe\\jars\\e0c99a6fe54b4931992e9353af05f884.jar started by jinlo in J:\\repository\\java\\xFrame\\XFrameCore)",
    "2020-07-28 20:38:34.948 INFO 7204 --- [main] org.y.logger.LoggerApplication : The following profiles are active: dev",
    "2020-07-28 20:38:34.945 INFO 7204 --- [main] org.y.logger.LoggerApplication : Starting LoggerApplication v0.0.1-SNAPSHOT on HY-Z2 with PID 7204 (J:\\xframe\\jars\\e0c99a6fe54b4931992e9353af05f884.jar started by jinlo in J:\\repository\\java\\xFrame\\XFrameCore)",
    "2020-07-28 20:38:34.948 INFO 7204 --- [main] org.y.logger.LoggerApplication : The following profiles are active: dev",
    "2020-07-28 20:38:34.945 INFO 7204 --- [main] org.y.logger.LoggerApplication : Starting LoggerApplication v0.0.1-SNAPSHOT on HY-Z2 with PID 7204 (J:\\xframe\\jars\\e0c99a6fe54b4931992e9353af05f884.jar started by jinlo in J:\\repository\\java\\xFrame\\XFrameCore)",
    "2020-07-28 20:38:34.948 INFO 7204 --- [main] org.y.logger.LoggerApplication : The following profiles are active: dev",
    "2020-07-28 20:38:34.945 INFO 7204 --- [main] org.y.logger.LoggerApplication : Starting LoggerApplication v0.0.1-SNAPSHOT on HY-Z2 with PID 7204 (J:\\xframe\\jars\\e0c99a6fe54b4931992e9353af05f884.jar started by jinlo in J:\\repository\\java\\xFrame\\XFrameCore)",
    "2020-07-28 20:38:34.948 INFO 7204 --- [main] org.y.logger.LoggerApplication : The following profiles are active: dev",
    "2020-07-28 20:38:34.945 INFO 7204 --- [main] org.y.logger.LoggerApplication : Starting LoggerApplication v0.0.1-SNAPSHOT on HY-Z2 with PID 7204 (J:\\xframe\\jars\\e0c99a6fe54b4931992e9353af05f884.jar started by jinlo in J:\\repository\\java\\xFrame\\XFrameCore)",
    "2020-07-28 20:38:34.948 INFO 7204 --- [main] org.y.logger.LoggerApplication : The following profiles are active: dev",
    "2020-07-28 20:38:34.945 INFO 7204 --- [main] org.y.logger.LoggerApplication : Starting LoggerApplication v0.0.1-SNAPSHOT on HY-Z2 with PID 7204 (J:\\xframe\\jars\\e0c99a6fe54b4931992e9353af05f884.jar started by jinlo in J:\\repository\\java\\xFrame\\XFrameCore)",
    "2020-07-28 20:38:34.948 INFO 7204 --- [main] org.y.logger.LoggerApplication : The following profiles are active: dev",
    "2020-07-28 20:38:34.945 INFO 7204 --- [main] org.y.logger.LoggerApplication : Starting LoggerApplication v0.0.1-SNAPSHOT on HY-Z2 with PID 7204 (J:\\xframe\\jars\\e0c99a6fe54b4931992e9353af05f884.jar started by jinlo in J:\\repository\\java\\xFrame\\XFrameCore)",
    "2020-07-28 20:38:34.948 INFO 7204 --- [main] org.y.logger.LoggerApplication : The following profiles are active: dev",
    "2020-07-28 20:38:34.945 INFO 7204 --- [main] org.y.logger.LoggerApplication : Starting LoggerApplication v0.0.1-SNAPSHOT on HY-Z2 with PID 7204 (J:\\xframe\\jars\\e0c99a6fe54b4931992e9353af05f884.jar started by jinlo in J:\\repository\\java\\xFrame\\XFrameCore)",
    "2020-07-28 20:38:34.948 INFO 7204 --- [main] org.y.logger.LoggerApplication : The following profiles are active: dev",
    "2020-07-28 20:38:34.945 INFO 7204 --- [main] org.y.logger.LoggerApplication : Starting LoggerApplication v0.0.1-SNAPSHOT on HY-Z2 with PID 7204 (J:\\xframe\\jars\\e0c99a6fe54b4931992e9353af05f884.jar started by jinlo in J:\\repository\\java\\xFrame\\XFrameCore)",
    "2020-07-28 20:38:34.948 INFO 7204 --- [main] org.y.logger.LoggerApplication : The following profiles are active: dev",
    "2020-07-28 20:38:34.945 INFO 7204 --- [main] org.y.logger.LoggerApplication : Starting LoggerApplication v0.0.1-SNAPSHOT on HY-Z2 with PID 7204 (J:\\xframe\\jars\\e0c99a6fe54b4931992e9353af05f884.jar started by jinlo in J:\\repository\\java\\xFrame\\XFrameCore)",
    "2020-07-28 20:38:34.948 INFO 7204 --- [main] org.y.logger.LoggerApplication : The following profiles are active: dev",
    "2020-07-28 20:38:34.945 INFO 7204 --- [main] org.y.logger.LoggerApplication : Starting LoggerApplication v0.0.1-SNAPSHOT on HY-Z2 with PID 7204 (J:\\xframe\\jars\\e0c99a6fe54b4931992e9353af05f884.jar started by jinlo in J:\\repository\\java\\xFrame\\XFrameCore)",
    "2020-07-28 20:38:34.948 INFO 7204 --- [main] org.y.logger.LoggerApplication : The following profiles are active: dev",
  ];
  module: Module = {};
  logIndex: number = 1;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private notification: NzNotificationService,
    private modalService: NzModalService,
    private downloadService: DownloadService,
    private router: Router,
  ) {
  }

  ngOnInit() {
    this.form = this.fb.group({
      author: [null],
      name: [null],
      status: [''],
    });

    this.search();
  }

  // 排序
  sort(name: string, value: string) {
    console.log(name, value);
  }

  // 搜索列表
  search() {
    this.loading = true;

    this.condition.pageIndex = this.pageIndex;
    this.condition.pageSize = this.pageSize;
    let param = createHttpParams(this.condition);

    // this.http.post('/module/list', param)
    // .pipe(
    // delay(3000),
    // map(() => {
    //   this.notification.warning('警告', '请求超时');
    //   return Result.timeout;
    // }),
    // catchError(handleError('search', Result.fail))
    // ...
    // fn,
    // )
    //   .subscribe(handleResult(this.notification, ret => {
    //     let page = ret.data as Page;
    //     this.rowTotal = page.rowCount;
    //     this.pageData = page.data;
    //   }, false, _ => this.loading = false));

    post(this.http, '/module/list', this.condition, ...catchErr())
      .subscribe(handleResult(this.notification, ret => {
        let page = ret.data as Page;
        this.rowTotal = page.rowCount;
        this.pageData = page.data;
      }, false, _ => this.loading = false));
  }

  // 改变状态
  changeStatus(module: Module, status: string) {
    this.boxLoading = true;
    this.http.get(`/module/status/${module.id}/${status}`)
      .subscribe(handleResult(
        this.notification,
        ret => module.status = ret.flag ? ret.data : module.status,
        true,
        _ => this.boxLoading = false
      ));
  }

  // 删除模块
  delModule(data: Module) {
    this.modalService.confirm({
      nzTitle: '提示',
      nzContent: `确定要删除模块【${data.name}】吗? <span class="red">该操作不可逆!!!</span>`,
      nzOkText: '删除',
      nzCancelText: '取消',
      nzOnOk: () => {
        this.loading = true;
        this.http.get('/module/del/' + data.id).subscribe(handleResult(
          this.notification,
          () => this.search(),
          true,
          () => this.loading = false
        ));
      }
    });
  }

  // 更新过滤条件
  updateCondition() {
    this.condition.name = this.form.value.name;
    this.condition.author = this.form.value.author;
    this.condition.status = this.form.value.status;
    this.search();
  }

  // 显示日志弹出框
  showLogModal(data: Module) {
    this.logIndex = 1;
    this.logModalVisible = true;
    this.module = data;

    // 加载日志信息
    this.http.get(`/module/log/${this.module.id}/${this.logIndex}`)
      .pipe(catchError(handleError("showLogModal", Result.fail)))
      .subscribe(handleResult(
        this.notification,
        ({data}) => this.logs = data,
        false
      ));
  }

  // 日志下载
  downloadLog(data: Module) {
    this.downloadService
      .file(`/module/logDownload/${data.id}`)
      .pipe()
      .subscribe(blob => {
        const a = document.createElement('a');
        const objectUrl = URL.createObjectURL(blob);
        a.href = objectUrl;
        a.download = `app-${data.name}.log`;
        a.click();
        URL.revokeObjectURL(objectUrl);
      });
  }

  // 编辑模块
  editModule(data: Module) {
    this.router.navigate([`/dashboard/module/edit/`], {
      queryParams: {
        id: data.id,
        iconId: data.iconInfo.id
      }
    }).finally();
  }
}
