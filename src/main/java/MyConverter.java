import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class MyConverter {
    public static void main(String[] args) throws Exception {

        final String SOURCE_PDF_DIR = "/home/sunil/Desktop/pdftoimage/sample.pdf";
        final String DEST_DIR = "/home/sunil/Desktop/pdftoimage/converted/";
        final String OUT_IMAGE_FORMAT = "jpg";

        byte[] data = Files.readAllBytes(Paths.get(SOURCE_PDF_DIR));

        List<byte[]> convertedImages = new PDF2ImageConverter.Builder(data)
                .build()
                .convertToImage();


        System.out.println("converted total images : " + convertedImages);
        int imageNumber = 0;
        for (byte[] cImage : convertedImages) {
            InputStream inputStream = new ByteArrayInputStream(cImage);
            BufferedImage bImageFromConvert = ImageIO.read(inputStream);

            String imageFileName = DEST_DIR + "sample" + "-" + imageNumber + "." + OUT_IMAGE_FORMAT;
            File outFile = new File(imageFileName);
            ImageIO.write(bImageFromConvert, OUT_IMAGE_FORMAT, outFile);
            System.out.println("Wrote image : " + outFile.getAbsolutePath());
            imageNumber++;
        }
    }
}
