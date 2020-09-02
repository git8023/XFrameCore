import {Component, ElementRef, HostListener, Input, OnInit, ViewChild} from '@angular/core';
import {Module} from "../../model/entity/Module";
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {EmitType, EventHandlerStore, XFrames} from "../../util/xframes";

type Status = 'OPEN' | 'MIN' | 'MAX' | 'RESTORE' | 'CLOSED' | 'RELOAD';

class Point {
  x: number;
  y: number;
}

@Component({
  selector: 'app-window',
  templateUrl: './window.component.html',
  styleUrls: ['./window.component.scss']
})
export class WindowComponent implements OnInit {

  private static MAX_Z_INDEX = 1;
  private static ACTIVE_COUNTER = 0;

  @ViewChild('windowBox', {static: true, read: ElementRef}) windowBox: ElementRef;
  @ViewChild('header', {static: true, read: ElementRef}) header: ElementRef;

  // 状态
  status: Status = 'RESTORE';

  // Z轴索引
  zIndex: number;

  /**
   * 数据
   */
  @Input()
  data: Module;

  /**
   * 窗口打开成功
   */
  @Input()
  onChangedStatus: (status: Status, data: any, _this: WindowComponent) => (Status | Promise<Status>);

  // 拖动状态
  moving: boolean = false;

  // 鼠标点击时的位置
  mouseDown: Point = {x: 0, y: 0};

  // 原始位置
  top: string | number = '20%';
  left: string | number = '25%';

  // 首页位置
  appIndexUrl: SafeResourceUrl;
  // 窗口名称
  windowName: string = '';
  // 图标地址
  iconSrc: any;
  // 副标题
  subhead: any;
  // 窗口事件仓库
  private eventHandlerStore: EventHandlerStore;

  constructor(
    private sanitizer: DomSanitizer
  ) {
  }

  ngOnInit() {
    this.onChangedStatus = this.onChangedStatus || (_ => _);
    this.data = this.data || {};

    WindowComponent.ACTIVE_COUNTER++;
    this.zIndex = WindowComponent.MAX_Z_INDEX++;
    this.iconSrc = `/upload/img/${this.data.iconInfo.id}`;
    this.reload();
    this.onChangedStatus('OPEN', this.data, this);
    this.changeStatus('RESTORE');

    this.eventHandlerStore = XFrames.onType()
      .lookup(EmitType.EXIT, () => this.changeStatus('CLOSED'))
      .lookup(EmitType.SUBHEAD, data => this.subhead = data);
  }

  // 设置状态
  changeStatus(status: Status) {
    let result = this.onChangedStatus(status, this.data, this);
    if (result instanceof Promise)
      result.then(value => this.doChangeStatus(value));
    else
      this.doChangeStatus(result);
  }

  // 执行窗口状态切换
  private doChangeStatus(status: Status) {
    switch (status) {
      case 'OPEN' :
        break;
      case 'MIN' :
        this.status = status;
        break;
      case 'MAX' :
        this.status = status;
        this.windowBox.nativeElement.style.left = null;
        this.windowBox.nativeElement.style.top = null;
        break;
      case 'RESTORE' :
        this.status = status;
        break;
      case 'CLOSED':
        this.status = status;
        if (0 === --WindowComponent.ACTIVE_COUNTER)
          WindowComponent.MAX_Z_INDEX = 1;
        this.eventHandlerStore.destroy();
        break;
      case 'RELOAD':
        this.reload();
        break;
    }
  }

  // 窗口拖动准备
  movingReady($event: MouseEvent) {
    $event.stopPropagation();
    this.toTopZIndex();

    if ('RESTORE' === this.status) {
      this.moving = true;
      //获取x坐标和y坐标
      this.mouseDown.x = $event.clientX;
      this.mouseDown.y = $event.clientY;

      //获取左部和顶部的偏移量
      this.left = this.windowBox.nativeElement.offsetLeft;
      this.top = this.windowBox.nativeElement.offsetTop;

    }
  }

  // 拖动窗口
  @HostListener('window:mousemove', ['$event'])
  moveWindow($event: MouseEvent) {
    if (!this.moving) return;
    let mx = $event.clientX - this.mouseDown.x + parseInt(this.left + '');
    let my = $event.clientY - this.mouseDown.y + parseInt(this.top + '');

    let maxWidth = window.innerWidth;
    let winWidth = this.windowBox.nativeElement.offsetWidth;
    mx = Math.min(Math.max(0, mx), maxWidth - winWidth);

    let maxHeight = window.innerHeight;
    let winHeight = this.windowBox.nativeElement.offsetHeight;
    my = Math.min(Math.max(0, my), maxHeight - winHeight);

    this.windowBox.nativeElement.style.left = mx + 'px';
    this.windowBox.nativeElement.style.top = my + 'px';
  }

  // 移动停止
  @HostListener('window:mouseup', ['$event'])
  movingStop($event:MouseEvent) {
    this.moving = false;
  }

  /**
   * 最小化->还原,
   * 还原 -> 最小化
   */
  restoreOr(status: Status) {
    if (this.status === status) {
      this.changeStatus("RESTORE");
    } else {
      this.changeStatus(status);
    }
  }

  // 重新加载
  reload() {
    let appIndexUrl = `${location.origin}/module/appIndex/${this.data.id}`;
    // location.protocol + "//" + location.hostname + ":" + port + "/" + uri

    // let appIndexUrl = `${location.protocol}//${location.hostname}:${this.data.webPort}`;
    // appIndexUrl += `/index/${this.data.id}/${this.data.token}`;
    // appIndexUrl += `?_=${Math.random()}&user=${Storages.SESSION.user()}`;

    // let appIndexUrl = 'https://www.baidu.com';

    this.appIndexUrl = this.sanitizer.bypassSecurityTrustResourceUrl(appIndexUrl);
    this.windowName = `WINDOW_NAME_${this.data.id}`;
  }

  // 把当前窗口设置为顶层
  toTopZIndex() {
    this.zIndex = WindowComponent.MAX_Z_INDEX++;
  }
}
