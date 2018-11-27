package com.mdsol.rcm.setup;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

public class FileUtils {

	public static String getFileContent(String filePath) {
		InputStream inputStream;
		try {
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);

			ByteSource byteSource = new ByteSource() {
				@Override
				public InputStream openStream() throws IOException {
					return inputStream;
				}
			};

			return byteSource.asCharSource(Charsets.UTF_8).read();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
