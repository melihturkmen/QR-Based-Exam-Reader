/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdftopng;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class FXMLDocumentController implements Initializable {

    @FXML
    private TextField questionfield;

    @FXML
    private TextArea comment;

    @FXML
    private Button savebutton;

    @FXML
    private Button errornext;

    @FXML
    private Button errorback;

    @FXML
    private Label errorlabel;

    @FXML
    private TextField studentname;

    @FXML
    private TextField totalgrade;

    @FXML
    private Button backbutton;

    @FXML
    private Button nextbutton;

    @FXML
    private Button CropImage;

    @FXML
    private Button CropQR;

    @FXML
    private Button choosePngPathBtn;

    @FXML
    private Button chooseExamPdfBtn;

    @FXML
    private Button QRreader;

    @FXML
    private Label label;

    @FXML
    private ImageView imagepane;

    @FXML
    private ImageView errorimage;

    @FXML
    private Label namelabel;

    @FXML
    private Label idlabel;

    @FXML
    private Label answerlabel;

    public String PdfPath = null;
    public String ExamPath = null;
    public String PngPath = null;
    String path;
    int sn = 0;
    int pagecounter = 1;
    int max = 0;
    int errorcounter = 0;
    
    StringBuilder t = new StringBuilder();

    ArrayList<Student> students = new ArrayList<>();
    ArrayList<Question> questions = new ArrayList<>();
    ArrayList<String> error = new ArrayList<>();
    ArrayList<String> success = new ArrayList<>();
    HashMap<String, Student> hash = new HashMap<>();
    HashMap<Integer, ArrayList<Question>> hmap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
      

        chooseExamPdfBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                FileChooser chooser = new FileChooser();
                chooser.setInitialDirectory(new File("."));
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
                chooser.setTitle("Select EXAM");
                Stage stage = new Stage();
                File file = chooser.showOpenDialog(stage);
                if (file == null) {
                    return;
                }

                PdfPath = file.getAbsolutePath();

            }
        });
        choosePngPathBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                Stage stage = new Stage();
                File selectedDirectory = directoryChooser.showDialog(stage);
                PngPath = selectedDirectory.getAbsolutePath();

                try {
                    PDFToPNG(PdfPath, PngPath);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        errornext.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                errorcounter++;
                int seccounter =0;
                
                if (errorcounter < error.size()+1) {

                    for (int i = 0; i < students.size(); i++) {
                        seccounter++;
                        System.out.println(students.get(i).name);
                        if (students.get(i).name.equals(studentname.getText())) {
                            seccounter--;
                            if (totalgrade.getText() != null) {
                               
                                students.get(i).setTotalGrade(Integer.parseInt(totalgrade.getText()));
                                
                            }
                        }
                    }
                if(seccounter == students.size()){
                    students.add(new Student(studentname.getText(),Integer.parseInt(totalgrade.getText())));
                }    
                    
                    try {
                        openError(error.get(errorcounter-1));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    errorcounter--;
                    try {
                        openError(error.get(errorcounter));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }

        });

        errorback.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                errorcounter--;
                if (errorcounter > -1) {
                    try {
                        openError(error.get(errorcounter));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    errorcounter = 0;
                    try {
                        openError(error.get(errorcounter));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }

        });
        savebutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = workbook.createSheet("Öğrenci notları");

                sheet.setColumnWidth(1, 9000);
                sheet.setColumnWidth(2, 9000);
                sheet.setColumnWidth(3, 9000);

                Object[][] bookData = new Object[students.size() + 1][3];
                bookData[0][0] = "Öğrenci İsimleri";
                bookData[0][1] = "ID";
                bookData[0][2] = "Notlar";
                for (int i = 0; i < students.size(); i++) {
                    bookData[i + 1][0] = students.get(i).name;
                    bookData[i + 1][1] = students.get(i).id;
                    bookData[i + 1][2] = students.get(i).getGrades();

                }

                int rowCount = 0;
                for (Object[] aBook : bookData) {
                    Row row = sheet.createRow(++rowCount);

                    int columnCount = 0;

                    for (Object field : aBook) {
                        Cell cell = row.createCell(++columnCount);
                        if (field instanceof String) {
                            cell.setCellValue((String) field);
                        } else if (field instanceof Integer) {
                            cell.setCellValue((Integer) field);
                        } else if (field instanceof Double) {
                            cell.setCellValue((Double) field);
                        }
                    }

                }

                try (FileOutputStream outputStream = new FileOutputStream("grades.xlsx")) {
                    workbook.write(outputStream);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });
        backbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (sn > 0) {
                    sn--;

                    path = path.substring(0, 0);
                    t.delete(0, t.length());
                    t.append(path);
                    t.append(students.get(sn).name);
                    t.append("\\");
                    t.append(pagecounter);
                    t.append(".png");
                    path = t.toString();

                    try {
                        
                        openBackFile(path);
                        namelabel.setText(students.get(sn).name);
                        idlabel.setText(students.get(sn).id);
                        answerlabel.setText(String.valueOf(pagecounter));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else if (sn == 0 && pagecounter == 1) {
                    path = path.substring(0, 0);
                    t.delete(0, t.length());
                    t.append(path);
                    t.append(students.get(sn).name);
                    t.append("\\");
                    t.append(pagecounter);
                    t.append(".png");
                    path = t.toString();

                    try {
                        
                        openBackFile(path);
                        namelabel.setText(students.get(sn).name);
                        idlabel.setText(students.get(sn).id);
                        answerlabel.setText(String.valueOf(pagecounter));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    pagecounter--;
                    sn = sn + students.size() - 1;
                    path = path.substring(0, 0);
                    t.delete(0, t.length());
                    t.append(path);
                    t.append(students.get(sn).name);
                    t.append("\\");
                    t.append(pagecounter);
                    t.append(".png");
                    path = t.toString();

                    try {
                        
                        openBackFile(path);
                        namelabel.setText(students.get(sn).name);
                        idlabel.setText(students.get(sn).id);
                        answerlabel.setText(String.valueOf(pagecounter));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }
        });

        nextbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                StringBuilder u = new StringBuilder();
                u.append(students.get(sn).name);
                u.append("\\");
                u.append("comment");
                u.append(".txt");
                String finalcomment = u.toString();

                File f2 = new File(finalcomment);
                if (f2.exists()) {
                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(f2, true));
                        bw.append(String.valueOf(pagecounter));
                        bw.append(". Question Comment is: ");
                        bw.append(comment.getText());
                        bw.append((System.getProperty("line.separator")));
                        bw.flush();
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        FileWriter fw = new FileWriter(finalcomment);
                        BufferedWriter bw = new BufferedWriter(new FileWriter(f2, true));
                        bw.append(String.valueOf(pagecounter));
                        bw.append(". Question Comment is: ");
                        bw.append(comment.getText());
                        bw.append((System.getProperty("line.separator")));
                        bw.flush();
                        bw.close();
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                String sname = namelabel.getText();
                
                
                for (int i = 0; i < students.size(); i++) {
                    if (students.get(i).name.equals(sname)) {
                        if (questions.get(pagecounter - 1).maxgrade >= Integer.parseInt(questionfield.getText())) {

                            hmap.get(sn + 1).get(pagecounter - 1).cgrade = Integer.parseInt(questionfield.getText());
                            students.get(sn).grades[pagecounter - 1] = hmap.get(sn + 1).get(pagecounter - 1).cgrade;

                            sn++;
                            if (sn == students.size()) {
                                sn = 0;
                                pagecounter++;

                            }

                            if (pagecounter > max) {
                                pagecounter--;
                                sn = sn + students.size() - 1;
                                path = path.substring(0, 0);
                                t.delete(0, t.length());
                                t.append(path);
                                t.append(students.get(sn).name);
                                t.append("\\");
                                t.append(pagecounter);
                                t.append(".png");
                                path = t.toString();

                                try {
                                openFile(path);
                                namelabel.setText(students.get(sn).name); 
                                path = path.substring(0, 0);
                                t.delete(0, t.length());
                                t.append(path);
                                t.append(students.get(sn).name);
                                t.append("\\");
                                t.append(pagecounter);
                                t.append(".png");
                                path = t.toString();
                                    idlabel.setText(students.get(sn).id);
                                    answerlabel.setText(String.valueOf(pagecounter));
                                } catch (MalformedURLException ex) {
                                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }

                            path = path.substring(0, 0);
                            t.delete(0, t.length());
                            t.append(path);
                            t.append(students.get(sn).name);
                            t.append("\\");
                            t.append(pagecounter);
                            t.append(".png");
                            path = t.toString();

                            try {
                                openFile(path);
                                namelabel.setText(students.get(sn).name);
                                idlabel.setText(students.get(sn).id);
                                answerlabel.setText(String.valueOf(pagecounter));
                            } catch (MalformedURLException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        } else {
                            errorlabel.setText("Grade is too high. Max is: " + questions.get(pagecounter - 1).maxgrade);

                        }

                    }

                }

            }
        });

        QRreader.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                ArrayList<BufferedImage> QRImage = new ArrayList<>();

                DirectoryChooser directoryChooser = new DirectoryChooser();
                Stage stage = new Stage();
                File selectedDirectory = directoryChooser.showDialog(stage);
                ExamPath = selectedDirectory.getAbsolutePath();

                boolean exist = true;
                int exist_counter = 0;

                while (exist) {

                    exist_counter++;

                    StringBuilder existbuilder = new StringBuilder();
                    existbuilder.append(ExamPath);
                    existbuilder.append("\\");
                    existbuilder.append("Page");
                    existbuilder.append(exist_counter);
                    existbuilder.append(".png");
                    String finalexist = existbuilder.toString();

                    BufferedImage img = null;
                    try {
                        img = ImageIO.read(new File(finalexist));
                    } catch (IOException ex) {

                        break;
                    }
                    BufferedImage QRCropImage = readImage(finalexist);

                    BufferedImage croppedImage = null;

                    int w = QRCropImage.getWidth();
                    int h = QRCropImage.getHeight();

                    /* Cropping the original image into sub-images according to percentages */
                    croppedImage = QRCropImage.getSubimage((int) (w * 3.0 / 4.0), (int) (h * (1.0 / 100.0)), (int) (w - (w * 3.0 / 4.0)), (int) (h * (((17.0 / 100.0) - (0.0 / 100.0)))));

                    try {
                        /* saving subImages as png into given path */
                        ImageIO.write(croppedImage, "png", new File("QRImage.png"));
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    try {
                        File file = new File("QRImage.png");
                        String decodedText = decodeQRCode(file);
                        if (decodedText == null) {

                            error.add(finalexist);
                            if (!hmap.containsKey(exist_counter)) {
                                hmap.put(exist_counter, questions);
                            }
                            openError(error.get(0));

                        } else {
                            String[] parts;

                            parts = decodedText.split(",");

                            String name = parts[4];
                            new File(name).mkdir();
                            System.out.println("Decoded text = " + decodedText);
                            int plength = parts.length - 6;
                            int[] percentages = new int[plength];
                            String[] firstpercentage;

                            int gradeslength = parts.length - 5;

                            students.add(new Student(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), parts[4], parts[5], gradeslength));
                            success.add(finalexist);
                            hash.put(finalexist, new Student(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), parts[4], parts[5], gradeslength));
                            for (int i = 0; i < plength; i++) {

                                firstpercentage = parts[i + 6].split("%");
                                String[] firstpercentagee = firstpercentage[1].split("\\.");
                                String[] q1 = firstpercentage[0].split("\\.");
                                String[] secondpercentagee = firstpercentage[0].split("\\.");
                                percentages[i] = Integer.parseInt(firstpercentagee[0]);
                                questions.add(new Question(Integer.parseInt(q1[1]), Integer.parseInt(q1[2])));

                            }
                            if (!hmap.containsKey(exist_counter)) {
                                hmap.put(exist_counter, questions);
                            }

                            ArrayList<BufferedImage> partsOfImage = new ArrayList<>();

                            
                            BufferedImage originalImage = readImage(finalexist);

                           
                            BufferedImage subImgage = null;

                           
                            for (int i = 0; i < percentages.length; i++) {
                                try {
                                    
                                    if (i + 1 != percentages.length) {

                                        subImgage = originalImage.getSubimage(0, (int) (h * (percentages[i] / 100.0)), w, (int) (h * (((percentages[i + 1] / 100.0) - (percentages[i] / 100.0)))));
                                    } else {
                                        subImgage = originalImage.getSubimage(0, (int) (h * (percentages[i] / 100.0)), w, (int) (h * (100.0 / 100.0 - (percentages[i] / 100.0))));
                                    }
                                    
                                    String partial[] = parts[6 + i].split("\\.");

                                    StringBuilder a = new StringBuilder();

                                   
                                    a.append(parts[4]);
                                    a.append("\\");
                                    int b;
                                    if ("1".equals(partial[0])) {
                                        b = i + 1;
                                    } else {
                                        b = i + 4;
                                    }
                                    if (b > max) {
                                        max = b;
                                    }
                                    String finalString = a.toString();

                                    ImageIO.write(subImgage, "png", new File(finalString + b + ".png"));

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    } catch (IOException e) {
                        System.out.println("Could not decode QR Code, IOException :: " + e.getMessage());
                    }

                }

                for (int i = 0; i < students.size(); i++) {
                    for (int j = 0; j < students.size(); j++) {
                        if (students.get(i).name.equals(students.get(j).name) && j != i) {
                            students.remove(students.get(j));
                            i = 0;
                        }
                    }
                }

                for (int i = 0; i < questions.size(); i++) {
                    for (int j = 0; j < questions.size(); j++) {
                        if (questions.get(i).qnumber == questions.get(j).qnumber && j != i) {
                            questions.remove(questions.get(j));
                            i = 0;
                        }
                    }
                }

                
                t.append(students.get(sn).name);
                t.append("\\");
                t.append(pagecounter);
                t.append(".png");
                path = t.toString();
                try {
                    namelabel.setText(students.get(sn).name);
                    idlabel.setText(students.get(sn).id);
                    answerlabel.setText(String.valueOf(pagecounter));
                    openFile(path);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public void openError(String x) throws MalformedURLException {
        File file = new File(x);
        Image image = new Image(file.toURI().toString());
        errorimage.setImage(image);
        errorimage.setSmooth(true);
        errorimage.setCache(true);
    }

    public void openFile(String x) throws MalformedURLException {
        File file = new File(x);
        
        if(!file.exists()){
            if(sn==students.size()-1){
                sn =0;
                pagecounter++;
                path = path.substring(0, 0);
                t.delete(0, t.length());
                t.append(path);
                t.append(students.get(sn).name);
                t.append("\\");
                t.append(pagecounter);
                t.append(".png");
                path = t.toString();
                openFile(path);
            }
            
            else{
            sn++;
            
            path = path.substring(0, 0);
            t.delete(0, t.length());
            t.append(path);
            t.append(students.get(sn).name);
            t.append("\\");
            t.append(pagecounter);
            t.append(".png");
            path = t.toString();
            openFile(path);
            }

        }
        else{
        Image image = new Image(file.toURI().toString());
        imagepane.setImage(image);
        imagepane.setSmooth(true);
        imagepane.setCache(true);
        }

    }
    
    public void openBackFile(String x) throws MalformedURLException {
        File file = new File(x);
        
        if(!file.exists()){
            if(sn==students.size()-1){
                sn --;
               
                path = path.substring(0, 0);
                t.delete(0, t.length());
                t.append(path);
                t.append(students.get(sn).name);
                t.append("\\");
                t.append(pagecounter);
                t.append(".png");
                path = t.toString();
                openFile(path);
            }
            
            else{
            sn++;
            pagecounter--;
            
            path = path.substring(0, 0);
            t.delete(0, t.length());
            t.append(path);
            t.append(students.get(sn).name);
            t.append("\\");
            t.append(pagecounter);
            t.append(".png");
            path = t.toString();
            openFile(path);
            }

        }
        else{
        Image image = new Image(file.toURI().toString());
        imagepane.setImage(image);
        imagepane.setSmooth(true);
        imagepane.setCache(true);
        }

    }

    private void PDFToPNG(String inputPdfPath, String outputPngPath) throws IOException {
        File f = new File(outputPngPath);
        if (!f.exists()) {
            f.mkdir();
        }
        PDDocument document = PDDocument.load(new File(inputPdfPath));
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        for (int page = 0; page < document.getNumberOfPages(); ++page) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
            ImageIOUtil.writeImage(bim, outputPngPath + "/Page" + (page + 1) + ".png", 300);
            System.out.println("page " + (page + 1) + "finished...");
        }
        document.close();

    }

    public static BufferedImage readImage(String path) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(path));//img = ImageIO.read(new File("f"));
            System.out.println("Image is read");
            return img;
        } catch (IOException e) {
            System.err.println("Image cannot be read");
        }
        return null;
    }

    private static String decodeQRCode(File qrCodeimage) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(qrCodeimage);
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            System.out.println("There is no QR code in the image");
            return null;
        }
    }

}
