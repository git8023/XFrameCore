package org.y.core.model.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.y.core.model.enu.ModuleStatus;
import org.y.core.util.page.PageCondition;

/**
 * 模块分页过滤条件
 */
@Getter
@Setter
@NoArgsConstructor
public class ModuleCondition extends PageCondition {
    private String name;
    private String author;
    private ModuleStatus status;
}
