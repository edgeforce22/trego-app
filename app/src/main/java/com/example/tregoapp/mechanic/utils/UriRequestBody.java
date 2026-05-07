package com.example.tregoapp.mechanic.utils;

import android.content.Context;
import android.net.Uri;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.io.InputStream;

public class UriRequestBody extends RequestBody {

    private final Context context;

    private final Uri uri;

    public UriRequestBody(
            Context context,
            Uri uri
    ) {

        this.context = context;
        this.uri = uri;
    }

    @Override
    public MediaType contentType() {

        return MediaType.parse(
                context.getContentResolver()
                        .getType(uri)
        );
    }

    @Override
    public void writeTo(
            okio.BufferedSink sink
    ) {

        try {

            InputStream inputStream =
                    context.getContentResolver()
                            .openInputStream(uri);

            byte[] buffer =
                    new byte[8192];

            int read;

            while (
                    (read = inputStream.read(buffer))
                            != -1
            ) {

                sink.write(
                        buffer,
                        0,
                        read
                );
            }

            inputStream.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}