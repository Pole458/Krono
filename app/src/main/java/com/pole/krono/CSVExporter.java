package com.pole.krono;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.opencsv.CSVWriter;
import com.pole.krono.model.Lap;
import com.pole.krono.model.Profile;
import com.pole.krono.model.TrackingSession;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.List;

public class CSVExporter {

    private static final String TAG = "Pole: CSVExporter";

    private static final String folder = "Krono";

    public static void exportToCsv(Context context, Profile profile, List<TrackingSession> sessions) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {

                String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

                if(noBaseDir(baseDir)) return false;

                String fileName = String.format("%s_%s.csv", profile.getName(), profile.getSurname());
                String filePath = baseDir + File.separator + folder + File.separator + fileName;
                File f = new File(filePath);
                CSVWriter writer;

                Log.v(TAG, "Exporting file to " + filePath);

                try {

                    // File exist
                    if(f.exists()&&!f.isDirectory())
                    {
                        FileWriter mFileWriter = new FileWriter(filePath, true);
                        writer = new CSVWriter(mFileWriter);
                    }
                    else
                    {
                        writer = new CSVWriter(new FileWriter(filePath));
                    }

                    writer.writeNext(profile.toCSV());

                    for(TrackingSession s : sessions) {
                        writer.writeNext(s.toCSV());
                    }

                    writer.close();

                } catch (IOException e) {
                    return false;
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                if(success)
                    Toast.makeText(context, "CSV exported!", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context, "Failed exporting CSV", Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    public static void exportToCsv(Context context, TrackingSession session, List<Lap> laps) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

                if(noBaseDir(baseDir)) return false;

                String fileName = String.format("%s_%s_%s.csv", session.profileName, session.profileSurname,
                        DateFormat.getDateTimeInstance().format(session.startTime));
                String filePath = baseDir + File.separator + folder + File.separator + fileName;
                File f = new File(filePath);
                CSVWriter writer;

                Log.v(TAG, "Exporting file to " + filePath);

                try {
                    // File exist
                    if(f.exists()&&!f.isDirectory())
                    {
                        FileWriter mFileWriter = new FileWriter(filePath, true);
                        writer = new CSVWriter(mFileWriter);
                    }
                    else
                    {
                        writer = new CSVWriter(new FileWriter(filePath));
                    }

                    writer.writeNext(session.toCSV());

                    for(Lap l : laps) {
                        writer.writeNext(l.toCSV());
                    }

                    writer.close();
                }  catch (IOException e) {
                    return false;
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                if(success)
                    Toast.makeText(context, "CSV exported!", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context, "Failed exporting CSV", Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    private static boolean noBaseDir(String baseDir) {
        File folderPath = new File(baseDir + File.separator + folder);
        boolean success = true;
        if (!folderPath.exists()) {
            success = folderPath.mkdirs();
        }
        return !success;
    }
}
