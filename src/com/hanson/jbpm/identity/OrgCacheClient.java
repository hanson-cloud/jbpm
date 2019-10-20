package com.hanson.jbpm.identity;

import com.suntek.opencc.pico.ILocalComponent;

/**
 * Created by Hanson on 2019/10/17.
 */
public class OrgCacheClient implements ILocalComponent {
    public OrgCacheClient() {
    }

    public Object invoke(Object[] arg) {
        if(arg != null) {
            OrgCache.getCache().load((OrgCache.OrgType)arg[0]);
        } else {
            OrgCache.getCache().unload();
            OrgCache.getCache().load();
        }

        return Boolean.valueOf(true);
    }
}
