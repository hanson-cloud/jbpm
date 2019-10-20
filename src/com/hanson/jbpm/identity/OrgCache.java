package com.hanson.jbpm.identity;

import com.hanson.jbpm.log.CommonLogger;
import com.suntek.opencc.OpenCC;
import com.suntek.opencc.pico.ILocalComponent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Hanson on 2019/10/17.
 */
public class OrgCache {
    public final Map<String, String> cache = new ConcurrentHashMap();
    private static final OrgCache orgCache = new OrgCache();

    public OrgCache() {
    }

    public static OrgCache getCache() {
        return orgCache;
    }

    public static OrgCache getComponentCache() {
        try {
            ILocalComponent client = (ILocalComponent) OpenCC.getComponentContainer().getComponent(OrgCacheClient.class);
            return (OrgCache)client.invoke((Object[])null);
        } catch (InstantiationException var1) {
            throw new RuntimeException(var1);
        } catch (IllegalAccessException var2) {
            throw new RuntimeException(var2);
        }
    }

    public void load() {
        OrgCacheLoader.getInstance().load();
    }

    public void unload() {
        this.cache.clear();
    }

    public String load(OrgCache.OrgType type) {
        OrgCacheLoader.getInstance().load(type);
        return String.valueOf(true);
    }

    public void putUser(String key, String value) {
        this.cache.put(OrgCache.OrgType.USER + "." + key, value);
    }

    public void putDelegatee(String key, String value) {
        this.cache.put(OrgCache.OrgType.DELEGATES + "." + key, value);
    }

    public void putUserDept(String key, String value) {
        this.cache.put(OrgCache.OrgType.USER + "." + OrgCache.OrgType.DEPT + "." + key, value);
    }

    public void putUserRole(String key, String value) {
        this.cache.put(OrgCache.OrgType.USER + "." + OrgCache.OrgType.ROLE + "." + key, value);
    }

    public void putDelegate(String key, String value) {
        this.cache.put(OrgCache.OrgType.DELEGATES + "." + key, value);
    }

    public void putOrgProc(String key, String value) {
        this.cache.put(OrgCache.OrgType.ORGPROC + "." + key, value);
    }

    public String getRoleProc(String key) {
        return (String)this.cache.get(OrgCache.OrgType.ORGPROC + ".R." + key);
    }

    public String getDeptProc(String key) {
        return (String)this.cache.get(OrgCache.OrgType.ORGPROC + ".D." + key);
    }

    public String getUserDept(String key) {
        return OrgCacheLoader.getInstance().queryUserDept(key);
    }

    public String getUserRole(String key) {
        return (String)this.cache.get(OrgCache.OrgType.USER + "." + OrgCache.OrgType.ROLE + "." + key);
    }

    public void putRole(String key, String value) {
        this.cache.put(OrgCache.OrgType.ROLE + "." + key, value);
    }

    public void putWorkGroup(String key, String value) {
        this.cache.put(OrgCache.OrgType.WORKGROUP + "." + key, value);
    }

    public void putDept(String key, String value) {
        this.cache.put(OrgCache.OrgType.DEPT + "." + key, value);
    }

    public String getUser(String key) {
        if(key != null && key.indexOf("D") != 0 && key.indexOf("R") != 0 && key.indexOf("G") != 0) {
            String name = (String)this.cache.get(OrgCache.OrgType.USER + "." + key);
            if(name == null) {
                try {
                    CommonLogger.logger.debug("组织架构缓存中找不到user:" + key + ", 重新从数据库中读取..");
                    SysUser user = new SysUser(key);
                    name = user.getName();
                    this.cache.put(OrgCache.OrgType.USER + "." + key, name);
                    Department[] depts = user.getDepts();
                    if(depts.length > 0) {
                        getCache().putUserDept(key, depts[0].getId());
                        getCache().putDept(depts[0].getId(), depts[0].getName());
                    }

                    Role[] roles = user.getRoles();
                    if(roles.length > 0) {
                        getCache().putUserRole(key, roles[0].getId());
                        getCache().putRole(roles[0].getId(), roles[0].getName());
                    }
                } catch (Exception var6) {
                    CommonLogger.logger.error(var6);
                    name = key;
                }
            }

            return name;
        } else {
            return "";
        }
    }

    public String getDept(String key) {
        String dept = (String)this.cache.get(OrgCache.OrgType.DEPT + "." + key);
        if(dept == null) {
            try {
                SysDepartment sysDept = new SysDepartment(key);
                dept = sysDept.getName();
                this.cache.put(OrgCache.OrgType.DEPT + "." + key, dept);
            } catch (Exception var4) {
                CommonLogger.logger.error(var4, var4);
            }
        }

        return dept;
    }

    public String getRole(String key) {
        return (String)this.cache.get(OrgCache.OrgType.ROLE + "." + key);
    }

    public String getDelegator(String key) {
        return (String)this.cache.get(OrgCache.OrgType.DELEGATES + "." + key);
    }

    public String getWorkGroup(String key) {
        return (String)this.cache.get(OrgCache.OrgType.WORKGROUP + "." + key);
    }

    public boolean containsUser(String key) {
        return this.cache.containsKey(OrgCache.OrgType.USER + "." + key);
    }

    public boolean containsDept(String key) {
        return this.cache.containsKey(OrgCache.OrgType.DEPT + "." + key);
    }

    public boolean containsRole(String key) {
        return this.cache.containsKey(OrgCache.OrgType.ROLE + "." + key);
    }

    public boolean containsWorkGroup(String key) {
        return this.cache.containsKey(OrgCache.OrgType.WORKGROUP + "." + key);
    }

    public boolean containsDelegatee(String key) {
        return this.cache.containsKey(OrgCache.OrgType.DELEGATES + "." + key);
    }

    public boolean containsRoleProc(String key) {
        return this.cache.containsKey(OrgCache.OrgType.ORGPROC + ".R." + key);
    }

    public boolean containsDeptProc(String key) {
        return this.cache.containsKey(OrgCache.OrgType.ORGPROC + ".D." + key);
    }

    public static void main(String[] args) throws InterruptedException {
        getCache().putDept("001", "测试部");
        System.out.println(getCache().getDept("001"));
    }

    public static enum OrgType {
        USER,
        DEPT,
        ROLE,
        WORKGROUP,
        DELEGATES,
        ORGPROC,
        USERDEPT,
        USERROLE;

        private OrgType() {
        }
    }
}
