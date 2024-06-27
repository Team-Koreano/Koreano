package org.ecommerce.productapi.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.ecommerce.productapi.exception.ProductErrorCode;
import org.ecommerce.productapi.util.FileUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.productapi.dto.ImageDto;
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

	public void deleteFile(List<String> fileUrls) {
		for (String fileUrl : fileUrls) {
			try {
				String filename = FileUtils.extractFileNameFromUrl(fileUrl);
				amazonS3Client.deleteObject(bucket, filename);
			} catch (CustomException e) {
				log.error("Invalid file URL: {}", fileUrl, e);
			}
		}
	}

	public String uploadImageFile(MultipartFile file) throws IOException {
		if (!FileUtils.validateImageFile(file))
			throw new CustomException(ProductErrorCode.IS_INVALID_FILE_OPTION);
		return upload(file);
	}

	public List<ImageDto> uploadImageFiles(MultipartFile thumbnailImage,
		List<MultipartFile> files) {

		FileUtils.validateImageFile(thumbnailImage);
		FileUtils.validateImageFiles(files);

		final int numberOfThreads = (files != null ? files.size() : 0) + (thumbnailImage != null ? 1 : 0);

		final ExecutorService executorService = Executors.newFixedThreadPool(Math.min(numberOfThreads, 4));

		List<CompletableFuture<ImageDto>> tasks = new ArrayList<>(numberOfThreads);

		short count = 0;
		boolean hasThumbnail = false;

		if (thumbnailImage != null) {
			count++;
			final short index = count;
			hasThumbnail = true;
			tasks.add(CompletableFuture.supplyAsync(() -> {
				try {
					String url = upload(thumbnailImage);
					return ImageDto.ofCreate(url, index, true);
				} catch (IOException e) {
					throw new CustomException(ProductErrorCode.FAILED_FILE_UPLOAD);
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
						return ImageDto.ofCreate(url, finalIndex, isThumbnail);
					} catch (IOException e) {
						throw new CustomException(ProductErrorCode.FAILED_FILE_UPLOAD);
					}
				}, executorService));
			}
		}

		try {
			CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
		} catch (CompletionException e) {
			throw new CustomException(ProductErrorCode.FAILED_FILE_UPLOAD);
		} finally {
			executorService.shutdown();
		}

		return tasks.stream()
			.map(CompletableFuture::join)
			.collect(Collectors.toList());
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

