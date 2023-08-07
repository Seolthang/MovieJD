package com.kh.movie.notice.model.service;

import java.sql.Connection;
import java.util.List;

import com.kh.movie.common.JDBCTemplate;
import com.kh.movie.notice.model.dao.NoticeDAO;
import com.kh.movie.notice.model.vo.Notice;
import com.kh.movie.notice.model.vo.PageData;

public class NoticeService {
	
	private NoticeDAO nDao;
	private JDBCTemplate jdbcTemplate;
	
	public NoticeService() {
		nDao = new NoticeDAO();
		// 싱글톤 패턴 적용으로 인하여 new로 객체 생성을 하지 못하고 getInstance를 통해 객체를 생성해준다.
		jdbcTemplate = JDBCTemplate.getInstance();
	}
	
	public int insertNotice(Notice notice) {
		Connection conn = jdbcTemplate.createConnection();
		int result = nDao.insertNotice(conn, notice);
		if(result > 0) {
			jdbcTemplate.commit(conn);
		}else {
			jdbcTemplate.rollback(conn);
		}
		jdbcTemplate.close(conn);
		return result;
	}
	
	public int updateNotice(Notice notice) {
		Connection conn = jdbcTemplate.createConnection();
		int result = nDao.updateNotice(conn, notice);
		if(result > 0) {
			jdbcTemplate.commit(conn);
		}else {
			jdbcTemplate.rollback(conn);
		}
		jdbcTemplate.close(conn);
		return result;
	}

	public int deleteNoticeByNo(int noticeNo) {
		Connection conn = jdbcTemplate.createConnection();
		int result = nDao.deleteNoticeByNo(conn, noticeNo);
		if(result > 0) {
			jdbcTemplate.commit(conn);
		}else {
			jdbcTemplate.rollback(conn);
		}
		jdbcTemplate.close(conn);
		return result;
	}

	// 공지사항 전체 목록 조회
	public PageData selectNoticeList(int currentPage){
		Connection conn = jdbcTemplate.createConnection();
		List<Notice> nList = nDao.selectNoticeList(conn, currentPage);
		String pageNavi = nDao.generatePageNavi(currentPage);
		// 1. Map 이용
		// 2. VO 클래스 이용
		PageData pd = new PageData(nList, pageNavi);
		jdbcTemplate.close(conn);
		return pd;
	}
	
	public Notice selectOneByNo(int noticeNo) {
		Connection conn = jdbcTemplate.createConnection();
		Notice notice = nDao.selectOneByNo(conn, noticeNo);
		jdbcTemplate.close(conn);
		return notice;
	}
}
