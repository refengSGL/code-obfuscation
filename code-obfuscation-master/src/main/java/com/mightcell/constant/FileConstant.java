package com.mightcell.constant;

/**
 * @author: MightCell
 * @description: 文件服务常量
 * @date: Created in 16:28 2023-03-14
 */
public interface FileConstant {
    String FILE_UPLOAD_PATH = "/home/project/upload/"; // 文件路径
    int OBFUSCATE_TYPE = 0;
    String WORK_DIRECTORY = "/home/project/"; //工作目录
    Integer FILE_EXIST = 1; // 判断是否存在文件
    Integer SUCCESS_EXIT = 0; // 成功退出
    Integer LIST_HEADER = 0;
    String CLANG = "clang";

    // OLLVM_OBFUSCATE 的 三种保护方式
    String BCF = "_bcf";
    String FLA = "_fla";
    String SUB = "_sub";

    String SCAN_BUILD = "scan-build";
    String OUTPUT_CONTAIN_WARNING = "warning:";

    String RUN_SHELL = "./";
    String GET_BY_ID = "id";
    String GET_BY_NAME = "name";
    String C_MIME = "text/x-c";

    String C_SUFFIX = ".c";
    String EMPTY_STRING = "";
    String OLLVM_ELF = "ELF 64-bit LSB executable";

    // case 2
    String TIGRESS_OBFUSCATE = "tigress_obfuscate.sh";
    // case 1
    String OLLVM_OBFUSCATE = "ollvm_obfuscate.sh";
    // case 3 暂时未用到
    String OUR_OBFUSCATE = "our_obfuscate.sh";

    // TIGRESS_OBFUSCATE 的 三种保护方式
    String TIGRESS_FLATTEN = "_flatten";
    String TIGRESS_ENCODE_ARITHMETIC = "_encodeArithmetic";
    String TIGRESS_ADD_OPAQUE = "_addOpaque";

    String TIGRESS_AFTER_FILE_TYPE = "ASCII text";
    int TIGRESS_TYPE = 1;

    int END_OF_FILE = -1;

}
