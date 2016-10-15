package com.zbsp.wepaysp.common.security;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

import com.zbsp.wepaysp.common.codec.UnicodeConverter;

public final class DigestHelper {

    private DigestHelper() {
    }

    public static String md5Hex(final String data) {
        return DigestUtils.md5Hex(data);
    }

    public static String sha1Hex(final String data) {
        return DigestUtils.sha1Hex(data);
    }

    public static String sha256Hex(final String data) {
        return DigestUtils.sha256Hex(data);
    }

    public static String sha512Hex(final String data) {
        return DigestUtils.sha512Hex(data);
    }

    public static String sha512HexUnicode(final String data) {
        return DigestUtils.sha512Hex(StringUtils.getBytesUtf8(UnicodeConverter.utf8ToUnicode(data)));
    }
}
