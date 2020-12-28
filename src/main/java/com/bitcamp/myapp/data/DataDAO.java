package com.bitcamp.myapp.data;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.bitcamp.myapp.Constants;

public class DataDAO {
	public JdbcTemplate template=null;
	public DataDAO(){
		this.template = Constants.template;
	}
	public List<DataVO> allList(){
		String sql = "select no, title, userid, filename1, filename2 from data order by no desc";
		return template.query(sql, new BeanPropertyRowMapper<DataVO>(DataVO.class));		
	}
	public int dataInsert(DataVO vo) {
		int result = 0;
		try {
			String sql = "insert into data(no, title, content, userid, ip, "
					+ "filename1, filename2) values(a_sq.nextval, ?,?,?,?,?,?)";
			result =  template.update(sql, vo.getTitle(), vo.getContent(), vo.getUserid(),
				               vo.getIp(), vo.getFilename1(), vo.getFilename2());
		}catch(Exception e) {
			System.out.println("자료실 글등록 에러 발생---->"+ e.getMessage());
		}
		return result;
	}
}
