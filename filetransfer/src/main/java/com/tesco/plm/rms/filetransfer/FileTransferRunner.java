package com.tesco.plm.rms.filetransfer;
/* ************************************************************************************
 * JIRA Story  : RMSODS-11148
 * Class Name  : FileTransferRunner
 * Author      : Prabu Selvaraj
 * Date        : 11-NOV-2022
 * Details 	   : This class will perfrom the below task
 *               #check serviceshutdown Y or N.
 *               #check Servicectrlflag on or off.
 *               #call filetransfertostoreline() method
 **************************************************************************************/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tesco.plm.rms.filetransfer.common.ApplicationProperty;
import com.tesco.plm.rms.filetransfer.common.Sleep;
import com.tesco.plm.rms.filetransfer.service.FileTransferToDestination;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class FileTransferRunner implements CommandLineRunner {

	@Autowired
	ApplicationProperty prop;

	@Autowired
	FileTransferToDestination filetransfer;

	@Autowired
	Sleep sleepmethod;

	/*
	 * This run method will override the spring default run and perform the below
	 */
	@Override
	public void run(String... args) {
		// This flag used for Service_on_off flag change identification.
		String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		log.debug(methodName + " Method Started");

		String servicectrlflagsnap = null;

		while (true) {
			try {

				// Read variable values from properties
				String servicectrlflag = prop.get("Servicectrlflag").toString().trim().toLowerCase();
				String serviceshutdown = prop.get("serviceshutdown").toString().trim().toLowerCase();

				// Checking the Service Shutdown config
				if (serviceshutdown.equals("y")) {
					log.info("Service is marked for Shutdown- serviceshutdown flag set as :" + serviceshutdown
							+ " ,Hence Integration Service is getting shutdown");
					System.exit(0);
				}

				// Checking the Service Control
				if (servicectrlflag.equals("on")) {
					// call filetransfertostoreline() method to copy files from source to
					// destination
					filetransfer.filetransfertostoreline();
					servicectrlflagsnap = servicectrlflag;
				} else if (!servicectrlflag.equalsIgnoreCase(servicectrlflagsnap) && servicectrlflag.equals("off")) {
					log.info("Service Control flag changed in Config as " + servicectrlflag
							+ " ,Hence Integration Service is paused");
					log.info("Service will be in sleep loop until the service flag changed from off to on ");
					servicectrlflagsnap = servicectrlflag;
				}
				// Service will go for standard sleep when there is nothing to do
				sleepmethod.sleep(Long.parseLong(prop.get("sleepinmilliseconds").toString()));

			} catch (Exception error) {
				log.error("Exception in override run method :" + error);
				log.error("Service will try to resume post standard sleep duration :" + error);
				// Service will go for sleep and resume
				sleepmethod.sleep(Long.parseLong(prop.get("sleepinmilliseconds").toString()));
			}

			log.debug(methodName + " Method completed");
		}

	}

}