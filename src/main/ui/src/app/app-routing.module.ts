import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DashboardComponent} from "./page/dashboard/dashboard.component";
import {StoreComponent} from "./page/module/store/store.component";
import {UploadComponent} from "./page/module/upload/upload.component";
import {AppComponent} from "./app.component";
import {DesktopComponent} from "./page/desktop/desktop.component";
import {EditComponent} from './page/module/edit/edit.component';

const routes: Routes = [
  // 控制面板
  {
    path: "dashboard", component: DashboardComponent, children: [
      {path: 'module/store', component: StoreComponent},
      {path: 'module/upload', component: UploadComponent},
      {path: 'module/edit', component: EditComponent},
    ]
  },

  // 桌面
  {path: "desktop", component: DesktopComponent},

  // 从AppComponent导航可以获取URL拼接的参数
  // {path: '', pathMatch: 'full', redirectTo: '/dashboard'},
  {path: '', component: AppComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
