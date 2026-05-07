package com.example.tregoapp.mechanic.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {

    public static String getPath(
            Context context,
            Uri uri
    ) {

        try {

            /*
             * GET FILE NAME
             */
            String fileName =
                    getFileName(
                            context,
                            uri
                    );

            /*
             * CREATE TEMP FILE
             */
            File tempFile =
                    new File(
                            context.getCacheDir(),
                            fileName
                    );

            /*
             * COPY URI DATA
             */
            InputStream inputStream =
                    context.getContentResolver()
                            .openInputStream(uri);

            FileOutputStream outputStream =
                    new FileOutputStream(tempFile);

            byte[] buffer =
                    new byte[1024];

            int length;

            while ((length =
                    inputStream.read(buffer)) > 0) {

                outputStream.write(
                        buffer,
                        0,
                        length
                );
            }

            outputStream.close();
            inputStream.close();

            /*
             * RETURN REAL FILE PATH
             */
            return tempFile.getAbsolutePath();

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    /*
     * GET FILE NAME
     */
    private static String getFileName(
            Context context,
            Uri uri
    ) {

        String result = null;

        Cursor cursor =
                context.getContentResolver()
                        .query(
                                uri,
                                null,
                                null,
                                null,
                                null
                        );

        try {

            if (
                    cursor != null
                            &&
                            cursor.moveToFirst()
            ) {

                int nameIndex =
                        cursor.getColumnIndex(
                                OpenableColumns.DISPLAY_NAME
                        );

                if (nameIndex >= 0) {

                    result =
                            cursor.getString(nameIndex);
                }
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        /*
         * FALLBACK
         */
        if (result == null) {

            result =
                    "temp_image_"
                            + System.currentTimeMillis()
                            + ".jpg";
        }

        return result;
    }
}