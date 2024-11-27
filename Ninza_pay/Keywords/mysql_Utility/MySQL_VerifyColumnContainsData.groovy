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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class MySQL_VerifyColumnContainsData {

	/**
	 * Verifies if a column in the database contains the expected data.
	 * @param connection The SQL connection object
	 * @param query The SQL query to retrieve data
	 * @param columnName The column name to check in the result
	 * @param expectedData The expected data to be verified in the column
	 * @return boolean true if the column contains the expected data, otherwise false
	 */
	@Keyword
	public static boolean verifyColumnContainsData(Connection connection, String query, String columnName, String expectedData) {
		boolean result = false;

		try {
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_SCROLL_INSENSITIVE);
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				String actualData = resultSet.getString(columnName);
				if (actualData != null && actualData.equalsIgnoreCase(expectedData)) {
					result = true;
					break;
				}
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		}

		return result;
	}

	/**
	 * Executes the check and returns the status message.
	 * @param connection The SQL connection object
	 * @param query The SQL query to fetch data
	 * @param columnName The name of the column to be verified
	 * @param expectedData The expected data to be found in the column
	 * @return String The status message indicating success or failure
	 */
	@Keyword
	public static String executeVerification(Connection connection, String query, String columnName, String expectedData) {
		boolean isDataFound = verifyColumnContainsData(connection, query, columnName, expectedData);

		if (isDataFound) {
			return "Column '" + columnName + "' contains the expected data '" + expectedData + "'.";
		} else {
			return "Column '" + columnName + "' does not contain the expected data '" + expectedData + "'.";
		}
	}
}

