package com.sprarta.sproutmarket.domain.image.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.image.service.ImageService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3ImageServiceImpl implements ImageService {
    private final AmazonS3 amazonS3;

    @Value("${s3.bucketName}")
    private String bucketName;

    public String uploadImage(MultipartFile image, Long ItemId, CustomUserDetails authUser) {
        // 입력된 이미지 파일이 빈 파일인지 검증
        if(image.isEmpty() || Objects.isNull(image.getOriginalFilename())){
            throw new ApiException(ErrorStatus.EMPTY_FILE_EXCEPTION);
        }
        // uploadImage 호출
        return this.getPublicImageUrl(image, ItemId, authUser);
    }

    // S3에 저장된 이미지의 public url을 반환해줌
    public String getPublicImageUrl(MultipartFile image, Long ItemId, CustomUserDetails authUser) {
        // 확장자 옳은지 확인
        this.validateImageFileExtention(image.getOriginalFilename());
        try {
            // 이미지를 S3에 업로드함(public url을 반환함)
            return this.uploadImageToS3(ItemId, image, authUser);
        } catch (IOException e) {
            throw new ApiException(ErrorStatus.IO_EXCEPTION_ON_IMAGE_UPLOAD);
        }
    }

    // 파일 확장자(jpg, jpeg, png, gif) 검증
    private String validateImageFileExtention(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new ApiException(ErrorStatus.NO_FILE_EXTENSION);
        }

        String extention = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtentionList.contains(extention)) {
            throw new ApiException(ErrorStatus.INVALID_FILE_EXTENSION);
        }
        return filename;
    }

    // 직접 S3에 업로드하는 메서드
    private String uploadImageToS3(Long itemId, MultipartFile image, CustomUserDetails authUser) throws IOException {
        // UUID로 파일명 재설정
        String s3FileName = createFileName(image);
        String extention = s3FileName.substring(s3FileName.lastIndexOf(".")); //확장자 명
        InputStream is = image.getInputStream();
        // image -> byte[]로 변환
        byte[] bytes = IOUtils.toByteArray(is);
        // 파일 경로 생성
        String filePath = String.format("user-uploads/%d/%d/%s", authUser.getId(), itemId, s3FileName);

        // metadata 생성
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(bytes.length);

        // S3에 요청할 때 사용할 byteInputStream 생성
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try{
            // S3로 pubObject할 때, 사용할 요청 객체
            // bucket 이름, 파일명, byteInputStream, metadata
            PutObjectRequest putObjectRequest =
                new PutObjectRequest(bucketName, filePath, byteArrayInputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            // 실제로 S3에 이미지 데이터를 넣는 부분
            amazonS3.putObject(putObjectRequest);
        } catch (AmazonServiceException e) {
            // S3 서비스 관련 예외 처리
            throw new ApiException(ErrorStatus.S3_SERVICE_EXCEPTION);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new ApiException(ErrorStatus.UNKNOWN_EXCEPTION);
        } finally {
            byteArrayInputStream.close();
            is.close();
        }

        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }

    // 이미지 삭제
    public void deleteImage(String imageAddress){
        // 삭제에 필요한 key 가져옴
        String key = getKeyFromImageAddress(imageAddress);
        try{
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        }catch (Exception e){
            throw new ApiException(ErrorStatus.IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }

    private String getKeyFromImageAddress(String imageAddress){
        try{
            URL url = new URL(imageAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1); // 맨 앞의 '/' 제거
        }catch (MalformedURLException | UnsupportedEncodingException e){
            throw new ApiException(ErrorStatus.IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }

    public static String createFileName(MultipartFile image) {
        String originalFilename = image.getOriginalFilename(); // 원본 파일명

        // 원본 파일명에서 확장자 추출
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")); // 확장자명 추출

        // UUID를 기반으로 변경된 파일명 생성
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + extension; // 변경된 파일명
        return s3FileName;
    }



}