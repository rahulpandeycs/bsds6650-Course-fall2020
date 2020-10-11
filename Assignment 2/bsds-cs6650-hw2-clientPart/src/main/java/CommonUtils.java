import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;


import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;

public class CommonUtils {

  public static <T> String writeCsvFromBean(Path path, List<T> CsvBean) throws Exception {
    Writer writer  = new FileWriter(path.toString());

    StatefulBeanToCsv sbc = new StatefulBeanToCsvBuilder(writer)
            .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
            .build();

    sbc.write(CsvBean);
    writer.close();
    return path.toString();
  }

//  /**
//   * Simple File Reader
//   */
//
//  public static String readFile(Path path) {
//    String response = "";
//    try {
//      FileReader fr = new FileReader(path.toString());
//      BufferedReader br = new BufferedReader(fr);
//      String strLine;
//      StringBuffer sb = new StringBuffer();
//
//      while ((strLine = br.readLine()) != null) {
//        sb.append(strLine);
//      }

//      response = sb.toString();
//      System.out.println(response);
//      fr.close();
//      br.close();
//    } catch (Exception ex) {
//      err(ex);
//    }
//    return response;
//  }

  public static void err(Exception ex) {
    System.out.println(Constants.GENERIC_EXCEPTION + " " + ex);
  }

}
