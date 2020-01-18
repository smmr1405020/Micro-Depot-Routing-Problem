package EvolutionaryAlgorithm;

import java.io.*;
import java.util.regex.Pattern;

public class kmlParser {

    public static void main(String args[]){
        String kmlText = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader("iowacity.kml"));
            String temp;
            int j = 0;
            while ((temp = br.readLine()) != null){
                //System.out.println(""+j);
                kmlText += temp+'\n';
            }
            //System.out.println(kmlText);
            BufferedWriter bw = new BufferedWriter(new FileWriter("micro_depots.txt"));

            if (kmlText != null & kmlText.length() > 0) {
                // Change case of relevant tags to match our search string case
                kmlText = kmlText.replaceAll("(?i)<Placemark>", "<Placemark>")
                        .replaceAll("(?i)</Placemark>", "</Placemark>")
                        .replaceAll("(?i)<name>", "<name>")
                        .replaceAll("(?i)</name>", "</name>")
                        .replaceAll("(?i)<coordinates>", "<coordinates>")
                        .replaceAll("(?i)</coordinates>", "</coordinates>");
                // Get <Placemark> tag
                String[] kmlPlacemarks = kmlText.split("</Placemark>");
                if (kmlPlacemarks.length > 0) {
                    for (Integer i = 0; i < kmlPlacemarks.length; i++) {
                        // Add '</Placemark>' to the end - actually not necessary
                        kmlPlacemarks[i] += "</Placemark>";
                        if (kmlPlacemarks[i].indexOf("<Placemark>") > -1)
                /* Trim front to start from '<Placemark>'
                Otherwise additional tags may be in between leading
                to parsing of incorrect values especially Name */
                            kmlPlacemarks[i] = kmlPlacemarks[i].substring(kmlPlacemarks[i].indexOf("<Placemark>"));
                    }
                    String tmpPlacemarkName;
                    String tmpPlacemarkCoordinates;
                    for (String kmlPlacemark: kmlPlacemarks)
                        if ((kmlPlacemark.indexOf("<name>") > -1 && kmlPlacemark.indexOf("</name>") > -1) &&
                                (kmlPlacemark.indexOf("<coordinates>") > -1 && kmlPlacemark.indexOf("</coordinates>") > -1)) {
                            tmpPlacemarkCoordinates = kmlPlacemark.substring(kmlPlacemark.indexOf("<coordinates>") + 13, kmlPlacemark.indexOf("</coordinates>"));
                            tmpPlacemarkName = kmlPlacemark.substring(kmlPlacemark.indexOf("<name>") + 6, kmlPlacemark.indexOf("</name>"));
                            /*System.out.println(tmpPlacemarkName);
                            System.out.println(tmpPlacemarkCoordinates);
                            System.out.println("");*/
                            boolean b =Pattern.compile(Pattern.quote("school"), Pattern.CASE_INSENSITIVE).matcher(tmpPlacemarkName).find();
                            if(b){
                                bw.write(tmpPlacemarkCoordinates+"\n");

                            }
                        }


                }
            }
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
