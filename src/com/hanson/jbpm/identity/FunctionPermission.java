package com.hanson.jbpm.identity;

import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.dao.DaoFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by Hanson on 2019/10/17.
 */
public class FunctionPermission implements IFunctionPermission {
    private static final String moduleName = "jbpm";
    private static final String dsName = "openEAP";
    private String loginName = "";

    public FunctionPermission(String loginName) {
        this.loginName = loginName;
    }

    public FunctionPermission() {
    }

    public FunctionList getFunctionList(String _loginName) {
        try {
            String sql = "select distinct FUNNAME from SYS_FUNLIST a, SYS_ROLEFUN b, user_role c where a.FUNID=b.FUNID and b.ROLEID=c.roleid and FUNTYPE=2 and c.user_code='" + _loginName + "'";
            CommonLogger.logger.debug(sql);
            List list = DaoFactory.getJdbc("jbpm", "openEAP").queryForList(sql);
            FunctionList funcList = new FunctionList();

            for(int i = 0; i < list.size(); ++i) {
                funcList.add((String)((Map)list.get(i)).get("FUNNAME"));
            }

            return funcList;
        } catch (Exception var6) {
            CommonLogger.logger.error(var6, var6);
            throw new RuntimeException("读取用户功能权限数据异常: " + var6.getMessage());
        }
    }

    public FunctionList getFunctionList() {
        return this.getFunctionList(this.loginName);
    }

    public boolean hasFunctionPermission(String _loginName, String functionName) {
        return this.getFunctionList(_loginName).contains(functionName);
    }

    public boolean hasFunctionPermission(String functionName) {
        return this.getFunctionList(this.loginName).contains(functionName);
    }

    public boolean hasRolePermission(String _loginName, String roleName) {
        String sql = "select count(1) HASROLE from user_role a, SYS_ROLES b where a.roleid=b.ROLEID and b.ROLENAME='" + roleName + "' and a.user_code='" + _loginName + "'";
        CommonLogger.logger.debug(sql);
        int hasRole = DaoFactory.getJdbc("jbpm", "openEAP").queryForInt(sql);
        return hasRole > 0;
    }

    public boolean hasRolePermission(String roleName) {
        return this.hasRolePermission(this.loginName, roleName);
    }
}

