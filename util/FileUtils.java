package org.electronic.electronicdocumentsystemjava.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    // 保存文件到项目目录的父目录
    public static void saveFile(byte[] fileBytes, String fileName) throws IOException {
        String parentDir = new File("../").getAbsolutePath() + File.separator + "images";
        try (FileOutputStream fos = new FileOutputStream(parentDir + File.separator + fileName)) {
            fos.write(fileBytes);
        }
    }

    // 以字节流形式读取文件
    public static byte[] readFile(String fileName) throws IOException {
        String parentDir = new File("../").getAbsolutePath() + File.separator + "images";
        File file = new File(parentDir + File.separator + fileName);
        byte[] fileBytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(fileBytes);
        }
        return fileBytes;
    }
}
