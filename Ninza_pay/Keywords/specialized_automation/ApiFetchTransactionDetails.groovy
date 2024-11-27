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

import internal.GlobalVariable

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ApiFetchTransactionDetails {

	/**
	 * Fetches the API response based on the API endpoint and transaction ID.
	 * @param apiEndpoint The API URL endpoint
	 * @param transactionId The transaction ID to fetch the details for
	 * @return String The API response as a string
	 */
	@Keyword
	static String fetchTransactionDetails(String apiEndpoint, String transactionId) {
		String urlString = apiEndpoint + transactionId  // Appending the transaction ID to the endpoint
		StringBuilder response = new StringBuilder()

		try {
			// Create a URL object with the API endpoint
			URL url = new URL(urlString)
			HttpURLConnection connection = (HttpURLConnection) url.openConnection()
			connection.setRequestMethod("GET")  // Change to POST if needed

			// Add headers (if required)
//			connection.setRequestProperty("Content-Type", "multipart/form-data")
//			connection.setRequestProperty("Authorization", "Basic rmgyantra:rmgy@9999")  // Replace with your API token if needed

			// Check if the connection was successful
			int status = connection.getResponseCode()
			if (status == HttpURLConnection.HTTP_OK) {
				// Read the response
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))
				String inputLine
				while ((inputLine = reader.readLine()) != null) {
					response.append(inputLine)
				}
				reader.close()
			} else {
				return "Failed to fetch data. HTTP Status Code: " + status
			}
		} catch (Exception e) {
			e.printStackTrace()
			return "Error occurred while fetching API response: " + e.getMessage()
		}

		return response.toString()
	}
}
