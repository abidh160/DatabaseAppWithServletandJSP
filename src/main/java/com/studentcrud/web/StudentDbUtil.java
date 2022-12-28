package com.studentcrud.web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class StudentDbUtil {
	private DataSource dataSource;
	
	public StudentDbUtil(DataSource theDataSource) {
		dataSource = theDataSource;
	}
	
	public List<Student> getStudents() throws Exception {
		List<Student> students = new ArrayList<>();
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			String sql = "select * from student order by first_name";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				int id = rs.getInt("id");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String email = rs.getString("email");
				Student theStudent = new Student(id, firstName, lastName, email);
				students.add(theStudent);
			}
			return students;
		}
		finally {
			close(conn, stmt, rs);
		}
	}

	private void close(Connection conn, Statement stmt, ResultSet rs) {
		try {
			if(conn != null) {
				conn.close();
			}
			if(stmt != null) {
				stmt.close();
			}
			if(rs != null) {
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addStudent(Student theStudent) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			
			conn = dataSource.getConnection();
			String sql = "INSERT INTO student (first_name, last_name, email) VALUES (?,?,?)";
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, theStudent.getFirstName());
			ps.setString(2, theStudent.getLastName());
			ps.setString(3, theStudent.getEmail());
			
			ps.execute();	
		} 
		finally {
			close(conn, ps, null);
		}
	}

	public Student loadStudent(String theStudentId) throws Exception {
		Student theStudent = null;
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		int studentId;
		
		try {
			// convert student id to integer
			studentId = Integer.parseInt(theStudentId);
			// get connection to database
			myConn = dataSource.getConnection();
			// create sql to get selected student 
			String sql = "SELECT * FROM student where id=?";
			// create prepared statement
			myStmt = myConn.prepareStatement(sql);
			// set params
			myStmt.setInt(1, studentId);
			// execute statement
			myRs = myStmt.executeQuery();
			// retrieve data from the result set row
			if(myRs.next()) {
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				// use the studentId during construction
				theStudent = new Student(studentId, firstName, lastName, email);
			}
			else {
				throw new Exception("could not find student id: " + studentId);
			}
			
			return theStudent;
		}
		finally {
			// clean up JDBC objects
			close(myConn, myStmt, myRs);
		}
	}
	
	public void updateStudent(Student theStudent) throws Exception {
		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
			// get db connection
			myConn = dataSource.getConnection();
			// create sql update statement 
			String sql = "UPDATE student set first_name=?, last_name=?, email=? where id=?";
			// prepare statement
			myStmt = myConn.prepareStatement(sql);
			// set params
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
			myStmt.setInt(4, theStudent.getId());
			// execute sql statement
			myStmt.execute();
		}
		finally {
			// clean the JDBC objects
			close(myConn, myStmt, null);
		}
	}

	public void deleteStudent(String theStudentId) throws Exception {
		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
			// convert student id to int
			int studentId = Integer.parseInt(theStudentId);
			// get connection to database
			myConn = dataSource.getConnection();
			// create sql to delete student
			String sql = "DELETE FROM student where id=?";
			// prepare statement
			myStmt = myConn.prepareStatement(sql);
			// set params
			myStmt.setInt(1, studentId);
			// execute sql statement
			myStmt.execute();
		}
		finally {
			// clean up JDBC objects
			close(myConn, myStmt, null);
		}		
	}
}
