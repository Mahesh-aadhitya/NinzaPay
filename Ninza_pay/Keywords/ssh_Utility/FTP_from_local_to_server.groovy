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

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.kms.katalon.core.annotation.Keyword;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FTPFileTransfer {

	/**
	 * Uploads a file from local to remote server via SFTP.
	 *
	 * @param session The active SSH session
	 * @param localFilePath The local file path to upload
	 * @param remoteDirectory The remote directory on the server where the file will be uploaded
	 * @return String The remote path where the file is uploaded
	 * @throws FileNotFoundException If the file does not exist on the local system
	 * @throws SftpException If an SFTP error occurs during file transfer
	 */
	@Keyword
	public static String uploadFileToServer(Session session, String localFilePath, String remoteDirectory) throws FileNotFoundException, SftpException {
		// Extract the file name from the local file path
		String localFileName = new File(localFilePath).getName();
		String remoteFilePath = remoteDirectory + "/" + localFileName;

		ChannelSftp sftpChannel = null;
		String remotePath = "";

		try {
			// Create SFTP channel and connect
			sftpChannel = (ChannelSftp) session.openChannel("sftp");
			sftpChannel.connect();

			// Check if remote file exists, if yes, remove it
			try {
				sftpChannel.stat(remoteFilePath); // Check if file exists
				sftpChannel.rm(remoteFilePath);   // Remove the existing file
			} catch (SftpException e) {
				// File does not exist, continue with upload
			}

			// Upload file
			sftpChannel.put(new FileInputStream(localFilePath), remoteFilePath);
			remotePath = remoteFilePath;

			System.out.println("File transfer successful to: " + remotePath);
		} catch (Exception e) {
			System.out.println("Failed to transfer the file: " + e.getMessage());
			throw e;
		} finally {
			if (sftpChannel != null) {
				sftpChannel.disconnect();
			}
		}

		return remotePath;
	}
}
