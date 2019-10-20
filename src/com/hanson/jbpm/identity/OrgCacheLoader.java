package com.hanson.jbpm.identity;


import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.dao.DaoFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by Hanson on 2019/10/17.
 */
public class OrgCacheLoader {
    private static final String MODULE = "jbpm";
    private JdbcTemplate jdbc = DaoFactory.getJdbc("jbpm", "openEAP");
    private JdbcTemplate bpmJdbc = DaoFactory.getJdbc("jbpm");
    private static final OrgCacheLoader loader = new OrgCacheLoader();

    public OrgCacheLoader() {
    }

    public static OrgCacheLoader getInstance() {
        return loader;
    }

    public void load() {
        this.loadUser();
        this.loadWorkGroup();
        this.loadRole();
        this.loadDept();
        this.loadDelegates();
        this.loadOrgProcPermission();
        this.loadUserDept();
        this.loadUserRole();
    }

    public void load(OrgCache.OrgType type) {
        if(type == OrgCache.OrgType.USER) {
            this.loadUser();
        }

        if(type == OrgCache.OrgType.DEPT) {
            this.loadDept();
        }

        if(type == OrgCache.OrgType.ROLE) {
            this.loadRole();
        }

        if(type == OrgCache.OrgType.WORKGROUP) {
            this.loadWorkGroup();
        }

        if(type == OrgCache.OrgType.DELEGATES) {
            this.loadDelegates();
        }

        if(type == OrgCache.OrgType.ORGPROC) {
            this.loadOrgProcPermission();
        }

    }

    private void load(String sql, OrgCache.OrgType type, JdbcTemplate jdbc) {
        CommonLogger.logger.debug("load " + type + ":" + sql);
        List list = jdbc.queryForList(sql);

        for(int i = 0; i < list.size(); ++i) {
            Map row = (Map)list.get(i);
            String key = row.get("FKEY").toString();
            String value = row.get("FNAME").toString();
            if(type == OrgCache.OrgType.DEPT) {
                OrgCache.getCache().putDept(key, value);
            }

            if(type == OrgCache.OrgType.ROLE) {
                OrgCache.getCache().putRole(key, value);
            }

            if(type == OrgCache.OrgType.WORKGROUP) {
                OrgCache.getCache().putWorkGroup(key, value);
            }

            if(type == OrgCache.OrgType.USER) {
                OrgCache.getCache().putUser(key, value);
            }

            if(type == OrgCache.OrgType.DELEGATES) {
                OrgCache.getCache().putDelegate(key, value);
            }

            if(type == OrgCache.OrgType.ORGPROC) {
                String key2 = (String)row.get("FKEY2");
                OrgCache.getCache().putOrgProc(key + "." + key2, value);
            }

            if(type.equals(OrgCache.OrgType.USER)) {
                String userDept = String.valueOf(row.get("DEPT_ID"));
                String userRole = String.valueOf(row.get("ROLE_ID"));
                if(userDept.equals("null")) {
                    userDept = "";
                }

                if(userRole.equals("null")) {
                    userRole = "";
                }

                OrgCache.getCache().putUserDept(key, userDept);
                OrgCache.getCache().putUserRole(key, userRole);
            }

            if(type.equals(OrgCache.OrgType.USERDEPT)) {
                OrgCache.getCache().putUserDept(key, value);
            }

            if(type.equals(OrgCache.OrgType.USERROLE)) {
                OrgCache.getCache().putUserRole(key, value);
            }
        }

    }

    private void loadDelegates() {
        this.load("select DELEGATEE FKEY, DELEGATOR FNAME from BPM_DELEGATES where STATUS = '1'", OrgCache.OrgType.DELEGATES, this.bpmJdbc);
    }

    private void loadUser() {
        String sql = "select a.user_code FKEY, full_name FNAME ";
        sql = sql + " from base_user a where full_name is not null";
        this.load(sql, OrgCache.OrgType.USER, this.jdbc);
    }

    private void loadWorkGroup() {
        this.load("select fram_name FNAME, fram_seq FKEY from framework where fram_type=4", OrgCache.OrgType.WORKGROUP, this.jdbc);
    }

    private void loadRole() {
        this.load("select ROLEID FKEY, ROLENAME FNAME from SYS_ROLES", OrgCache.OrgType.ROLE, this.jdbc);
    }

    private void loadDept() {
        this.load("select fram_code FKEY, fram_name FNAME from framework where fram_type=0", OrgCache.OrgType.DEPT, this.jdbc);
    }

    private void loadOrgProcPermission() {
        this.load("select ORGTYPE FKEY2, ORG_ID FKEY, PROC_NAME FNAME from BPM_ORG_PROC", OrgCache.OrgType.ORGPROC, this.bpmJdbc);
    }

    private void loadUserDept() {
        this.load("select user_code FKEY, fram_code FNAME from user_dept order by fram_code ", OrgCache.OrgType.USERDEPT, this.jdbc);
    }

    private void loadUserRole() {
        this.load("select user_code FKEY, roleid FNAME from user_role order by roleid  ", OrgCache.OrgType.USERROLE, this.jdbc);
    }

    public String queryUserDept(String userId) {
        String sql = "select fram_code from user_dept where user_code=?";
        List<Map> list = this.jdbc.queryForList(sql, new String[]{userId});
        return list.size() > 0?(String)((Map)list.get(0)).get("fram_code"):"";
    }
}
