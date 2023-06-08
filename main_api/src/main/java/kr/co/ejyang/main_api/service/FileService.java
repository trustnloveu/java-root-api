package kr.co.ejyang.main_api.service;

import kr.co.ejyang.main_api.dto.FileParamDto;
import kr.co.ejyang.main_api.dto.FileRedisDto;
import kr.co.ejyang.main_api.dto.FileResponseDto;
import kr.co.ejyang.main_api.submodule.module_file.FileModuleUtil;
import kr.co.ejyang.main_api.submodule.module_file_util.FileCommonUtilModuleUtil;
import kr.co.ejyang.main_api.submodule.module_redis.RedisModuleUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static kr.co.ejyang.main_api.config.CommonConsts.RD_KEY_TEMP_URL_PREFIX;

@Slf4j
@Service
public class FileService {

    private final FileModuleUtil fileModuleUtil;    // 파일 모듈
    private final FileCommonUtilModuleUtil fileCommonUtilModuleUtil;      // 파일 모듈 유틸
    private final RedisModuleUtil redisModuleUtil;    // 레디스 모듈

    // 생성자
    FileService(
            @Autowired FileModuleUtil fileModuleUtil,
            @Autowired FileCommonUtilModuleUtil fileCommonUtilModuleUtil,
            @Autowired RedisModuleUtil redisModuleUtil
    ) {
        this.fileModuleUtil = fileModuleUtil;
        this.fileCommonUtilModuleUtil = fileCommonUtilModuleUtil;
        this.redisModuleUtil = redisModuleUtil;
    }
    /*******************************************************************************************
     * 단일 파일 업로드 - 파일명 입력 X
     *******************************************************************************************/
    public FileResponseDto uploadSingleFileWithoutName(FileParamDto.Upload param, MultipartFile file) {
        return fileModuleUtil.uploadSingleFileWithoutName(param.savePath, file);
    }

    /*******************************************************************************************
     * 단일 파일 업로드 - 파일명 입력 O
     *******************************************************************************************/
    public FileResponseDto uploadSingleFileWithName(FileParamDto.UploadWithName param, MultipartFile file) {
        return fileModuleUtil.uploadSingleFileWithName(param.savePath, param.saveName, file);
    }

    /*******************************************************************************************
     * 복수 파일 업로드
     *******************************************************************************************/
    public List<FileResponseDto> uploadMultiFiles(FileParamDto.Upload param, MultipartFile[] files) {
        return fileModuleUtil.uploadMultiFiles(param.savePath, files);
    }

    /*******************************************************************************************
     * 파일 삭제
     *******************************************************************************************/
    public void deleteFile(String path) {
        fileModuleUtil.deleteFile(path);
    }

    /*******************************************************************************************
     * 파일 다운로드
     *******************************************************************************************/
    public InputStreamResource downloadFile(String path) {
        return fileModuleUtil.downloadFile(path);
    }

    /*******************************************************************************************
     * 임시 URL 발급 ( Redis Key )
     *******************************************************************************************/
    public String updateFileTempUrlOnRedis(String savePath, String saveName) {
        // 임시 URL 생성
        String tempUrl = RD_KEY_TEMP_URL_PREFIX + fileCommonUtilModuleUtil.generateFileTempUrl();

        // Redis Value 설정
        FileRedisDto redisDto = FileRedisDto.builder()
                .savePath(savePath)
                .saveName(saveName)
                .build();

        // Redis 업데이트
        redisModuleUtil.setRedisDataWithTTL(tempUrl, redisDto.toString());

        // 등록된 임시 URL 반환
        return tempUrl;
    }


}
