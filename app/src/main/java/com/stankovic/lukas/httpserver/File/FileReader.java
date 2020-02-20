package com.stankovic.lukas.httpserver.File;

import android.os.Environment;
import android.webkit.MimeTypeMap;

import java.io.File;

public class FileReader {

    private String path;

    private File file;

    private String fileType;

    public FileReader(String path) {
        String externalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        this.path = externalStorageDirectoryPath + path;

        this.file = new File(this.path);

        if (file.exists() && file.isFile()) {
            fileType = getFileType(this.path);
        }

        if (file.exists() && !file.isFile()) {
            this.file = new File(this.path + "/");
        }
    }

    private static String getFileType(String path) {
        String type = null;

        if (path.charAt(path.length() - 1) == '/') {
            path = path.substring(0, path.length() - 1);;
        }

        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }

        return type;
    }

    public File getFile() {
        return file;
    }

    public String getFileType() {
        return fileType;
    }

    public String getPath() {
        return path;
    }
}
