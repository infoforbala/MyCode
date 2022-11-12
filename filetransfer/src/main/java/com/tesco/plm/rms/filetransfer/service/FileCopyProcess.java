package com.tesco.plm.rms.filetransfer.service;

/* ************************************************************************************
 * JIRA Story  : RMSODS-11148
 * Class Name  : FileCopyProcess
 * Author      : Prabu Selvaraj
 * Date        : 04-NOV-2022
 * Details 	   : This class will perform the below
 *               #copy files from source to destination
 *               #if any failure program will retry 3 times 
 **************************************************************************************/

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tesco.plm.rms.filetransfer.common.ApplicationProperty;
import com.tesco.plm.rms.filetransfer.common.FileCopy;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class FileCopyProcess {

	@Autowired
	private ApplicationProperty prop;

	@Autowired
	FileCopy filecopy;

	public boolean filecopyprocess(String interfacename, String storefilecopy, String storeno,String storeipaddress) {
		String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		log.debug(methodName + " Method Started");

		try {
			// check file storefilecopy "Y" - copy or "N" - not copy
			if (storefilecopy.equalsIgnoreCase("Y")) {

				// source and destination configuration
				File sourceDir = new File(System.getenv("KAFKAHOME") + prop.get(interfacename + "sourcepath"));
				File destinationDir = new File(storeipaddress+ prop.getProperty(interfacename + "destinationpath"));

				// fetch files from source directory
				File[] files = sourceDir.listFiles();

				// check files exists to process
				if (files.length > 0) {

					// loop the files to copy
					for (File filename : files) {
						log.info("filename to copy from source:" + filename);
						// declare and initiate the variable
						int count = 0;
						boolean filecopystatus = true;
						// retry value get from property file
						int retrycount = Integer.parseInt(prop.getProperty("retrycount"));

						// initiate do while loop for retry process if copy method failed
						do {

							// call file copy class
							filecopystatus = filecopy.FileCopyToDir(filename.toString(), destinationDir.toString());
							// check file copy success or failed
							if (filecopystatus == false) {

								// if failed write log and increase the count to retry the copy process
								if (count == 0) {
									log.info("File copy failed for store:" + storeno + ",Hence retry initiated");
								} else if (count >= 1 && count <= retrycount) {
									log.info("File copy retry-" + count + "failed for store:" + storeno);

								}
								// increase the count
								count++;

								// check count and retrycount equal then story retry process
								if (count == retrycount + 1) {
									return false;
								}
							} else {
								// file copy success then reset the retry count
								count = retrycount;
								count++;
								log.info("File copied successfully for store:" + storeno);

							}

						} while (count <= retrycount); // if file copy failed program retry based on configurations

					}

				} else { // file not exists from source path
					log.info("No files to process for store:" + storeno);
				}

			} else { // store copy disabled
				log.info("File copy skipped for store:" + storeno);
			}

		} catch (Exception error) {
			log.error(methodName + " -Exception:" + error);
			return false;
		}
		log.debug(methodName + " Method completed");
		return true;
	}

}
