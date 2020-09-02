package org.y.core.util.page;

import java.util.List;

/**
 * Mysql数据库数据处理器
 *
 * @param <E>
 */
public abstract class MySqlDataHandler<E> extends DataHandlerHelper<E> {

    @Override
    public List<E> getElements(long index, long size) {
        return elements(calcOffset(index, size), size);
    }
}
