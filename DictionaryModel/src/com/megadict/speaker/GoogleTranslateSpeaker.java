package com.megadict.speaker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Locale;

import android.content.Context;
import android.media.MediaPlayer;

import com.megadict.exception.InternetConnectionFailedException;
import com.megadict.exception.OperationFailedException;
import com.megadict.format.dict.util.FileUtil;
import com.megadict.model.Speaker;

public class GoogleTranslateSpeaker implements Speaker {

    private static final String GOOGLE_URL = "http://translate.google.com/translate_tts?tl=%s&q=%s";

    private static final String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1;" +
            " en-US; rv:1.9.2.3) Gecko/20100401";

    private static final String UNICODE_CHARSET = "UTF-8";

    private static byte[] buffer = new byte[FileUtil.DEFAULT_BUFFER_SIZE_IN_BYTES];

    private final Locale locale;
    private final Context context;

    public GoogleTranslateSpeaker(Locale language, Context context) {
        this.locale = language;
        this.context = context;
    }

    /**
     * @throws InternetConnectionFailedException - when failed to establish an connection to Internet.
     */
    @Override
    public void speak(String text) {

        File soundFile = null;

        try {
            soundFile = downloadSoundFile(text);
            FileInputStream soundStream = new FileInputStream(soundFile);
            play(soundStream);
        } catch (FileNotFoundException fnf) {
            throw new OperationFailedException("playing sound file", fnf);
        } finally {
            if (soundFile != null) {
                soundFile.delete();
            }
        }

    }

    private File downloadSoundFile(String text) {
        File soundFile = null;
        InputStream reader = null;
        OutputStream writer = null;

        URLConnection connection = establishConnection(text);

        try {

            reader = new BufferedInputStream(connection.getInputStream(), FileUtil.DEFAULT_BUFFER_SIZE_IN_BYTES);

            writer = context.openFileOutput(text, Context.MODE_WORLD_READABLE);

            int readByte = 0;

            synchronized (buffer) {
                while ((readByte = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, readByte);
                }
                Arrays.fill(buffer, (byte) 0);
            }

        } catch (IOException ioe) {
            throw new OperationFailedException("downloading data from connection", ioe);
        } finally {
            FileUtil.closeInputStream(reader);
            FileUtil.closeOutputStream(writer);
        }

        String path = context.getFilesDir().getPath() + File.separator + text;
        soundFile = new File(path);
        return soundFile;
    }

    private URLConnection establishConnection(String text) {
        try {
            String requestUrl = makeUrlString(text);

            URLConnection connection = new URL(requestUrl).openConnection();
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            return connection;
        } catch (MalformedURLException mue) {
            throw new OperationFailedException("creating a url", mue);
        } catch (IOException ioe) {
            throw new InternetConnectionFailedException("Cannot establish an internet connection", ioe);
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

    private void play(FileInputStream stream) {
        try {
            MediaPlayer player = new MediaPlayer();

            player.setDataSource(stream.getFD());
            player.prepare();
            player.start();

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });

        } catch (IOException ioe) {
            throw new OperationFailedException("setting up MediaPlayer", ioe);
        }
    }

    @Override
    public Locale getSupportedLanguage() {
        return locale;
    }
}
