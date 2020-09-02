import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {urlParam} from "./util/utils";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  constructor(private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit(): void {
    let params = urlParam(location.search);
    let uri = <string> params.uri;
    switch (uri) {
      case null:
      case undefined:
      case '/':
        // uri = '/dashboard';
        uri = '/desktop';
        break;
    }
    this.router.navigateByUrl(uri).finally()
  }

}
