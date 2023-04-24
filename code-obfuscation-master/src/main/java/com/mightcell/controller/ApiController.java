package com.mightcell.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mightcell.entity.File;
import com.mightcell.entity.request.PageBo;
import com.mightcell.entity.response.FileVo;
import com.mightcell.entity.response.PageVo;
import com.mightcell.exception.CodeException;
import com.mightcell.service.ApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author: MightCell
 * @description: 文件控制层
 * @date: Created in 22:30 2023-02-23
 */
@Slf4j
@Validated
@CrossOrigin
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class ApiController {

    private final ApiService apiService;

    private static final String[] C_MIME_TYPES = {"text/x-csrc", "text/plain", "text/x-c++src", "application/x-csrc", "application/octet-stream"};

    /**
     * 获取配置文件中的文件存储路径，将文件存储到本地/服务器
     */
    @Value("${file.uploadUrl}")
    private String uploadPath;

    /**
     * 查询当前登录用户所有的文件
     * @return 文件信息列表
     */
    @GetMapping("/file/list")
    public SaResult getFileList() {
        List<FileVo> files = apiService.getFileInfoByUserId();
        if (!Objects.isNull(files)) {
            return SaResult.ok("获取成功").setData(files);
        }

        return SaResult.error("获取失败");
    }

    /**
     * 根据文件ID查询文件信息
     * @param fileId
     * @return 文件信息
     */
    @GetMapping("/file/id/{fileId}")
    public SaResult getFileInfo(@PathVariable Long fileId) {
        if (Objects.isNull(fileId)) {
            log.info("File id is null");
            throw new CodeException("文件id为空");
        }
        FileVo safetyFile = apiService.getFileInfoByFileId(fileId);
        if (!Objects.isNull(safetyFile)) {
            return SaResult.ok("获取成功").setData(safetyFile);
        }
        return SaResult.error("获取失败");
    }


    /**
     * 文件上传
     *
     * @param file 上传文件
     * @return 上传结果信息
     * @throws IOException 读写异常
     */
    @PostMapping("/file/upload")
    public SaResult fileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            log.info("Upload file is empty");
            throw new CodeException("上传文件为空");
        }
        // 对上传的文件名进行验证和过滤
        // 只允许字母、数字、下划线和短横线
        String originalFilename = file.getOriginalFilename();
        String pattern = "^[a-zA-Z0-9_-]+\\.[a-zA-Z0-9]+$";
        if (!originalFilename.matches(pattern)) {
            log.info("The upload file name contains invalid characters");
            throw new CodeException("上传文件名包含非法字符");
        }
        // 获取文件名部分
        String safeFilename = FilenameUtils.getName(originalFilename);
        if (!originalFilename.equals(safeFilename)) {
            log.info("The upload file name contains path separators");
            throw new CodeException("上传文件名包含路径分隔符");
        }
        // 替换双引号
        StringUtils.replaceChars(originalFilename, "\"", "_");
        if (!originalFilename.equals(safeFilename)) {
            log.info("The upload file name contains special characters");
            throw new CodeException("上传文件名包含特殊字符");
        }
        Tika tika = new Tika();
        String mimeType = tika.detect(file.getInputStream());
        log.info("The MIME type of the current file is : {}", mimeType);
        boolean isValid = false;
        for (String cType : C_MIME_TYPES) {
            if (cType.equals(mimeType)) {
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            log.info("The upload file is not a C source file");
            throw new CodeException("上传文件类型不符");
        }

        FileVo safetyFile = apiService.uploadFile(file, uploadPath);
        if (!Objects.isNull(safetyFile)) {
            return SaResult.ok("文件上传成功").setData(safetyFile);
        }
        return SaResult.error("文件上传失败");
    }

    /**
     * 文件下载
     * @param fileName 文件名
     * @param response 响应
     * @return 文件以比特流响应
     */
    @GetMapping("/file/download/{fileName}")
    public SaResult fileDownload(@PathVariable(value = "fileName") String fileName, HttpServletResponse response) {
        if (StringUtils.isBlank(fileName)) {
            log.info("File name is blank");
            throw new CodeException("文件名为空");
        }
        apiService.downloadFile(fileName, uploadPath, response);
        return SaResult.ok("文件下载成功");
    }

    /**
     * OLLVM保护操作
     * @param fileName 文件名
     * @param paramList 参数列表
     * @return 保护后文件信息
     */
    @PostMapping("/file/protect/ollvm/{fileName}")
    public SaResult performShellScript(@PathVariable(value = "fileName") String fileName, @RequestBody ArrayList<String> paramList) {
        if (StringUtils.isBlank(fileName)) {
            log.info("File name is blank");
            throw new CodeException("文件名为空");
        }
        if (Objects.isNull(paramList)) {
            log.info("Param list is null");
            throw new CodeException("参数列表为空");
        }
        log.info("参数列表paramList为：{}", paramList);
        FileVo safetyFile = apiService.performProtectionOperation(fileName, paramList);
        if (!Objects.isNull(safetyFile)) {
            return SaResult.ok("文件保护成功").setData(safetyFile);
        }
        return SaResult.error("文件保护失败");
    }

    /**
     * 根据文件名查询文件信息
     * @param fileName 文件名
     * @return 目标文件信息
     */
    @GetMapping("/file/fileName/{fileName}")
    public SaResult getFileInfoByFileName(@PathVariable String fileName) {
        return getResultByFileName(fileName, (name) -> {
            FileVo safetyFile = apiService.getFileInfoByFileName(name);
            if (!Objects.isNull(safetyFile)) {
                return SaResult.ok("获取成功").setData(safetyFile);
            }
            return SaResult.error("获取失败");
        });
    }

    /**
     * 获取执行保护操作日志信息
     * @param pageBo 包含当前页码和显示记录数
     * @return 分页数据
     */
    @GetMapping("/file/page")
    public SaResult getPageVoInfo(PageBo pageBo) {
        if (Objects.isNull(pageBo)) {
            log.info("PageBo is null");
            throw new CodeException("分页参数接收对象为空");
        }
        Page<PageVo> pageInfo = apiService.getPageVoInfo(pageBo);
        if (!Objects.isNull(pageInfo)) {
            return SaResult.ok("获取成功").setData(pageInfo);
        }
        return SaResult.error("获取失败");
    }

    /**
     * 测试原始文件信息内容
     * @param pageBo
     * @return 原始文件的内容
     */
    @GetMapping("/file/test")
    public SaResult getPageInfo(PageBo pageBo) {
        Integer page = pageBo.getPage();
        Integer limit = pageBo.getLimit();
        Page<File> pageInfo = new Page<>(page, limit);
        LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fileLambdaQueryWrapper.orderByDesc(File::getUpdateTime);
        apiService.page(pageInfo, fileLambdaQueryWrapper);
        return SaResult.ok("获取成功").setData(pageInfo);
    }

    /**
     * 删除文件（文件名）
     * @param fileName 文件名
     * @return 是否删除成功
     */
    @DeleteMapping("/file/remove/fileName")
    public SaResult removeByFileName(@RequestParam("fileName") String fileName) {
        return getResultByFileName(fileName, (name) -> {
            boolean isDeleted = apiService.deleteFileByFileName(name);
            if (isDeleted) {
                return SaResult.ok("删除成功");
            }
            return SaResult.error("删除失败");
        });
    }

    /**
     * 删除文件（文件Id）
     * @param fileId 文件Id
     * @return 是否成功删除
     */
    @DeleteMapping("/file/remove/fileId/{fileId}")
    public SaResult removeByFileId(@PathVariable Long fileId) {
        boolean isDeleted = apiService.deleteFileByFileId(fileId);
        if (isDeleted) {
            return SaResult.ok("删除成功");
        }
        return SaResult.error("删除失败");
    }

    /**
     * 判断文件名是否为空
     * @param fileName 文件名
     * @param function 函数
     * @return 文件名
     */
    private SaResult getResultByFileName(String fileName, Function<String, SaResult> function) {
        if (StringUtils.isBlank(fileName)) {
            log.info("File name is blank");
            throw new CodeException("文件名为空");
        }
        return function.apply(fileName);
    }

    /**
     * 保护接口
     * @param fileName 文件名
     * @param pType 保护类型
     * @param arguments 参数列表
     * @return 保护后文件信息
     */
    @PostMapping("/file/protect/{pType}/{fileName}")
    public SaResult protect(@NotNull @PathVariable String fileName,
                            @NotNull @PathVariable int pType,
                            @NotNull @RequestBody List<String> arguments) {
        FileVo safetyFile = apiService.executeShell(fileName, pType, arguments);
        return SaResult.ok("保护成功").setData(safetyFile);
    }

    /**
     * 批量删除文件（文件id列表）
     * @param idList 文件id列表
     * @return 是否成功删除
     */
    @DeleteMapping("/file/batch-remove")
    public SaResult removeRows(@RequestBody List<String> idList) {
        boolean result = apiService.removeRowsByIds(idList);
        if (result) {
            return SaResult.ok("删除成功");
        }
        return SaResult.error("数据不存在");
    }

    /**
     * 根据日期统计注册人数
     * @param day 日期
     * @return 该日期注册人数
     */
    @GetMapping("/count-register-num/{day}")
    public SaResult countRegisterNum(@PathVariable String day) {
        log.info("Start to count the register number of the day");
        Integer num = apiService.countRegisterNum(day);
        return SaResult.ok("获取成功").setData(num);
    }

    /**
     * 获取当前登录人数
     * @return 登录人数
     */
    @GetMapping("/count-current-login")
    public SaResult countCurrentLoginNum() {
        Integer num = apiService.countLoginNum();
        if (!Objects.isNull(num)) {
            return SaResult.ok("获取成功").setData(num);
        }
        return SaResult.ok("获取失败");
    }

    /**
     * 根据日期统计上传文件数
     * @return 文件上传数
     */
    @GetMapping("/count-upload-num/{day}")
    public SaResult countUploadFileNum(@PathVariable String day) {
        log.info("Start to count the upload file number of the day");
        Integer num = apiService.countFileUploadNum(day);
        return SaResult.ok("获取成功").setData(num);
    }

    /**
     * 根据日期统计保护文件数
     * @param day 日期
     * @return 保护文件数
     */
    @GetMapping("/count-protect-file/{day}")
    public SaResult countProtectFileNum(@PathVariable String day) {
        log.info("Start to count the protect file number of the day");
        Integer num = apiService.countFileProtectNum(day);
        return SaResult.ok("获取成功").setData(num);
    }

}
