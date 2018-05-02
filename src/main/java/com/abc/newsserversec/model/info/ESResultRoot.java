package com.abc.newsserversec.model.info;

import java.util.Map;

/**
 * Created by admin on 2017/7/13.
 */
public class ESResultRoot {
    public int took;
    public boolean timed_out;
    public Shards _shards;
    public Hits hits;
    public Map aggregations;
}
