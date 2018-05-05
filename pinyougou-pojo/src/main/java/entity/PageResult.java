package entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author congzi
 * @Description: 分页结果封装对象
 * @create 2018-05-04
 * @Version 1.0
 */
public class PageResult implements Serializable {

    //总记录数
    private long total;

    //每页数据
    private List rows;

    public PageResult() {
    }

    public PageResult(long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
