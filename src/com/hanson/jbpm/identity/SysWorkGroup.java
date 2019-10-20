package com.hanson.jbpm.identity;

import com.suntek.ccf.dao.DaoFactory;
import com.suntek.eap.structure.BaseUserModel;
import com.suntek.eap.structure.WorkGroupModel;
import com.suntek.eap.structure.exception.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Hanson on 2019/10/17.
 */
public class SysWorkGroup extends WorkGroup {
    public SysWorkGroup(String groupId) throws EntityNotFoundException {
        super(groupId);
        if(StringUtil.isChineseString(groupId)) {
            String sql = "select grop_name, grop_code from workgroup where grop_name=?";
            Map row = DaoFactory.getJdbc("jbpm", "openEAP").queryForMap(sql, new String[]{groupId});
            this.setId((String)row.get("grop_code"));
            this.setName((String)row.get("grop_name"));
        } else {
            this.setName((new WorkGroupModel(groupId)).getGroupName());
        }

    }

    public String getNameById(String groupId) throws EntityNotFoundException {
        return (new WorkGroupModel(groupId)).getGroupName();
    }

    public List<String> getUserIds() throws EntityNotFoundException {
        List<BaseUserModel> users = (new WorkGroupModel(this.getId())).getGrougUsers();
        List<String> list = new ArrayList();

        for(int i = 0; i < users.size(); ++i) {
            list.add(((BaseUserModel)users.get(i)).getUserCode());
        }

        return list;
    }

    public List<User> getUsers() throws EntityNotFoundException {
        List<BaseUserModel> users = (new WorkGroupModel(this.getId())).getGrougUsers();
        List<User> list = new ArrayList();

        for(int i = 0; i < users.size(); ++i) {
            User user = new SysUser(((BaseUserModel)users.get(i)).getUserCode());
            list.add(user);
        }

        return list;
    }

    public List<User> getUsersByGroupNames() throws EntityNotFoundException {
        return null;
    }
}
