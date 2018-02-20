import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import java.security.DigestInputStream;

public class MP3 {
    public static void main(String[] args) throws IOException {
        System.setProperty("log4j.configurationFile",
                "log4j2.xml");
        Logger logger = LogManager.getRootLogger();
        //logger.info("Information message");
        if (args.length == 0) {
            System.out.println("This program searches for files with given format in given directories and creates an XML file with information about found files.\n" +
                    "Please, enter paths of directories you'd like to check, format you want to search, and full path to an XML file.\n" +
                    "Example: java Search c:/somepath1 c:/somepath2/dir1/ txt d:/output.xml");
            return;
        }

        LinkedHashSet<String> data = new LinkedHashSet<>(); //creating LinkedHashSet to exclude duplicity of paths
        String patternForDirectories = "\\w:[\\/\\w]*"; //setting a format for valid directory name
        Pattern pDir = Pattern.compile(patternForDirectories);

        for (int i = 0; i < args.length; i++) {
            Matcher mDir = pDir.matcher(args[i]);
            if (mDir.matches()) { //adding directories to LinkedHashSet
                data.add(args[i]);
            } else {
                System.out.println("Incorrect directory name: " + args[i]);
                System.out.println("Example for correct directory name: c:/dir1");
                return;
            }
        }

        for (String path : data) {
            File folder = new File(path); //getting directory by path
            File[] listOfFiles = folder.listFiles(); //getting the array of files and directories in chosen directory

            if (listOfFiles != null) {
                File htmlData = new File("mp3info.html"); //creating an XML file which'll store the data
                FileWriter fw = new FileWriter(htmlData, false); //opening new stream for writing in file ("false" means re-writing the file)
                fw.write("<!DOCTYPE html>\n");
                fw.write("<html lang=\"en\">\n");
                fw.write("<head>\n");
                fw.write("<meta charset=\"CP1251\">\n");
                fw.write("<title>MP3 INFO</title>\n");
                fw.write("</head>\n");
                fw.write("<body>\n");
                try {
                    new MP3().printInfo(listOfFiles, 2, fw); //sending the array of directory items to function which'll print the data to file
                } catch (NullPointerException n) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fw.write("\t</body>\n");
                fw.close(); //closing the stream for writing in file
            } else {
                System.out.println("There's no files with format mp3 in directory " + path);
                return;
            }
        }
    }

    private static byte[] readFileSegment(File file, int index) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        byte[] buffer = new byte[128];
        try {
            raf.skipBytes(index);
            raf.readFully(buffer, 0, 128);
            return buffer;
        } finally {
            raf.close();
        }
    }

    private void printInfo(File[] files, int tabNumber, FileWriter fw) throws Exception {
        for (int i = 0; i < files.length; i++) {
            Path j = Paths.get(String.valueOf(files[i])); //setting a path to [i] file/directory
            String path = j.toString();
            ArrayList<Artists> artists = new ArrayList<>();

            if (files[i].isDirectory()) {
                printInfo(files[i].listFiles(), tabNumber, fw); //recursion for directories to get info from inner folders
            } else if (files[i].isFile() && new MP3().isMP3(files[i].getName())) {
                byte[] array = Files.readAllBytes(j);
                int ind = array.length - 129;
                byte[] info = readFileSegment(files[i], ind);
                String id3 = new String(info);
                String title = id3.substring(4, 32).trim();
                if (title.length() == 0) {
                    title = "Default";
                }
                String artist = id3.substring(33, 62).trim();
                if (artist.length() == 0) {
                    artist = "Default";
                }
                String album = id3.substring(63, 92).trim();
                if (album.length() == 0) {
                    album = "Default";
                }

                artists.add(new Artists(artist, album, title));
                fw.write(printTabs(tabNumber) + "<p>Artist: " + artist + "</p>\n");
                fw.write(printTabs(tabNumber) + "<p>Album : " + album + "</p>\n");
                fw.write(printTabs(tabNumber) + "<p>Title: " + title + "</p>\n");
                fw.write(printTabs(tabNumber) + "<a href=\"" + path + "\">listen</a>\n");
                fw.write("<p>-----------------------------------------------------</p>\n");
            }
            for (Artists a : artists) {
                System.out.println(path);
                a.showInfo();
                System.out.println("-------------------------------");
            }
        }
    }

    private boolean isMP3(String path) { //this function checks the equality of found file to given format
        if (path.endsWith("mp3")) {
            return true;
        }
        return false;
    }

    private String printTabs(int tabNumber) { //this function adds tabs before the strings of xml file (just for better visualisation)
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tabNumber; i++) {
            builder.append('\t');
        }
        return builder.toString();
    }
}