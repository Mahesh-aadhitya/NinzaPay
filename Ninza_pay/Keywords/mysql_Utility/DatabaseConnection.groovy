package mysql_Utility
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import internal.GlobalVariable
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import com.mysql.cj.jdbc.Driver
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.annotation.Keyword

public class DatabaseConnection {

	// Method to connect to the MySQL database
	@Keyword
	public Connection connectToDB(String url, String username, String password) {
		Connection connection = null
		try {
			Driver driver = new Driver()
			DriverManager.registerDriver(driver)
			connection = DriverManager.getConnection(url, username, password)
			KeywordUtil.logInfo("Connected to the database successfully!")
		} catch (SQLException e) {
			KeywordUtil.markFailed("Failed to connect to the database. Error: " + e.getMessage())
		}
		return connection
	}

	// Method to close the connection
	@Keyword
	public void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close()
				KeywordUtil.logInfo("Database connection closed successfully.")
			} catch (SQLException e) {
				KeywordUtil.markFailed("Failed to close the database connection. Error: " + e.getMessage())
			}
		}
	}
}


