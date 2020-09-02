package org.y.core.util.page;

import java.util.List;

/**
 * 数据处理器抽象类,
 *
 * @param <E>
 */
public abstract class DataHandlerHelper<E> implements DataHandler<E> {

    /**
     * 计算分页偏移量
     *
     * @param index 页码
     * @param size  页大小
     * @return 偏移量
     */
    protected long calcOffset(long index, long size) {
        return (index - 1) * size;
    }

    /**
     * 获取数据列表
     *
     * @param offset 偏移量
     * @param size   页大小
     * @return 数据列表
     */
    protected abstract List<E> elements(long offset, long size);
}
