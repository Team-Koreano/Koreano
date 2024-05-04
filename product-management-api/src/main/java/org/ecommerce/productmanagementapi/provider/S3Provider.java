package org.ecommerce.productmanagementapi.provider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.productmanagementapi.dto.ProductManagementDto;
import org.ecommerce.productmanagementapi.exception.ProductManagementErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Component
public class S3Provider {

	private final AmazonS3Client amazonS3Client;
	private final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(".png", ".jpg", ".jpeg");

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private void validateFileExtension(String fileName) {
		try {
			String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
			if (validateImageExtension(extension)) return;

			throw new CustomException(ProductManagementErrorCode.IS_INVALID_FILE_OPTION);
		} catch (StringIndexOutOfBoundsException e) {
			throw new CustomException(ProductManagementErrorCode.IS_INVALID_FILE_OPTION);
		}
	}

	public void deleteFile(String fileUrl) {
		try {
			URL url = new URL(fileUrl);
			String path = url.getPath();
			String filename = path.substring(path.lastIndexOf('/') + 1);

			amazonS3Client.deleteObject(bucket, filename);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public String uploadImageFile(MultipartFile file) throws IOException {
		if (!validateImageFile(file)) throw new CustomException(ProductManagementErrorCode.IS_INVALID_FILE_OPTION);
		return upload(file);
	}

	private boolean validateImageFile(MultipartFile file) {
		return validateImageExtension(parseExtension(file));
	}

	public List<ProductManagementDto.Request.Image> uploadImageFiles(MultipartFile thumbnailImage,
		List<MultipartFile> files) {
		final int numberOfThreads = (files != null ? files.size() : 0) + (thumbnailImage != null ? 1 : 0);
		final CountDownLatch latch = new CountDownLatch(numberOfThreads);
		ConcurrentHashMap<Short, ProductManagementDto.Request.Image> result = new ConcurrentHashMap<>(numberOfThreads);

		short count = 0;
		boolean hasThumbnail = false;

		if (thumbnailImage != null) {
			if (!validateImageFile(thumbnailImage))
				throw new CustomException(ProductManagementErrorCode.IS_INVALID_FILE_OPTION);
			count++;
			final short index = count;
			hasThumbnail = true;
			new Thread(() -> {
				try {
					String url = upload(thumbnailImage);
					result.put(index, ProductManagementDto.Request.Image.from(url, index, true));
				} catch (IOException e) {
					throw new RuntimeException(e);
				} finally {
					latch.countDown();
				}
			}).start();
		}

		if (files != null) {
			if (!validateImageFiles(files))
				throw new CustomException(ProductManagementErrorCode.IS_INVALID_FILE_OPTION);
			for (MultipartFile file : files) {
				count++;
				final short index = count;
				boolean isThumbnail = !hasThumbnail && index == 1; // 썸네일 이미지가 없고, 첫 번째 이미지일 경우
				new Thread(() -> {
					try {
						String url = upload(file);
						result.put(index, ProductManagementDto.Request.Image.from(url, index, isThumbnail));
					} catch (IOException e) {
						throw new RuntimeException(e);
					} finally {
						latch.countDown();
					}
				}).start();
			}
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		return new ArrayList<>(result.values());
	}

	private boolean validateImageFiles(Collection<MultipartFile> files) {
		Objects.requireNonNull(files);

		return files.stream()
			.map(this::parseExtension)
			.allMatch(this::validateImageExtension);
	}

	private String parseExtension(MultipartFile file) {
		String fileName = Objects.requireNonNull(file.getOriginalFilename());
		return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
	}

	private Boolean validateImageExtension(String extension) {
		return ALLOWED_IMAGE_EXTENSIONS.contains(extension);
	}

	private String upload(MultipartFile file) throws IOException {
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentType(file.getContentType());
		meta.setContentLength(file.getSize());

		String fileName = file.getOriginalFilename();

		amazonS3Client.putObject(bucket, fileName, file.getInputStream(), meta);

		return amazonS3Client.getUrl(bucket, fileName).toString();
	}
}

