package specialized_automation;

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint;
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase;
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData;
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject;
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject;

import com.kms.katalon.core.annotation.Keyword;
import com.kms.katalon.core.checkpoint.Checkpoint;
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW;
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testcase.TestCase;
import com.kms.katalon.core.testdata.TestData;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS;
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI;
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows;

import internal.GlobalVariable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class GenerateTransactionJsons {

	@Keyword
	public String execute(String excelFilePath, String sheetName, InputStream txtFilePath) {
		String absolutePath = "";
		try {
			generateAndWriteDataToExcel(excelFilePath, sheetName);
			List<Map<String, String>> entireData = getdataFromExcel(excelFilePath, sheetName);
			String jsonStaticData = readTextFile(txtFilePath);
			String copyStaticData = jsonStaticData;

			String jsonArrayAsString = "[";
			for (Map<String, String> map : entireData) {
				for (Entry<String, String> singleRowData : map.entrySet()) {
					jsonStaticData = jsonStaticData.replaceAll(singleRowData.getKey().trim(), singleRowData.getValue().trim());
				}
				jsonArrayAsString = jsonArrayAsString + jsonStaticData.trim() + ",";
				jsonStaticData = copyStaticData;
			}

			// Get jsons file path
			absolutePath = writeTextToFile(jsonArrayAsString.substring(0, jsonArrayAsString.length() - 1) + "]");

			System.out.println("Data Simulated successfully...");
		} catch (Exception e) {
			System.out.println("Failed to Simulate data..." + e);
		}

		return absolutePath;
	}

	private static void generateAndWriteDataToExcel(String filePath, String sheetName) throws IOException, InterruptedException {
		InputStream inputStream = new FileInputStream(filePath);
		Workbook workbook = WorkbookFactory.create(inputStream);
		Sheet sheet = workbook.getSheet(sheetName);
		DataFormatter df = new DataFormatter();
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

		int rowCount = sheet.getPhysicalNumberOfRows();
		int columnCount = sheet.getRow(0).getPhysicalNumberOfCells();

		int tatDateColumn = 0;
		for (int i = 0; i < columnCount; i++) {
			if (df.formatCellValue(sheet.getRow(0).getCell(i), evaluator).trim().equalsIgnoreCase("TAT DATE")) {
				tatDateColumn = i;
				break;
			}
		}

		for (int i = 1; i < rowCount; i++) {
			String tatDate = df.formatCellValue(sheet.getRow(i).getCell(tatDateColumn), evaluator).trim();
			String transactionId = "TR" + generateDateWithOffset(tatDate, "yyyyMMddhhmmssSSS");
			String transactionDate = generateDateWithOffset(tatDate, "dd-MM-yyyy");
			String transactionTime = generateDateWithOffset(tatDate, "hh:mm:ss:SSS");

			for (int j = 0; j < columnCount; j++) {
				String cellData = df.formatCellValue(sheet.getRow(0).getCell(j), evaluator);

				if (cellData.equalsIgnoreCase("TRANSACTION ID")) {
					sheet.getRow(i).getCell(j).setCellValue(transactionId);
				} else if (cellData.equalsIgnoreCase("TRANSACTION DATE")) {
					sheet.getRow(i).getCell(j).setCellValue(transactionDate);
				} else if (cellData.equalsIgnoreCase("TRANSACTION TIME")) {
					sheet.getRow(i).getCell(j).setCellValue(transactionTime);
				}
			}
			Thread.sleep(100);
		}
		OutputStream outputStream = new FileOutputStream(filePath);
		workbook.write(outputStream);
		workbook.close();
		inputStream.close();
		outputStream.close();
	}

	private static List<Map<String, String>> getdataFromExcel(String excelPath, String sheetName)
	throws EncryptedDocumentException, IOException {

		List<Map<String, String>> entireData = new LinkedList<Map<String, String>>();
		InputStream inputStream = new FileInputStream(excelPath);
		Workbook workbook = WorkbookFactory.create(inputStream);
		Sheet sheet = workbook.getSheet(sheetName);
		DataFormatter df = new DataFormatter();
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

		int rowCount = sheet.getLastRowNum();
		for (int i = 0; i < rowCount; i++) {
			Map<String, String> singleRowData = new LinkedHashMap<String, String>();
			int columnCount = sheet.getRow(i).getLastCellNum();
			for (int j = 0; j < columnCount; j++) {
				singleRowData.put(df.formatCellValue(sheet.getRow(0).getCell(j), evaluator),
						df.formatCellValue(sheet.getRow(i + 1).getCell(j), evaluator));
			}
			entireData.add(singleRowData);
		}
		workbook.close();
		inputStream.close();
		return entireData;
	}

	private static String readTextFile(InputStream inputStream) throws IOException {
		StringBuilder content = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while ((line = reader.readLine()) != null) {
			content.append(line).append("\n");
		}
		if (reader != null) {
			reader.close();
		}
		return content.toString();
	}

	private static String writeTextToFile(String data) throws IOException {
		String directoryPath = System.getProperty("user.home") + File.separator + "Updated_Json_Files";
		System.out.println("Directory path: " + directoryPath); // Debug

		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdirs(); // creates parent directories if necessary
			System.out.println("Directory created: " + directoryPath); // Debug
		}

		String fileName = "Transaction_Details_JsonData_" + new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(new Date()) + ".txt";
		File file = new File(directory, fileName);

		System.out.println("File path: " + file.getAbsolutePath()); // Debug

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(data);
		} catch (IOException e) {
			System.out.println("Error writing to file: " + e.getMessage());
			throw e;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					System.out.println("Error closing writer: " + e.getMessage());
				}
			}
		}

		return file.getAbsolutePath();
	}

	private static String generateDateWithOffset(String offset, String format) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(offset));
		return new SimpleDateFormat(format).format(calendar.getTime());
	}
}
