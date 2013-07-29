package au.org.aurin.sample;

import java.io.IOException;
import java.text.DecimalFormat;

import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Name;
import oms3.annotations.Out;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.uq.aurin.util.Rscript;


@Name("Sample Component") // This name appears in the AURIN UI
@Description("This is a sample analyical component. It computes the mean of a variable.") // This description appears in the AURIN UI. Full Sentences please.
public class SampleComponent {
  
  @In
  @Description("Rconnection")
  public RConnection cIn; // A connection to the R environment. Typically it
                          // contains an dataframe to be analysed. 
  
  @In
  @Description("dataFrameName")
  public String dataFrameName; // The name of the dataframe to be analysed. Used
                               // to retrieve the dataframe in R.

  @In
  @Name("Attribute to be Analysed") // This name appears in the AURIN UI. Note Capitalisation.
  @Description("This is an attribute to be analysed by sample component. A mean will be computed.") // This description appears in the AURIN UI. Full Sentences please.
  public String attributeName;
  
  @Out
  @Description("Pretty print result")
  public String textResult;
  
  protected REXP worker;
  
  protected final static Logger LOG = LoggerFactory.getLogger(SampleComponent.class);

  @Execute
  public void execute() throws REXPMismatchException, RserveException, IOException {    
    try {
      this.cIn.assign("script", Rscript.load("/rscripts/mean.r"));
      } catch (IOException e) {
        throw new IOException("Unable to load Script", e);
      }   
    this.cIn.assign("attributeName", this.attributeName);
    this.cIn.assign("dataFrameName", this.dataFrameName);
    
    // 3. call the function defined in the script
    this.worker = this.cIn.eval("try(eval(parse(text=script)),silent=FALSE)");
    
    // 5. Setup textual result automatically
    this.textResult = this.prettyPrint();

  }
  
  public String prettyPrint() {

    StringBuilder s = new StringBuilder();

    // use the REXP worker result
    try {
      if (!this.worker.isNull()) {
        LOG.debug("We have content back from R");

        if (this.worker.inherits("try-error") || this.worker.isString()) {
          throw new REXPMismatchException(this.worker,
              "Try-Error from R \n" + this.worker.toString());
        } else if (this.worker.isList()) {
          RList resultL = this.worker.asList();
          RList r1 = null;

          // -1 from the size as we dont parse JSON here
          for (int ridx = 0; ridx < resultL.size() - 1; ridx++) {
            // last result in list is JSON
            LOG.debug("ridx = " + ridx);
            r1 = resultL.at(ridx).asList();
            s.append(parseResults(r1));
          }
        }
      } else {
        // worker is null, we did not get any results back from R
        throw new REXPMismatchException(this.worker,
            "No Result returned from R \n"
                + this.worker.toDebugString());
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    
    LOG.info(s.toString());
    return s.toString();
  }

  private String parseResults(RList r1) throws REXPMismatchException {

    StringBuilder s = new StringBuilder();
    String lineSep = System.getProperty("line.separator");

    DecimalFormat df = new DecimalFormat("####.####");

    RList attribs = r1.at(0)._attr().asList();
    String[] colNames = attribs.at(0).asStrings();
    String[] rowNames = attribs.at(1).asStrings();
    for (String e : colNames) {
      s.append("\t" + e + "\t");
    }
    s.append(lineSep);

    RList result = r1.at(0).asList();
    for (int j = 0; j < result.at(0).asDoubles().length; j++) {
      for (int i = 0; i < result.size(); i++) {
        double[] val = result.at(i).asDoubles();
        if (i == 0) {
          s.append(rowNames[j] + "\t");
        }
        if (Double.isNaN(val[j]) || Double.isInfinite(val[j])) {
          s.append(val[j]);
        } else {
          s.append(df.format(val[j]));
        }
        s.append("\t");
      }
      s.append(lineSep);
    }
    s.append(lineSep);

    return s.toString();
  }

}