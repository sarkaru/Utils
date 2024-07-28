package utp.utils.siteblocker;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class SiteBlocker {

    public static void modifyHostsFile1(String siteName, int blockFlag, String sudoPassword) throws IOException, InterruptedException {
        String hostsFilePath = "/etc/hosts";
        List<String> lines = Files.readAllLines(Paths.get(hostsFilePath));

        String siteEntry = "127.0.0.1 " + siteName;
        String siteEntryWWW = "127.0.0.1 www." + siteName;
        String commentedSiteEntry = "#" + siteEntry;
        String commentedSiteEntryWWW = "#" + siteEntryWWW;

        boolean changesMade = false;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (blockFlag == 1) {
                // Block the site
                if (line.equals(commentedSiteEntry)) {
                    lines.set(i, siteEntry);
                    changesMade = true;
                } else if (line.equals(commentedSiteEntryWWW)) {
                    lines.set(i, siteEntryWWW);
                    changesMade = true;
                }
            } else if (blockFlag == 0) {
                // Unblock the site
                if (line.equals(siteEntry)) {
                    lines.set(i, commentedSiteEntry);
                    changesMade = true;
                } else if (line.equals(siteEntryWWW)) {
                    lines.set(i, commentedSiteEntryWWW);
                    changesMade = true;
                }
            }
        }

        if (changesMade) {
            writeToHostsFile(lines, sudoPassword);
        } else {
            System.out.println("No changes were made.");
        }
    }

   public  static void modifyHostsFile(String siteName, int blockFlag, String sudoPassword) throws IOException, InterruptedException {
        String hostsFilePath = "/etc/hosts";
        List<String> lines = Files.readAllLines(Paths.get(hostsFilePath));

        String siteEntry = "127.0.0.1 " + siteName + ".com";
        String siteEntryWWW = "127.0.0.1 www." + siteName + ".com";;
        String commentedSiteEntry = "#" + siteEntry + ".com";
        String commentedSiteEntryWWW = "#" + siteEntryWWW + ".com";

        boolean changesMade = false;
        boolean siteexists = lines.contains(siteEntry) || lines.contains(siteEntryWWW) || lines.contains(commentedSiteEntry) || lines.contains(commentedSiteEntryWWW);



        if (!siteexists) {
            lines.add(siteEntry);
            lines.add(siteEntryWWW);
            changesMade = true;
            siteexists  = true;
        } else {

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (blockFlag == 1) {
                    // Block the site
                    if (line.equals(commentedSiteEntry)) {
                        lines.set(i, siteEntry);
                        changesMade = true;
                    } else if (line.equals(commentedSiteEntryWWW)) {
                        lines.set(i, siteEntryWWW);
                        changesMade = true;
                    }
                } else if (blockFlag == 0) {
                    // Unblock the site
                    if (line.equals(siteEntry)) {
                        lines.set(i, commentedSiteEntry);
                        changesMade = true;
                    } else if (line.equals(siteEntryWWW)) {
                        lines.set(i, commentedSiteEntryWWW);
                        changesMade = true;
                    }
                }
            }
        }


        if (changesMade) {
            writeToHostsFile(lines, sudoPassword);
        } else {
            System.out.println("No changes were made.");
        }
    }

    private static void writeToHostsFile(List<String> lines, String sudoPassword) throws IOException, InterruptedException {
        Path tempFile = Files.createTempFile("hosts_temp", null);
        Files.write(tempFile, lines);

        String[] cmd = {"/bin/sh", "-c", String.format("echo %s | sudo -S cp %s /etc/hosts", sudoPassword, tempFile.toString())};
        Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();

        if (process.exitValue() == 0) {
            System.out.println("Successfully updated the configuration.");
        } else {
            System.err.println("Failed to update the config. Check your sudo password and permissions.");
        }

        Files.delete(tempFile);
    }
}

