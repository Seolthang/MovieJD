package com.kh.movie.notice.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kh.movie.notice.model.vo.Notice;

public class NoticeDAO {

	public int insertNotice(Connection conn, Notice notice) {
		PreparedStatement pstmt = null;
		int result = 0;
		String query = "INSERT INTO NOTICE_TBL VALUES(SEQ_NOTICENO.NEXTVAL,?,?,'admin',DEFAULT,DEFAULT)";
		
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, notice.getNoticeSubject());
			pstmt.setString(2, notice.getNoticeContent());
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public int updateNotice(Connection conn, Notice notice) {
		PreparedStatement pstmt = null;
		int result = 0;
		String query = "UPDATE NOTICE_TBL SET NOTICE_SUBJECT = ?, NOTICE_CONTENT = ? WHERE NOTICE_NO = ?";
		
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(3, notice.getNoticeNo());
			pstmt.setString(1, notice.getNoticeSubject());
			pstmt.setString(2, notice.getNoticeContent());
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public int deleteNoticeByNo(Connection conn, int noticeNo) {
		PreparedStatement pstmt = null;
		int result = 0;
		String query = "DELETE FROM NOTICE_TBL WHERE NOTICE_NO = ?";
		
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, noticeNo);
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public List<Notice> selectNoticeList(Connection conn, int currentPage) {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String query = "SELECT * FROM(SELECT ROW_NUMBER() OVER(ORDER BY NOTICE_NO DESC) ROW_NUM, NOTICE_TBL.*FROM NOTICE_TBL)WHERE ROW_NUM BETWEEN ? AND ?";
		List<Notice> nList = new ArrayList<Notice>();
		int recordCountPerPage = 8;
		int start = currentPage*recordCountPerPage - (recordCountPerPage - 1);
		int end = currentPage*recordCountPerPage;
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, start);
			pstmt.setInt(2, end);
			rset = pstmt.executeQuery();
			while(rset.next()) {
				Notice notice = rsetToNotice(rset);
				nList.add(notice);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rset.close();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return nList;
	}

	public String generatePageNavi(int currentPage) {
		int totalCount = 195; // 전체 게시물의 개수
		int recordCountPerPage = 8; //1페이지에서 보여줄 게시물의 수
		int naviTotalCount = 0; // 범위의 개수 (네비게이터의 수)
		if(totalCount % recordCountPerPage > 0) {
			naviTotalCount = totalCount / recordCountPerPage + 1;
		}else {
			naviTotalCount = totalCount / recordCountPerPage;
		}
		int naviCountPerPage = 5; // 페이지를 5개까지만 보여준다.
		int startNavi = ((currentPage - 1) / naviCountPerPage) * naviCountPerPage + 1;
		int endNavi = startNavi + naviCountPerPage -1;
		if(endNavi > naviTotalCount) {
			endNavi = naviTotalCount;
		}
		boolean needPrev = true;
		boolean needNext = true;
		if(startNavi == 1) {
			needPrev = false;
		}
		if(endNavi == naviTotalCount) {
			needNext = false;
		}
		StringBuilder result = new StringBuilder();
		if(needPrev) {
			result.append("<a href='/notice/list.do?currentPage="+(startNavi-1)+"'>[<]&nbsp;&nbsp;</a>");
		}
		for(int i = startNavi; i <= endNavi; i++) {
			result.append("<a href='/notice/list.do?currentPage="+i+"'>"+i+"</a>&nbsp;&nbsp;");
		}
		if(needNext) {
			result.append("<a href='/notice/list.do?currentPage="+(endNavi+1)+"'>[>]</a>");
		}
		return result.toString();
	}

	public Notice selectOneByNo(Connection conn, int noticeNo) {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String query = "SELECT * FROM NOTICE_TBL WHERE NOTICE_NO = ?";
		Notice notice = null;
		
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, noticeNo);
			rset = pstmt.executeQuery();
			if(rset.next()) {
				notice = rsetToNotice(rset);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rset.close();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return notice;
	}

	private Notice rsetToNotice(ResultSet rset) throws SQLException {
		Notice notice = new Notice();
		notice.setNoticeNo(rset.getInt("NOTICE_NO"));
		notice.setNoticeSubject(rset.getString("NOTICE_SUBJECT"));
		notice.setNoticeContent(rset.getString("NOTICE_CONTENT"));
		notice.setNoticeWriter(rset.getString("NOTICE_WRITER"));
		notice.setNoticeDate(rset.getDate("NOTICE_DATE"));
		notice.setViewCount(rset.getInt("VIEW_COUNT"));
		return notice;
	}
}
