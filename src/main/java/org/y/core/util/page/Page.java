package org.y.core.util.page;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.y.core.exception.BusinessException;
import org.y.core.model.enu.ErrorCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页VO实体类
 *
 * @param <E> 数据类型
 */
@Getter
@Setter
@ToString
public class Page<E> {

    public static final Page DEFAULT =
            new Page() {
                public void setRowCount(Integer rowCount) {
                    illegalOperation();
                }

                public void setData(List data) {
                    illegalOperation();
                }

                public void setPageIndex(Integer pageIndex) {
                    illegalOperation();
                }

                public void setPageSize(Integer pageSize) {
                    illegalOperation();
                }

                public void setPageTotal(Integer pageTotal) {
                    illegalOperation();
                }

                public void setBeginNum(Integer beginNum) {
                    illegalOperation();
                }

                public void setEndNum(Integer endNum) {
                    illegalOperation();
                }

                private void illegalOperation() {
                    throw new BusinessException(ErrorCode.ILLEGAL_OPERATION);
                }
            };

    private long rowCount; // 总行数
    private List<E> data; // 数据列表
    private long pageIndex; // 页码
    private long pageSize; // 页大小
    private long pageTotal; // 总页数
    private long beginNum; // 起始页码
    private long endNum; // 结束页码

    public Page() {
        rowCount = 0;
        data = new ArrayList<>();
        pageIndex = 1;
        pageSize = 1;
        pageTotal = 1;
        beginNum = endNum = 1;
    }

    public Page(PageCondition pageCondition, DataHandler<E> dataHandler) {
        this(
                pageCondition.getPageIndex(),
                pageCondition.getPageSize(),
                dataHandler,
                pageCondition.getPageNumCount());
    }

    public Page(Page<?> page) {
        super();
        this.rowCount = page.rowCount;
        this.pageIndex = page.pageIndex;
        this.pageSize = page.pageSize;
        this.pageTotal = page.pageTotal;
        this.beginNum = page.beginNum;
        this.endNum = page.endNum;
    }

    public Page(Integer pageIndex, Integer pageSize, DataHandler<E> handler) {
        this(pageIndex, pageSize, handler, null);
    }

    public Page(Integer pageIndex, Integer pageSize, DataHandler<E> handler, Integer pageNumCount) {
        invoke(pageIndex, pageSize, handler, pageNumCount);
    }

    public static <T> Page<T> gen(PageCondition base, DataHandler<T> handler) {
        return new Page<>(base, handler);
    }

    /**
     * 获取数据
     *
     * @param pageIndex    页码
     * @param pageSize     页大小
     * @param handler      数据获取器
     * @param pageNumCount 页码数量
     */
    private void invoke(Integer pageIndex, Integer pageSize, DataHandler<E> handler, Integer pageNumCount) {
        reviseSize(pageSize);
        setRowCount(handler.getRowCount());
        calcPageTotal();
        reviseIndex(pageIndex);
        setData(handler.getElements(this.pageIndex, this.pageSize));
        calcPagerNums(pageNumCount);
    }

    /**
     * 计算页码列表
     *
     * @param pageNumCount 页码数量
     */
    private void calcPagerNums(Integer pageNumCount) {
        if (pageNumCount == null || pageNumCount <= 0) {
            return;
        }

        if (pageNumCount == 1) {
            beginNum = endNum = pageIndex;
            return;
        }

        int offset = pageNumCount / 2;
        long begin = pageIndex - offset;
        long end = pageIndex + offset;

        if (end > pageTotal) {
            begin -= end - pageTotal;
            end = pageTotal;
            if (begin < 1) {
                begin = 1;
            }
        }

        if (begin < 1) {
            end += Math.abs(begin) + 1;
            begin = 1;
            if (end > pageTotal) {
                end = pageTotal;
            }
        }

        this.beginNum = begin;
        this.endNum = end;
    }

    /**
     * 修复页大小, 页大小默认为10
     *
     * @param pageSize 页大小
     */
    private void reviseSize(Integer pageSize) {
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        this.pageSize = pageSize;
    }

    /**
     * 计算总页数
     */
    private void calcPageTotal() {
        if (rowCount == 0) {
            pageTotal = 1;
        } else {
            pageTotal = (rowCount + pageSize - 1) / pageSize;
        }
    }

    /**
     * 修复索引, 索引总是从1开始, 且范围在[0, pageTotal]之间
     *
     * @param pageIndex 索引
     */
    private void reviseIndex(Integer pageIndex) {
        if (pageIndex == null || pageIndex <= 0) {
            this.pageIndex = 1;
        } else {
            this.pageIndex = pageIndex;
        }

        if (this.pageIndex > this.pageTotal) {
            this.pageIndex = this.pageTotal;
        }

        if (this.pageIndex <= 0) {
            this.pageIndex = 1;
        }
    }
}
