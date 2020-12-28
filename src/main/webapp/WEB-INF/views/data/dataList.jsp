<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div style="overflow:auto">
	<h1>자료실 리스트</h1>
	<ul id="dataList">
		<li>번호</li>
		<li>제목</li>
		<li>작성자</li>
		<li>첨부파일</li>
		
		<c:forEach var="vo" items="${list}">	
			<li>${vo.no }</li>
			<li class="wordCut">${vo.title }</li>
			<li>${vo.userid }</li>
			<li class="wordCut">${vo.filename1 }, ${vo.filename2}</li>
		</c:forEach>		
	</ul>
</div>