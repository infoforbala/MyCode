package com.tesco.plm.rms.filetransfer;

/* ************************************************************************************
 * JIRA Story  : RMSODS-11148
 * Author      : Prabu Selvaraj
 * Date        : 11-NOV-2022
 * Details 	   : New microservice for transferring file from source to destination.
 *               
 *               This service will perform the below actions
 *               # Read the properties file from  "$KAFKAHOME/filetransfer/config/filetransfer.yml"
 *               #check serviceshutdown Y or N.
 *               #check Servicectrlflag on or off.
 *               #read interface details & store list from property files
 *               #based on configuration file transfer from source to destination
 **************************************************************************************/
/* ************************************************************************************
 * JIRA Story  : RMSODS-11115
 * Class Name  : FileTransfer
 * Author      : Prabu Selvaraj
 * Date        : 11-NOV-2022
 * Details 	   : This class contains the main method and control starts from here.
 * 				it is responsible for setting up the config variables for the program
 * 				and call the run override method.
 **************************************************************************************/
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.tesco.plm.rms.filetransfer.common.ApplicationProperty;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class FileTransfer {

	// Application configuration file loading
	@Bean
	@ConditionalOnProperty(name = "spring.config.additional-location", matchIfMissing = false)
	public ApplicationProperty properties(@Value("${spring.config.additional-location}") String configFiles) {
		ApplicationProperty properties;
		String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		try {
			log.debug(methodName + "-Method Started");
			log.info("Application property load Started ...");
			//Load property files
			properties = new ApplicationProperty(Arrays.asList(configFiles.split(",")));
			log.info("Application Properties and Config load completed Successfully");
		} catch (Exception error) {
			log.error("Error While loading the application properties and config- File-" + configFiles + "-Error-"
					+ error);
			return null;
		}
		log.debug(methodName + "-Method Completed");
		return properties;
	}

	// Main method
	public static void main(String[] args) {
		String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		log.debug(methodName + " Method Started");
		FileTransfer getclassname = new FileTransfer();
		//Assign configuration file path into appConfig variable
		String appConfig = System.getenv("KAFKAHOME") + getclassname.getClass().getSimpleName().toLowerCase()
				+ "/config/filetransferconfig.yml";
		log.info("Application Config File : " + appConfig);
		String[] args_upd = new String[args.length + 1];
		System.arraycopy(args, 0, args_upd, 0, args.length);
		args_upd[args.length] = "--spring.config.additional-location=" + appConfig;
		try {
			log.info("Spring application run method getting started");
			log.debug("Main Method run override Started ");
			//Spring application start
			SpringApplication.run(FileTransfer.class, args_upd);
			log.info("Spring application shutdown initiated");
			//Shutdown the service
			System.exit(0);			
		} catch (Exception error) {
			log.error("Unable to start the run override method-Service getting stopped-" + error);
			System.exit(-1);
		}
	}

}