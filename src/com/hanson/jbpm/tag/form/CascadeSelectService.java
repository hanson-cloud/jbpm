package com.hanson.jbpm.tag.form;

import java.util.List;

import com.suntek.ccf.dao.DaoFactory;

/**
 * �����еļ���ѡ��
 * @author lcw
 *
 */
public class CascadeSelectService {

	public List buildOptions(String sql, String idVal) {
		//List<String[]> list = new ArrayList<String[]>();
		sql = sql.replace("?", idVal);
		return DaoFactory.getJdbc("jbpm").queryForList(sql);
	}
}
