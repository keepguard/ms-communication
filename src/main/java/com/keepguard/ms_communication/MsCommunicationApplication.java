package com.keepguard.ms_communication;

import com.keepguard.lib_common.config.MetricsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {"com.keepguard.ms_communication", "com.keepguard.lib_common"})
@Import(MetricsConfig.class)
public class MsCommunicationApplication {

	// HOT DEPLOY FUNCIONANDO! - ENTRYPOINT CORRIGIDO - 2025-07-28
	public static void main(String[] args) {
		SpringApplication.run(MsCommunicationApplication.class, args);
	}

}