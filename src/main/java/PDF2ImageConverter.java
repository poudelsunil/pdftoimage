import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PDF2ImageConverter {
    private byte[] pdfByteData;

    private int maxPdfPage; /*3*/
    private boolean skipBlankPage; /*true*/
    private double blankPageDecidingFactor; /*0.999*/
    private int nonBlankWhiteMaxValue; /*248*/
    private String outputImageFormat; /*png/jpg*/
    private int dpi; /*100 - control imagequality*/
    private ImageType imageType; /*ImageType.RGB*/

    public static class Builder {
        private final byte[] pdfByteData;
        private int maxPdfPage; /*3*/
        private boolean skipBlankPage; /*true*/
        private double blankPageDecidingFactor; /*0.999*/
        private int nonBlankWhiteMaxValue; /*248*/
        private String outputImageFormat; /*png/jpg*/
        private int dpi; /*100 - control imagequality*/
        private ImageType imageType; /*ImageType.RGB*/

        public Builder(byte[] pdfByteData) {

            this.pdfByteData = pdfByteData;
            maxPdfPage = 0;
            skipBlankPage = true;
            blankPageDecidingFactor = 0.999;
            nonBlankWhiteMaxValue = 248;
            outputImageFormat = "jpg";
            dpi = 100;
            imageType = ImageType.RGB;
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
        PDDocument document = PDDocument.load(this.pdfByteData);
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        List<byte[]> convertedImages = new ArrayList<>();
        int nonEmptyPages = 0;
        for (int page = 0; page < document.getNumberOfPages(); ++page) {

            BufferedImage image = pdfRenderer.renderImageWithDPI(page, this.dpi, this.imageType);

            if (this.skipBlankPage && isBlank(image, this.blankPageDecidingFactor, this.nonBlankWhiteMaxValue)) {
                System.out.println("Skipping empty page with page number :" + page);
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
        return convertedImages;
    }


    private static Boolean isBlank(BufferedImage bufferedImage, double percentageToDecideBlankPage, int nonBlankWhiteMaxValue) {
        long count = 0;
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();
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

}
