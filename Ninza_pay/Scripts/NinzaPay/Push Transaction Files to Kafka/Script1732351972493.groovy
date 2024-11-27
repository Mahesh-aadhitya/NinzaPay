import java.sql.Connection
import com.jcraft.jsch.Session
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import ssh_Utility.FTP_from_local_to_server
import mysql_Utility.DatabaseConnection
import ssh_Utility.SSHLoginBasicAuth
import specialized_automation.GenerateTransactionJsons
import specialized_automation.FileUtils
import ssh_Utility.LinuxCommandExecutor
import specialized_automation.FetchTransactionIDs
import com.kms.katalon.core.testobject.TestObject
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

// Step 1: Connect to the MySQL Database
Connection connection = CustomKeywords.'mysql_utility.DatabaseConnection.connectToDB'(
		"jdbc:mysql://49.249.29.6:3333/ninza_hrm", "root@%", "root")

assert connection != null : "Failed to connect to database"

// Step 2: Connect to Unix Box
Session sshSession = CustomKeywords.'ssh_Utility.SSHLoginBasicAuth.executeSSHLogin'(
		"49.249.29.5", "22", "chidori", "1234")

// Step 3: Generate Transaction JSON file from Excel and upload to server
String excelPath = "C:\\Users\\User\\Katalon Studio\\Ninza_pay\\Resources\\Transaction_Details.xlsx"
String sheetName = "Transaction Details"
InputStream txtFilePath = new FileInputStream("C:\\Users\\User\\Katalon Studio\\Ninza_pay\\Resources\\Transaction_Schema.txt")

String jsonFilePath = CustomKeywords.'specialized_automation.GenerateTransactionJsons.execute'(
		excelPath, sheetName, txtFilePath)

String remoteDirectory = "/home/chidori/Ninza_Kafka/kafka/bin/"
String capability = CustomKeywords.'ssh_Utility.FTP_from_local_to_server.uploadFileToServer'(
		sshSession, jsonFilePath, remoteDirectory)

// Step 4: Execute command on Linux to push transaction
String command = new FileUtils().generatePath(
		"/home/chidori/Ninza_Kafka/kafka/bin/pushTxn.sh //home/chidori/Ninza_Kafka/kafka/bin/", jsonFilePath)
LinuxCommandExecutor executor = new LinuxCommandExecutor()
String result = executor.executeLinuxCommand(sshSession, command)

// Step 5: Fetch transaction IDs from Excel
List<String> transactionIDs = CustomKeywords.'specialized_automation.FetchTransactionIDs.getValuesFromColumn'(
		"Data Files/Transaction_Details", sheetName, "TRANSACTION ID")

// Step 6: Open and navigate web application
WebUI.openBrowser("http://49.249.29.5:8091/")
WebUI.maximizeWindow()
WebUI.setText(findTestObject("Object Repository/NinzaPay/Username"), "rmgyantra")
WebUI.setText(findTestObject("Object Repository/NinzaPay/Password"), "rmgy@9999")
WebUI.click(findTestObject("Object Repository/NinzaPay/SignIn"))
WebUI.click(findTestObject("Object Repository/NinzaPay/All Transactions"))

// Step 7: Loop through transaction IDs to process each one
for (String transactionId : transactionIDs) {
	// Fetch API response for the current transaction ID
	String apiResponse = CustomKeywords.'specialized_automation.ApiFetchTransactionDetails.fetchTransactionDetails'(
			"http://49.249.29.5:8091/transaction?transactionId=", transactionId)
	
	WebUI.comment("Transaction ID: " + transactionId)
	WebUI.comment("API Response: " + apiResponse)
	
	// Database verification
	String query = "SELECT * FROM transaction;"
	String columnName = "transaction_id"
	String expectedData = transactionId
	String dbResultMessage = CustomKeywords.'mysql_Utility.MySQL_VerifyColumnContainsData.executeVerification'(
			connection, query, columnName, expectedData)
	WebUI.comment("Database Verification Result: " + dbResultMessage)
	
	// UI verification
	TestObject searchField = findTestObject("Object Repository/NinzaPay/SearchField")
	WebUI.clearText(searchField)
	WebUI.setText(searchField, transactionId)
	WebUI.sendKeys(searchField, Keys.chord(Keys.ENTER))
	
	// Create a new dynamic TestObject
	TestObject dynamicElement = new TestObject("dynamicElement")
	
	// Set the dynamic XPath for the TestObject
	String dynamicXPath = "//th[text()='TransactionId']/ancestor::table/descendant::td[text()='" + transactionId + "']"
	dynamicElement.addProperty("xpath", ConditionType.EQUALS, dynamicXPath)
	
	// Check if the element is displayed
	boolean isDisplayed = WebUI.verifyElementPresent(dynamicElement, 1,FailureHandling.OPTIONAL)
	
	if (isDisplayed) {
		WebUI.comment("The dynamic element with Transaction ID '" + transactionId + "' is displayed.")
	} else {
		WebUI.comment("The dynamic element with Transaction ID '" + transactionId + "' is NOT displayed.")
	}
}

CustomKeywords.'mysql_Utility.DatabaseConnection.closeConnection'(connection)
// Close browser
WebUI.closeBrowser()
