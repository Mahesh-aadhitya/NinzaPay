package specialized_automation

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
import com.kms.katalon.core.testdata.TestDataFactory
import internal.GlobalVariable

public class FetchTransactionIDs {

	/**
	 * This keyword fetches all the values from a specified column in an Excel sheet.
	 * @param excelDataID The ID of the Excel data in the Test Data folder
	 * @param sheetName The name of the sheet in the Excel file
	 * @param columnName The name of the column from which values are to be fetched
	 * @return List of values from the specified column as Strings
	 */
	@Keyword
	def static List<String> getValuesFromColumn(String excelDataID, String sheetName, String columnName) {
		// Load the Excel file (Test Data in Katalon) using the ID
		def testData = TestDataFactory.findTestData(excelDataID) // Access the Test Data by ID

		// List to store the values from the specified column
		List<String> columnValuesList = []

		// Get all column names
		List<String> allColumnNames = testData.getColumnNames()

		// Find the index of the specified column name
		int columnIndex = allColumnNames.indexOf(columnName) + 1  // +1 as Katalon indexes start from 1

		// Check if the column was found
		if (columnIndex == 0) {
			// If columnIndex is 0, the column wasn't found
			println("Column '" + columnName + "' not found in sheet '" + sheetName + "'!")
		} else {
			int rowCount = testData.getRowNumbers() // Get total number of rows

			// Loop through all rows and fetch the values from the specified column
			for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {
				String value = testData.getValue(columnIndex, rowIndex) // Get the value from the specified column
				columnValuesList.add(value)
			}
		}

		// Return the list of values from the specified column
		return columnValuesList
	}
}