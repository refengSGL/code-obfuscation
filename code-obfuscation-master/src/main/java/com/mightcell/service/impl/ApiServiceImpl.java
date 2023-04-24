package com.mightcell.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mightcell.entity.File;
import com.mightcell.entity.User;
import com.mightcell.entity.request.PageBo;
import com.mightcell.entity.response.FileVo;
import com.mightcell.entity.response.PageVo;
import com.mightcell.exception.CodeException;
import com.mightcell.mapper.FileMapper;
import com.mightcell.mapper.UserMapper;
import com.mightcell.service.ApiService;
import com.mightcell.utils.ExceptionUtils;
import com.mightcell.utils.ProtectiveAction;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mightcell.constant.FileConstant.*;
import static com.mightcell.constant.UserConstant.IS_LOGIN;

/**
 * @author: MightCell
 * @description:
 * @date: Created in 22:36 2023-02-23
 */
@Service
@Slf4j
@Data
@RequiredArgsConstructor
public class ApiServiceImpl extends ServiceImpl<FileMapper, File> implements ApiService {

    private final UserMapper userMapper;
    public static Integer userId = 9;

    /**
     * 获取当前在线人数
     * @return 在线人数
     */
    @Override
    public Integer countLoginNum() {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getIsLogin, IS_LOGIN);
        return userMapper.selectCount(queryWrapper);
    }

    /**
     * 统计当日用户注册数
     * @param day 日期
     * @return 当日用户注册数
     */
    @Override
    public Integer countRegisterNum(String day) {
        return baseMapper.selectRegisterNumByDay(day);
    }

    /**
     * 统计当日文件上传数
     * @param day 日期
     * @return
     */
    @Override
    public Integer countFileUploadNum(String day) {
        return baseMapper.selectUploadFileNumByDay(day);
    }

    /**
     * 统计当日文件保护数
     * @param day 日期
     * @return
     */
    @Override
    public Integer countFileProtectNum(String day) {
        return baseMapper.selectProtectFileNumByDay(day);
    }

    /**
     * 查找文件（用户id）
     * @return 文件列表
     */
    @Override
    public List<FileVo> getFileInfoByUserId() {
        log.info("Getting a list of all files for the current user");
        // 根据用户id查询文件列表
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(File::getUserId, userId).orderByDesc(File::getCreateTime);
        List<File> originalFileList = baseMapper.selectList(queryWrapper);
        if (!Objects.isNull(originalFileList)) {
            // 数据脱敏
            List<FileVo> safetyFileList = new ArrayList<>();
            log.info("Data desensitization is underway");
            for (File file : originalFileList) {
                FileVo safetyFile = getSafetyFile(file);
                safetyFileList.add(safetyFile);
            }
            return safetyFileList;
        }
        throw new CodeException("当前用户文件列表为空");
    }

    /**
     * 查找文件（文件id）
     * @param fileId 文件id
     * @return 文件信息
     */
    @Override
    public FileVo getFileInfoByFileId(Long fileId) {
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(File::getId, fileId);
        Integer isExistFile = baseMapper.selectCount(queryWrapper);
        if (isExistFile < FILE_EXIST) {
            log.info("The target file does not exist");
            throw new CodeException("目标文件不存在");
        }
        File originalFile = baseMapper.selectOne(queryWrapper);
        // 数据脱敏
        return getSafetyFile(originalFile);
    }

    /**
     * 上传文件
     * @param file  上传文件
     * @param uploadPath 保存路径
     * @return 文件信息
     */
    @Override
    public FileVo uploadFile(MultipartFile file, String uploadPath) {
        if (Objects.isNull(file)) {
            throw new CodeException("未找到上传文件");
        }
        // 获取上传路径的目录对象
        java.io.File curFile = new java.io .File(uploadPath);
        if (!curFile.exists()) {
            // 目录对象不存在，创建目录结构
            boolean mkdirResult = curFile.mkdir();
            if (!mkdirResult) {
                log.info("Failed to create the directory structure");
                throw new CodeException("创建保存目录结构失败");
            }
            log.info("The directory structure is created successfully");
        }
        // 获取原始文件的后缀名
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isNotEmpty(originalFilename)) {
            log.info("Original file name exists");
            // 动态获取原始文件的后缀名
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 利用UUID随机生成文件名，防止文件名称重复，造成文件覆盖
            String fileName = UUID.randomUUID() + suffix;
            java.io.File dest = new java.io.File(uploadPath + fileName);
            // 文件转存
            try {
                log.info("The file is being transferred");
                file.transferTo(dest);

                // 封装返回类型
                File target = new File();
                // 设置文件名（包含后缀名）
                target.setName(fileName);
                // 设置文件类型（取出后缀名）
                target.setType(suffix.substring(1));
                // 设置文件大小（以字节为单位）
                target.setMemory(String.valueOf(file.getSize()));
                // 设置上传文件的用户ID
                target.setUserId(userId);
                // 设置文件在服务器上存储的路径
                target.setStore(uploadPath + fileName);
                // 设置文件原始文件名
                target.setOriginalFileName(originalFilename);
                // 将上传文件信息存入数据库中
                baseMapper.insert(target);
                // TODO 修改用户的上传数量

                return getSafetyFile(target);
            } catch (IOException e) {
                log.error(ExceptionUtils.getMessage(e));
                throw new CodeException("文件存储失败");
            }
        }
        log.info("Original file name does not exists");
        throw new CodeException("文件名为空");
    }

    /**
     * 调用开源工具进行扫描和分析：这里使用Clang Static Analyzer
     * @param filePath 文件存储路径
     * @return 文件是否包含恶意代码
     */
    private boolean runAnalysis(String filePath) {
        ProcessBuilder pb = new ProcessBuilder(SCAN_BUILD, CLANG, "-c", filePath);
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            // 获取输出结果文件
            String scanResultFilePath = readOutput(p);
            if (scanResultFilePath.contains(OUTPUT_CONTAIN_WARNING)) {
                log.info("The output contains {}", scanResultFilePath.toString());
                return false;
            }
        } catch (IOException e) {
            throw new CodeException("文件读写异常");
        }
        return true;
    }

    /**
     * 读取Clang Static Analyzer的输出
     * @param p 进程管理器
     * @return 输出信息
     * @throws IOException 读写异常
     */
    private String readOutput(Process p) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = p.getInputStream().read()) != END_OF_FILE) {
            sb.append((char) c);
        }
        return sb.toString();
    }

    /**
     * 文件信息脱敏
     * @param originalFile 原始文件
     * @return 脱敏文件
     */
    private FileVo getSafetyFile(File originalFile) {
        FileVo safetyFile = new FileVo();
        safetyFile.setName(originalFile.getName());
        safetyFile.setType(originalFile.getType());
        safetyFile.setMemory(originalFile.getMemory());
        safetyFile.setOriginalFileName(originalFile.getOriginalFileName());
        safetyFile.setCreateTime(originalFile.getUpdateTime());
        safetyFile.setOriginalFileId(originalFile.getOriginalFile());
        return safetyFile;
    }

    /**
     * 下载文件
     * @param fileName 文件名
     * @param uploadUrl 存储地址
     * @param response 响应信息
     */
    @Override
    public void downloadFile(String fileName, String uploadUrl, HttpServletResponse response) {
        String storeUrl = uploadUrl + fileName;
        // 获取文件对象
        java.io.File file = new java.io.File(storeUrl);
        if (!file.exists()) {
            log.info("The target file does not exists");
            throw new CodeException("目标文件不存在");
        }
        // 清空response
        response.reset();
        // 设置response的Header
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.addHeader("Content-Length", "" + file.length());
        ServletOutputStream outputStream;
        byte[] array;
        try {
            outputStream = response.getOutputStream();
            array = FileUtils.readFileToByteArray(file);
            outputStream.write(array);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new CodeException("文件下载失败");
        }
    }

    /**
     * OLLVM
     * @param fileName 待保护文件名
     * @param paramList 参数列表
     * @return 保护后文件信息
     */
    @Override
    public FileVo performProtectionOperation(String fileName, ArrayList<String> paramList) {
        // 获取ollvm类型
        String protectionCode = paramList.get(0);
        ProtectiveAction.callShellScript(fileName, paramList);

        // 文件名：fileName + protectionType
        String protectionType = ProtectiveAction.getProtectType(Integer.parseInt(protectionCode));
        String protectedFileName = fileName + protectionType;

        // 文件类型：protectedFileName的suffix
        String protectedSuffix = protectedFileName.substring(protectedFileName.lastIndexOf(".")).substring(1);

        // 用户ID：当前登录用户的ID

        // 存储路径：uploadPath + protectedFileName
        String storePath = FILE_UPLOAD_PATH + protectedFileName;

        // 取出resultFile的后缀名resultFileSuffix
        String resultFileSuffix = protectedFileName.substring(protectedFileName.lastIndexOf("."));
        // 利用UUID生成新的文件名
        String tempFileName = UUID.randomUUID() + resultFileSuffix;
        String newResultFileName = tempFileName.replace(C_SUFFIX, EMPTY_STRING);
        // 文件内存大小
        String fileMemory = ProtectiveAction.getFileMemory(protectedFileName);
        // 获取resultFile的文件对象，重命名为新的文件名
        java.io.File resultFile = ProtectiveAction.getResultFileObject(protectedFileName, newResultFileName);
        if (Objects.isNull(resultFile)) {
            log.info("Failed to rename the protected file");
            throw new CodeException("修改保护后文件名失败");
        }

        Integer originalFileId = getFileIdByFileName(fileName);
        if (Objects.isNull(originalFileId)) {
            log.info("The original file does not exist");
            throw new CodeException("原始文件不存在");
        }
        String originalFileNameByFileId = getOriginalFileNameByFileId(originalFileId);
        File resultStoreFile = new File();
        resultStoreFile.setName(newResultFileName);
        resultStoreFile.setType(OLLVM_ELF);
        resultStoreFile.setMemory(fileMemory);
        resultStoreFile.setUserId(userId);
        resultStoreFile.setStore(storePath);
        resultStoreFile.setOriginalFileName(originalFileNameByFileId);
        resultStoreFile.setOriginalFile(originalFileId);

        baseMapper.insert(resultStoreFile);

        return getSafetyFile(resultStoreFile);
    }

    /**
     * 获取原文件名（文件id）
     * @param fileId 文件id
     * @return 原文件名
     */
    private String getOriginalFileNameByFileId(Integer fileId) {
        File originalFile = getFileByIdOrName(fileId.toString(), GET_BY_ID);
        return originalFile.getOriginalFileName();
    }

    /**
     * 获取文件信息（文件名）
     * @param fileName 文件名
     * @return 文件信息
     */
    @Override
    public FileVo getFileInfoByFileName(String fileName) {
        File originalFile = getFileByIdOrName(fileName, GET_BY_NAME);
        return getSafetyFile(originalFile);
    }

    /**
     * 获取文件（文件名或者文件id）
     * @param identifier 文件名或者文件id
     * @param identifierType 获取类型
     * @return 文件对象
     */
    private File getFileByIdOrName(String identifier, String identifierType) {
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<>();
        if (GET_BY_ID.equals(identifierType)) {
            queryWrapper.eq(File::getId, identifier);
        } else if (GET_BY_NAME.equals(identifierType)) {
            queryWrapper.eq(File::getName, identifier);
        }
        Integer selectCount = baseMapper.selectCount(queryWrapper);
        if (selectCount < FILE_EXIST) {
            log.info("Target file does not exist");
            throw new CodeException("文件不存在");
        }
        File file = baseMapper.selectOne(queryWrapper);
        if (Objects.isNull(file)) {
            log.info("Target file does not exist");
            throw new CodeException("文件不存在");
        }
        return file;
    }


    /**
     * 分页查询
     * @param pageBo 分页查询参数
     * @return 分页返回数据
     */
    @Override
    public Page<PageVo> getPageVoInfo(PageBo pageBo) {
        Integer page = pageBo.getPage();
        Integer limit = pageBo.getLimit();
        String fileName = pageBo.getFileName();
        Page<File> pageInfo = new Page<>(page, limit);

        // 筛选出当前用户的保护后的文件
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.isNotNull(File::getOriginalFile).orderByDesc(File::getUpdateTime)
                .eq(File::getUserId, userId);
        if (StringUtils.isNotBlank(fileName)) {
            queryWrapper.likeRight(File::getOriginalFileName, fileName);
        }
        baseMapper.selectPage(pageInfo, queryWrapper);

        // 封装页面数据
        List<File> pageInfoRecords = pageInfo.getRecords();
        List<PageVo> safetyPageList = pageInfoRecords.stream().map(this::getSafetyPage).collect(Collectors.toList());
        Page<PageVo> resultPageInfo = new Page<>(page, limit, pageInfo.getTotal());
        resultPageInfo.setRecords(safetyPageList);
        return resultPageInfo;
    }

    /**
     * 数据脱敏（页面数据）
     * @param originalFile 原文件
     * @return 脱敏页面数据
     */
    private PageVo getSafetyPage(File originalFile) {
        PageVo safetyPage = new PageVo();
        Integer originalFileId = originalFile.getOriginalFile();
        File file = baseMapper.selectById(originalFileId);
        safetyPage.setQueryFileId(originalFile.getId());
        safetyPage.setOriginalFileName(originalFile.getOriginalFileName());
        if (Objects.isNull(file)) {
            log.info("Original file does not exist");
            safetyPage.setOriginalFileName("文件不存在");
        }
        safetyPage.setResultFileName(originalFile.getName());
        safetyPage.setUpdateTime(originalFile.getCreateTime());
        return safetyPage;
    }

    /**
     * 删除文件（文件名）
     * @param fileName 文件名
     * @return 是否成功删除
     */
    @Override
    public boolean deleteFileByFileName(String fileName) {
        Integer fileIdByFileName = getFileIdByFileName(fileName);
        if (Objects.isNull(fileIdByFileName)) {
            return false;
        }
        return deleteFileByFileId(Long.valueOf(fileIdByFileName));
    }

    /**
     * 【核心代码】执行脚本文件
     * @param fileName 文件名
     * @param pType 保护类型
     * @param arguments 参数列表
     * @return 保护后文件信息
     */

    @Override
    // 传入三个参数 文件名 保护类型 参数列表
    public FileVo executeShell(String fileName, int pType, List<String> arguments) {
        // 文件地址  filePath  = /home/project/upload/ + 文件名(hello.c)
        String filePath = FILE_UPLOAD_PATH + fileName;
        // 检查文件是否存在
        log.info("待保护文件为：{}", filePath);

        // 创建一个 File 对象 参数为 文件地址
        java.io.File file = new java.io.File(filePath);
        // 调用 判断文件存在的方法 如果不存在 就抛出异常
        if (!file.exists()) {
            log.info("Target file does not exist");
            throw new CodeException("目标文件不存在");
        }

        // 根据pType选择脚本文件
        String script; // ？
        String suffix;

        // 根据 方法传入的参数 pType 判断需要进行的保护类型
        switch (pType) {

            case 1:
                // 采取 ollvm_obfuscate.sh 脚本的方式去保护文件
                script = OLLVM_OBFUSCATE;
                // 根据arguments第一个参数获取具体保护方式
                String obfuscateType = arguments.get(OBFUSCATE_TYPE);
                log.info("参数列表为：{}", arguments);
                log.info("具体混淆方式为：{}", obfuscateType);
                int oType = Integer.parseInt(obfuscateType);

                switch (oType) {
                    case 1:
                        suffix = BCF;
                        break;
                    case 2:
                        suffix = FLA;
                        break;
                    case 3:
                        suffix = SUB;
                        break;
                    default:
                        log.info("Invalid ollvm obfuscate type");
                        throw new CodeException("无效的混淆类型");
                }
                // 拼接调用脚本的命令: ./ollvm_obfuscate.sh clang /home/project/upload/hello.c
                //                           ./ + OLLVM_OBFUSCATE + clang + 刚拼接的文件地址
                String[] command = new String[]{RUN_SHELL + script, CLANG, filePath};
                log.info("调用脚本的命令为：{}", Arrays.toString(command));
                command = Stream.concat(Arrays.stream(command), arguments.stream()).toArray(String[]::new);
                log.info("调用脚本的命令为：{}", Arrays.toString(command));
                // 调用脚本并等待执行完成 脚本拼接完成
                // 判空 为空则抛出 不为空 则赋值
                ProcessBuilder pb = new ProcessBuilder(command);
                // 指定工作目录
                pb.directory(new java.io.File(WORK_DIRECTORY));
                // 将标准错误和标准输出合并
                pb.redirectErrorStream(true);
                Process p = null;
                try {
                    p = pb.start();
                    p.waitFor();
                } catch (IOException | InterruptedException e) {
                    log.info(ExceptionUtils.getMessage(e));
                    throw new CodeException("保护失败");
                }
                // 获取生成的保护文件并返回文件对象
                // 文件名 + OLLVM_OBFUSCATE的三种保护方式 1 / 2 / 3
                String protectedFileName = fileName + suffix;
                log.info("保护文件的文件名为：{}", protectedFileName);
                // protectFile = 文件路径 + 文件*真名（文件名+保护方式）
                java.io.File protectFile = new java.io.File(FILE_UPLOAD_PATH + protectedFileName);
                // 判断 如果不存在就抛出异常
                if (!protectFile.exists()) {
                    log.info("Protected file does not exist");
                    throw new CodeException("保护失败");
                }
                // 生成UUID作为新的文件名
                String newFileName = UUID.randomUUID().toString();
                // 将新的文件名与后缀名拼接成新保护文件名
                // UUID 会随机生成 一个 30位的随机数 拼接上保护方式后缀 生成新的保护文件名
                String newProtectedFileName = newFileName + suffix;
                // 将新保护文件名拼接到文件上传路径 /home/project/upload/ + UUID + 保护方式后缀
                String protectedFilePath = FILE_UPLOAD_PATH + newProtectedFileName;
                java.io.File newFile = new java.io.File(protectedFilePath);
                // 再新建一个文件名 并赋值给 protectFile 并判空
                if (!protectFile.renameTo(newFile)) {
                    log.info("Failed to rename protected file");
                    throw new CodeException("保护失败");
                }

                File resultFile = new File();
                // 获取文件名
                String protectFileName = newFile.getName();
                // 获取文件大小
                long fileSize = newFile.length();
                resultFile.setName(protectFileName);
                resultFile.setMemory(String.valueOf(fileSize));
                resultFile.setType(OLLVM_ELF);
                resultFile.setMemory(String.valueOf(newFile.length()));
                resultFile.setUserId(userId);// userId = 9 ？
                resultFile.setStore(protectedFilePath);
                resultFile.setOriginalFile(getFileIdByFileName(fileName));
                resultFile.setOriginalFileName(getOriginalFileNameByFileId(getFileIdByFileName(fileName)));
                baseMapper.insert(resultFile);
                return getSafetyFile(resultFile);

            case 2:
                // 文件地址  filePath  = /home/project/upload/ + 文件名(hello.c)
                // 去除filePath末尾的扩展名 相当于 去除 “.c”
                // 返回去除 扩展名后的 文件长度
                int lastDotIndex = filePath.lastIndexOf('.');

                // 将 文件地址 重新加载 改为 去除扩展名后的 新文件地址名
                filePath = filePath.substring(0, lastDotIndex);
                log.info("arguments: {}", arguments);
                // 指定调用的脚本文件
                script = TIGRESS_OBFUSCATE;
                // 根据arguments的第三个参数获取具体的保护方式
                String tigressType = arguments.get(TIGRESS_TYPE);
                int tType = Integer.parseInt(tigressType);
                log.info("Specific method of obfuscation: {}", tType);
                switch (tType) {
                    case 1:
                        suffix = TIGRESS_FLATTEN;
                        break;
                    case 2:
                        suffix = TIGRESS_ENCODE_ARITHMETIC;
                        break;
                    case 3:
                        suffix = TIGRESS_ADD_OPAQUE;
                        break;
                    default:
                        log.info("Invaild tigress obfuscate type");
                        throw new CodeException("无效的混淆类型");
                }
    // 拼接调用的脚本命令: ./tigress_obfuscate.sh /home/project/upload/hello
                String[] tigressCmd = {RUN_SHELL + script, filePath};
                log.info("Init shell is : {}", Arrays.toString(tigressCmd));
                // 添加 main 1 参数
                tigressCmd = Stream.concat(Arrays.stream(tigressCmd), arguments.stream()).toArray(String[]::new);
                log.info("After shell is : {}", Arrays.toString(tigressCmd));
                // 调用脚本并等待执行完成
                ProcessBuilder processBuilder = new ProcessBuilder(tigressCmd);
                // 指定工作目录
                processBuilder.directory(new java.io.File(WORK_DIRECTORY));
                // 将标准错误和标准输出合并
                processBuilder.redirectErrorStream(true);
                Process process = null;
                try {
                    process = processBuilder.start();
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                    log.info(ExceptionUtils.getMessage(e));
                    throw new CodeException("保护失败");
                }
                // 获取生成的保护文件并返回文件对象
                // 去除fileName末尾的扩展名
                int lastIndexOf = fileName.lastIndexOf('.');
                String tmpFileName = fileName.substring(0, lastIndexOf);
                String afterFileName = tmpFileName + suffix + C_SUFFIX;// .c
                log.info("After file name is : {}", afterFileName);
                // 下载路径 + tmpFileName（截断后的文件名） + suffix + C_SUFFIX
                java.io.File targetAfterFile = new java.io.File(FILE_UPLOAD_PATH + afterFileName);
                if (!targetAfterFile.exists()) {
                    log.info("Protected file does not exist");
                    throw new CodeException("保护失败");
                }
                // 生成UUID作为新的文件名
                String uuidFileName = UUID.randomUUID().toString();
                // 将新的文件名和后缀名拼接成新保护文件名
                String newAfterFileName = uuidFileName + suffix + C_SUFFIX;
                // 将新保护文件名拼接到文件上传路径
                String afterFilePath = FILE_UPLOAD_PATH + newAfterFileName;
                java.io.File targetFile = new java.io.File(afterFilePath);
                if (!targetAfterFile.renameTo(targetFile)) {
                    log.info("Failed to rename protected file");
                    throw new CodeException("保护失败");
                }
                File responseFile = new File();
                String targetFileName = targetFile.getName();
                long length = targetFile.length();
                responseFile.setName(targetFileName);
                responseFile.setMemory(String.valueOf(length));
                responseFile.setUserId(userId);
                responseFile.setType(TIGRESS_AFTER_FILE_TYPE);
                responseFile.setStore(afterFilePath);
                responseFile.setOriginalFile(getFileIdByFileName(fileName));
                responseFile.setOriginalFileName(getOriginalFileNameByFileId(getFileIdByFileName(fileName)));
                baseMapper.insert(responseFile);
                return getSafetyFile(responseFile);

            case 3:
                break;
            default:
                log.info("Invalid protection type");
                throw new CodeException("无效的保护类型");
        }

        return null;
    }

    /**
     * 删除文件（文件Id）
     * @param fileId 文件Id
     * @return 是否成功删除
     */
    @Override
    public boolean deleteFileByFileId(Long fileId) {
        return deleteFileById(fileId);
    }

    @Override
    public boolean removeRowsByIds(List<String> idList) {
        if (Objects.isNull(idList)) {
            log.info("The file id list is empty");
            throw new CodeException("文件id列表为空");
        }
        int count = baseMapper.deleteBatchIds(idList);
        return count > 0;
    }

    /**
     * 获取文件id（文件名）
     * @param fileName 文件名
     * @return 文件id
     */
    private Integer getFileIdByFileName(String fileName) {
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(File::getName, fileName);
        File file = baseMapper.selectOne(queryWrapper);
        if (!Objects.isNull(file)) {
            return file.getId();
        }
        throw new CodeException("找不到该文件");
    }

    /**
     * 获取文件名
     * @param fileId 文件id
     * @return 文件名
     */
    private String getFileNameByFileId(Integer fileId) {
        LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fileLambdaQueryWrapper.eq(File::getId, fileId);
        File file = baseMapper.selectOne(fileLambdaQueryWrapper);
        if (Objects.isNull(file)) {
            return "文件已删除";
        }
        return file.getName();
    }

    /**
     * 封装：删除文件（文件Id）
     * @param fileId 文件Id
     * @return 是否删除信息
     */
    private boolean deleteFileById(Long fileId) {
        File file = baseMapper.selectById(fileId);
        if (Objects.isNull(file)) {
            log.info("Target file does not exist");
            throw new CodeException("目标文件不存在");
        }
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(File::getId, fileId);
        int count = baseMapper.delete(wrapper);
        return count > 0;
    }

}
