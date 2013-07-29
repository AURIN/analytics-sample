package au.org.aurin.sample.tests;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;


import au.edu.uq.aurin.util.Rscript;
import au.edu.uq.aurin.util.Rserve;
import au.org.aurin.sample.SampleComponent;


public class SampleComponentTest {

  @BeforeClass
  public static void initRserve() {
    boolean rRunning = false;
    // 0. Start Rserve - This should already be running, if not we start it
    rRunning = Rserve.checkLocalRserve();
    System.out.println("Rserve running? " + rRunning);
    if (!rRunning) {
      Assert.fail("Without Rserve running we cannot proceed");
    }
  }
  @AfterClass
  public static void terminateRserve() {
    boolean rRunning = true;
    // Stop Rserve if we started it
    rRunning = Rserve.shutdownRserve();
    System.out.println("Rserve shutdown? " + rRunning);
    if (!rRunning) {
      Assert.fail("Cannot Shutdown Rserve, Check if there are permissions "
          + "to shut it down if the process is owned by a different user");
    }
  }
  
  @Test
  public void test() throws RserveException, IOException, REXPMismatchException {
    
    System.out.println("========= Test case SampleComponent");
    
    // Load data into RConnection and prepare data
    RConnection cOut = new RConnection();    
    cOut.assign("myDataFrame", dataGenerator());
    cOut.assign("attributeName", "Col0");

    SampleComponent wc = new SampleComponent();
    wc.dataFrameName = "myDataFrame";
    wc.attributeName = "Col0";
    wc.cIn = cOut;
    wc.execute();
    
    System.out.println("textRestult=" + wc.textResult);


  }
  
  public REXP dataGenerator() throws REXPMismatchException {
    
    REXP dataframe = null;
  
    double[] i0 = {1.1, 2.2, 3.3, 11.1, 22.2, 33.3}; 
    double[] i1 = {10.0, 20.0, 30.0, 40.0, 50.0, 60.0};  
    double[] i2 = {100.0, 200.0, 300.0, 400.0, 500.0, 600.0}; 
    double[] i3 = {100.1, 200.2, 300.3, 110.1, 220.2, 3300.3};
  
    RList a = new RList();
    // add each column separately
    a.put("Col0", new REXPDouble(i0));
    a.put("Col1", new REXPDouble(i1));
    a.put("Col2", new REXPDouble(i2));
    a.put("Col3", new REXPDouble(i3));
    
    dataframe = REXP.createDataFrame(a);
    
    return dataframe;
  }
  
  

}