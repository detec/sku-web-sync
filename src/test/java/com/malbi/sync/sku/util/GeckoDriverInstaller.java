package com.malbi.sync.sku.util;

import java.net.URL;

/**
 * This class holds methodd that installs Gecko driver.
 *
 * @author Andrii Duplyk
 *
 */
public class GeckoDriverInstaller {

	public static void setGeckoDriverPath() {
		String osString = System.getProperty("os.name");

		if (osString.contains("Windows")) {
			URL geckoWindowsExecutableUrl = ClassLoader.getSystemResource("geckodriver.exe");
			System.setProperty("webdriver.gecko.driver", geckoWindowsExecutableUrl.getPath());
		} else {
			URL geckoLinux64Url = ClassLoader.getSystemResource("geckodriver");
			System.setProperty("webdriver.gecko.driver", geckoLinux64Url.getPath());
		}
	}

}
