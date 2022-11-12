package com.tesco.plm.rms.filetransfer.common;
/* ************************************************************************************
 * JIRA Story  : RMSODS-11148
 * Class Name  : Sleep
 * Author      : Prabu Selvaraj
 * Date        : 04-NOV-2022
 * Details 	   : This class will be used for sleep the services
 **************************************************************************************/
import org.springframework.stereotype.Component;

import com.tesco.plm.rms.filetransfer.FileTransferRunner;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class Sleep {
	
	//This method will initiate the service sleep
	public boolean sleep(Long sleepmin) {
		String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		log.debug(methodName + " Method Started");
		log.info("Service going to sleep mode for next " + sleepmin + " Milliseconds");
		try {
			Thread.sleep(sleepmin);
		} catch (NumberFormatException error) {
			log.error("NumberFormatException in Sleep Method :" + error);
			return false;
		} catch (InterruptedException error) {
			log.error("InterruptedException in Sleep Method :" + error);
			return false;
		}
		log.info("Service getting resumed post sleep");
		log.debug(methodName + " Method Completed");
		return true;
	}

}
