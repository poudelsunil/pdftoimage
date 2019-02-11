# pdftoimage converter using (org.apache.pdfbox)

## this will convert pdf file to image(jpg or png), following options are available.
- option to skip blank page : default true
- option to add max page number restriction : default 0 (no page count restriction)
- option to select output image format (jpg/png) : default jpg
- option to select image type (pdfbox.rendering.ImageType: BINARY, GRAY, RGB and ARGB) : default ImageType.RGB
- option to select dpi value : defaut 100
- option to select max white rgb value to treat as blank pixel: will be use to decide whether page is blank or not : default 248
- option to select percentage value to allow how much % blank space will avialable. : default 0.999 (if 99.9% of page is white(>248 white value) then page will treat as blank)
