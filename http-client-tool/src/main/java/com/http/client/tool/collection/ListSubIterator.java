package com.http.client.tool.collection;


import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linzhou
 * @version 1.0.0
 * @ClassName ListSubIterator.java
 * @Description list分段器
 * @createTime 2021年01月26日 11:17:00
 */
public class ListSubIterator<T> {

    private List<T> list;

    private int pageSize;

    private int curIndex = 0;

    public ListSubIterator(List<T> list, int pageSize) {
        this.list = list;
        this.pageSize = pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean hasNext() {
        if (pageSize <= 0 || CollectionUtils.isEmpty(list) || curIndex >= list.size() || curIndex < 0) {
            return false;
        }
        return true;
    }


    public List<T> next() {
        if (!hasNext()) {
            return new ArrayList<>();
        }

        int nextIndex = curIndex + pageSize;
        if (nextIndex > list.size()) {
            nextIndex = list.size();
        }

        List<T> result = list.subList(curIndex, nextIndex);
        curIndex = nextIndex;

        return result;
    }


}
