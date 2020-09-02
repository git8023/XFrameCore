import {Component, OnInit} from '@angular/core';
import {Navigate} from "../../model/vo/Navigate";
import {Router} from "@angular/router";
import {Title} from "@angular/platform-browser";

@Component({
  selector: 'app-head',
  templateUrl: './head.component.html',
  styleUrls: ['./head.component.scss']
})
export class HeadComponent implements OnInit {

  navs: Array<Navigate> = [{name: "首页"}];

  constructor(private router: Router, private title: Title) {
  }

  ngOnInit() {
  }

  // 设置导航路径
  navPath(...navs: Navigate[]) {
    this.navs.length = 0;
    navs.forEach(nav => this.navs.push(nav));
    this.navTo();
  }

  // 导航到最后一个路径
  private navTo() {
    let len = this.navs.length;
    if (0 < len) {
      let nav = this.navs[len - 1];
      this.router.navigateByUrl(nav.path || '/').finally();

      let titleName = '';
      this.navs.forEach(nav => titleName += '-' + nav.name);
      this.title.setTitle(titleName.substr(1));
    }
  }

}
