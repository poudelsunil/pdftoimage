import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import javax.print.attribute.standard.Compression;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class PdfConverter {

    public static void main(String[] args) throws Exception {
        final String sourceDir = "/home/sunil/Desktop/pdftoimage/sample.pdf";
        final String destinationDir = "/home/sunil/Desktop/pdftoimage/converted/";

        convertPdfToImages(sourceDir, destinationDir);
//        convertPdfToImages("/home/sunil/Desktop/pdftoimage/sampleA.pdf", destinationDir);
//        convertPdfToImages("/home/sunil/Desktop/pdftoimage/sampleB.pdf", destinationDir);
//        convertPdfToImages("/home/sunil/Desktop/pdftoimage/sampleC.pdf", destinationDir);
//


//        File file = new File(sourceDir);
//        FileInputStream fis = new FileInputStream(file);
//        byte [] data = getByteFromInputStream(fis);
//
//        OutputStream outStream = null;
//        ByteArrayOutputStream byteOutStream = null;
//        try {
//            outStream = new FileOutputStream("/home/sunil/Desktop/pdftoimage/converted/sample.pdf");
//            byteOutStream = new ByteArrayOutputStream();
//            // writing bytes in to byte output stream
//            byteOutStream.write(convertByteArrayPdfToImage(data)); //data
//            byteOutStream.writeTo(outStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            outStream.close();
//        }

        /*File file = new File(sourceDir);
        FileInputStream fis = new FileInputStream(file);
        byte [] data = getByteFromInputStream(fis);
        byte [] converted =  convertByteArrayPdfToImage(data);


        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  OutputStream outputStream = new FileOutputStream(destinationDir+"123"+".jpg");){
            byteArrayOutputStream.write(converted);
            byteArrayOutputStream.writeTo(outputStream);
        }

        System.out.println("converted file : "+ converted);*/


    }

    public static byte[] getByteFromInputStream(InputStream inputStream) {
        byte[] byteArray = new byte[0];
        try {
            byteArray = IOUtils.toByteArray(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArray;
    }

    public static byte[] convertByteArrayPdfToImage(byte [] data){

        return data;
    }



    public static void convertPdfToImages(final String sourceDir, final String destinationDir) {
        try {

            System.out.println("Processing : "+sourceDir);

            File sourceFile = new File(sourceDir);
            File destinationFile = new File(destinationDir);

            if (!destinationFile.exists()) {
                destinationFile.mkdir();
                System.out.println("Destination folder created : " + destinationDir);
            }

            if (!sourceFile.exists()) {
                throw new Exception("Source File does not exists");
            }

            String sourceFilename = sourceFile.getName().replace(".pdf", "");

            PDDocument document = PDDocument.load(new File(sourceDir));
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int nonEmptyPages = 0;
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 100, ImageType.RGB);
                if( isBlank( bim) ){
                    System.out.println("Skipping empty page with page number :"+page);
                    continue;
                }
                if(nonEmptyPages >= 3){
                    throw new Exception("PDF file cannot contain more then 3 pages");
                }
                nonEmptyPages++;
                File outputFile = new File(destinationDir + sourceFilename + "_" + nonEmptyPages + ".png");
                ImageIO.write(bim, "png", outputFile);

            }

            document.close();
            System.out.println("Image saved at " + destinationDir);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Boolean isBlank(BufferedImage bufferedImage) throws IOException {
        long count = 0;
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();
        Double areaFactor = (width * height) * 0.999;

        for (int x = 0; x < width ; x++) {
            for (int y = 0; y < height ; y++) {
                Color c = new Color(bufferedImage.getRGB(x, y));
                // verify light gray and white
                if (c.getRed() == c.getGreen() && c.getRed() == c.getBlue()
                        && c.getRed() >= 248) {
                    count++;
                }
            }
        }

        if (count >= areaFactor) {
            return true;
        }

        return false;
    }

}
