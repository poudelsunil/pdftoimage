import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PDF2ImageConverter {

    public static final Logger LOG = Logger.getLogger(MyConverter.class.getName());

    private byte[] pdfByteData;

    private int maxPdfPage; /* default 0 for no page number size restriction*/
    private boolean skipBlankPage; /*default true to skip blank page*/
    private double blankPageDecidingFactor; /*default 0.999 : if 99.9% of page is white then we assume that page as blank*/
    private int nonBlankWhiteMaxValue; /* default 248; pixel with more then this rgb white value will treat as blank*/
    private String outputImageFormat; /*default : jpg can accept png also*/
    private int dpi; /*default : 100; less dpi indicate low quality/dpi image*/
    private ImageType imageType; /*default : ImageType.RGB; can be any value from enum pdfbox.rendering.ImageType*/

    public static class Builder {

        private final byte[] pdfByteData;
        private int maxPdfPage;
        private boolean skipBlankPage;
        private double blankPageDecidingFactor;
        private int nonBlankWhiteMaxValue;
        private String outputImageFormat;
        private int dpi;
        private ImageType imageType;

        public Builder(byte[] pdfByteData) {

            this.pdfByteData = pdfByteData;
            this.maxPdfPage = 0;
            this.skipBlankPage = true;
            this.blankPageDecidingFactor = 0.999;
            this.nonBlankWhiteMaxValue = 248;
            this.outputImageFormat = "jpg";
            this.dpi = 100;
            this.imageType = ImageType.RGB;
        }

        public Builder maxPdfPage(int maxPdfPage) {
            this.maxPdfPage = maxPdfPage;
            return this;
        }

        public Builder skipBlankPage(boolean skipBlankPage) {
            this.skipBlankPage = skipBlankPage;
            return this;
        }

        public Builder blankPageDecidingFactor(double blankPageDecidingFactor) {
            this.blankPageDecidingFactor = blankPageDecidingFactor;
            return this;
        }

        public Builder nonBlankWhiteMaxValue(int nonBlankWhiteMaxValue) {
            this.nonBlankWhiteMaxValue = nonBlankWhiteMaxValue;
            return this;
        }

        public Builder outputImageFormat(String outputImageFormat) {
            this.outputImageFormat = outputImageFormat;
            return this;
        }

        public Builder dpi(int dpi) {
            this.dpi = dpi;
            return this;
        }

        public Builder imageType(ImageType imageType) {
            this.imageType = imageType;
            return this;
        }

        public PDF2ImageConverter build() {
            return new PDF2ImageConverter(this);
        }

    }

    private PDF2ImageConverter(Builder builder) {

        this.pdfByteData = builder.pdfByteData;
        this.maxPdfPage = builder.maxPdfPage;
        this.skipBlankPage = builder.skipBlankPage;
        this.blankPageDecidingFactor = builder.blankPageDecidingFactor;
        this.nonBlankWhiteMaxValue = builder.nonBlankWhiteMaxValue;
        this.outputImageFormat = builder.outputImageFormat;
        this.dpi = builder.dpi;
        this.imageType = builder.imageType;
    }

    public List<byte[]> convertToImage() throws Exception {

        if(!isPDF(this.pdfByteData)){
            throw new Exception("Invalid input file format, input byte must be in pdf format");
        }

        PDDocument document = PDDocument.load(this.pdfByteData);
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        List<byte[]> convertedImages = new ArrayList<>();
        int nonEmptyPages = 0;
        for (int page = 0; page < document.getNumberOfPages(); ++page) {

            BufferedImage image = pdfRenderer.renderImageWithDPI(page, this.dpi, this.imageType);

            if (this.skipBlankPage && isBlank(image, this.blankPageDecidingFactor, this.nonBlankWhiteMaxValue)) {
                LOG.info("Skipping empty page with page number :" + page);
                continue;
            }
            if (this.maxPdfPage != 0 && nonEmptyPages >= this.maxPdfPage) {
                throw new Exception("PDF file cannot contain more then " + this.maxPdfPage + " page(s)");
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, this.outputImageFormat, outputStream);
            convertedImages.add(outputStream.toByteArray());
        }

        document.close();
        LOG.info("PDF to Image conversion completed");
        return convertedImages;
    }


    public static boolean isBlank(BufferedImage bufferedImage, double percentageToDecideBlankPage, int nonBlankWhiteMaxValue) {
        long count = 0;
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();

        LOG.info("Checking is current page is blank or not");

        Double areaFactor = (width * height) * percentageToDecideBlankPage;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
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

    public static boolean isPDF(byte[] data) {
        LOG.info( "Validating input byte array is of PDF or not");
        if (data != null && data.length > 4 &&
                data[0] == 0x25 && // %
                data[1] == 0x50 && // P
                data[2] == 0x44 && // D
                data[3] == 0x46 && // F
                data[4] == 0x2D) { // -

            LOG.info( "File validated");
            return true;
        }
        LOG.info( "Invalid file format");
        return false;
    }

}
