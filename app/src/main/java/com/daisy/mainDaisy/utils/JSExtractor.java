package com.daisy.mainDaisy.utils;

import com.daisy.mainDaisy.common.session.SessionManager;
import com.daisy.mainDaisy.interfaces.CallBack;
import com.daisy.mainDaisy.pojo.response.Download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JSExtractor {
    private static int BUFFER_SIZE = 6 * 1024;

    private SessionManager sessionManager = SessionManager.get();
    int i = 0;

    public static void unzip(String zipFile, String location, CallBack callBack) throws IOException {

        try {
            File f = new File(location);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
            try {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + File.separator + ze.getName();

                    if (ze.isDirectory()) {
                        File unzipFile = new File(path);
                        if (!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        FileOutputStream fout = new FileOutputStream(path, false);

                        try {
                            for (int c = zin.read(); c != -1; c = zin.read()) {
                                fout.write(c);
                            }
                            zin.closeEntry();
                        } finally {
                            fout.close();

                        }
                    }
                }

            } finally {
                SessionManager.get().setFilePath(location);
                callBack.callBack(Constraint.SUCCESS);
                zin.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean unpackZip(String path, Download download) {
        InputStream is;
        ZipInputStream zis;
        try {
            String filename;
            is = new FileInputStream(path);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;
            File file = new File(path);

            while ((ze = zis.getNextEntry()) != null) {
                // zapis do souboru
                filename = ze.getName();


                FileOutputStream fout = new FileOutputStream(file.getParent() + Constraint.SLASH + filename, false);
                // cteni zipu a zapis
                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}