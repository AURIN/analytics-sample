package au.org.aurin.sample;

import java.io.IOException;  

import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Name;
import oms3.annotations.Out;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
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
  
  public String prettyPrint() throws REXPMismatchException {

    String result;
    result = "mean(" + attributeName + ") = " + this.worker.asString();
    return result;
    
  }
}