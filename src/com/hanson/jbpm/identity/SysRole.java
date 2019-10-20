package com.hanson.jbpm.identity;

import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.dao.DaoFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Hanson on 2019/10/17.
 */
public class SysRole extends Role {
    public SysRole() {
    }

    public SysRole(String name) {
        super(name);
        String sql = "select ROLEID, ROLENAME from SYS_ROLES where ROLEID='" + name + "'";
        Map row = DaoFactory.getJdbc("jbpm", "openEAP").queryForMap(sql);
        this.setId(row.get("ROLEID").toString());
        this.setName(row.get("ROLENAME").toString());
    }

    public List<User> getUsers() {
        String sql = "select distinct a.user_code, c.user_name from user_role a, SYS_ROLES b, frameuser c where a.roleid=b.ROLEID and a.user_code=c.user_code and b.ROLEID='" + this.getId() + "'";
        CommonLogger.logger.debug(sql);
        List list = DaoFactory.getJdbc("jbpm", "openEAP").queryForList(sql);
        List<User> users = new ArrayList();

        for(int i = 0; i < list.size(); ++i) {
            Map row = (Map)list.get(i);
            User user = new SysUser();
            user.setUserId((String)row.get("user_code"));
            user.setUserName((String)row.get("user_name"));
            users.add(user);
        }

        return users;
    }

    public List<User> getUsersByRoleNames() {
        return null;
    }
}
