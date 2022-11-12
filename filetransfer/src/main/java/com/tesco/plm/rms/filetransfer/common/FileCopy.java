package com.tesco.plm.rms.filetransfer.common;
/* ************************************************************************************
 * JIRA Story  : RMSODS-11148
 * Class Name  : FileCopy
 * Author      : Prabu Selvaraj
 * Date        : 11-NOV-2022
 * Details 	   : This class will be used for file copy from one folder to another
 **************************************************************************************/
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.logging.log4j.core.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

@Log4j2
@Component
public class FileCopy {
	
	@Autowired
	private ApplicationProperty prop;
	
	// Processed and error directory declartion
		public boolean FileCopyToDir(String inputFilename, String destFileOrDir) {
			String methodName = new Object() {}
					.getClass()
					.getEnclosingMethod()
					.getName();
			log.debug(methodName+" Method started");
			try {
			
			File sourceFile = new File(inputFilename);
			File destinationFile = new File(destFileOrDir);
			String destiationFileName;

			// Check destFileOrDir argument is file or Directory
			if (destinationFile.isFile())
				destiationFileName = destFileOrDir;
			else
				destiationFileName = destFileOrDir + File.separator + sourceFile.getName();

			try {
				Files.copy(Paths.get(inputFilename), Paths.get(destiationFileName), StandardCopyOption.REPLACE_EXISTING);
				log.info(inputFilename+" file successfully moved to target directory "+ destFileOrDir);
			} catch (IOException error) {
				log.error("File movement to target directory failed: Source file :" + inputFilename + ":Target directory"
						+ destFileOrDir + "-Exception" + error);
				return false;
			}
				log.debug(methodName+" Method completed");
		
			}catch(Exception error) {
				log.error(methodName+ "Method failed with Exception :"+ error);
				return false;
			}
			
			return true;
		}			
}
