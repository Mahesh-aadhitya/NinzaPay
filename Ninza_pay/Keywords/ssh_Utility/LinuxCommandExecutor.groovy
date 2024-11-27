package ssh_Utility

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


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import com.kms.katalon.core.annotation.Keyword;
import com.kms.katalon.core.util.KeywordUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class LinuxCommandExecutor {

	/**
	 * Executes a command on a Linux system via SSH and returns the output.
	 *
	 * @param session The SSH session.
	 * @param command The command to execute.
	 * @return The result of the command execution as a string.
	 */
	@Keyword
	public String executeLinuxCommand(Session session, String command) {
		StringBuilder result = new StringBuilder();
		ChannelExec channelExec = null;

		try {
			// Open an exec channel
			channelExec = (ChannelExec) session.openChannel("exec");
			channelExec.setCommand(command);

			// Get the input stream to read the output of the command
			InputStream inputStream = channelExec.getInputStream();
			channelExec.connect();

			// Read the output of the command
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line).append("\n");
			}

			KeywordUtil.markPassed("Command executed successfully: " + command);
		} catch (Exception e) {
			e.printStackTrace();
			KeywordUtil.markFailed("Command execution failed: " + command);
		} finally {
			if (channelExec != null) {
				channelExec.disconnect();
			}
		}

		// Return the result of the command execution
		return result.toString();
	}
}
