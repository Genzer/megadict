package com.megadict.speaker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Locale;

import android.content.Context;
import android.media.MediaPlayer;

import com.megadict.exception.OperationFailedException;
import com.megadict.model.Speaker;

public class GoogleTranslateSpeaker implements Speaker {
    
    private static final String GOOGLE_URL = "http://translate.google.com/translate_tts?tl=%s&q=%s";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1;" +
            " en-US; rv:1.9.2.3) Gecko/20100401";
    private static final String UNICODE_CHARSET = "UTF-8";

    private final Locale locale;
    private final Context context;

    public GoogleTranslateSpeaker(Locale language, Context context) {
        this.locale = language;
        this.context = context;
    }

    @Override
    public void speak(String text) {

        URLConnection connection = makeConnection(text);

        File soundFile = null;
        InputStream reader = null;
        OutputStream writer = null;

        try {
            reader = new BufferedInputStream(connection.getInputStream(), 8 * 1024);

            writer = context.openFileOutput(text, Context.MODE_WORLD_READABLE);

            byte[] buffer = new byte[8 * 1024];

            int readByte = 0;

            while ((readByte = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, readByte);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            try {
                reader.close();
                writer.close();
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }

        try {

            String path = context.getFilesDir().getPath() + File.separator + text;
            soundFile = new File(path);

            MediaPlayer player = new MediaPlayer();

            FileInputStream fis = new FileInputStream(soundFile);

            player.setDataSource(fis.getFD());

            player.prepare();
            player.start();

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
        } catch (Exception ioe) {
            throw new RuntimeException(ioe);
        } finally {
            // soundFile.delete();
        }

    }

    private URLConnection makeConnection(String text) {
        try {
            String requestUrl = makeUrlString(text);

            URLConnection connection = new URL(requestUrl).openConnection();
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            return connection;
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String makeUrlString(String text) {
        String encodedLanguage = "";
        String encodedText = "";

        try {
            encodedLanguage = URLEncoder.encode(locale.getLanguage(), UNICODE_CHARSET);
            encodedText = URLEncoder.encode(text, UNICODE_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            throw new OperationFailedException("encoding URL parameters", uee);
        }

        return String.format(GOOGLE_URL, encodedLanguage, encodedText);
    }

    @Override
    public Locale getSupportedLanguage() {
        return locale;
    }
}
