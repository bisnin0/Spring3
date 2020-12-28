package com.bitcamp.myapp.register;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.bitcamp.myapp.Constants;

public class RegisterDAO {
	public JdbcTemplate template;
	public RegisterDAO() {
		this.template = Constants.template;
	}	
	public RegisterVO loginCheck(RegisterVO vo) {
		RegisterVO resultVO = null;
		try {
			String sql = "select userid, username from register where userid=? and userpwd=?";
			resultVO = template.queryForObject(sql, new Object[] {vo.getUserid(), vo.getUserpwd()}, new BeanPropertyRowMapper<RegisterVO>(RegisterVO.class));
		    resultVO.setLogStatus("Y");
		}catch(Exception e) {
			e.printStackTrace();
		}
		return resultVO;
	}
}
