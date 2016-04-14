package com.guang.sun.video;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.os.Environment;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

public class ClipUtil {
    private static final String TAG = "ClipUtil";

    /**
     * 截取指定时间段的视频
     *
     * @param path  视频的路径
     * @param begin 需要截取的开始时间
     * @param end   截取的结束时间
     * @throws IOException
     */
    public static void clipVideo(String path, double begin, double end)
            throws IOException {
        String SAVE_PATH = "aa";
        File mSdCardDir = Environment.getExternalStorageDirectory();
        File f = new File(mSdCardDir.getAbsolutePath() + File.separator
                + SAVE_PATH);
        if (!f.exists()) {
            f.mkdir();
        }
        Movie movie = MovieCreator.build(path);

        List<Track> tracks = movie.getTracks();
        movie.setTracks(new LinkedList<Track>());

        double startTime1 = begin;
        double endTime1 = end;

        boolean timeCorrected = false;
        for (Track track : tracks) {
            if (track.getSyncSamples() != null
                    && track.getSyncSamples().length > 0) {
                if (timeCorrected) {
                    Log.e(TAG,
                            "The startTime has already been corrected by another track with SyncSample. Not Supported.");
                    throw new RuntimeException(
                            "The startTime has already been corrected by another track with SyncSample. Not Supported.");
                }
                startTime1 = correctTimeToSyncSample(track, startTime1, false);
                endTime1 = correctTimeToSyncSample(track, endTime1, true);
                timeCorrected = true;
            }
        }

        for (Track track : tracks) {
            long currentSample = 0;
            double currentTime = 0;
            double lastTime = 0;
            long startSample1 = -1;
            long endSample1 = -1;

            for (int i = 0; i < track.getSampleDurations().length; i++) {
                long delta = track.getSampleDurations()[i];

                if (currentTime > lastTime && currentTime <= startTime1) {
                    startSample1 = currentSample;
                }
                if (currentTime > lastTime && currentTime <= endTime1) {
                    endSample1 = currentSample;
                }
                lastTime = currentTime;
                currentTime += (double) delta
                        / (double) track.getTrackMetaData().getTimescale();
                currentSample++;
            }
            movie.addTrack(new CroppedTrack(track, startSample1, endSample1));// new
        }
        long start1 = System.currentTimeMillis();
        Container out = new DefaultMp4Builder().build(movie);
        long start2 = System.currentTimeMillis();
        String name = String.format("output-%f-%f.mp4", startTime1, endTime1);
        File fs = new File(f.getAbsolutePath()
                + File.separator + name);
        FileOutputStream fos = new FileOutputStream(fs);
        FileChannel fc = fos.getChannel();
        out.writeContainer(fc);

        fc.close();
        fos.close();
        long start3 = System.currentTimeMillis();
        Log.e(TAG, "Building IsoFile took : " + (start2 - start1) + "ms");
        Log.e(TAG, "Writing IsoFile took : " + (start3 - start2) + "ms");
        Log.e(TAG,
                "Writing IsoFile speed : "
                        + (new File(String.format("output-%f-%f.mp4",
                        startTime1, endTime1)).length()
                        / (start3 - start2) / 1000) + "MB/s");
    }

    private static double correctTimeToSyncSample(Track track, double cutHere,
                                                  boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];

            if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                // samples always start with 1 but we start with zero therefore
                // +1
                timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(),
                        currentSample + 1)] = currentTime;
            }
            currentTime += (double) delta
                    / (double) track.getTrackMetaData().getTimescale();
            currentSample++;

        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }

}