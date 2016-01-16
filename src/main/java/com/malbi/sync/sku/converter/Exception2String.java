package com.malbi.sync.sku.converter;

import java.io.PrintWriter;
import java.io.StringWriter;

// http://stackoverflow.com/questions/12007747/display-exception-stack-trace-into-facelets-page

public class Exception2String {

	public static String printStackTrace(Throwable exception) {
		StringWriter stringWriter = new StringWriter();
		stringWriter.append(exception.getMessage() + "\n");

		exception.printStackTrace(new PrintWriter(stringWriter, true));
		return stringWriter.toString();
	}

}
