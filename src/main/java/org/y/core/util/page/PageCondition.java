package org.y.core.util.page;

import lombok.Data;

/** 分页查询基础条件 */
@Data
public class PageCondition {
  private int pageIndex = 1;
  private int pageSize = 10;
  private int pageNumCount = 7;
}
