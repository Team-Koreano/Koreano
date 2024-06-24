package org.ecommerce.productapi.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.productapi.exception.ProductErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUtils {
	private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(".png", ".jpg", ".jpeg");

	public static String parseExtension(MultipartFile file) {
		String fileName = Objects.requireNonNull(file.getOriginalFilename());
		return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
	}

	public static boolean validateImageExtension(String extension) {
		return ALLOWED_IMAGE_EXTENSIONS.contains(extension);
	}

	public static void validateFileExtension(String fileName) {
		try {
			String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
			if (validateImageExtension(extension)) return;

			throw new CustomException(ProductErrorCode.IS_INVALID_FILE_OPTION);
		} catch (StringIndexOutOfBoundsException e) {
			throw new CustomException(ProductErrorCode.IS_INVALID_FILE_OPTION);
		}
	}

	public static boolean validateImageFile(MultipartFile file) {
		return validateImageExtension(parseExtension(file));
	}

	public static boolean validateImageFiles(Collection<MultipartFile> files) {
		Objects.requireNonNull(files);
		return files.stream()
			.map(FileUtils::parseExtension)
			.allMatch(FileUtils::validateImageExtension);
	}

	public static String extractFileNameFromUrl(String fileUrl) {
		try {
			URL url = new URL(fileUrl);
			String path = url.getPath();
			return path.substring(path.lastIndexOf('/') + 1);
		} catch (MalformedURLException e) {
			throw new CustomException(ProductErrorCode.IS_INVALID_FILE_OPTION);
		}
	}
}
