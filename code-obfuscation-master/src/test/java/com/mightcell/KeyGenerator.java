package com.mightcell;

import cn.dev33.satoken.secure.SaSecureUtil;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

/**
 * @author: MightCell
 * @description:
 * @date: Created in 21:08 2023-02-22
 */
public class KeyGenerator {

    @Test
    public void keyGenerator() throws Exception {
        HashMap<String, String> map = SaSecureUtil.rsaGenerateKeyPair();
        System.out.println(map.get("public"));
        System.out.println(map.get("private"));
    }
}
