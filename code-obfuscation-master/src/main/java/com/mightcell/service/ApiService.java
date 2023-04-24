package com.mightcell.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mightcell.entity.File;
import com.mightcell.entity.request.PageBo;
import com.mightcell.entity.response.FileVo;
import com.mightcell.entity.response.PageVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: MightCell
 * @description: 文件处理服务层
 * @date: Created in 22:35 2023-02-23
 */
public interface ApiService extends IService<File> {

    /**
     * 统计当前在线用户数
     * @return 用户登录次数
     */
    Integer countLoginNum();

    /**
     * 统计当日用户注册数
     * @param day 日期
     * @return 用户注册数
     */
    Integer countRegisterNum(String day);

    /**
     * 统计当日文件上传数
     * @param day 日期
     * @return 文件上传数
     */
    Integer countFileUploadNum(String day);

    /**
     * 统计当日保护文件数
     * @param day 日期
     * @return 文件保护数
     */
    Integer countFileProtectNum(String day);

    /**
     * 根据用户ID获取文件列表
     * @return 对应用户ID的所有文件列表
     */
    List<FileVo> getFileInfoByUserId();

    /**
     * 根据文件id获取文件信息
     * @param fileId 文件id
     * @return 文件信息
     */
    FileVo getFileInfoByFileId(Long fileId);


    /**
     * 文件删除更换
     * @param file  上传文件
     * @param uploadPath 保存路径
     * @return 文件属性信息
     * @throws IOException 读写异常
     */
    FileVo uploadFile(MultipartFile file, String uploadPath) throws IOException;

    /**
     * 文件下载
     * @param fileName 文件名
     * @param uploadUrl 存储地址
     * @param response 响应信息
     */
    void downloadFile(String fileName, String uploadUrl, HttpServletResponse response);

    /**
     * OLLVM
     * @param fileName 待保护文件名
     * @param paramList 参数列表
     * @return 保护后文件信息
     */
    FileVo performProtectionOperation(String fileName , ArrayList<String> paramList);


    /**
     * 根据文件名获取文件信息
     * @param fileName
     * @return 文件信息
     */
    FileVo getFileInfoByFileName(String fileName);

    /**
     * 获取封装好的页面信息
     * @param pageBo
     * @return 页面分页信息
     */
    Page<PageVo> getPageVoInfo(PageBo pageBo);

    /**
     * 根据文件名删除文件
     * @param fileName
     * @return 是否删除文件
     */
    boolean deleteFileByFileName(String fileName);

    /**
     * 整合ollvm、tigress和our
     * @param fileName 文件名
     * @param pType 保护类型
     * @param arguments 参数列表
     * @return 保护后文件信息
     */
    FileVo executeShell(String fileName, int pType, List<String> arguments);

    /**
     * 删除文件（文件Id）
     * @param fileId 文件Id
     * @return 是否成功删除文件
     */
    boolean deleteFileByFileId(Long fileId);


    /**
     * 批量删除文件（文件id列表）
     * @param idList id列表
     * @return 是否成功删除文件列表
     */
    boolean removeRowsByIds(List<String> idList);

}
