package ssh_Utility;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.kms.katalon.core.annotation.Keyword;

class SSHLoginBasicAuth {

	/**
	 * Method to perform SSH login using basic authentication
	 * @param host - the SSH host
	 * @param port - the SSH port (e.g., "22")
	 * @param username - SSH username
	 * @param password - SSH password
	 * @return Session - the SSH session if successful, null if failed
	 */
	@Keyword
	def static Session executeSSHLogin(String host, String port, String username, String password) {
		Session session = null
		try {
			// Create a JSch instance
			JSch jsch = new JSch()

			// Create session with provided credentials
			session = jsch.getSession(username, host, Integer.parseInt(port))
			session.setPassword(password)

			// Disable host key checking (to avoid prompts for unknown host keys)
			session.setConfig("StrictHostKeyChecking", "no")

			// Connect to the remote server
			session.connect()

			// Log success message
			println("SSH Connection to Unix Box is Successful")
			return session
		} catch (Exception e) {
			e.printStackTrace()
			// Log failure message
			println("SSH Connection to Unix Box is Failed")
			return null
		}
	}
}
