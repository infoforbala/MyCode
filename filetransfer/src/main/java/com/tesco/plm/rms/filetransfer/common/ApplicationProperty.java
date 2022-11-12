package com.tesco.plm.rms.filetransfer.common;
/* ************************************************************************************
 * JIRA Story  : RMSODS-11148
 * Class Name  : ApplicationProperty
 * Author      : Prabu Selvaraj
 * Date        : 11-NOV-2022
 * Details 	   : This class will perform the below task
 *               #load the application properties
 *               #file watcher service will monitor the property file changes 
 *               and reload the properties
 **************************************************************************************/
import java.io.FileInputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;


@SuppressWarnings("serial")
@Log4j2
public class ApplicationProperty extends Properties {

	public ApplicationProperty(List<String> configFile) {
		loadProperties(configFile);
		configFileWatcher(configFile);
	}


	private void configFileWatcher(List<String> configFile) {
		String methodName = new Object() {}
				.getClass()
				.getEnclosingMethod()
				.getName();
		log.debug(methodName+" Method started");
		try {

			log.debug("WatchService Started");
			final WatchService watchService = FileSystems.getDefault().newWatchService();
			ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
			executor.execute(() -> {
				try {
					Path path = Paths.get(configFile.get(0)).toAbsolutePath().normalize().getParent();
					path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
					WatchKey watchKey;
					while ((watchKey = watchService.take()) != null) {
						Thread.sleep(1000);

						watchKey.pollEvents().stream().forEach(event -> {
							List<String> filteredList = configFile.stream().filter(
									p -> Paths.get(p).getFileName().toString().equals(event.context().toString()))
									.collect(Collectors.toList());

							if (filteredList.size() > 0)
								loadProperties(filteredList);
						});
						watchKey.reset();
					}
				} catch (Exception error) {
					log.error("ERROR in Watchservice while loading the Config: " + error.getMessage(), error);
				}
			});


			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						log.info("WatchService Shutdown initiated");
						executor.shutdown();
						log.info("WatchService Shutdown completed");
					} catch (Exception error) {
						log.error("Error while Shutdown the watch service "+error.getMessage(), error);
					}
				}
			});


			log.debug(methodName+" Method completed");
		} catch (Exception error) {
			log.error(methodName+" Method failed with Exception : " + error);
			throw new RuntimeException(error.getMessage(), error);
		}
	}



	private void loadProperties(List<String> configFile) {
		String methodName = new Object() {}
				.getClass()
				.getEnclosingMethod()
				.getName();
		log.debug(methodName+" Method started");
		try {
			for (String curFile : configFile) {				
				try (FileInputStream fis = new FileInputStream(curFile.toString())) {
					super.load(fis);
					Thread.sleep(1000);
				} catch (Exception error) {
					log.error(methodName+" Method failed with Exception :" + error);
					throw new RuntimeException(error);
				}
				log.info("Config file - {} loaded successfully", Paths.get(curFile).toAbsolutePath().normalize());
				log.debug(methodName+" Method completed");
			}
		} catch (Exception error) {
			log.error(methodName+" Method failed with Exception :" + error);
			throw new RuntimeException(error);
		}
	}

}
