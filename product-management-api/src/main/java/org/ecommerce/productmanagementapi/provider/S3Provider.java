package org.ecommerce.productmanagementapi.provider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.productmanagementapi.aop.TimeCheck;
import org.ecommerce.productmanagementapi.dto.ProductManagementDto;
import org.ecommerce.productmanagementapi.exception.ProductManagementErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
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

	@TimeCheck
	public List<ProductManagementDto.Request.Image> uploadImageFiles(MultipartFile thumbnailImage,
		List<MultipartFile> files) {

		final int numberOfThreads = (files != null ? files.size() : 0) + (thumbnailImage != null ? 1 : 0);

		final ExecutorService executorService = Executors.newFixedThreadPool(Math.min(numberOfThreads,
			4));

		List<CompletableFuture<ProductManagementDto.Request.Image>> tasks = new ArrayList<>(numberOfThreads);

		short count = 0;
		boolean hasThumbnail = false;

		if (thumbnailImage != null) {
			if (!validateImageFile(thumbnailImage))
				throw new CustomException(ProductManagementErrorCode.IS_INVALID_FILE_OPTION);
			count++;
			final short index = count;
			hasThumbnail = true;
			tasks.add(CompletableFuture.supplyAsync(() -> {
				try {
					String url = upload(thumbnailImage);
					return ProductManagementDto.Request.Image.ofCreate(url, index, true);
				} catch (IOException e) {
					throw new CustomException(ProductManagementErrorCode.FAILED_FILE_UPLOAD);
				}
			}, executorService));
		}

		if (files != null) {
			short index = thumbnailImage != null ? (short)2 : (short)1;
			for (MultipartFile file : files) {
				final short finalIndex = index++;
				boolean isThumbnail = !hasThumbnail && finalIndex == 1;
				tasks.add(CompletableFuture.supplyAsync(() -> {
					try {
						String url = upload(file);
						return ProductManagementDto.Request.Image.ofCreate(url, finalIndex, isThumbnail);
					} catch (IOException e) {
						throw new CustomException(ProductManagementErrorCode.FAILED_FILE_UPLOAD);
					}
				}, executorService));
			}
		}

		try {
			CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
		} catch (CompletionException e) {
			throw new CustomException(ProductManagementErrorCode.FAILED_FILE_UPLOAD);
		} finally {
			executorService.shutdown();
		}

		return tasks.stream()
			.map(CompletableFuture::join)
			.collect(Collectors.toList());
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
		log.info("now ThreadName = {}", Thread.currentThread().getName());
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentType(file.getContentType());
		meta.setContentLength(file.getSize());

		String fileName = file.getOriginalFilename();

		amazonS3Client.putObject(bucket, fileName, file.getInputStream(), meta);

		return amazonS3Client.getUrl(bucket, fileName).toString();
	}
}

