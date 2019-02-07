import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PDFToImageConverter {
    public static void main(String[] args) throws Exception {

        final String SOURCE_PDF_DIR = "/home/sunil/Desktop/pdftoimage/sample.pdf";
        final String DEST_DIR = "/home/sunil/Desktop/pdftoimage/converted/";
        final String OUT_IMAGE_FORMAT = "jpg";

        byte [] data = Files.readAllBytes(Paths.get(SOURCE_PDF_DIR));
//        List<byte[]> convertedImages = convertPDFByteToImageByte(data, 3,
//                true,0.999, 248,
//                OUT_IMAGE_FORMAT,100, ImageType.RGB);

        List<byte[]> convertedImages = new PDF2ImageConverter.Builder(data)
                .build()
                .convertToImage();


        System.out.println("converted total images : "+convertedImages);
        int imageNumber =  0;
        for(byte[] cImage: convertedImages){
            InputStream inputStream = new ByteArrayInputStream(cImage);
            BufferedImage bImageFromConvert = ImageIO.read(inputStream);

            String imageFileName = DEST_DIR + "sample" + "-" + imageNumber + "."+OUT_IMAGE_FORMAT;
            File outFile = new File(imageFileName);
            ImageIO.write(bImageFromConvert, OUT_IMAGE_FORMAT, outFile);
            System.out.println("Wrote image : "+outFile.getAbsolutePath());
            imageNumber++;
        }
    }

    public static List<byte[]> convertPDFByteToImageByte(byte[] pdfByteData, int maxPdfPage,
                                                         boolean skipBlank,
                                                         double blankPageDecidingFactor, int nonBlankWhiteMaxValue,
                                                         String outputImageFormatName,
                                                         int dpi, ImageType imageType ) throws Exception {

        PDDocument document = PDDocument.load(pdfByteData);
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        List<byte[]> convertedImages = new ArrayList<>();
        int nonEmptyPages = 0;
        for (int page = 0; page < document.getNumberOfPages(); ++page) {

            BufferedImage image = pdfRenderer.renderImageWithDPI(page, dpi, imageType);

            if( skipBlank && isBlank( image, blankPageDecidingFactor, nonBlankWhiteMaxValue) ){
                System.out.println("Skipping empty page with page number :"+page);
                continue;
            }
            if(maxPdfPage != 0 && nonEmptyPages >= maxPdfPage){
                throw new Exception("PDF file cannot contain more then 3 pages");
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, outputImageFormatName, outputStream);
            convertedImages.add(outputStream.toByteArray());
        }

        document.close();
        return convertedImages;
    }

    private static Boolean isBlank(BufferedImage bufferedImage, double percentageToDecideBlankPage, int nonBlankWhiteMaxValue) {
        long count = 0;
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();
        Double areaFactor = (width * height) * percentageToDecideBlankPage;

        for (int x = 0; x < width ; x++) {
            for (int y = 0; y < height ; y++) {
                Color c = new Color(bufferedImage.getRGB(x, y));
                if (c.getRed() == c.getGreen() && c.getRed() == c.getBlue()
                        && c.getRed() >= nonBlankWhiteMaxValue) {
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