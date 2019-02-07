import org.apache.commons.logging.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

public class MyConverter {

    public static final Logger LOG = Logger.getLogger(MyConverter.class.getName());

    public static void main(String[] args) {

        final String SOURCE_PDF_DIR = "/home/sunil/Desktop/pdftoimage/sample.pdf";
        final String DEST_DIR = "/home/sunil/Desktop/pdftoimage/converted/";
        final String OUT_IMAGE_FORMAT = "jpg";

        try {
            byte[] data = Files.readAllBytes(Paths.get(SOURCE_PDF_DIR));

            LOG.info("Converting pdf to image");
            List<byte[]> convertedImages = new PDF2ImageConverter.Builder(data)
                    .outputImageFormat(OUT_IMAGE_FORMAT)
                    .build()
                    .convertToImage();


            LOG.info("converted total images : " + convertedImages);
            int imageNumber = 0;
            for (byte[] cImage : convertedImages) {
                InputStream inputStream = new ByteArrayInputStream(cImage);
                BufferedImage bImageFromConvert = ImageIO.read(inputStream);

                String imageFileName = DEST_DIR + "sample" + "-" + imageNumber + "." + OUT_IMAGE_FORMAT;
                File outFile = new File(imageFileName);
                ImageIO.write(bImageFromConvert, OUT_IMAGE_FORMAT, outFile);

                LOG.info("Wrote image : " + outFile.getAbsolutePath());

                imageNumber++;
            }File validated

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
