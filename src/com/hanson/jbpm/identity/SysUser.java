package com.hanson.jbpm.identity;

import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.dao.DaoFactory;
import com.suntek.eap.structure.DepartmentModel;
import com.suntek.eap.structure.FrameUserModel;
import com.suntek.eap.structure.exception.EntityNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Hanson on 2019/10/17.
 */
public class SysUser extends User {
    private FrameUserModel userModel = null;

    public SysUser() {
    }

    public SysUser(String loginName) {
        super(loginName);

        try {
            this.userModel = new FrameUserModel(this.getLoginName());
        } catch (EntityNotFoundException var3) {
            throw new RuntimeException("获取员工信息异常: " + var3.getMessage());
        }

        this.setUserId(this.userModel.getUserCode());
        this.setUserName(this.userModel.getUserName());
        this.setEmail(this.userModel.getExtendProperty("e1_mail"));
        this.setMobile(this.userModel.getExtendProperty("mobi_tel1"));
        this.setDeptManager(this.userModel.isDepartmentManager());
    }

    public boolean isAdministrator() {
        return this.userModel.isSystemManager();
    }

    public boolean isLeader(String deptId) {
        return this.userModel.isDepartmentManager();
    }

    public Department[] getDepts() {
        List<DepartmentModel> list = this.userModel.getOwnerDepartment();
        Department[] deptList = new Department[list.size()];

        for(int i = 0; i < list.size(); ++i) {
            DepartmentModel dept = (DepartmentModel)list.get(i);
            deptList[i] = new SysDepartment(dept.getDepartmentCode());
        }

        return deptList;
    }

    public Role[] getRoles() {
        String sql = "select a.roleid, user_code, ROLENAME from user_role a, SYS_ROLES b where a.roleid=b.ROLEID and user_code='" + this.getLoginName() + "'";
        CommonLogger.logger.debug(sql);
        List list = DaoFactory.getJdbc("jbpm", "openEAP").queryForList(sql);
        Role[] roles = new Role[list.size()];

        for(int i = 0; i < list.size(); ++i) {
            Map row = (Map)list.get(i);
            Role role = new SysRole();
            role.setId(row.get("roleid").toString());
            role.setName((String)row.get("ROLENAME"));
            roles[i] = role;
        }

        return roles;
    }

    public String getUserNamesByLoginNames(List<String> userList) {
        String loginNames = "";

        for(int i = 0; i < userList.size(); ++i) {
            loginNames = loginNames + (String)userList.get(i) + ",";
        }

        return loginNames.substring(0, loginNames.length() - 1);
    }

    /** @deprecated */
    @Deprecated
    public User[] getMultiInstance(List<String> userIds) {
        String usersSql = "select user_code, full_name, mobi_tel1, e1_mail, user_pwd from base_user  where user_code in (''";

        String userId;
        for(Iterator var4 = userIds.iterator(); var4.hasNext(); usersSql = usersSql + ",'" + userId + "'") {
            userId = (String)var4.next();
        }

        usersSql = usersSql + ") and e1_mail is not null";
        CommonLogger.logger.debug("[MailActionHandler]查询派发用户:" + usersSql);
        JdbcTemplate openEAPJdbc = DaoFactory.getJdbc("jbpm", "openEAP");
        List list = openEAPJdbc.queryForList(usersSql);
        User[] users = new User[list.size()];

        for(int i = 0; i < list.size(); ++i) {
            User user = new SysUser();
            Map row = (Map)list.get(i);
            user.setEmail((String)row.get("e1_mail"));
            user.setLoginName((String)row.get("user_code"));
            user.setMobile((String)row.get("mobi_tel1"));
            user.setPassword((String)row.get("user_pwd"));
            user.setUserId((String)row.get("user_code"));
            user.setUserName((String)row.get("full_name"));
            users[i] = user;
        }

        return users;
    }

    public static User[] newMultiInstance(List<String> userIds) {
        String usersSql = "select user_code, full_name, mobi_tel1, e1_mail, user_pwd from base_user  where user_code in (''";

        String userId;
        for(Iterator var3 = userIds.iterator(); var3.hasNext(); usersSql = usersSql + ",'" + userId + "'") {
            userId = (String)var3.next();
        }

        usersSql = usersSql + ") and e1_mail is not null";
        CommonLogger.logger.debug("[MailActionHandler]查询派发用户:" + usersSql);
        JdbcTemplate openEAPJdbc = DaoFactory.getJdbc("jbpm", "openEAP");
        List list = openEAPJdbc.queryForList(usersSql);
        User[] users = new User[list.size()];

        for(int i = 0; i < list.size(); ++i) {
            User user = new SysUser();
            Map row = (Map)list.get(i);
            user.setEmail((String)row.get("e1_mail"));
            user.setLoginName((String)row.get("user_code"));
            user.setMobile((String)row.get("mobi_tel1"));
            user.setPassword((String)row.get("user_pwd"));
            user.setUserId((String)row.get("user_code"));
            user.setUserName((String)row.get("full_name"));
            users[i] = user;
        }

        return users;
    }

    public User getDeptManager() {
        Department[] depts = this.getDepts();
        if(depts.length == 0) {
            return null;
        } else {
            List<User> users = depts[0].getUsers();

            for(int i = 0; i < users.size(); ++i) {
                if(((User)users.get(i)).isDeptManager()) {
                    return (User)users.get(i);
                }
            }

            return null;
        }
    }

    public void setDeptManager(boolean isDeptManager) {
        this.deptManager = isDeptManager;
    }

    public boolean isDeptManager() {
        return this.deptManager;
    }
}
