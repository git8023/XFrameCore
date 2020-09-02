import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {NgZorroAntdModule, NZ_I18N, zh_CN} from 'ng-zorro-antd';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {registerLocaleData} from '@angular/common';
import zh from '@angular/common/locales/zh';
import {DashboardComponent} from './page/dashboard/dashboard.component';
import {HeadComponent} from './com/head/head.component';
import {StoreComponent} from './page/module/store/store.component';
import {UploadComponent} from './page/module/upload/upload.component';
import {JsonIndexPipe} from "./pipe/json-index.pipe";
import {DateFmtPipe} from "./pipe/date-fmt.pipe";
import {InsidePipe} from "./pipe/inside.pipe";
import {DefValPipe} from "./pipe/def-val.pipe";
import {StringFormatPipe} from "./pipe/string-format.pipe";
import {DesktopComponent} from './page/desktop/desktop.component';
import {WindowComponent} from './com/window/window.component';
import {EditComponent} from './page/module/edit/edit.component';
import {StopPropagationDirective} from './com/directive/StopPropagationDirective';

registerLocaleData(zh);

@NgModule({
  declarations: [
    //<editor-fold desc="Pipe">
    JsonIndexPipe,
    DateFmtPipe,
    InsidePipe,
    DefValPipe,
    StringFormatPipe,
    //</editor-fold>

    //<editor-fold desc="指令">
    StopPropagationDirective,
    //</editor-fold>

    AppComponent,
    DashboardComponent,
    HeadComponent,
    StoreComponent,
    UploadComponent,
    DesktopComponent,
    WindowComponent,
    EditComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgZorroAntdModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    BrowserAnimationsModule
  ],
  providers: [
    {provide: NZ_I18N, useValue: zh_CN}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
