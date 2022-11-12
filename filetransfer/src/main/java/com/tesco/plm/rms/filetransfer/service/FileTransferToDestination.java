package com.tesco.plm.rms.filetransfer.service;
/* ************************************************************************************
 * JIRA Story  : RMSODS-11148
 * Class Name  : FileTransferToDestination
 * Author      : Prabu Selvaraj
 * Date        : 04-NOV-2022
 * Details 	   : This class will perform the below 
 *               #read interface name from porperty file
 *               #read storelist from property file
 *               #call filecopyprocess class
 **************************************************************************************/

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tesco.plm.rms.filetransfer.common.ApplicationProperty;
import com.tesco.plm.rms.filetransfer.common.FileCopy;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class FileTransferToDestination {

	@Autowired
	private ApplicationProperty prop;

	@Autowired
	FileCopyProcess filecopyprocess;

	@Autowired
	FileCopy filecopy;

	public boolean filetransfertostoreline() {

		String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		log.info(methodName + " Method started");
		try {
			// read the interface list from property files
			String interfacenamefromporperty = prop.getProperty("interfacename");
			// split the interface name from interfacenamefromporperty
			List<String> interfacelist = Stream.of(interfacenamefromporperty.split(";")).collect(Collectors.toList());

			// loop the interface list to file transfer
			for (String interfacename : interfacelist) {
				log.info("Interface name -" + interfacename + "initiated for file transfer");
				// read the store list from property file for respective interface
				String storeconfig = prop.getProperty(interfacename + "storelist");
				// split the storelist
				List<String> storeconfiglist = Stream.of(storeconfig.split(";")).collect(Collectors.toList());

				// loop the store list to transfer the files
				for (String store : storeconfiglist) {

					//split storeno and ipaddress and control flag
					String[] storevalueSplit = store.split(",");
					String storeno = storevalueSplit[0];
					String storeipaddress = storevalueSplit[1];
					String storefilecopy = storevalueSplit[2];
					log.debug(
							"store-" + store + ",storeipaddress-" + storeipaddress + ",storefilecopy-" + storefilecopy);

					//initiate the filecopy process
					boolean status = filecopyprocess.filecopyprocess(interfacename, storefilecopy, storeno,storeipaddress);

					//check file copy failed
					if (status == false) {
						log.error("File copy failed for store:" + storeno + ",After " + prop.getProperty("retrycount")
								+ " retry attempts.");
					}

				}

			}

		} catch (Exception error) {
			log.error("Exception" + error);
			return false;
		}

		return true;

	}

}
