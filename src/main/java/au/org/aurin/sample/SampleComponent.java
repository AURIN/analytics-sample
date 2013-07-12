package au.org.aurin.sample;

import java.io.IOException;

import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Name;
import oms3.annotations.Out;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import au.edu.uq.aurin.util.Rscript;


@Name("Sample Component") // This name appears in the AURIN UI
@Description("This is a sample analyical component") // This description appears in the AURIN UI. Full Sentences please.
public class SampleComponent {
  
  @In
  @Description("Rconnection")
  public RConnection cIn; // A connection to the R environment. Typically it
                          // contains an dataframe to be analysed. 
  
  @In
  @Description("dataframeName")
  public String dataframeName; // The name of the dataframe to be analysed. Used
                               // to retrieve the dataframe in R.

  @In
  @Name("Attribute to be Analysed") // This name appears in the AURIN UI. Note Capitalisation.
  @Description("This is an attribute to be analysed by sample component.") // This description appears in the AURIN UI. Full Sentences please.
  public String attribute01;

  @Description("attribute02")
  @Out
  public String attribute02;

  @Execute
  public void execute() throws REXPMismatchException, RserveException, IOException {    
    try {
      this.cIn.assign("script", Rscript.load("/rscripts/Addition.r"));
      } catch (IOException e) {
        throw new IOException("Unable to load Script", e);
      }   
    this.cIn.assign("attribute01", this.attribute01);
    this.cIn.assign("attribute02", this.attribute02);
    this.cIn.assign("dataframeName", this.dataframeName);
    this.cIn.eval("try(eval(parse(text=script)),silent=FALSE)");

  }

}