import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {NzMessageService, NzNotificationService, UploadFile, UploadFilter, UploadXHRArgs} from 'ng-zorro-antd';
import {HttpClient, HttpEvent, HttpEventType, HttpRequest, HttpResponse} from '@angular/common/http';
import {catchErr, createHttpParams, handleResult, post, validNgForm} from '../../../util/utils';
import {Result} from '../../../model/result/Result';
import {Observable, Observer} from 'rxjs';

@Component({
  selector: 'app-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss']
})
export class EditComponent implements OnInit {
  // 待上传文件列表
  moduleFile = [];
  // 表单组件
  validateForm: FormGroup;
  // 上传文件过滤器
  moduleFileFilters: UploadFilter[] = [
    {
      name: 'type',
      fn: (fileList: UploadFile[]) => {
        const filterFiles = fileList.filter(f => /^.*.jar$/.test(f.name));
        if (filterFiles.length !== fileList.length) {
          this.msg.error("文件格式不是有效jar文件");
          return filterFiles;
        }

        return fileList;
      }
    }
  ];
  // 加载中...标记
  loading: boolean = false;
  // 被修改地模块ID
  moduleId: any;
  // 原始图标
  originIconUrl: string = 'http://localhost/png/57027f64e1d4455eb647322eaf5f997c.png';

  // 文件上传前
  moduleFileBeforeUpload = (file: UploadFile): boolean => {
    this.moduleFile = [file];
    this.validateForm.controls.jarInfoId.setValue(file.name);
    return true;
  };

  // 自定义请求行为
  moduleCustomReq = (item: UploadXHRArgs) => {
    // 拼装请求参数
    const formData = new FormData();
    this.moduleFile.forEach((file: any) => formData.append('file', file));
    const req = new HttpRequest('POST', item.action!, formData, {
      reportProgress: true,
      withCredentials: true
    });

    // 必须返回一个 Observable
    return this.http.request(req).subscribe(
      (event: HttpEvent<any>) => {
        // 上传进度
        if (event.type === HttpEventType.UploadProgress) {
          if (event.total! > 0) {
            (event as any).percent = (event.loaded / event.total!) * 100;
          }
          item.onProgress!(event, item.file!);
          return;
        }

        // 处理响应
        if (event instanceof HttpResponse) {
          item.onSuccess!(event.body, item.file!, event);

          let result = event.body as Result;
          if (!result.flag)
            this.msg.error(result.message);
          this.validateForm.controls.jarInfoId.setValue(result.data);
        }
      },
      err => {
        console.log(err);
        this.msg.error('Jar包上传失败');
      }
    );
  };

  // 点击了文件列表的 x 按钮
  onModuleFileRemove = (file: UploadFile): boolean => {
    this.moduleFile = this.moduleFile.filter(f => f.uid !== file.uid);
    this.validateForm.controls.jarInfoId.setValue(null);
    return true;
  };

  // 应用程序图标
  moduleIconFile/*: Array<UploadFile>*/ = [];

  // 应用程序图标上传前
  moduleIconBeforeUpload = (file: UploadFile) => {
    return new Observable((observer: Observer<boolean>) => {
      // 类型
      let typeOk: boolean = /\.(png|jpg|gif|jpeg|webp)$/.test(file.name);
      if (!typeOk) {
        this.msg.error("仅支持格式: .png, .jpg, .gif, .jpeg");
        observer.complete();
        return;
      }

      // 大小
      let sizeOk: boolean = file.size / 1024 < 500;
      if (!sizeOk) {
        this.msg.error("启动图片必须在500KB以内");
        observer.complete();
        return;
      }

      // 尺寸
      this.iconDimensionCheck(file).then(dimensionOk => {
        if (!dimensionOk) {
          this.msg.error("启动图标尺寸仅支持 100*100 以下");
          observer.complete();
          return;
        }

        this.moduleIconFile = [file];
        this.validateForm.controls.iconInfoId.setValue(file.name);
        observer.next(typeOk && sizeOk && dimensionOk);
        observer.complete();
      });
    });
  };

  // 图片上传请求拦截
  moduleIconCustomReq = (item: UploadXHRArgs) => {
    // 拼装请求参数
    const formData = new FormData();
    this.moduleIconFile.forEach((file: any) => formData.append('file', file));
    const req = new HttpRequest('POST', item.action!, formData, {
      reportProgress: true,
      withCredentials: true
    });

    // 必须返回一个 Observable
    return this.http.request(req).subscribe(
      (event: HttpEvent<any>) => {
        // 上传进度
        if (event.type === HttpEventType.UploadProgress) {
          if (event.total! > 0) {
            (event as any).percent = (event.loaded / event.total!) * 100;
          }
          item.onProgress!(event, item.file!);
          return;
        }

        // 处理响应
        if (event instanceof HttpResponse) {
          item.onSuccess!(event.body, item.file!, event);

          let result = event.body as Result;
          if (!result.flag)
            this.msg.error(result.message);
          this.validateForm.controls.iconInfoId.setValue(result.data);
        }
      },
      err => {
        console.log(err);
        this.msg.error('启动图标上传失败');
      }
    );
  };

  // 图标删除事件
  onModuleIconRemove = (file: UploadFile): boolean => {
    this.moduleIconFile = this.moduleIconFile.filter(f => f.uid !== file.uid);
    this.validateForm.controls.uploadIconId.setValue(null);
    return true;
  };
  previewImage: string | undefined = '';
  previewVisible = false;

  // 应用图片预览
  handlePreview = (file: UploadFile) => {
    this.previewImage = file.url || file.thumbUrl;
    this.previewVisible = true;
  };

  // 图标校验器
  appIconValidator = (c: FormControl): { [s: string]: boolean } => {
    if (!(this.validateForm && c))
      return {};
    if (!this.validateForm.value.jarInfoId)
      return {};
    if ('APPLICATION' === this.validateForm.value.type)
      return Validators.required(c);
  };
  // 原图片地址
  iconId: any;

  // 构造器
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private msg: NzMessageService,
    private http: HttpClient,
    private notification: NzNotificationService) {
  }

  // 初始化
  ngOnInit() {
    this.validateForm = this.fb.group({
      id: [null],
      name: [null, [Validators.required]],
      author: [null, [Validators.required]],
      note: [null, [Validators.required]],
      type: ['APPLICATION'],
      jarInfoId: [null],
      iconInfoId: [null, [this.appIconValidator]],
      webPort: [8000, [Validators.required]]
    });

    this.backfill();
  }

  // 表单数据回填
  backfill() {
    this.route.queryParams.subscribe(({id, iconId}) => {
      this.moduleId = id;
      this.iconId = iconId;
      this.restore();
    });
  }

  // 提交表单
  submit() {
    if (validNgForm(this.validateForm)) {
      this.loading = true;
      let param = this.validateForm.value;

      if (null != param.jarInfoId) param.jarInfo = {id: param.jarInfoId};
      if (null != param.iconInfoId) param.iconInfo = {id: param.iconInfoId};

      this.http.post('/module/edit', createHttpParams(param))
        .subscribe(handleResult(
          this.notification,
          () => this.router.navigateByUrl('/dashboard/module/store').finally(),
          true,
          () => this.loading = false
        ));
    }
  }

  // 修改还原
  restore() {
    if (!this.moduleId) {
      this.router.navigateByUrl('/').finally();
      return;
    }

    this.loading = true;
    this.moduleIconFile = [{
      uid: -1,
      name: 'icon',
      status: 'done',
      url: `/upload/img/${this.iconId}`
    }];

    post(this.http, `/module/detail/${this.moduleId}`, null, ...catchErr())
      .subscribe(handleResult(
        this.notification,
        ({data}) => {
          this.validateForm.controls.name.setValue(data.name);
          this.validateForm.controls.id.setValue(data.id);
          this.validateForm.controls.author.setValue(data.author);
          this.validateForm.controls.note.setValue(data.note);
          this.validateForm.controls.type.setValue(data.type);
          this.validateForm.controls.webPort.setValue(data.webPort);

          this.moduleFile = [{
            uid: -1,
            name: data.jarInfo.name,
            status: 'done',
          }];
        },
        false,
        () => this.loading = false
      ));
  }

  // 检查启动图片尺寸
  iconDimensionCheck(file: UploadFile): Promise<boolean> {
    return new Promise(resolve => {
      const img = new Image(); // create image
      img.src = window.URL.createObjectURL(file);
      img.onload = () => {
        const width = img.naturalWidth;
        const height = img.naturalHeight;
        window.URL.revokeObjectURL(img.src!);
        resolve(width === height && width <= 100);
      };
    });
  }

}
