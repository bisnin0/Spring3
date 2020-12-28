package com.bitcamp.myapp.data;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DataController {
	@Autowired
	public DataDAO dDao;
	
	@RequestMapping("/dataList")
	public ModelAndView dataList() {		
		List<DataVO> list = dDao.allList();
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("list", list);
		mav.setViewName("data/dataList");
		return mav;
	}
	@RequestMapping("/fileUpload1")
	public String fileUpload1() {
		return "data/uploadForm1";
	}
	//============  파일업로드 방법 1  ================================
	@RequestMapping(value="/fileUpload1Ok", method=RequestMethod.POST)
	public ModelAndView Upload1(@RequestParam("title") String title,
			                    @RequestParam("content") String content,
			                    @RequestParam("filename1") MultipartFile filename1,
			                    @RequestParam("filename2") MultipartFile filename2,
			                    HttpServletRequest req) {
		DataVO vo = new DataVO();
		vo.setTitle(title);
		vo.setContent(content);
		//아이피
		vo.setIp(req.getRemoteAddr());
		//아이디
		HttpSession ses = req.getSession();
		vo.setUserid((String)ses.getAttribute("userid"));
		
		//--------------파일 업로드
		//파일이 저장될곳
		String path = ses.getServletContext().getRealPath("/upload");
		System.out.println("path="+path);
		
		String fileParamName1 = filename1.getName();//폼의 파일첨부 객체 변수 
		String oriFileName1 = filename1.getOriginalFilename();//원래파일명
		System.out.println(fileParamName1 +"->"+ oriFileName1);
		try {
			if(oriFileName1!=null) {
				filename1.transferTo(new File(path, oriFileName1));//실제파일업로드 발생
			}
		}catch(IOException ie) {
			ie.printStackTrace();
		}
		
		String oriFileName2 = filename2.getOriginalFilename();
		try {
			if(oriFileName2!=null) {
				filename2.transferTo(new File(path, oriFileName2));
			}
		}catch(IOException e) {		
		}
		
		vo.setFilename1(oriFileName1);
		vo.setFilename2(oriFileName2);
		
		int result = dDao.dataInsert(vo);
		
		//--------------------------------------
		ModelAndView mav = new ModelAndView();
		if(result>0) {
			mav.setViewName("redirect:dataList");
		}else {
			//레코드 추가 실패시 업로드 파일을 삭제 한다.
			if(oriFileName1 != null) {
				File f = new File(path, oriFileName1);
				f.delete();
			}
			if(oriFileName2 != null) {
				File f = new File(path, oriFileName2);
				f.delete();
			}
			mav.addObject("msg", "파일업로드 실패하였습니다..");
			mav.setViewName("data/dataResult");
		}		
		return mav;
	}
	//================== 파일업로드 방법 2 ============================
	@RequestMapping("/fileUpload2")
	public String fileUpload2() {
		return "data/uploadForm2";
	}
	@RequestMapping(value="/fileUpload2Ok", method=RequestMethod.POST)
	public ModelAndView upload2(DataVO vo, HttpServletRequest req, HttpSession ses) {
		//파일 저장할 위치
		String path = ses.getServletContext().getRealPath("/upload");
		
		//파일업로드를 하기위해서 req에서 MultipartHttpServletRequest 를 생성한다.
		MultipartHttpServletRequest mr= (MultipartHttpServletRequest)req;
		
		// mr에서 MultipartFile객체를 얻어온다.   -> List
		List<MultipartFile> files = mr.getFiles("filename");
		
		//파일명을 저장 할 변수 
		String fileNames[] = new String[files.size()];  //[2]
		int idx=0;		
		if(files!=null) {//첨부파일이 있을때			
			for(int i=0; i<files.size(); i++) {//for   111	
				MultipartFile mf = files.get(i);				
				String fName = mf.getOriginalFilename();//폼의 파일명얻어오기
				if(fName!=null && !fName.equals("")) {
					String oriFileName = fName.substring(0, fName.lastIndexOf("."));//원파일의 앞부분
					String oriExt = fName.substring(fName.lastIndexOf(".")+1);//확장
					//이름을 바꿔야 한다.
					File f= new File(path, fName);
					if(f.exists()) {//원래 파일객체가 서버에 있으면 실행
						for(int renameNum=1;;renameNum++) {//1,2,3,4......
							String renameFile = oriFileName+renameNum+"."+oriExt;//변경된 파일명
							f = new File(path, renameFile);
							
							if(!f.exists()) {//파일이 있으면 true, 없으면  false
								fName = renameFile;
								break;
							}					
						}//for
					}
					try {
						mf.transferTo(f);
					}catch(Exception e) {}
					fileNames[idx++] = fName;
				}//if   fName
			}//for   111			
		}		
		
		//아이디
		vo.setUserid((String)ses.getAttribute("userid"));
		vo.setFilename1(fileNames[0]);
		vo.setFilename2(fileNames[1]);
		
		int result = dDao.dataInsert(vo);
		//
		ModelAndView mav = new ModelAndView();
		if(result<=0) {//레코드 추가 실패시
			//파일 삭제
			for(int j=0; j<fileNames.length; j++) {
				if(fileNames[j]!=null) {
					File ff = new File(path, fileNames[j]);
					ff.delete();
				}
			}
			//폼으로 보내기
			mav.addObject("msg", "자료실 글등록 실패하였습니다.");
			mav.setViewName("data/dataResult");
		}else {
			mav.setViewName("redirect:dataList");
		}			
		return mav;
	}
}














