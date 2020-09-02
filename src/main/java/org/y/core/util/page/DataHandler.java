package org.y.core.util.page;

import java.util.List;

/**
 * 分页数据处理器
 *
 * @param <E> 数据类型
 */
public interface DataHandler<E> {

    /**
     * 获取数据列表
     *
     * @param size  页大小
     * @param index 页码
     * @return 数据列表
     */
    List<E> getElements(long index, long size);

    /**
     * 获取数据总行数
     *
     * @return 总行数
     */
    long getRowCount();
}
