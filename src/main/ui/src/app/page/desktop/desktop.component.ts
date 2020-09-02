import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {Module} from "../../model/entity/Module";
import {HttpClient} from "@angular/common/http";
import {
  apply,
  catchErr,
  clone,
  handleResult,
  handleResult2,
  post,
  pushUniqueA,
  refBox,
  Storages,
  validNgForm
} from "../../util/utils";
import {NzNotificationService} from "ng-zorro-antd";
import {WindowComponent} from "../../com/window/window.component";
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Title} from '@angular/platform-browser';
import {Md5} from 'ts-md5';
import {Result} from "../../model/result/Result";

type FormModel = 'REGISTER' | 'LOGIN' | 'FORGET_PASSWORD';
type Status = 'OPEN' | 'MIN' | 'MAX' | 'RESTORE' | 'CLOSED' | 'RELOAD';

@Component({
  selector: 'app-desktop',
  templateUrl: './desktop.component.html',
  styleUrls: ['./desktop.component.scss']
})
export class DesktopComponent implements OnInit {

  @ViewChild('taskBox', {static: true, read: ElementRef}) taskBox: ElementRef;
  windowBoundsStyle = {width: '100%', height: '100%'};

  // 任务栏中被激活的应用
  active: Module;
  // 应用列表
  apps: Array<Module> = [
    {id: 1, name: '记事本1', iconInfo: {id: 4}},
    {id: 2, name: '记事本2', iconInfo: {id: 5}},
  ];
  // 已打开窗口列表
  openedApps: Array<Module> = [];
  // 已打开窗口映射
  // K - app.id
  // V - WindowComponent
  winMap: { [s: string]: WindowComponent } = {};

  // 待执行任务
  tasks: Array<Function> = [];

  // 窗口状态改变
  onChangedStatus = (status: Status, app: Module, win: WindowComponent): Status | Promise<Status> => {
    console.log(status, app, win);

    switch (status) {
      case 'OPEN' :
        this.winMap[app.id] = win;
        break;
      case 'MIN' :
        break;
      case 'MAX' :
        break;
      case 'RESTORE':
        break;
      case 'CLOSED':
        this.openedApps = this.openedApps.filter(({id}) => id !== app.id);
        delete this.winMap[app.id];
        break;
      case 'RELOAD':
        return new Promise(resolve => {
          post(this.http, `/user/sid`, null, ...catchErr())
            .subscribe(handleResult2({
              notify: this.notify,
              onOk: ({data}) => {
                Storages.SESSION.user(data);
                resolve(status);
              },
              final: ret => {
                if (Result.isNotLogin(ret)) {
                  this.isLogin = false;
                  this.loading = false;
                  Storages.SESSION.user(null);
                  this.tasks.push(() => resolve(status));
                }
              }
            }));
        })
    }

    return status;
  };

  // 登录/注册/找回密码表单状态
  formModel: FormModel = 'LOGIN';

  // 是否远程校验中
  asyncValid = {username: 0, email: 0};
  // 注册表单
  regForm: FormGroup;
  // 确认密码校验
  confirmPwdValidator = (control: FormControl): { [s: string]: boolean } => {
    if (!control.value) {
      return {error: true, required: true};
    } else if (control.value !== this.regForm.controls.password.value) {
      return {confirm: true, error: true};
    }
    return {};
  };
  // 异步校验用户名是否重复
  // usernameAsyncValidator = (control: FormControl) => new Observable((observer: Observer<ValidationErrors | null>) => {
  //   // this.asyncValid.username++;
  //   // let userName = control.value.trim();
  //   // post(this.http, `/user/existUsername/${userName}`, null, ...catchErr())
  //   //   .subscribe(handleResult2({
  //   //     notify: this.notify,
  //   //     final: ret => {
  //   //       this.asyncValid.username--;
  //   //       observer.next(!ret.flag || ret.data ? {error: true, duplicated: true} : null);
  //   //       observer.complete();
  //   //     }
  //   //   }));
  //
  //   observer.next(null);
  //   observer.complete();
  // });
  // 异步校验邮箱是否重复
  // emailAsyncValidator = (control: FormControl) => new Observable((observer: Observer<ValidationErrors | null>) => {
  //   // this.asyncValid.email++;
  //   // let email = control.value.trim();
  //   // post(this.http, `/user/existEmail/${email}`, null, ...catchErr())
  //   //   .subscribe(handleResult2({
  //   //     notify: this.notify,
  //   //     final: ret => {
  //   //       this.asyncValid.email--;
  //   //       observer.next(!ret.flag || ret.data ? {error: true, duplicated: true} : null);
  //   //       observer.complete();
  //   //     }
  //   //   }));
  //   observer.next(null);
  //   observer.complete();
  // });

  // 校验用户名是否合法
  regexpValidUsername = (c: FormControl): { [s: string]: boolean } => {
    if (c.value && !/^[a-zA-Z_][a-zA-Z_0-9]{4,11}$/.test(c.value)) {
      return {error: true, invalid: true};
    }
    return {};
  };
  // 校验码发送倒计时
  emailSenderTimer: number = 0;
  // 注册框loading
  loading = false;

  // 登录表单
  loginForm: FormGroup;
  // 当前用户是否已经登录
  isLogin = false;

  // 忘记密码表单
  forgetPwdForm: FormGroup;
  // 用户名或者邮箱校验
  usernameOrEmailValidator = (c: FormControl): { [s: string]: boolean } => {
    if (/@/.test(c.value)) return Validators.email(c);
    else return this.regexpValidUsername(c);
  };

  constructor(
    private http: HttpClient,
    private notify: NzNotificationService,
    private fb: FormBuilder,
    private title: Title
  ) {
  }

  ngOnInit() {
    this.regForm = this.fb.group({
      username: [null, [Validators.required, this.regexpValidUsername]/*, [this.usernameAsyncValidator]*/],
      email: [null, [Validators.required, Validators.email]/*, [this.emailAsyncValidator]*/],
      password: [null, [Validators.required]],
      confirmPwd: [null, [this.confirmPwdValidator]],
      emailCode: [null, [Validators.required]],
    });
    this.loginForm = this.fb.group({
      username: [null, [Validators.required]],
      password: [null, [Validators.required]],
      remember: [null]
    });
    this.forgetPwdForm = this.fb.group({
      username: [null, [Validators.required, this.usernameOrEmailValidator]],
      emailCode: [null, [Validators.required]],
      password: [null, [Validators.required]]
    });

    refBox(this.taskBox);

    this.title.setTitle("XFrame桌面");
    this.checkLogin();
    // this.loadApps();
  }

  // 加载App模块
  private loadApps() {
    let user = Storages.SESSION.user();
    if (null === user) {
      this.setFormModel('LOGIN');
      return;
    }

    this.isLogin = true;
    post(this.http, '/module/apps', null, ...catchErr())
      .subscribe(handleResult(
        this.notify,
        ret => this.apps = ret.data
      ));
  }

  // 激活应用选中状态
  // 如果已经被选中就打开
  activeApp(app: Module) {
    if (this.active != app) {
      this.active = app;
    } else {
      this.openApp(app);
    }
  }

  // 打开应用
  openApp(app: Module) {
    this.active = null;
    pushUniqueA(this.openedApps, app, 'id');
  }

  // 最小化或还原窗口
  minOrRestoreWin(app: Module) {
    let win = this.winMap[app.id];
    win.restoreOr('MIN');
  }

  // 用户注册
  reg() {
    if (!validNgForm(this.regForm)) return;
    this.loading = true;

    let param = clone(this.regForm.value);
    param.password = Md5.hashStr(param.password);
    delete param.confirmPwd;

    post(this.http, `/user/reg`, param, ...catchErr())
      .subscribe(handleResult2({
        notify: this.notify,
        showSuccess: true,
        onOk: () => this.setFormModel('LOGIN'),
        final: () => this.loading = false
      }));
  }

  // 发送注册邮箱校验码
  sendRegEmailCode() {
    // 30秒内只能发送一次
    if (this.emailSenderTimer) return;
    this.emailSenderTimer = 30;
    let id = setInterval(() => {
      if (0 == --this.emailSenderTimer) {
        clearInterval(id);
      }
    }, 1000);

    // 发送邮箱验证码
    post(this.http, '/user/checkCode', {email: this.regForm.value.email.trim()}, ...catchErr())
      .subscribe(handleResult2({notify: this.notify, showSuccess: true}));
  }

  // 校验确认密码
  validConfirmPwd() {
    setTimeout(() => this.regForm.controls.confirmPwd.updateValueAndValidity());
  }

  // 执行登录
  login() {
    if (!validNgForm(this.loginForm)) return;

    this.loading = true;
    let param = clone(this.loginForm.value);

    post(this.http, `/user/login`, param, ...catchErr())
      .subscribe(handleResult2({
        notify: this.notify,
        showSuccess: true,
        onOk: ({data}) => {
          this.isLogin = true;
          Storages.SESSION.user(data);
          this.loginForm.reset();
          apply(this.tasks.pop());
          this.loadApps();
        },
        onFail: () => this.loading = false
      }));
  }

  // 重置密码
  resetPwd() {
    if (validNgForm(this.forgetPwdForm)) {
      this.loading = true;
      let param = clone(this.forgetPwdForm.value);
      param.password = Md5.hashStr(param.password);
      post(this.http, `/user/resetPwd`, param, ...catchErr())
        .subscribe(handleResult2({
          notify: this.notify,
          onOk: () => this.setFormModel('LOGIN'),
          showSuccess: true,
          final: () => this.loading = false
        }));
    }
  }

  // 设置表单模式
  setFormModel(model: FormModel) {
    this.formModel = model;
    this.emailSenderTimer = 0;
    switch (model) {
      case 'LOGIN':
        this.isLogin = false;
        this.loginForm.reset();
        break;
      case 'FORGET_PASSWORD':
        this.forgetPwdForm.reset();
        break;
      case 'REGISTER':
        this.regForm.reset();
        break;
    }
  }

  // 发送重置密码邮箱验证码
  sendResetEmailCode() {
    // 30秒内只能发送一次
    if (this.emailSenderTimer) return;
    this.emailSenderTimer = 30;
    let id = setInterval(() => {
      if (0 == --this.emailSenderTimer) {
        clearInterval(id);
      }
    }, 1000);

    // 发送邮箱验证码
    post(this.http, '/user/checkCodeEmailOrUsername', {username: this.forgetPwdForm.value.username.trim()}, ...catchErr())
      .subscribe(handleResult2({notify: this.notify, showSuccess: true}));
  }

  // 登录检查
  private checkLogin() {
    post(this.http, `/user/sid`, null, ...catchErr())
      .subscribe(handleResult2({
        notify: this.notify,
        onOk: ({data}) => {
          Storages.SESSION.user(data);
          this.loadApps();
        },
        onFail: () => {
          this.isLogin = false;
          this.loading = false;
        }
      }));
  }
}
