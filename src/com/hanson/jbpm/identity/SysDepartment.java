package com.hanson.jbpm.identity;

import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.dao.DaoFactory;
import com.suntek.eap.structure.DepartmentModel;
import com.suntek.eap.structure.FrameUserModel;
import com.suntek.eap.structure.exception.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hanson on 2019/10/17.
 */
public class SysDepartment extends Department {
    private DepartmentModel dept = null;

    public SysDepartment(String deptId) {
        super(deptId);

        try {
            if(deptId.charAt(0) != 48) {
                String sql = "select fram_code from depart_view where dept_name = '" + deptId + "'";
                CommonLogger.logger.debug(sql);
                deptId = (String) DaoFactory.getJdbc("jbpm", "openEAP").queryForMap(sql).get("fram_code");
                this.setId(deptId);
            }

            this.dept = new DepartmentModel(deptId);
        } catch (EntityNotFoundException var3) {
            throw new RuntimeException("根据id获取部门时异常: " + var3.getMessage());
        }
    }

    public List<User> getUsers() {
        List<FrameUserModel> list = this.dept.getDepartmentUsers();
        List<User> users = new ArrayList();

        for(int i = 0; i < list.size(); ++i) {
            User user = new SysUser();
            user.setUserId(((FrameUserModel)list.get(i)).getUserCode());
            user.setUserName(((FrameUserModel)list.get(i)).getUserName());
            user.setEmail(((FrameUserModel)list.get(i)).getExtendProperty("e1_mail"));
            user.setMobile(((FrameUserModel)list.get(i)).getExtendProperty("mobi_tel1"));
            user.setDeptManager(((FrameUserModel)list.get(i)).isDepartmentManager());
            users.add(user);
        }

        return users;
    }

    public List<String> getUserIds() {
        List<FrameUserModel> list = this.dept.getDepartmentUsers();
        List<String> users = new ArrayList();

        for(int i = 0; i < list.size(); ++i) {
            users.add(((FrameUserModel)list.get(i)).getUserCode());
        }

        return users;
    }

    public String getNameById(String deptId) {
        return this.dept.getDepartmentName();
    }
}

